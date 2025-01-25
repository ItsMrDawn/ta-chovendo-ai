package com.ucs.chove2.ui.tempo;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class TempoViewModel extends ViewModel {

	private final MutableLiveData<String> mText;

	public TempoViewModel() {
		mText = new MutableLiveData<>();
		mText.setValue("This is slideshow fragment");
	}

	public LiveData<String> getText() {
		return mText;
	}
}