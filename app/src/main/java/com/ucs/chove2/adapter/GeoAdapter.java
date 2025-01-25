package com.ucs.chove2.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.ucs.chove2.R;
import com.ucs.chove2.model.geo.Geo;

import java.util.List;

public class GeoAdapter extends RecyclerView.Adapter<GeoAdapter.GeoViewHolder> {

	private List<Geo> geos;
	private final int rowLayout;
	private final Context context;

	public interface OnItemClickListener {
		void onItemClick(Geo geo);
	}
	private OnItemClickListener listener;


	public class GeoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
		final LinearLayout geosLayout;
		final TextView cidade;
		final TextView pais;

		public GeoViewHolder(View v) {
			super(v);
			geosLayout = (LinearLayout) v.findViewById(R.id.layoutGeo);
			cidade = (TextView) v.findViewById(R.id.txtGeo);
			pais = (TextView) v.findViewById(R.id.txtPaisGeo);
			v.setOnClickListener(this);
		}


		@Override
		public void onClick(View v) {
			int position = getAdapterPosition();
			if (position != RecyclerView.NO_POSITION && listener != null) {
				Geo geo = geos.get(position);
				listener.onItemClick(geo);
			}
		}
	}

	public GeoAdapter(List<Geo> Geos, int rowLayout, Context context) {
		this.geos = Geos;
		this.rowLayout = rowLayout;
		this.context = context;
	}

	@Override
	public GeoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(rowLayout, parent, false);
		return new GeoViewHolder(view);
	}

	public void setGeos(List<Geo> geos) {
		this.geos = geos;
	}

	public void setOnItemClickListener(OnItemClickListener listener) {
		this.listener = listener;
	}



	@Override
	public void onBindViewHolder(GeoViewHolder holder, final int position) {
		Geo geo = geos.get(position);

		holder.cidade.setText(geo.getName());

		String pais = geo.getCountry();
		int strid = context.getResources().getIdentifier(pais, "string", context.getPackageName());

		if (strid != 0) {
			pais = context.getResources().getString(strid);
		}

		if (geo.getState() != null) {
			pais = pais + " - " + geo.getState();
		}

		holder.pais.setText(pais);
	}

	@Override
	public int getItemCount() {
		if (geos != null) {
			return geos.size();
		} else {
			return 0;
		}
	}


}
