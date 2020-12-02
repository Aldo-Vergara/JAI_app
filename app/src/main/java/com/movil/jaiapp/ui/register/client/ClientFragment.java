package com.movil.jaiapp.ui.register.client;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.movil.jaiapp.R;
import com.movil.jaiapp.ui.member.add.AddViewModel;


public class ClientFragment extends Fragment {

    private ClientViewModel clientViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                              ViewGroup container, Bundle savedInstanceState) {

        clientViewModel = ViewModelProviders.of(this).get(ClientViewModel.class);
        View root = inflater.inflate(R.layout.fragment_register_client, container, false);

        return root;
    }
}