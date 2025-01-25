package com.ucs.chove2.rest;

import com.google.gson.Gson;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class APIClient {

	public static final String BASE_URL = "https://api.openweathermap.org/";
	public static final String API_KEY = "YOUR_KEY_HERE";

	public static final String UNITS = "metric";

	public static final String LANG = "pt_br";



	static final Gson gson = new Gson();
	private static Retrofit retrofit = null;

	public static Retrofit getClient() {
		if (retrofit==null) {
			retrofit = new Retrofit.Builder()
					.baseUrl(BASE_URL)
					.addConverterFactory(GsonConverterFactory.create())
					.build();
		}
		return retrofit;
	}

}
