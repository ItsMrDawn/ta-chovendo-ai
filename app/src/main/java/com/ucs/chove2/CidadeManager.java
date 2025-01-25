package com.ucs.chove2;

import android.content.Context;
import android.util.Log;

import com.ucs.chove2.db.Cidade;
import com.ucs.chove2.db.CidadeDBHelper;
import com.ucs.chove2.model.geo.Geo;
import com.ucs.chove2.model.tempo.Tempo;
import com.ucs.chove2.model.tempo.Weather;
import com.ucs.chove2.rest.APIClient;
import com.ucs.chove2.rest.ApiInterface;

import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CidadeManager {

	final CidadeDBHelper db;
	private boolean result_att;

	private final Date agora;

	public interface TempoCallback {
		void onSuccess(Tempo tempo);
		void onError(String errorMessage);
	}

	public CidadeManager(Context context) {
		Context context1 = context.getApplicationContext();
		db = new CidadeDBHelper(context);
		agora = new Date();
	}



	public boolean atualizarTodasCidades() {

		result_att = true;

		List<Cidade> cidades = db.getAllCidade();

		// iterar por todas as cidades
		for (Cidade cidade : cidades) {
			if (result_att) {
				inserirCidade(cidade);
			}
		}

		return result_att;

	}

	public void inserirCidade(Cidade cidade) {

		// se a diferença de tempo for menor do que 10 minutos, não atualizar
		if ((Math.abs(cidade.getDataUnix() - (agora.getTime() / 1000))) < 600) {
			result_att = false;
			return;
		}

		getTempo(cidade.getLat(), cidade.getLon(), new TempoCallback() {
			@Override
			public void onSuccess(Tempo tempo) {
				// completar os novos dados no objeto cidade
				tempoToCidade(tempo, cidade);
				// inserir no banco
				db.insertCidade(cidade);

			}

			@Override
			public void onError(String errorMessage) {
				//
			}
		});

	}

	public void inserirCidade(Geo geo) {

		Cidade cidade = new Cidade(geo.getName(), geo.getState(), geo.getCountry(), geo.getLat(), geo.getLon());

		inserirCidade(cidade);

	}


	public void getTempo(Double lat, Double lon, TempoCallback callback) {

		ApiInterface apiService = APIClient.getClient().create(ApiInterface.class);

		Call<Tempo> call = apiService.getTempo(lat, lon, APIClient.UNITS, APIClient.LANG, APIClient.API_KEY);

		call.enqueue(new Callback<Tempo>() {
			@Override
			public void onResponse(Call<Tempo> call, Response<Tempo> response) {
				int statusCode = response.code();
				if (response.isSuccessful()) {
					Tempo tempo = response.body();
					callback.onSuccess(tempo);
				} else {
					String errorMessage = "Erro com código: " + statusCode + " - " + response.message();
					callback.onError(errorMessage);
				}
			}

			@Override
			public void onFailure(Call<Tempo> call, Throwable t) {
				String errorMessage = "Erro " + t.getMessage();

				//Log.e(SecondFragment.class.getSimpleName(), t.toString());
				callback.onError(errorMessage);
			}
		});

	}


	public void tempoToCidade(Tempo tempo, Cidade cidade) {
		cidade.setId(tempo.getId());
		//cidade.setCidade(tempo.getName());
		cidade.setDataUnix(tempo.getDt());
		cidade.setTemp(tempo.getMain().getTemp());
		cidade.setLat(tempo.getCoord().getLat());
		cidade.setLon(tempo.getCoord().getLon());


		Weather weather = tempo.getWeather().get(0);
		cidade.setIcone_tempo(weather.getIcon());
		cidade.setDesc_tempo(weather.getDescription());

	}



}
