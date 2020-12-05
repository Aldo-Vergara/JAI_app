package com.movil.jaiapp.ui.member.profile;

import android.app.AlertDialog;
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
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.movil.jaiapp.R;
import com.movil.jaiapp.models.UserMember;

public class ProfileFragment extends Fragment implements View.OnClickListener{

    private ProfileViewModel profileViewModel;
    
    private EditText etName, etLastname, etNMember, etPhone, etEmail, etPassword, etNewPassword;
    private Button btnUpdate;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseUser user;
    private UserMember userData;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        profileViewModel = ViewModelProviders.of(this).get(ProfileViewModel.class);

        View root = inflater.inflate(R.layout.fragment_member_profile, container, false);

        initFirebase();
        initComponents(root);
        getUserActive(root);

        return root;
    }

    private void initComponents(View root) {
        etName = root.findViewById(R.id.member_frag_profile_txtInputEdit_name);
        etLastname = root.findViewById(R.id.member_frag_profile_txtInputEdit_lastName);
        etNMember = root.findViewById(R.id.member_frag_profile_txtInputEdit_numMember);
        etPhone = root.findViewById(R.id.member_frag_profile_txtInputEdit_phoneNumber);
        etEmail = root.findViewById(R.id.member_frag_profile_txtInputEdit_email);
        etPassword = root.findViewById(R.id.member_frag_profile_txtInputEdit_password);
        etNewPassword = root.findViewById(R.id.member_frag_profile_txtInputEdit_newPassword);

        btnUpdate = root.findViewById(R.id.member_frag_profile_btn_update);
        btnUpdate.setOnClickListener(this);
    }

    private void initFirebase() {
        FirebaseApp.initializeApp(getContext());
        firebaseDatabase = FirebaseDatabase.getInstance();
        //firebaseDatabase.setPersistenceEnabled(true);
        databaseReference = firebaseDatabase.getReference();
    }

    private void getUserActive(final View root) {
        user = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference.child("UserMember").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot objSnaptshot : dataSnapshot.getChildren()){
                    UserMember userMember = objSnaptshot.getValue(UserMember.class);
                    if(user.getEmail().equals(userMember.getEmail())){
                        userData = userMember;
                        setData(userData);
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setData(UserMember userData) {
        etName.setText(userData.getName());
        etLastname.setText(userData.getLastname());
        etNMember.setText(userData.getNumMember());
        etPhone.setText(userData.getPhoneNumber());
        etEmail.setText(userData.getEmail());
        etPassword.setText("");
        etNewPassword.setText("");
    }

    private void mShowAlert(String title, String message){
        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
        dialog.setTitle(title);
        dialog.setMessage(message);
        dialog.setPositiveButton("Aceptar", null);
        dialog.show();
    }

    private void updateRegister(final UserMember user){
        databaseReference.child("UserMember").child(user.getId()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    setData(user);
                    Toast.makeText(getContext(), "Datos actualizados correctamente", Toast.LENGTH_SHORT).show();
                    getActivity().finish();
                }else{
                    mShowAlert("Error", "No se pudo actualizar la información");
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.member_frag_profile_btn_update:
                if(!etPhone.getText().toString().trim().isEmpty()){
                    if(!etPassword.getText().toString().isEmpty()){
                        if(etPassword.getText().toString().trim().length() >= 6){
                            if(etPassword.getText().toString().trim().equals(etNewPassword.getText().toString().trim())){
                                user.updatePassword(etPassword.getText().toString().trim()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            userData.setPhoneNumber(etPhone.getText().toString().trim());
                                            userData.setPassword(etPassword.getText().toString().trim());
                                            updateRegister(userData);
                                        }else{
                                            mShowAlert("Error", "No se pudo establecer conexión con el servidor");
                                        }
                                    }
                                });
                            }else{
                                etNewPassword.setError("Las contraseñas no coinciden");
                            }
                        }else{
                            etPassword.setError("La contraseña debe tener al menos 6 carácteres");
                        }
                    }else{
                        userData.setPhoneNumber(etPhone.getText().toString().trim());
                        updateRegister(userData);
                    }
                }else{
                    etPhone.setError("Teléfono requerido");
                }
                break;
        }
    }
}