package com.example.renukamatkar.pinterest;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.List;

/**
 * Created by AVIN PC on 17-10-2015.
 */
public class PinsAdapter extends RecyclerView.Adapter<PinsAdapter.PinsHolder> {

    List<PinsInfo> info = Collections.emptyList();
    Context cc;
    LayoutInflater inflater;
    String pids[];
    SharedPreferences sp;
    SharedPreferences.Editor edt;

    public PinsAdapter(Context c, List<PinsInfo> data, String[] pinids)
    {
        this.cc = c;
        this.info = data;
        inflater = LayoutInflater.from(c);
        this.pids = pinids;
        sp = cc.getSharedPreferences("config",0);
        edt = sp.edit();
    }

    @Override
    public PinsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.custompin,parent,false);
        PinsHolder ph = new PinsHolder(v);
        return ph;
    }

    @Override
    public void onBindViewHolder(PinsHolder holder, int position) {
        PinsInfo pi = info.get(position);
        Picasso.with(cc).load(pi.icon).into(holder.pinico);
        holder.pintext.setText(pi.pintext);
    }

    @Override
    public int getItemCount() {
        return info.size();
    }

    class PinsHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView pintext;
        ImageView pinico;
        public PinsHolder(View itemView) {
            super(itemView);
            pintext = (TextView)itemView.findViewById(R.id.pintitle);
            pinico = (ImageView)itemView.findViewById(R.id.pinico);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            edt.putString("pinclicked",pids[getPosition()]);
            edt.commit();
            cc.startActivity(new Intent(cc,Pin.class));
        }
    }
}
