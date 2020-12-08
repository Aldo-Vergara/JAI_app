package com.movil.jaiapp.ui.about;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.movil.jaiapp.R;

public class AboutFragment extends Fragment {

    private AboutViewModel aboutViewModel;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        aboutViewModel = ViewModelProviders.of(this).get(AboutViewModel.class);
        View root = inflater.inflate(R.layout.fragment_about, container, false);


        return root;
    }


}