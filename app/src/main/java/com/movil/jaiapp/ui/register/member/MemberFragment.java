package com.movil.jaiapp.ui.register.member;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.movil.jaiapp.R;
import com.movil.jaiapp.ui.register.client.ClientViewModel;


public class MemberFragment extends Fragment {

    private MemberViewModel memberViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                              ViewGroup container, Bundle savedInstanceState) {

        memberViewModel = ViewModelProviders.of(this).get(MemberViewModel.class);
        View root = inflater.inflate(R.layout.fragment_register_member, container, false);

        return root;
    }
}