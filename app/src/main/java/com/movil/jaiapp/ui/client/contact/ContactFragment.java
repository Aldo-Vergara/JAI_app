package com.movil.jaiapp.ui.client.contact;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
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
import com.movil.jaiapp.models.UserMember;

public class ContactFragment extends Fragment implements OnMapReadyCallback {

    private ContactViewModel contactViewModel;
    private ImageView imgViewContact;
    private TextView txtViewNameContact, txtViewPhoneContact;
    private MapView mapView;
    private double dLat, dLong;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseUser user;
    private UserClient userData;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        contactViewModel = ViewModelProviders.of(this).get(ContactViewModel.class);
        View root = inflater.inflate(R.layout.fragment_client_contact, container, false);

        initFirebase();
        initComponents(root);
        getUserActive();

        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        return root;
    }

    private void initFirebase() {
        FirebaseApp.initializeApp(getContext());
        firebaseDatabase = FirebaseDatabase.getInstance();
        //firebaseDatabase.setPersistenceEnabled(true);
        databaseReference = firebaseDatabase.getReference();
    }

    private void initComponents(View root) {
        imgViewContact = root.findViewById(R.id.client_frag_contact_imgView_photoMember);
        imgViewContact.setClipToOutline(true);
        txtViewNameContact = root.findViewById(R.id.client_frag_contact_txtView_nameMember);
        txtViewPhoneContact = root.findViewById(R.id.client_frag_contact_txtView_phoneMember);
        mapView = root.findViewById(R.id.client_frag_contact_mapView);
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
                        databaseReference.child("UserMember").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot objSnaptshot : dataSnapshot.getChildren()){
                                    UserMember userMember = objSnaptshot.getValue(UserMember.class);
                                    if(userData.getNumSeller().equals(userMember.getNumMember())){
                                        Glide.with(getActivity()).load(userMember.getImage()).centerCrop().into(imgViewContact);
                                        txtViewNameContact.setText(userMember.getName());
                                        txtViewPhoneContact.setText(userMember.getPhoneNumber());
                                        dLat = userMember.getUbLat();
                                        dLong = userMember.getUbLong();
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

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng latLng = new LatLng(dLat, dLong);
        googleMap.addMarker(new MarkerOptions().position(latLng).title("Ubicación de "+txtViewNameContact.getText().toString()));
        CameraPosition cameraPosition = CameraPosition.builder()
                .target(latLng)
                .zoom(10)
                .build();
        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    @Override
    public void onPause() {
        mapView.onPause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }
}