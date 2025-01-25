package com.ucs.chove2.ui.pesq;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ucs.chove2.CidadeManager;
import com.ucs.chove2.R;
import com.ucs.chove2.adapter.GeoAdapter;
import com.ucs.chove2.databinding.FragmentPesqBinding;
import com.ucs.chove2.databinding.FragmentTempoBinding;
import com.ucs.chove2.model.geo.Geo;
import com.ucs.chove2.rest.APIClient;
import com.ucs.chove2.rest.ApiInterface;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PesqFragment extends Fragment {

	private FragmentPesqBinding binding;

	private EditText txtPesq;

	private ProgressBar progLoading;
	private GeoAdapter adapter;
	private List<Geo> geos = new ArrayList<Geo>();

	public View onCreateView(@NonNull LayoutInflater inflater,
	                         ViewGroup container, Bundle savedInstanceState) {
		PesqViewModel pesqViewModel =
				new ViewModelProvider(this).get(PesqViewModel.class);

		binding = FragmentPesqBinding.inflate(inflater, container, false);
		View rootView = binding.getRoot();

		RecyclerView recyclerGeo = binding.recyclerGeo;
		recyclerGeo.setLayoutManager(new LinearLayoutManager(requireContext()));

		// precisa setar o adapter antes de aparecer qq coisa
		adapter = new GeoAdapter(geos, R.layout.item_geo, requireContext());
		recyclerGeo.setAdapter(adapter);

		adapter.setOnItemClickListener(new GeoAdapter.OnItemClickListener() {
			@Override
			public void onItemClick(Geo geo) {
				// puxar os demais dados da API e inserir no banco
				CidadeManager man = new CidadeManager(getContext());
				man.inserirCidade(geo);

				// voltar pro 1o fragmento
				//NavHostFragment.findNavController(PesqFragment.this).navigate(R.id.action_SecondFragment_to_FirstFragment);

			}
		});

		txtPesq = binding.txtPesqCidade;
		progLoading = binding.progressBar;
		progLoading.setVisibility(View.INVISIBLE);

		return rootView;

	}

	public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		binding.buttonSecond.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				pedirPermissoes();
			}
		});

		binding.btnPesq.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				onPesquisar(null, null);
			}
		});

	}

	////////////// GPS //////////////////////////////////

	private void pedirPermissoes() {

		if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
				&& ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

			ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
		}
		else
			configurarServico();
	}

	public void configurarServico(){

		progLoading.setVisibility(View.VISIBLE);
		geos.clear();
		PesqFragment.this.adapter.notifyDataSetChanged();

		try {
			LocationManager locationManager = (LocationManager)  getActivity().getSystemService(Context.LOCATION_SERVICE);

			LocationListener locationListener = new LocationListener() {
				public void onLocationChanged(Location location) {

					// assim que tiver a localização, pesquisar usando lat/lon
					onPesquisar(location.getLatitude(), location.getLongitude());

					// parar de receber atualizações
					locationManager.removeUpdates(this);

				}

				public void onStatusChanged(String provider, int status, Bundle extras) { }

				public void onProviderEnabled(String provider) { }

				public void onProviderDisabled(String provider) { }
			};

			locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
		} catch(SecurityException ex) {
			Toast.makeText(getActivity(), ex.getMessage(), Toast.LENGTH_LONG).show();
		}
	}

	//////////////////////////// fim GPS //////////////////////////////////

	public void onPesquisar(Double lat, Double lon) {

		progLoading.setVisibility(View.VISIBLE);

		ApiInterface apiService = APIClient.getClient().create(ApiInterface.class);

		Call<List<Geo>> call;

		if (lat == null) {
			// se não tem info de lat/lon, pesquisar o texto
			String pesq = txtPesq.getText().toString();
			call = apiService.getGeo(pesq, 10, APIClient.API_KEY);
		} else {
			// pesquisar reverso usando lat/lon do gps
			call = apiService.getGeoReverse(lat, lon, 10, APIClient.API_KEY);
		}

		call.enqueue(new Callback<List<Geo>>() {
			@Override
			public void onResponse(Call<List<Geo>> call, Response<List<Geo>> response) {
				int statusCode = response.code();
				geos = response.body();
				//recyclerView.setAdapter(adapter);
				PesqFragment.this.adapter.setGeos(geos);
				PesqFragment.this.adapter.notifyDataSetChanged();
				progLoading.setVisibility(View.INVISIBLE);
			}

			@Override
			public void onFailure(Call<List<Geo>> call, Throwable t) {
				if (t instanceof UnknownHostException) {
					Toast.makeText(getActivity(), R.string.erro_internet, Toast.LENGTH_SHORT).show();
				} else {
					mostraAlerta("Erro", t.toString());
				}
				// Log error here since request failed
				Log.e(PesqFragment.class.getSimpleName(), t.toString());
				progLoading.setVisibility(View.INVISIBLE);
			}
		});

	}

	private void mostraAlerta(String titulo, String mensagem) {
		AlertDialog alertDialog = new
				AlertDialog.Builder(getContext()).create();
		alertDialog.setTitle(titulo);
		alertDialog.setMessage(mensagem);
		alertDialog.setButton(AlertDialog. BUTTON_NEUTRAL ,
				getString(R.string.ok),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		alertDialog.show();
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		binding = null;
	}


}