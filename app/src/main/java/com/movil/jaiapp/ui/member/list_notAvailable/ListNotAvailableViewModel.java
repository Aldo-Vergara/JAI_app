package com.movil.jaiapp.ui.member.list_notAvailable;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ListNotAvailableViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public ListNotAvailableViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is wish_list fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}