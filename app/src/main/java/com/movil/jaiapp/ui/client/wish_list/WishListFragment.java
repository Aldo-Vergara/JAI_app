package com.movil.jaiapp.ui.client.wish_list;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

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
import com.movil.jaiapp.models.UserClient;

public class WishListFragment extends Fragment {

    private WishListViewModel wishListViewModel;
    private ProgressDialog progressDialog;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseUser user;
    private UserClient userData;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        wishListViewModel = ViewModelProviders.of(this).get(WishListViewModel.class);
        View root = inflater.inflate(R.layout.fragment_client_wish_list, container, false);

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

        recyclerView = root.findViewById(R.id.member_frag_list_recyclerView_notAvailable);
        recyclerView.setHasFixedSize(true);
    }

    private void getUserActiveAndLoadProducts() {
        user = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference.child("UserClient").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot objSnaptshot : dataSnapshot.getChildren()){
                    UserClient userClient = objSnaptshot.getValue(UserClient.class);
                    if(user.getEmail().equals(userClient.getEmail())){
                        userData = userClient;
                        loadData(userData);
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void loadData(final UserClient user){
        databaseReference.child("UserClient").child(user.getId()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){

                    adapter = new WishProductsAdapter(user.getWishProductsList(), getActivity(),
                            databaseReference, userData, progressDialog);
                    recyclerView.setAdapter(adapter);
                }else{
                    Toast.makeText(getContext(), "No se pudieron cargar sus productos", Toast.LENGTH_SHORT).show();
                    //mShowAlert("Error", "No se pudo guardar el registro");
                }
            }
        });
    }
}