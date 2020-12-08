package com.movil.jaiapp.ui.member.list_available;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import java.util.ArrayList;

public class ListAvailableFragment extends Fragment {

    private ListAvailableViewModel listAvailableViewModel;
    private ProgressDialog progressDialog;

    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseUser user;
    private UserMember userData;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        listAvailableViewModel = ViewModelProviders.of(this).get(ListAvailableViewModel.class);
        View root = inflater.inflate(R.layout.fragment_member_list_available, container, false);

        initFirebase();
        initComponents(root);
        getUserActiveAndLoadProducts();

        return root;
    }

    private void initFirebase() {
        FirebaseApp.initializeApp(getContext());
        firebaseDatabase = FirebaseDatabase.getInstance();
        //firebaseDatabase.setPersistenceEnabled(true);
        databaseReference = firebaseDatabase.getReference();
    }

    private void initComponents(View root) {
        progressDialog = new ProgressDialog(getContext());

        recyclerView = root.findViewById(R.id.member_frag_list_recyclerView_available);
        recyclerView.setHasFixedSize(true);
    }

    private void getUserActiveAndLoadProducts() {
        user = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference.child("UserMember").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot objSnaptshot : dataSnapshot.getChildren()){
                    UserMember userMember = objSnaptshot.getValue(UserMember.class);
                    if(user.getEmail().equals(userMember.getEmail())){
                        userData = userMember;

                        layoutManager = new LinearLayoutManager(getContext());
                        recyclerView.setLayoutManager(layoutManager);

                        ArrayList<Product> listProducts = new ArrayList<>();
                        for(int j = 0; j < userData.getProductsList().size(); j++){
                            if(userData.getProductsList().get(j).getStatus() == 1){
                                listProducts.add(userData.getProductsList().get(j));
                            }
                        }

                        adapter = new ListProductsAdapter(listProducts, getActivity(), true,
                                databaseReference, userData, progressDialog);
                        recyclerView.setAdapter(adapter);
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}