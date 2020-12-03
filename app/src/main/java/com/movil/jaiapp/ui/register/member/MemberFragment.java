package com.movil.jaiapp.ui.register.member;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.movil.jaiapp.MainMemberActivity;
import com.movil.jaiapp.R;
import com.movil.jaiapp.ui.register.client.ClientViewModel;


public class MemberFragment extends Fragment implements View.OnClickListener{

    private MemberViewModel memberViewModel;
    private Button btnRegister;

    public View onCreateView(@NonNull LayoutInflater inflater,
                              ViewGroup container, Bundle savedInstanceState) {

        memberViewModel = ViewModelProviders.of(this).get(MemberViewModel.class);
        View root = inflater.inflate(R.layout.fragment_register_member, container, false);

        initComponents(root);

        return root;
    }

    private void initComponents(View root) {

        btnRegister = root.findViewById(R.id.member_btn_register);
        btnRegister.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.member_btn_register:
                startActivity(new Intent(getContext(), MainMemberActivity.class));
                getActivity().finish();
                break;
        }
    }
}