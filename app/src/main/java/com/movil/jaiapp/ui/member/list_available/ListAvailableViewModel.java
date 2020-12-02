package com.movil.jaiapp.ui.member.list_available;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ListAvailableViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public ListAvailableViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is list_available fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}