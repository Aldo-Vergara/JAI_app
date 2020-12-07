package com.movil.jaiapp.ui.member.list_notAvailable;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import com.movil.jaiapp.models.UserMember;
import com.movil.jaiapp.ui.member.list_available.ListProductsAdapter;

public class ListNotAvailableFragment extends Fragment {

    private ListNotAvailableViewModel listNotAvailableViewModel;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseUser user;
    private UserMember userData;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        listNotAvailableViewModel = ViewModelProviders.of(this).get(ListNotAvailableViewModel.class);
        View root = inflater.inflate(R.layout.fragment_member_list_not_available, container, false);

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

                        adapter = new ListProductsAdapter(userData.getProductsList(), getActivity(), false,
                                databaseReference, userData);
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