package com.ucs.chove2.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.ucs.chove2.R;
import com.ucs.chove2.db.Cidade;
import com.ucs.chove2.db.CidadeDBHelper;

import java.util.List;

public class CidadeAdapter extends RecyclerView.Adapter<CidadeAdapter.CidadeViewHolder> {

	private List<Cidade> cidades;
	private final int rowLayout;
	private final Context context;

	public interface OnItemClickListener {
		void onItemClick(Cidade cidade);
	}
	private OnItemClickListener listener;


	public class CidadeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
		final ConstraintLayout cidadeLayout;
		final TextView cidade;
		final TextView desc_tempo;
		final TextView temperatura;
		final ImageView icone;

		public CidadeViewHolder(View v) {
			super(v);

			cidadeLayout = (ConstraintLayout) v.findViewById(R.id.layoutCidade);
			cidade = (TextView) v.findViewById(R.id.txtCidade);
			desc_tempo = (TextView) v.findViewById(R.id.txtCondicao);
			temperatura = (TextView) v.findViewById(R.id.txtTemp);
			icone = (ImageView) v.findViewById(R.id.imgIcone);

			v.setOnClickListener(this);
			v.setOnLongClickListener(this);
		}


		@Override
		public void onClick(View v) {
			int position = getAdapterPosition();
			if (position != RecyclerView.NO_POSITION && listener != null) {
				Cidade cidade = cidades.get(position);
				listener.onItemClick(cidade);
			}
		}

		@Override
		public boolean onLongClick(View v) {

			// dialogo para confirmar a deleção

			AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
			builder.setTitle("Apagar cidade")
					.setMessage("Tem certeza que deseja apagar essa cidade?")
					.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {

							// deletar a cidade
							int position = getAdapterPosition();
							if (position != RecyclerView.NO_POSITION) {

								CidadeDBHelper db = new CidadeDBHelper(context);
								db.apagarCidade(cidades.get(position));
								cidades.remove(position);

								notifyItemRemoved(position);
							}
						}
					})
					.setNegativeButton("Não", null)
					.show();

			// Return true to indicate that the long click event is consumed
			return true;
		}

	}

	public CidadeAdapter(List<Cidade> cidades, int rowLayout, Context context) {
		this.cidades = cidades;
		this.rowLayout = rowLayout;
		this.context = context;
	}

	@Override
	public CidadeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(rowLayout, parent, false);
		return new CidadeViewHolder(view);
	}

	public void setCidades(List<Cidade> cidades) {
		this.cidades = cidades;
	}

	public void setOnItemClickListener(OnItemClickListener listener) {
		this.listener = listener;
	}


	@Override
	public void onBindViewHolder(CidadeViewHolder holder, final int position) {

		Cidade cidade = cidades.get(position);

		holder.cidade.setText(cidade.getCidade());

		String desc = cidade.getDesc_tempo();

		if (!desc.isEmpty()) {
			// Deixar a 1a letra maiuscula
			desc = desc.substring(0, 1).toUpperCase() + desc.substring(1);
		}

		holder.desc_tempo.setText(desc);

		holder.temperatura.setText(String.format("%.0f", cidade.getTemp()) + "ºC");

		String nomeicone = "_" + cidade.getIcone_tempo();
		int iconeid = context.getResources().getIdentifier(nomeicone, "drawable", context.getPackageName());

		if (iconeid != 0) {
			holder.icone.setImageResource(iconeid);
		}

	}

	@Override
	public int getItemCount() {
		if (cidades != null) {
			return cidades.size();
		} else {
			return 0;
		}
	}


}

