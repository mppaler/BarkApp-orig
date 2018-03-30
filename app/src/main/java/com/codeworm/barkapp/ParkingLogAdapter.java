package com.codeworm.barkapp;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import de.codecrafters.tableview.TableView;
import de.codecrafters.tableview.toolkit.LongPressAwareTableDataAdapter;

/**
 * Created by Harvie Marcelino on 02/20/2018.
 */

public class ParkingLogAdapter extends LongPressAwareTableDataAdapter<ParkingLog> {
    /*private Context mContext;
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
    }*/

    private static final int TEXT_SIZE = 14;

    public ParkingLogAdapter(final Context context, final List<ParkingLog> data, final TableView<ParkingLog> tableView) {
        super(context, data, tableView);
    }

    @Override
    public View getDefaultCellView(int rowIndex, int columnIndex, ViewGroup parentView) {
        ParkingLog parkingLog = getRowData(rowIndex);
        View renderedView = null;

        switch (columnIndex) {
            case 0:
                renderedView = getView(parkingLog, parentView, columnIndex);
                break;
            case 1:
                renderedView = getView(parkingLog, parentView, columnIndex);;
                break;
            case 2:
                renderedView = getView(parkingLog, parentView, columnIndex);;
                break;
            case 3:
                renderedView = getView(parkingLog, parentView, columnIndex);;
                break;
        }

        return renderedView;
    }

    @Override
    public View getLongPressCellView(int rowIndex, int columnIndex, ViewGroup parentView) {
        return null;
    }

    private View getView(ParkingLog parkingLog, ViewGroup parentView, int columnIndex){
        TextView textView = new TextView(getContext());
        switch (columnIndex) {
            case 0:
                textView.setText(parkingLog.getTimestamp());
                break;
            case 1:
                textView.setText(parkingLog.getEvent());
                break;
            case 2:
                textView.setText(parkingLog.getParking_area());
                break;
            case 3:
                textView.setText(parkingLog.getSlot_id());
                break;
        }
        textView.setPadding(20, 10, 20, 10);
        textView.setTextSize(TEXT_SIZE);

        return textView;
    }
}
