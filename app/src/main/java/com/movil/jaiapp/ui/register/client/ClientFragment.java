package com.movil.jaiapp.ui.register.client;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.movil.jaiapp.MainClientActivity;
import com.movil.jaiapp.R;
import com.movil.jaiapp.ui.member.add.AddViewModel;


public class ClientFragment extends Fragment {

    private ClientViewModel clientViewModel;
    private Button btnRegister;

    public View onCreateView(@NonNull LayoutInflater inflater,
                              ViewGroup container, Bundle savedInstanceState) {

        clientViewModel = ViewModelProviders.of(this).get(ClientViewModel.class);
        View root = inflater.inflate(R.layout.fragment_register_client, container, false);

        btnRegister = root.findViewById(R.id.client_btn_register);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), MainClientActivity.class);
                startActivity(intent);
            }
        });

        return root;
    }
}