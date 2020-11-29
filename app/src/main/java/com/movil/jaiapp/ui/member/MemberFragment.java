package com.movil.jaiapp.ui.member;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.movil.jaiapp.R;


public class MemberFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater,
                              ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_member, container, false);

        return root;
    }
}