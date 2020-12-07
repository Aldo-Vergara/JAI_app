package com.movil.jaiapp.ui.register.member;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.movil.jaiapp.MainMemberActivity;
import com.movil.jaiapp.R;
import com.movil.jaiapp.models.Product;
import com.movil.jaiapp.models.UserMember;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class MemberFragment extends Fragment implements View.OnClickListener, OnMapReadyCallback,
        GoogleMap.OnMarkerDragListener {

    private ProgressDialog progressDialog;
    private MemberViewModel memberViewModel;
    public static final int CAMERA_PERM_CODE = 101;
    public static final int CAMERA_REQUEST_CODE = 102;
    // Estado del settings de verificación de permisos de GPS
    public static final int REQUEST_CHECK_SETTINGS = 102;
    public static final int REQUEST_TAKE_PHOTO = 1;
    public static final int GALLERY_REQUEST_CODE = 105;
    private Uri contentUri;
    private ImageView imgViewProduct;
    private ImageButton imgBtnCamera, imgBtnGallery;
    private String currentPhotoPath, imageName = "";
    private String imagePrb = "https://www.google.com/url?sa=i&url=http%3A%2F%2Fzazsupercentro.com%2F%3Fattachment_id%3D2338&psig=AOvVaw0mNIC2HF31XkNKb4TZnSHz&ust=1607457787753000&source=images&cd=vfe&ved=0CAIQjRxqFwoTCJjppIbVvO0CFQAAAAAdAAAAABAD";

    private EditText etName, etLastname, etNumMember, etPhoneNumber, etEmail, etPassword, etConfirmPassword;
    double dLat = 0, dLong = 0;
    private Marker markerUbication;
    private Button btnRegister;
    private MapView mapView;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        memberViewModel = ViewModelProviders.of(this).get(MemberViewModel.class);
        final View root = inflater.inflate(R.layout.fragment_register_member, container, false);

        initFirebase();
        initComponents(root);

        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        return root;
    }

    private void initFirebase() {
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();
    }

    private void initComponents(View root) {
        progressDialog = new ProgressDialog(getContext());

        imgViewProduct = root.findViewById(R.id.member_imgView_photoMember);
        imgBtnCamera = root.findViewById(R.id.member_imgBtn_camera);
        imgBtnGallery = root.findViewById(R.id.member_imgBtn_gallery);
        imgBtnCamera.setOnClickListener(this);
        imgBtnGallery.setOnClickListener(this);

        etName = root.findViewById(R.id.member_txtInputEdit_name);
        etLastname = root.findViewById(R.id.member_txtInputEdit_lastName);
        etNumMember = root.findViewById(R.id.member_txtInputEdit_numMember);
        etPhoneNumber = root.findViewById(R.id.member_txtInputEdit_phoneNumber);
        etEmail = root.findViewById(R.id.member_txtInputEdit_email);
        etPassword = root.findViewById(R.id.member_txtInputEdit_password);
        etConfirmPassword = root.findViewById(R.id.member_txtInputEdit_confirmPassword);

        btnRegister = root.findViewById(R.id.member_btn_register);
        btnRegister.setOnClickListener(this);

        mapView = root.findViewById(R.id.member_mapView_ubication);
    }

    private void mValidate() {
        if (etName.getText().toString().equals("")) {
            etName.setError("Nombre requerido");
        } else if (etLastname.getText().toString().equals("")) {
            etLastname.setError("Apellidos requeridos");
        } else if (etPhoneNumber.getText().toString().equals("")) {
            etPhoneNumber.setError("Número de teléfono requerido");
        } else if (etNumMember.getText().toString().equals("")) {
            etNumMember.setError("Número de socio requerido");
        } else if (etEmail.getText().toString().equals("")) {
            etEmail.setError("Correo requerido");
        } else if (etPassword.getText().toString().equals("")) {
            etPassword.setError("Contraseña requerida");
        } else if (etConfirmPassword.getText().toString().equals("")) {
            etConfirmPassword.setError("Contraseña requerida");
        }
    }

    private void mClean() {
        etNumMember.setText("");
        etName.setText("");
        etLastname.setText("");
        etPhoneNumber.setText("");
        etEmail.setText("");
        etPassword.setText("");
        etConfirmPassword.setText("");
    }

    private void mShowAlert(String title, String message) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
        dialog.setTitle(title);
        dialog.setMessage(message);
        dialog.setPositiveButton("Aceptar", null);
        dialog.show();
    }

    private void mRegisterUser(String email, String password) {
        progressDialog.setIcon(R.mipmap.ic_launcher);
        progressDialog.setMessage("Cargando...");
        progressDialog.show();

        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {
                    //referencia hacia el nodo padre de Storage
                    final StorageReference image = storageReference.child("pictures/" + imageName);
                    UploadTask uploadTask = image.putFile(contentUri);// insertas la foto en Storage.

                    //continuo con la operación para obtener la ruta de Storage
                    uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw Objects.requireNonNull(task.getException());
                            }
                            return image.getDownloadUrl(); //RETORNO LA  URL DE DESCARGA DE LA FOTO
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if(task.isSuccessful()){
                                Uri uri = task.getResult();  //AQUI YA TENGO LA RUTA DE LA FOTO LISTA PARA INSERTRLA EN DATABASE
                                assert uri != null;

                                List<Product> productList = new ArrayList<>();
                                Product product = new Product(UUID.randomUUID().toString(), imagePrb);
                                productList.add(product);
                                UserMember userMember = new UserMember(
                                        UUID.randomUUID().toString(),
                                        etNumMember.getText().toString().trim(),
                                        etName.getText().toString().trim(),
                                        etLastname.getText().toString().trim(),
                                        etPhoneNumber.getText().toString().trim(),
                                        etEmail.getText().toString().trim(),
                                        etPassword.getText().toString().trim(),
                                        uri.toString(),
                                        productList,
                                        dLat,
                                        dLong,
                                        "admin",
                                        new Date().toString(),
                                        "",
                                        1
                                );

                                databaseReference.child("UserMember").child(userMember.getId()).setValue(userMember).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            mClean();
                                            startActivity(new Intent(getContext(), MainMemberActivity.class));
                                            getActivity().finish();
                                        } else {
                                            mShowAlert("Error", "Se ha producido un error en la conexión");
                                        }
                                        progressDialog.dismiss();
                                    }
                                });
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            mShowAlert("Error en la imagen", "No se pudo subir la imagen al servidor");
                        }
                    });

                } else {
                    mShowAlert("Error", "Se ha producido un error al crear el usuario");
                }
            }
        });
    }

    private void askCameraPermissions() {
        if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(getActivity(), new String[] {Manifest.permission.CAMERA}, CAMERA_PERM_CODE);
        }else{
            //openCamera();
            dispatchTakePictureIntent();
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "MOBSHOP_" + timeStamp + "_";
        //File fileStorageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File fileStorageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", fileStorageDir);

        currentPhotoPath = image.getAbsolutePath();
        return  image;
    }

    private void dispatchTakePictureIntent(){
        Intent intentTakePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(intentTakePicture.resolveActivity(getActivity().getPackageManager()) != null){
            File photoFile = null;
            try {
                photoFile = createImageFile();
            }catch (IOException ex){
                mShowAlert("Error en camára", "No se pudo tomar la foto");
            }
            if(photoFile != null){
                Uri photoUri = FileProvider.getUriForFile(getContext(), "com.movil.jaiapp.android.fileprovider", photoFile);
                intentTakePicture.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(intentTakePicture, CAMERA_REQUEST_CODE);
            }
        }
    }

    private String getFileExt(Uri contentUri) {
        ContentResolver contentResolver = getActivity().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(contentUri));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.member_imgBtn_camera:
                askCameraPermissions();
                break;
            case R.id.member_imgBtn_gallery:
                Intent gallery = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(gallery, GALLERY_REQUEST_CODE);
                break;
            case R.id.member_btn_register:
                if (!etNumMember.getText().toString().isEmpty() && !etName.getText().toString().isEmpty() &&
                        !etLastname.getText().toString().isEmpty() && !etEmail.getText().toString().isEmpty() &&
                        !etPassword.getText().toString().isEmpty() && !etConfirmPassword.getText().toString().isEmpty() &&
                        !etPhoneNumber.getText().toString().isEmpty()) {

                    if (etPassword.getText().toString().length() >= 6) {
                        if (etPassword.getText().toString().equals(etConfirmPassword.getText().toString())) {
                            if(dLat != 0 && dLong != 0){
                                if(!imageName.equals("") && contentUri != null) {
                                    String email = etEmail.getText().toString().trim();
                                    String password = etPassword.getText().toString().trim();
                                    mRegisterUser(email, password);
                                }else{
                                    Toast.makeText(getContext(), "Debe subir una foto de perfil", Toast.LENGTH_SHORT).show();
                                }
                            }else{
                                Toast.makeText(getContext(), "Debe establecer una ubicación", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getContext(), "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getContext(), "La contraseña debe tener al menos 6 carácteres", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    mValidate();
                    Toast.makeText(getContext(), "Debe llenar todos los campos", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == CAMERA_PERM_CODE){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                //openCamera();
                dispatchTakePictureIntent();
            }else{
                Toast.makeText(getContext(), "Se requiere permiso para usar la cámara", Toast.LENGTH_SHORT).show();
            }
        }
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == CAMERA_REQUEST_CODE){
            if(requestCode == Activity.RESULT_OK){
                File file = new File(currentPhotoPath);
                imgViewProduct.setImageURI(Uri.fromFile(file));
                Intent intentMediaScan = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                Uri uri = Uri.fromFile(file);
                intentMediaScan.setData(uri);
                getActivity().sendBroadcast(intentMediaScan);

                imageName = file.getName();
                contentUri = uri;
                //uploadImageToFirebase(file.getName(),contentUri);
            }
        }

        if(requestCode == GALLERY_REQUEST_CODE){
            if(resultCode == Activity.RESULT_OK){
                Uri uri = data.getData();
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                String imageFileName = "MOBSHOP_" + timeStamp +"."+getFileExt(uri);
                imgViewProduct.setImageURI(uri);

                imageName = imageFileName;
                contentUri = uri;
                //uploadImageToFirebase(imageFileName, contentUri);
            }
        }
        //super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(getActivity(), new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CHECK_SETTINGS);
        }else{

            LatLng ubication = new LatLng(19.067577, -99.383843);
            markerUbication = googleMap.addMarker(new MarkerOptions()
                    .position(ubication)
                    .title("Tu ubicación")
                    .draggable(true)
            );
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ubication, 7));
            googleMap.getUiSettings().setZoomControlsEnabled(true);
            googleMap.setOnMarkerDragListener(this);
            googleMap.setMyLocationEnabled(true);
        }
    }

    @Override
    public void onPause() {
        mapView.onPause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onMarkerDragStart(Marker marker) {
    }

    @Override
    public void onMarkerDrag(Marker marker) {
        if(marker.equals(markerUbication)){
            dLat = marker.getPosition().latitude;
            dLong = marker.getPosition().longitude;
        }
    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        if(marker.equals(markerUbication)){
            Toast.makeText(getContext(), "Ubicación establecida", Toast.LENGTH_SHORT).show();
        }
    }
}