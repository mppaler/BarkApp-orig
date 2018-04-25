package com.codeworm.barkapp;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import de.codecrafters.tableview.TableView;
import de.codecrafters.tableview.toolkit.LongPressAwareTableDataAdapter;

/**
 * Created by Harvie Marcelino on 02/20/2018.
 */

public class ParkingLogAdapter extends LongPressAwareTableDataAdapter<ParkingLog>{

    private static final int TEXT_SIZE = 14;

    public ParkingLogAdapter(final Context context, final List<ParkingLog> data, final TableView<ParkingLog> tableView) {
        super(context, data, tableView);
    }

    @Override
    public View getDefaultCellView(int rowIndex, int columnIndex, ViewGroup parentView) {
        System.out.println("getRowData(rowIndex) value is -->> " + getRowData(rowIndex));
        ParkingLog parkingLog = getRowData(rowIndex);

        if(parkingLog == null){
            System.out.println("parkingLog is null OAAAAAAAAa");
        }else{
            System.out.println("parkingLog is not null OAAAAAAAAa");
        }

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
                if(parkingLog == null){
                    System.out.println("parkingLog is null");
                }else{
                    System.out.println("parkingLog is not null");
                }
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
