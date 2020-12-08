package com.movil.jaiapp.ui.client.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
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
import com.movil.jaiapp.ui.member.list_available.ListProductsAdapter;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private GridLayoutManager gridLayoutManager;
    //private RecyclerView.LayoutManager layoutManager;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseUser user;
    private UserMember userMember;
    private UserClient userData;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_client_home, container, false);

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
        recyclerView = root.findViewById(R.id.client_frag_home_recyclerView_products);
        recyclerView.setHasFixedSize(true);
    }

    private void getUserActive() {
        user = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference.child("UserClient").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot objSnaptshot : dataSnapshot.getChildren()){
                    UserClient userClient = objSnaptshot.getValue(UserClient.class);
                    if(user.getEmail().equals(userClient.getEmail())){
                        userData = userClient;
                        getUserMember();
                        databaseReference.child("UserMember").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot objSnaptshot : dataSnapshot.getChildren()){
                                    UserMember userMember = objSnaptshot.getValue(UserMember.class);
                                    if(userData.getNumSeller().equals(userMember.getNumMember())){
                                        userData.setSellerProductsList(userMember.getProductsList());

                                        gridLayoutManager = new GridLayoutManager(getContext(), 2);
                                        recyclerView.setLayoutManager(gridLayoutManager);

                                        ArrayList<Product> listProducts = new ArrayList<>();
                                        for(int j = 0; j < userData.getSellerProductsList().size(); j++){
                                            if(userData.getSellerProductsList().get(j).getStatus() == 1){
                                                listProducts.add(userData.getSellerProductsList().get(j));
                                            }
                                        }

                                        adapter = new HomeProductsAdapter(listProducts, getActivity(), databaseReference, userData, userMember);
                                        recyclerView.setAdapter(adapter);

                                        break;
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
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