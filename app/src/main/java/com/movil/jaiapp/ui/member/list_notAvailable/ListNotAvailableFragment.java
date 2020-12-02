package com.movil.jaiapp.ui.member.list_notAvailable;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.movil.jaiapp.R;

public class ListNotAvailableFragment extends Fragment {

    private ListNotAvailableViewModel listNotAvailableViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        listNotAvailableViewModel = ViewModelProviders.of(this).get(ListNotAvailableViewModel.class);
        View root = inflater.inflate(R.layout.fragment_member_list_not_available, container, false);

        return root;
    }
}