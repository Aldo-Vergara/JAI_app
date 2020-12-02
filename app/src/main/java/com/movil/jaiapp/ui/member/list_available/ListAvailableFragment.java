package com.movil.jaiapp.ui.member.list_available;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.movil.jaiapp.R;

public class ListAvailableFragment extends Fragment {

    private ListAvailableViewModel listAvailableViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        listAvailableViewModel = ViewModelProviders.of(this).get(ListAvailableViewModel.class);
        View root = inflater.inflate(R.layout.fragment_member_list_available, container, false);

        return root;
    }
}