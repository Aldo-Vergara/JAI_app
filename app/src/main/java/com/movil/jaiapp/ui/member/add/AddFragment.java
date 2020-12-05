package com.movil.jaiapp.ui.member.add;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.movil.jaiapp.R;
import com.movil.jaiapp.models.Product;
import com.movil.jaiapp.models.UserMember;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public class AddFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener{

    private AddViewModel addViewModel;
    private EditText etIdP, etNameP, etCostP, etDescP;
    private Spinner spnCatP;
    private Switch switchP;
    private Button btnSave, btnClean;
    private int statusP;
    private String strCategory;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseUser user;
    private UserMember userData;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        addViewModel = ViewModelProviders.of(this).get(AddViewModel.class);
        View root = inflater.inflate(R.layout.fragment_member_add, container, false);

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
    }

    private void initComponents(View root) {
        etIdP = root.findViewById(R.id.member_frag_add_txtEdit_id);
        etNameP = root.findViewById(R.id.member_frag_add_txtEdit_name);
        etCostP = root.findViewById(R.id.member_frag_add_txtEdit_cost);
        etDescP = root.findViewById(R.id.member_frag_add_txtEdit_description);


        ArrayAdapter<CharSequence> categoryAdapter = ArrayAdapter.createFromResource(getContext(), R.array.array_category, android.R.layout.simple_spinner_item);
        spnCatP = root.findViewById(R.id.member_frag_add_spn_category);
        spnCatP.setAdapter(categoryAdapter);
        spnCatP.setOnItemSelectedListener(this);

        switchP = root.findViewById(R.id.member_frag_add_switch_status);

        btnSave = root.findViewById(R.id.member_frag_add_btn_save);
        btnSave.setOnClickListener(this);
        btnClean = root.findViewById(R.id.member_frag_add_btn_clean);
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

        ArrayAdapter<CharSequence> categoryAdapter = ArrayAdapter.createFromResource(getContext(), R.array.array_category, android.R.layout.simple_spinner_item);
        spnCatP.setAdapter(categoryAdapter);
        strCategory = "";
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
        databaseReference.child("UserMember").child(user.getId()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    mClean();
                    Toast.makeText(getContext(), "Producto agregado", Toast.LENGTH_SHORT).show();
                    //getActivity().finish();
                }else{
                    mShowAlert("Error", "No se pudo guardar el registro");
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.member_frag_add_btn_save:
                if(!etIdP.getText().toString().trim().isEmpty() && !etNameP.getText().toString().trim().isEmpty() &&
                        !etCostP.getText().toString().trim().isEmpty() && !etDescP.getText().toString().trim().isEmpty() &&
                        !strCategory.equals("")){
                    if(switchP.isChecked()){
                        statusP = 1;
                    }else{
                        statusP = 0;
                    }
                    Product product = new Product(
                            UUID.randomUUID().toString(),
                            strCategory,
                            etNameP.getText().toString().trim(),
                            etCostP.getText().toString().trim(),
                            etDescP.getText().toString().trim(),
                            statusP,
                            null,
                            new Date().toString(),
                            null
                    );
                    userData.getProductsList().add(product);
                    updateRegister(userData);
                }else{
                    mValidate();
                }
                break;
            case R.id.member_frag_add_btn_clean:
                mClean();
                break;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        switch (adapterView.getId()){
            case R.id.member_frag_add_spn_category:
                if(position != 0){
                    strCategory = adapterView.getItemAtPosition(position).toString();
                }else{
                    strCategory = "";
                }
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}