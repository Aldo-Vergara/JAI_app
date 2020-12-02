package com.movil.jaiapp.ui.register.client;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.movil.jaiapp.MainClientActivity;
import com.movil.jaiapp.R;
import com.movil.jaiapp.models.UserClient;

import java.util.Date;
import java.util.UUID;


public class ClientFragment extends Fragment implements View.OnClickListener {

    private ClientViewModel clientViewModel;
    private Button btnRegister;
    private EditText etNumMember, etName, etLastname, etEmail, etPassword, etConfirmPassword;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    public View onCreateView(@NonNull LayoutInflater inflater,
                              ViewGroup container, Bundle savedInstanceState) {

        clientViewModel = ViewModelProviders.of(this).get(ClientViewModel.class);
        View root = inflater.inflate(R.layout.fragment_register_client, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        initComponents(root);

        return root;
    }

    private void initComponents(View root) {
        etNumMember = root.findViewById(R.id.client_txtInputEdit_nMember);
        etName = root.findViewById(R.id.client_txtInputEdit_name);
        etLastname = root.findViewById(R.id.client_txtInputEdit_lastName);
        etEmail = root.findViewById(R.id.client_txtInputEdit_email);
        etPassword = root.findViewById(R.id.client_txtInputEdit_password);
        etConfirmPassword = root.findViewById(R.id.client_txtInputEdit_confirmPassword);

        btnRegister = root.findViewById(R.id.client_btn_register);
        btnRegister.setOnClickListener(this);
    }

    private void mValidate(){
        if (etNumMember.getText().toString().equals("")){
            etNumMember.setError("Número de socio requerido");
        }else if (etName.getText().toString().equals("")){
            etName.setError("Nombre requerido");
        }else if (etLastname.getText().toString().equals("")){
            etLastname.setError("Apellidos requeridos");
        }else if (etEmail.getText().toString().equals("") ){
            etEmail.setError("Correo requerido");
        }else if (etPassword.getText().toString().equals("")){
            etPassword.setError("Contraseña requerida");
        }else if (etConfirmPassword.getText().toString().equals("")){
            etConfirmPassword.setError("Contraseña requerida");
        }
    }

    private void mClean(){
        etNumMember.setText("");
        etName.setText("");
        etLastname.setText("");
        etEmail.setText("");
        etPassword.setText("");
        etConfirmPassword.setText("");
    }

    private void mShowAlert(String title, String message){
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

                if(task.isSuccessful()){
                    UserClient userClient = new UserClient(
                            UUID.randomUUID().toString(),
                            etNumMember.getText().toString().trim(),
                            etName.getText().toString().trim(),
                            etLastname.getText().toString().trim(),
                            etEmail.getText().toString().trim(),
                            etPassword.getText().toString().trim(),
                            null,
                            null,
                            "user",
                            new Date().toString(),
                            null,
                            1
                    );

                    databaseReference.child("UserClient").child(userClient.getId()).setValue(userClient).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                mClean();
                                startActivity(new Intent(getContext(), MainClientActivity.class));
                                getActivity().finish();
                            }else{
                                mShowAlert("Error", "Se ha producido un error en la conexión");
                            }
                        }
                    });

                }else{
                    mShowAlert("Error", "Se ha producido un error al crear el usuario");
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.client_btn_register:
                if(!etNumMember.getText().toString().isEmpty() && !etName.getText().toString().isEmpty() &&
                        !etLastname.getText().toString().isEmpty() && !etEmail.getText().toString().isEmpty() &&
                        !etPassword.getText().toString().isEmpty() && !etConfirmPassword.getText().toString().isEmpty()){

                    if(etPassword.getText().toString().length() >= 6){
                        if(etPassword.getText().toString().equals(etConfirmPassword.getText().toString())){
                            String email = etEmail.getText().toString().trim();
                            String password = etPassword.getText().toString().trim();
                            mRegisterUser(email, password);
                        }else{
                            Toast.makeText(getContext(), "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(getContext(), "La contraseña debe tener al menos 6 carácteres", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    mValidate();
                    Toast.makeText(getContext(), "Debe llenar todos los campos", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }


}