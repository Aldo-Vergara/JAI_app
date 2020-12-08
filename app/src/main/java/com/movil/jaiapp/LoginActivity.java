package com.movil.jaiapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.movil.jaiapp.models.UserClient;
import com.movil.jaiapp.models.UserMember;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnSignUp, btnRegister;
    private EditText etEmail, etPassword;
    private ProgressDialog progressDialog;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReferenceClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Obtain the FirebaseAnalytics instance.
        FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Bundle bundle = new Bundle();
        bundle.putString("message", "Integración de Firebase completa");
        firebaseAnalytics.logEvent("InitScreeen", bundle);

        initComponents();
        initFirebase();
    }

    private void initComponents() {
        progressDialog = new ProgressDialog(this);

        btnSignUp = findViewById(R.id.login_btn_signUp);
        btnRegister = findViewById(R.id.login_btn_register);

        etEmail = findViewById(R.id.login_txtInputEdit_email);
        etPassword = findViewById(R.id.login_txtInputEdit_password);

        btnSignUp.setOnClickListener(this);
        btnRegister.setOnClickListener(this);

    }

    private void initFirebase(){
        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReferenceClient = firebaseDatabase.getReference();
    }

    private void mShowAlert(String title, String message){
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(title);
        dialog.setMessage(message);
        dialog.setPositiveButton("Aceptar", null);
        dialog.show();
    }

    private void mValidate(){
        if (etEmail.getText().toString().equals("")){
            etEmail.setError("Correo requerido");
        }else if (etPassword.getText().toString().equals("")){
            etPassword.setError("Contraseña requerida");
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.login_btn_signUp:
                if(!etEmail.getText().toString().isEmpty() && !etPassword.getText().toString().isEmpty()){
                    final Task<AuthResult> firebaseAuth = FirebaseAuth.getInstance().signInWithEmailAndPassword(
                            etEmail.getText().toString().trim(),
                            etPassword.getText().toString().trim());

                    progressDialog.setIcon(R.mipmap.ic_launcher);
                    progressDialog.setMessage("Cargando...");
                    progressDialog.show();

                    firebaseAuth.addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(firebaseAuth.isSuccessful()){
                                databaseReferenceClient.child("UserClient").addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        for (DataSnapshot objSnaptshot : dataSnapshot.getChildren()){
                                            UserClient userClient = objSnaptshot.getValue(UserClient.class);
                                            if(etEmail.getText().toString().trim().equals(userClient.getEmail())){
                                                startActivity(new Intent(getApplicationContext(), MainClientActivity.class));
                                                finish();
                                                progressDialog.dismiss();
                                                break;
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                                databaseReferenceClient.child("UserMember").addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        for (DataSnapshot objSnaptshot : dataSnapshot.getChildren()){
                                            UserMember userMember = objSnaptshot.getValue(UserMember.class);
                                            if(etEmail.getText().toString().trim().equals(userMember.getEmail())){
                                                startActivity(new Intent(getApplicationContext(), MainMemberActivity.class));
                                                finish();
                                                break;
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                            }else{
                                mShowAlert("Error", "Hubo un error al autenticar al usuario");
                            }
                            progressDialog.dismiss();
                        }
                    });

                }else{
                    mValidate();
                }

                break;
            case R.id.login_btn_register:
                startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
                finish();
                break;
        }
    }
}