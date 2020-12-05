package com.movil.jaiapp.ui.register.member;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.movil.jaiapp.MainMemberActivity;
import com.movil.jaiapp.R;
import com.movil.jaiapp.models.UserMember;

import java.util.Date;
import java.util.UUID;

public class MemberFragment extends Fragment implements View.OnClickListener, OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private MemberViewModel memberViewModel;
    private EditText etName, etLastname, etNumMember, etPhoneNumber, etEmail, etPassword, etConfirmPassword;
    private String lat, log;
    double dLat, dLong;
    private Marker ubicacion;
    private Button btnRegister;
    private MapView mapView;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        memberViewModel = ViewModelProviders.of(this).get(MemberViewModel.class);
        final View root = inflater.inflate(R.layout.fragment_register_member, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        initComponents(root);

        mapView = root.findViewById(R.id.member_mapView_ubication);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        return root;
    }

    private void initComponents(View root) {
        etName = root.findViewById(R.id.member_txtInputEdit_name);
        etLastname = root.findViewById(R.id.member_txtInputEdit_lastName);
        etNumMember = root.findViewById(R.id.member_txtInputEdit_numMember);
        etPhoneNumber = root.findViewById(R.id.member_txtInputEdit_phoneNumber);
        etEmail = root.findViewById(R.id.member_txtInputEdit_email);
        etPassword = root.findViewById(R.id.member_txtInputEdit_password);
        etConfirmPassword = root.findViewById(R.id.member_txtInputEdit_confirmPassword);

        btnRegister = root.findViewById(R.id.member_btn_register);
        btnRegister.setOnClickListener(this);
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
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {
                    UserMember userMember = new UserMember(
                            UUID.randomUUID().toString(),
                            etNumMember.getText().toString().trim(),
                            etName.getText().toString().trim(),
                            etLastname.getText().toString().trim(),
                            etPhoneNumber.getText().toString().trim(),
                            etEmail.getText().toString().trim(),
                            etPassword.getText().toString().trim(),
                            null,
                            null,
                            dLat,
                            dLong,
                            "admin",
                            new Date().toString(),
                            null,
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
                        }
                    });

                } else {
                    mShowAlert("Error", "Se ha producido un error al crear el usuario");
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.member_btn_register:
                if (!etNumMember.getText().toString().isEmpty() && !etName.getText().toString().isEmpty() &&
                        !etLastname.getText().toString().isEmpty() && !etEmail.getText().toString().isEmpty() &&
                        !etPassword.getText().toString().isEmpty() && !etConfirmPassword.getText().toString().isEmpty() &&
                        !etPhoneNumber.getText().toString().isEmpty()) {

                    if (etPassword.getText().toString().length() >= 6) {
                        if (etPassword.getText().toString().equals(etConfirmPassword.getText().toString())) {
                            String email = etEmail.getText().toString().trim();
                            String password = etPassword.getText().toString().trim();
                            mRegisterUser(email, password);
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
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng ubication = new LatLng(19.067577, -99.383843);
        ubicacion = googleMap.addMarker(new MarkerOptions()
                .position(ubication)
                .title("Seleciona tu ubicación")
                .draggable(true)
        );
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ubication, 7));
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.setOnMarkerClickListener(this);
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        googleMap.setMyLocationEnabled(true);
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
    public boolean onMarkerClick(Marker marker) {
        if (marker.equals(ubicacion)){
            dLat = marker.getPosition().latitude;
            dLong = marker.getPosition().longitude;
            Toast.makeText(getContext(),"Mi ubicacion es: "+dLat+" , "+dLong,Toast.LENGTH_LONG).show();
        }
        return false;
    }
}