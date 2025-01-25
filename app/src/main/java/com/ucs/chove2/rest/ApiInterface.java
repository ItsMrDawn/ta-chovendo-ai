package com.ucs.chove2.rest;

import com.ucs.chove2.model.geo.Geo;
import com.ucs.chove2.model.tempo.Tempo;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiInterface {

	// curl -X GET -i 'http://api.openweathermap.org/geo/1.0/direct?q=London&limit=5'

	@GET("geo/1.0/direct")
	Call<List<Geo>> getGeo(@Query("q") String cidade, @Query("limit") int limite, @Query("appid") String apiKey);

	// curl -X GET -i 'http://api.openweathermap.org/geo/1.0/reverse?lat=35.6828&lon=139.7595&limit=5'

	@GET("geo/1.0/reverse")
	Call<List<Geo>> getGeoReverse(@Query("lat") Double lat, @Query("lon") Double lon, @Query("limit") int limite, @Query("appid") String apiKey);


	// curl -X GET -i 'https://api.openweathermap.org/data/2.5/weather?lat=44.34&lon=10.99'

	@GET("data/2.5/weather")
	Call<Tempo> getTempo(@Query("lat") Double lat, @Query("lon") Double lon, @Query("units") String units, @Query("lang") String lang, @Query("appid") String apiKey);
}


