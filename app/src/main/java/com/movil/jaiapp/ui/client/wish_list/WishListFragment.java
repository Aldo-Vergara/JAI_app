package com.movil.jaiapp.ui.client.wish_list;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.movil.jaiapp.R;

public class WishListFragment extends Fragment {

    private WishListViewModel wishListViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        wishListViewModel = ViewModelProviders.of(this).get(WishListViewModel.class);
        View root = inflater.inflate(R.layout.fragment_client_wish_list, container, false);

        return root;
    }
}