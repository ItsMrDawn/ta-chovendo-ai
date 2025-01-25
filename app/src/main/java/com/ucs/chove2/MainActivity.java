package com.ucs.chove2;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.ucs.chove2.databinding.ActivityMainBinding;
import com.ucs.chove2.db.CidadeDBHelper;
import com.ucs.chove2.ui.tempo.TempoFragment;

public class MainActivity extends AppCompatActivity {

	private AppBarConfiguration mAppBarConfiguration;
	private ActivityMainBinding binding;

	private LinearLayout layoutPesq;

	private Fragment currentFragment;
	private NavHostFragment navHostFragment;


	private EditText txtPesq;
	private ImageButton btnFechar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		binding = ActivityMainBinding.inflate(getLayoutInflater());
		setContentView(binding.getRoot());

		setSupportActionBar(binding.appBarMain.toolbar);

		DrawerLayout drawer = binding.drawerLayout;
		NavigationView navigationView = binding.navView;
		// Passing each menu ID as a set of Ids because each
		// menu should be considered as top level destinations.
		mAppBarConfiguration = new AppBarConfiguration.Builder(
				R.id.nav_pesq, R.id.nav_tempo)
				.setOpenableLayout(drawer)
				.build();
		NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
		NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
		NavigationUI.setupWithNavController(navigationView, navController);

		/////////////////////////////////

		layoutPesq = findViewById(R.id.layoutPesq);
		layoutPesq.setVisibility(View.GONE);

		// viagem pra conseguir referenciar o firstfragment
		navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_content_main);
		currentFragment = navHostFragment.getChildFragmentManager().getPrimaryNavigationFragment();

		txtPesq = findViewById(R.id.txtPesq);

		// eventos pra pesquisar quando o texto do edit mudar
		txtPesq.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			@Override
			public void afterTextChanged(Editable s) {

				// esse é o relevante, pra pegar a string depois de mudada
				String updatedText = s.toString();

				// pegar ref pro 1o fragmento
				if (currentFragment instanceof TempoFragment) {
					TempoFragment tempoF = (TempoFragment) currentFragment;

					tempoF.carregarCidades(updatedText);
				}
			}
		});

		btnFechar = findViewById(R.id.btnFechar);

		btnFechar.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				txtPesq.setText("");
				layoutPesq.setVisibility(View.GONE);
			}
		});

		// Intent serviceIntent = new Intent(this, NTempo.class);
		// ContextCompat.startForegroundService(this, serviceIntent);


	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.

		int id = item.getItemId();

		// mostrar linearlayout com a pesquisa de cidades salvas
		if (id == R.id.menu_pesquisar) {
			if (currentFragment instanceof TempoFragment) {
				layoutPesq.setVisibility(View.VISIBLE);
			}
			return true;
		} else if (id == R.id.menu_limpar || id == R.id.menu_zerar) {

			CidadeDBHelper db = new CidadeDBHelper(this);

			// apagar todas cidades
			if (id == R.id.menu_limpar) {

				// dialog que pede confirmação
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setTitle("Apagar tudo");
				builder.setMessage("Tem certeza que deseja apagar TODAS as cidades?");
				builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						db.apagarTodas();
					}
				});
				builder.setNegativeButton("Não", null);
				builder.show();

			}

			// zerar as temperaturas das cidades salvas
			if (id == R.id.menu_zerar) {
				db.zerarTemperaturas();
			}

			// recarregar cidades
			if (currentFragment instanceof TempoFragment) {
				TempoFragment tempoF = (TempoFragment) currentFragment;
				tempoF.carregarCidades();
			}

			return true;

		}

		return super.onOptionsItemSelected(item);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onSupportNavigateUp() {
		NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
		return NavigationUI.navigateUp(navController, mAppBarConfiguration)
				|| super.onSupportNavigateUp();
	}
}