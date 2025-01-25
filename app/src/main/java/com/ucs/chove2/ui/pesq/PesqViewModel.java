package com.ucs.chove2.ui.pesq;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class PesqViewModel extends ViewModel {

	private final MutableLiveData<String> mText;

	public PesqViewModel() {
		mText = new MutableLiveData<>();
		mText.setValue("This is slideshow fragment");
	}

	public LiveData<String> getText() {
		return mText;
	}
}