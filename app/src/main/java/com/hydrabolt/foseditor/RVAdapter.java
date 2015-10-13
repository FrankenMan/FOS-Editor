package com.hydrabolt.foseditor;

import android.content.Context;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.DwellerViewHolder> {

    List<Dweller> dwellers;
    Context c;

    RVAdapter(List<Dweller> dwellers, Context c){
        this.dwellers = dwellers;
        this.c = c;
    }

    @Override
    public DwellerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.content_vault_dwellers, parent, false);
        DwellerViewHolder dvh = new DwellerViewHolder(v);
        return dvh;
    }

    @Override
    public void onBindViewHolder(DwellerViewHolder holder, int position) {
        holder.dwellerName.setText(dwellers.get(position).name);
        holder.dwellerLevel.setText( Long.toString(dwellers.get(position).level));

        if (dwellers.get(position).isMale) {
            holder.dwellerLevel.setTextColor( getColor(R.color.blue) );
        }else{
            holder.dwellerLevel.setTextColor( getColor(R.color.pink) );
        }
    }

    @Override
    public int getItemCount() {
        return dwellers.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public static class DwellerViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView dwellerName;
        TextView dwellerLevel;

        DwellerViewHolder(View itemView) {
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.cv);
            dwellerName = (TextView) itemView.findViewById(R.id.dwellerName);
            dwellerLevel = (TextView) itemView.findViewById(R.id.dwellerLevel);
        }
    }


    // credits to http://stackoverflow.com/questions/31590714/getcolorint-id-deprecated-on-android-6-0-marshmallow-api-23
    private final int getColor(int id) {
        final int version = Build.VERSION.SDK_INT;
        if (version >= 23) {
            return ContextCompat.getColor(c, id);
        } else {
            return c.getResources().getColor(id);
        }
    }

}
