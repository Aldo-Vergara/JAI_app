package com.movil.jaiapp.ui.client;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.movil.jaiapp.R;


public class ClientFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater,
                              ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_client, container, false);

        return root;
    }
}