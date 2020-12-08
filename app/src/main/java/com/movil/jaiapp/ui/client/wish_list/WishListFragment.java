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
import androidx.recyclerview.widget.LinearLayoutManager;
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
import com.movil.jaiapp.models.Product;
import com.movil.jaiapp.models.UserClient;
import com.movil.jaiapp.models.UserMember;

import java.util.ArrayList;

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
    private UserMember userMember;

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

        recyclerView = root.findViewById(R.id.client_frag_recyclerView_wishList);
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
                        getUserMember();

                        layoutManager = new LinearLayoutManager(getContext());
                        recyclerView.setLayoutManager(layoutManager);

                        ArrayList<Product> listProducts = new ArrayList<>();
                        for(int j = 0; j < userData.getWishProductsList().size(); j++){
                            if(userData.getWishProductsList().get(j) != null && userData.getWishProductsList().get(j).getStatus() == 1 &&
                                    userData.getWishProductsList().get(j).getCreated() != null){
                                listProducts.add(userData.getWishProductsList().get(j));
                            }
                        }

                        adapter = new WishProductsAdapter(listProducts, getActivity(),
                                databaseReference, userData, userMember, progressDialog);
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

    private void getUserMember() {
        databaseReference.child("UserMember").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot objSnaptshot : dataSnapshot.getChildren()){
                    UserMember userMem = objSnaptshot.getValue(UserMember.class);
                    if(userData.getNumSeller().equals(userMem.getNumMember())){
                        userMember = userMem;
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