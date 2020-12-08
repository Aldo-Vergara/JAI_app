package com.movil.jaiapp.ui.member.search;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.movil.jaiapp.R;
import com.movil.jaiapp.models.UserMember;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SearchFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener{

    private SearchViewModel searchViewModel;
    public static final int CAMERA_PERM_CODE = 101;
    public static final int CAMERA_REQUEST_CODE = 102;
    public static final int REQUEST_TAKE_PHOTO = 1;
    public static final int GALLERY_REQUEST_CODE = 105;
    private Uri contentUri;
    private ImageView imgViewProduct;
    private ImageButton imgBtnCamera, imgBtnGallery;
    private ProgressDialog progressDialog;

    private EditText etIdP, etNameP, etCostP, etDescP;
    private Spinner spnCatP;
    private Switch switchP;
    private Button btnUpdate, btnDelete, btnClean;
    private ImageButton imgBtnSearch;
    private int statusP, position;
    private String strCategory, currentPhotoPath, imageName = "";
    static int flag = 0;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseUser user;
    private UserMember userData;
    private StorageReference storageReference;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        searchViewModel = ViewModelProviders.of(this).get(SearchViewModel.class);
        View root = inflater.inflate(R.layout.fragment_member_search, container, false);

        initFirebase();
        initComponents(root);
        getUserActive();

        return root;
    }

    private void initFirebase() {
        FirebaseApp.initializeApp(getContext());
        firebaseDatabase = FirebaseDatabase.getInstance();
        //firebaseDatabase.setPersistenceEnabled(true);
        databaseReference = firebaseDatabase.getReference();
        storageReference = FirebaseStorage.getInstance().getReference();
    }

    private void initComponents(View root) {
        progressDialog = new ProgressDialog(getContext());

        imgViewProduct = root.findViewById(R.id.member_frag_search_imgView_photoProduct);
        imgBtnCamera = root.findViewById(R.id.member_frag_search_imgBtn_camera);
        imgBtnGallery = root.findViewById(R.id.member_frag_search_imgBtn_gallery);
        imgBtnCamera.setOnClickListener(this);
        imgBtnGallery.setOnClickListener(this);

        etIdP = root.findViewById(R.id.member_frag_search_txtEdit_id);
        etNameP = root.findViewById(R.id.member_frag_search_txtEdit_name);
        etCostP = root.findViewById(R.id.member_frag_search_txtEdit_cost);
        etDescP = root.findViewById(R.id.member_frag_search_txtEdit_description);

        spnCatP = root.findViewById(R.id.member_frag_search_spn_category);
        loadDataSpinner(spnCatP);
        spnCatP.setOnItemSelectedListener(this);

        switchP = root.findViewById(R.id.member_frag_search_switch_status);

        imgBtnSearch = root.findViewById(R.id.member_frag_search_imgBtn_search);
        imgBtnSearch.setOnClickListener(this);

        btnUpdate = root.findViewById(R.id.member_frag_search_btn_update);
        btnUpdate.setOnClickListener(this);
        btnDelete = root.findViewById(R.id.member_frag_search_btn_delete);
        btnDelete.setOnClickListener(this);
        btnClean = root.findViewById(R.id.member_frag_search_btn_clean);
        btnClean.setOnClickListener(this);
    }

    private void getUserActive() {
        user = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference.child("UserMember").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot objSnaptshot : dataSnapshot.getChildren()){
                    UserMember userMember = objSnaptshot.getValue(UserMember.class);
                    if(user.getEmail().equals(userMember.getEmail())){
                        userData = userMember;
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void mClean(){
        etIdP.setText("");
        etNameP.setText("");
        etCostP.setText("");
        etDescP.setText("");
        imgViewProduct.setImageResource(R.drawable.icon_splash);

        loadDataSpinner(spnCatP);
        strCategory = "";
        flag = 0;
    }

    private void mValidate(){
        if(etIdP.getText().toString().equals("")){
            etIdP.setError("ID del producto es requerido");
        }else if(etNameP.getText().toString().equals("")){
            etNameP.setError("Nombre del producto es requerido");
        }else if(etCostP.getText().toString().equals("")){
            etCostP.setError("Precio del producto es requerido");
        }else if(etDescP.getText().toString().equals("")){
            etDescP.setError("Descripción del producto es requerida");
        }

    }

    private void mShowAlert(String title, String message){
        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
        dialog.setTitle(title);
        dialog.setMessage(message);
        dialog.setPositiveButton("Aceptar", null);
        dialog.show();
    }

    private void updateRegister(final UserMember user){
        progressDialog.setIcon(R.mipmap.ic_launcher);
        progressDialog.setMessage("Cargando...");
        progressDialog.show();

        if(contentUri != null){
            final StorageReference image = storageReference.child("pictures/" + imageName);
            image.putFile(contentUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    image.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            userData.getProductsList().get(position).setId(etIdP.getText().toString().trim());
                            userData.getProductsList().get(position).setCategory(strCategory);
                            userData.getProductsList().get(position).setName(etNameP.getText().toString().trim());
                            userData.getProductsList().get(position).setCost(etCostP.getText().toString().trim());
                            userData.getProductsList().get(position).setDescription(etDescP.getText().toString().trim());
                            if(switchP.isChecked()){
                                statusP = 1;
                            }else{
                                statusP = 0;
                            }
                            userData.getProductsList().get(position).setStatus(statusP);
                            userData.getProductsList().get(position).setImage(uri.toString());

                            databaseReference.child("UserMember").child(user.getId()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        mClean();
                                        Toast.makeText(getContext(), "Producto actualizado correctamente", Toast.LENGTH_SHORT).show();
                                        //getActivity().finish();
                                    }else{
                                        mShowAlert("Error", "No se pudo actualizar el registro");
                                    }
                                    progressDialog.dismiss();
                                }
                            });
                        }
                    });

                    Toast.makeText(getContext(), "Image Is Uploaded.", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    mShowAlert("Error en la imagen", "No se pudo subir la imagen al servidor");
                }
            });
        }else{
            userData.getProductsList().get(position).setId(etIdP.getText().toString().trim());
            userData.getProductsList().get(position).setCategory(strCategory);
            userData.getProductsList().get(position).setName(etNameP.getText().toString().trim());
            userData.getProductsList().get(position).setCost(etCostP.getText().toString().trim());
            userData.getProductsList().get(position).setDescription(etDescP.getText().toString().trim());
            if(switchP.isChecked()){
                statusP = 1;
            }else{
                statusP = 0;
            }
            userData.getProductsList().get(position).setStatus(statusP);

            databaseReference.child("UserMember").child(user.getId()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        mClean();
                        Toast.makeText(getContext(), "Producto actualizado correctamente", Toast.LENGTH_SHORT).show();
                        //getActivity().finish();
                    }else{
                        mShowAlert("Error", "No se pudo actualizar el registro");
                    }
                    progressDialog.dismiss();
                }
            });
        }


    }

    private void loadDataSpinner(final Spinner spn){
        databaseReference.child("Category").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final List<String> categories = new ArrayList<String>();

                for (DataSnapshot dataSnapshot1: dataSnapshot.getChildren()) {
                    String categoryName = dataSnapshot1.child("name").getValue(String.class);
                    categories.add(categoryName);
                }

                ArrayAdapter<String> categoryAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, categories);
                categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spn.setAdapter(categoryAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static int getPosition(Spinner spinner, String item){
        int posicion = 0;
        for (int i = 0; i < spinner.getCount(); i++){
            if(spinner.getItemAtPosition(i).toString().equalsIgnoreCase(item)){
                posicion = i;
            }
        }
        return posicion;
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
                Uri photoUri= FileProvider.getUriForFile(getContext(), "com.movil.jaiapp.android.fileprovider", photoFile);
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
        switch (view.getId()){
            case R.id.member_frag_search_imgBtn_camera:
                askCameraPermissions();
                break;
            case R.id.member_frag_search_imgBtn_gallery:
                Intent gallery = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(gallery, GALLERY_REQUEST_CODE);
                break;
            case R.id.member_frag_search_imgBtn_search:
                if(!etIdP.getText().toString().trim().isEmpty()){
                    boolean exist = true;
                    for(int i = 1; i < userData.getProductsList().size(); i++){
                        if(etIdP.getText().toString().trim().equals(userData.getProductsList().get(i).getId())){
                            position = i;
                            strCategory = userData.getProductsList().get(i).getCategory();

                            etIdP.setText(userData.getProductsList().get(i).getId());
                            spnCatP.setSelection(getPosition(spnCatP, strCategory));
                            etNameP.setText(userData.getProductsList().get(i).getName());
                            etCostP.setText(userData.getProductsList().get(i).getCost());
                            etDescP.setText(userData.getProductsList().get(i).getDescription());
                            Glide.with(getActivity()).load(userData.getProductsList().get(i).getImage()).centerCrop().into(imgViewProduct);

                            if(userData.getProductsList().get(i).getStatus() == 1){
                                switchP.setChecked(true);
                            }else{
                                switchP.setChecked(false);
                            }
                            exist = true;
                            flag = 1;
                            break;
                        }else{
                            flag = 0;
                            exist = false;
                        }
                    }
                    if(!exist){
                        flag = 0;
                        exist = true;
                        mShowAlert("Información", "El producto con el ID: "+etIdP.getText().toString().trim() + " no existe");
                        mClean();
                    }
                }else{
                    flag = 0;
                    etIdP.setError("Ingrese el ID del producto");
                }
                break;
            case R.id.member_frag_search_btn_update:
                if(flag == 1){
                    boolean exist = false;
                    if(!etIdP.getText().toString().trim().isEmpty() && !etNameP.getText().toString().trim().isEmpty() &&
                            !etCostP.getText().toString().trim().isEmpty() && !etDescP.getText().toString().trim().isEmpty()){
                        if(!strCategory.equals("")){
                            if(etIdP.getText().toString().trim().equals(userData.getProductsList().get(position).getId())){
                                exist = false;
                            }else{
                                for(int i = 0; i < userData.getProductsList().size(); i++) {
                                    if (etIdP.getText().toString().trim().equals(userData.getProductsList().get(i).getId())) {
                                        exist = true;
                                        break;
                                    }else{
                                        exist = false;
                                    }
                                }
                            }
                            if(!exist){
                                updateRegister(userData);
                            }else{
                                Toast.makeText(getContext(), "El ID del producto ingresado ya existe", Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            Toast.makeText(getContext(), "Debe seleccionar una categoría", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        mValidate();
                    }
                }else{
                    flag = 0;
                    mShowAlert("Información", "Primero debe buscar un producto");
                }
                break;
            case R.id.member_frag_search_btn_delete:
                if(flag == 1){
                    progressDialog.setIcon(R.mipmap.ic_launcher);
                    progressDialog.setMessage("Eliminando...");
                    progressDialog.show();

                    boolean exist = true;
                    for(int i = 1; i < userData.getProductsList().size(); i++){
                        if(etIdP.getText().toString().trim().equals(userData.getProductsList().get(i).getId())){
                            databaseReference.child("UserMember").child(userData.getId()).child("productsList").child(String.valueOf(i+1)).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){

                                    }else{
                                        mShowAlert("Error", "No se pudo eliminar el producto");
                                    }
                                    progressDialog.dismiss();
                                }
                            });
                            exist = true;
                            flag = 1;
                            break;
                        }else{
                            flag = 0;
                            exist = false;
                        }
                    }
                    if(!exist){
                        flag = 0;
                        exist = true;
                        mShowAlert("Información", "El producto con el ID: "+etIdP.getText().toString().trim() + " no existe");
                        mClean();
                    }
                }else{
                    flag = 0;
                    mShowAlert("Información", "Primero debe buscar un producto");
                }
                break;
            case R.id.member_frag_search_btn_clean:
                mClean();
                break;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

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
}