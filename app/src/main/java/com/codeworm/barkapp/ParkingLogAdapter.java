package com.codeworm.barkapp;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Harvie Marcelino on 02/20/2018.
 */

public class ParkingLogAdapter extends BaseAdapter {
    private Context mContext;
    private List<ParkingLog> mParkingLog;

    public ParkingLogAdapter(Context mContext, List<ParkingLog> mParkingLog){
        this.mContext = mContext;
        this.mParkingLog = mParkingLog;
    }

    public void addListItemToAdapter(List<ParkingLog> list) {
        //Add list to current array list of data
        mParkingLog.addAll(list);
        //Notify UI
        this.notifyDataSetChanged();
    }
    @Override
    public int getCount() {
        return mParkingLog.size();
    }

    @Override
    public Object getItem(int position) {
        return mParkingLog.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = View.inflate(mContext, R.layout.parking_log_list, null);
        TextView tvTimestamp = (TextView)view.findViewById(R.id.tv_timestamp);
        TextView tvParkingArea = (TextView)view.findViewById(R.id.tv_parking_area);
        TextView tvSlotID = (TextView)view.findViewById(R.id.tv_slot_id);
        TextView tvEvent = (TextView)view.findViewById(R.id.tv_event);

        //Set text for TextView
        tvTimestamp.setText(mParkingLog.get(position).getTimestamp());
        tvParkingArea.setText(mParkingLog.get(position).getParking_area());
        tvSlotID.setText(mParkingLog.get(position).getSlot_id());
        tvEvent.setText(mParkingLog.get(position).getEvent());

        //Save product id to tag
        view.setTag(mParkingLog.get(position).getId());

        return view;
    }
}
