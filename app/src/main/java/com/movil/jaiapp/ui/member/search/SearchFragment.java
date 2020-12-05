package com.movil.jaiapp.ui.member.search;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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

public class SearchFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener{

    private SearchViewModel searchViewModel;
    private EditText etIdP, etNameP, etCostP, etDescP;
    private Spinner spnCatP;
    private Switch switchP;
    private Button btnUpdate, btnDelete, btnClean;
    private ImageButton imgBtnSearch;
    private int statusP, position;
    private String strCategory;
    static int flag = 0;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseUser user;
    private UserMember userData;

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
    }

    private void initComponents(View root) {
        etIdP = root.findViewById(R.id.member_frag_edit_txtEdit_id);
        etNameP = root.findViewById(R.id.member_frag_edit_txtEdit_name);
        etCostP = root.findViewById(R.id.member_frag_edit_txtEdit_cost);
        etDescP = root.findViewById(R.id.member_frag_edit_txtEdit_description);


        ArrayAdapter<CharSequence> categoryAdapter = ArrayAdapter.createFromResource(getContext(), R.array.array_category, android.R.layout.simple_spinner_item);
        spnCatP = root.findViewById(R.id.member_frag_edit_spn_category);
        spnCatP.setAdapter(categoryAdapter);
        spnCatP.setOnItemSelectedListener(this);

        switchP = root.findViewById(R.id.member_frag_edit_switch_status);

        imgBtnSearch = root.findViewById(R.id.member_frag_edit_imgBtn_search);
        imgBtnSearch.setOnClickListener(this);

        btnUpdate = root.findViewById(R.id.member_frag_edit_btn_update);
        btnUpdate.setOnClickListener(this);
        btnDelete = root.findViewById(R.id.member_frag_edit_btn_delete);
        btnDelete.setOnClickListener(this);
        btnClean = root.findViewById(R.id.member_frag_edit_btn_clean);
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
        databaseReference.child("UserMember").child(user.getId()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    mClean();
                    Toast.makeText(getContext(), "Producto actualizado correctamente", Toast.LENGTH_SHORT).show();
                    getActivity().finish();
                }else{
                    mShowAlert("Error", "No se pudo actualizar el registro");
                }
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

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.member_frag_edit_imgBtn_search:
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
            case R.id.member_frag_edit_btn_update:
                if(flag == 1){
                    if(!etIdP.getText().toString().trim().isEmpty() && !etNameP.getText().toString().trim().isEmpty() &&
                            !etCostP.getText().toString().trim().isEmpty() && !etDescP.getText().toString().trim().isEmpty()){
                        if(!strCategory.equals("")){
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
                            updateRegister(userData);
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
            case R.id.member_frag_edit_btn_clean:
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
}