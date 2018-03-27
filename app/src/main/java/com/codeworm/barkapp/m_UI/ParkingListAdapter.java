package com.codeworm.barkapp.m_UI;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import com.codeworm.barkapp.R;
import com.codeworm.barkapp.m_Model.LocationModel;
import com.codeworm.barkapp.m_Model.Spacecraft;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by Mariah on 11/03/2018
 */

public class ParkingListAdapter extends BaseAdapter implements Filterable {
    Context c;
    ArrayList<LocationModel> locationModels;
    ArrayList<LocationModel> orig;


    public ParkingListAdapter(Context c, ArrayList<LocationModel> locationModels) {
        this.c = c;
        this.locationModels = locationModels;

    }

    @Override
    public int getCount() {
        return locationModels.size();
    }

    @Override
    public Object getItem(int position) {
        return locationModels.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void update(LocationModel locationModel, String newName){
        locationModel.setName(newName);
        notifyDataSetChanged();

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null)
        {

            convertView= LayoutInflater.from(c).inflate(R.layout.model,parent,false);
        }

        TextView nameTxt= (TextView) convertView.findViewById(R.id.nameTxt);
        TextView ratioTxt= (TextView) convertView.findViewById(R.id.ratioTxt);
        TextView totalTxt= (TextView) convertView.findViewById(R.id.totalTxt);
        TextView fullTxt = (TextView) convertView.findViewById(R.id.fullTxt);
        TextView availTxt = (TextView) convertView.findViewById(R.id.availTxt);



        final LocationModel s= (LocationModel) this.getItem(position);

        nameTxt.setText(s.getName());
        ratioTxt.setText(String.valueOf(s.getRatio()));
        totalTxt.setText(String.valueOf(s.getTotal_slot()));

        if(s.getRatio()==s.getTotal_slot()){
            fullTxt.setText("FULL");
            fullTxt.setTextColor(Color.rgb(255,0,0));




        } else{
            fullTxt.setText("AVAILABLE");
            fullTxt.setTextColor(Color.rgb(100,221,23));


        }


        //ONITECLICK
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(c,s.getName(),Toast.LENGTH_SHORT).show();
            }
        });
        return convertView;
    }

    public void filter(String charText){
        charText = charText.toLowerCase(Locale.getDefault());
        locationModels.clear();

        if(charText.length()==0){
            locationModels.addAll(locationModels);
        }else{
            for(LocationModel lm: locationModels){
                if(lm.getName().toLowerCase(Locale.getDefault()).contains(charText)){
                    locationModels.add(lm);
                }
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                final FilterResults oReturn = new FilterResults();
                final ArrayList<LocationModel> results = new ArrayList<LocationModel>();
                if (orig == null)
                    orig = locationModels;
                if (constraint != null) {
                    if (orig != null && orig.size() > 0) {
                        for (final LocationModel lm :orig) {
                            if (lm.getName().toLowerCase()
                                    .contains(constraint.toString()))
                                results.add(lm);
                        }
                    }
                    oReturn.values = results;
                }
                return oReturn;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults results) {
                locationModels = (ArrayList<LocationModel>)results.values;
                notifyDataSetChanged();

            }
        };
    }
}
