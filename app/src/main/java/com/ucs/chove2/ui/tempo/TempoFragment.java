package com.ucs.chove2.ui.tempo;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.ucs.chove2.CidadeManager;
import com.ucs.chove2.R;
import com.ucs.chove2.ShakeDetector;
import com.ucs.chove2.adapter.CidadeAdapter;
import com.ucs.chove2.databinding.FragmentTempoBinding;
import com.ucs.chove2.db.Cidade;
import com.ucs.chove2.db.CidadeDBHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class TempoFragment extends Fragment implements ShakeDetector.OnShakeListener {

	private FragmentTempoBinding binding;

	private ShakeDetector shakeDetector;

	private SwipeRefreshLayout refreshlayout;
	private CidadeAdapter adapter;
	private TextView txtatualiza;
	private List<Cidade> cidades = new ArrayList<Cidade>();

	public View onCreateView(@NonNull LayoutInflater inflater,
	                         ViewGroup container, Bundle savedInstanceState) {
		TempoViewModel tempoViewModel =
				new ViewModelProvider(this).get(TempoViewModel.class);

		binding = FragmentTempoBinding.inflate(inflater, container, false);
		View rootView = binding.getRoot();

		RecyclerView recyclerCidade = binding.recyclerCidade;
		recyclerCidade.setLayoutManager(new LinearLayoutManager(requireContext()));

		// precisa setar o adapter antes de aparecer qq coisa
		adapter = new CidadeAdapter(cidades, R.layout.item_cidade, requireContext());
		recyclerCidade.setAdapter(adapter);

		refreshlayout = binding.swipeRefreshLayout;
		txtatualiza = binding.txtUltimaAtt;

		shakeDetector = new ShakeDetector();
		shakeDetector.setOnShakeListener(this);

		return rootView;

	}

	private boolean isNetworkAvailable() {
		ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivityManager != null) {
			NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
			return activeNetworkInfo != null && activeNetworkInfo.isConnected();
		}
		return false;
	}

	public void refreshCidades() {

		Window window = getActivity().getWindow();

		// se não tiver rede, mudar a cor da barra de status
		if (!isNetworkAvailable()) {
			Toast.makeText(getActivity(), R.string.erro_internet, Toast.LENGTH_SHORT).show();
			window.setStatusBarColor(ContextCompat.getColor(getActivity(), R.color.red));
		} else {
			window.setStatusBarColor(ContextCompat.getColor(getActivity(), R.color.black));
		}

		// atualizar a lista de cidades
		CidadeManager man = new CidadeManager(getContext());

		if (man.atualizarTodasCidades()) {
			// esperar 2  segundos
			Handler handler = new Handler();
			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					carregarCidades();
					refreshlayout.setRefreshing(false);
				}
			}, 2000 + (200L * cidades.size()));
		} else {
			carregarCidades();
			refreshlayout.setRefreshing(false);
		}

	}


	public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {

		super.onViewCreated(view, savedInstanceState);

		binding.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				refreshCidades();
			}
		});

		refreshCidades();
	}

	@Override
	public void onResume() {

		super.onResume();

		// serviço do shake
		SensorManager sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
		Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		sensorManager.registerListener(shakeDetector, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);

		carregarCidades();

		// esperar 2  segundos antes de recarregar as cidades do banco
		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				carregarCidades();
			}
		}, 2000);

	}

	@Override
	public void onPause() {
		super.onPause();
		SensorManager sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
		sensorManager.unregisterListener(shakeDetector);
	}

	@Override
	public void onShake() {
		// Evento do ShakeDetector pra quando mexer o dispositivo

		Toast.makeText(getActivity(), R.string.shake, Toast.LENGTH_SHORT).show();

		refreshlayout.setRefreshing(true);

		refreshCidades();

	}

	public void carregarCidades() {
		carregarCidades(null);
	}

	public void carregarCidades(String pesq) {

		CidadeDBHelper db = new CidadeDBHelper(getContext());
		cidades = db.getAllCidade(pesq);

		//FirstFragment.this.adapter.setCidades(cidades);
		//FirstFragment.this.adapter.notifyDataSetChanged();

		adapter.setCidades(cidades);
		adapter.notifyDataSetChanged();

		tempoUltimaAtt();

	}

	private void tempoUltimaAtt() {
		// texto da ultima atualização

		if (cidades == null || cidades.size() == 0) {
			txtatualiza.setText("");
			return;
		}

		Date atual = new Date();
		Date ult = cidades.get(0).getData();
		long difmill = atual.getTime() - ult.getTime();

		// Convert milliseconds to hours and minutes
		long dias    = TimeUnit.MILLISECONDS.toDays(difmill);
		long horas   = TimeUnit.MILLISECONDS.toHours(difmill) % 24;
		long minutos = TimeUnit.MILLISECONDS.toMinutes(difmill) % 60;

		String textoatt = String.format("Tempo desde a última atualização:\n%d dias %d horas %d minutos", dias, horas, minutos);

		txtatualiza.setText(textoatt);

	}


	@Override
	public void onDestroyView() {
		super.onDestroyView();
		binding = null;
	}

}