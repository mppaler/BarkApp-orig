package com.codeworm.barkapp;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import de.codecrafters.tableview.SortableTableView;
import de.codecrafters.tableview.listeners.OnScrollListener;
import de.codecrafters.tableview.model.TableColumnWeightModel;
import de.codecrafters.tableview.toolkit.EndlessOnScrollListener;
import de.codecrafters.tableview.toolkit.SimpleTableHeaderAdapter;
import de.codecrafters.tableview.toolkit.SortStateViewProviders;
import de.codecrafters.tableview.toolkit.TableDataRowBackgroundProviders;

import static com.codeworm.barkapp.R.id.match_parent;
import static com.codeworm.barkapp.R.id.tableView;
import static com.codeworm.barkapp.R.id.view;
import static com.codeworm.barkapp.R.id.wrap_content;


/**
 * A simple {@link Fragment} subclass.
 */
public class ParkingLogFragment extends Fragment {
    private ParkingLogAdapter parkingLogAdapter;
    private List<ParkingLog> mParkingLog = new ArrayList<ParkingLog>();
    private List<ParkingLog> holder = new ArrayList<ParkingLog>();
    public Handler mHandler;
    public boolean isLoading = false;
    public int currentId=0;
    public View ftView;
    TextView tvNoRecordFound;
    ParkingLogFactory parkingLogFactory = new ParkingLogFactory();
    SortableTableView tableView;
    ProgressBar progressBar;


    private LinearLayout mLinearScroll;
    int rowSize = 10;

//    ParkingLogAdapter parkingLogAdapter;
//    ParkingLogFactory parkingLogFactory = new ParkingLogFactory();
//    private LinearLayout mLinearScroll;
//    SortableTableView tableView;
//    private List<ParkingLog> mParkingLog = new ArrayList<ParkingLog>();
//    private List<ParkingLog> mParkingLogTemp = new ArrayList<ParkingLog>();
//    public View ftView;
//    // change row size according to your need, how many row you needed per page
//    int rowSize = 5;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_parking_log, container, false);

        LayoutInflater li = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ftView = li.inflate(R.layout.footer_view, null);

//        tvNoRecordFound = (TextView) view.findViewById(R.id.tv_NoRecordFound);
//        tableView = (SortableTableView) view.findViewById(R.id.tableView);
//        progressBar = (ProgressBar) view.findViewById(R.id.loadingList);
//
//
//        if(parkingLogFactory.getParkingLogList().isEmpty()){
//            tvNoRecordFound.setVisibility(View.VISIBLE);
//            tableView.setVisibility(View.GONE);
//        }
//
//        initializeTableView(tableView, getActivity().getApplicationContext());
//
//        mParkingLog = parkingLogFactory.getParkingLogList();
//
//
//        parkingLogAdapter = new ParkingLogAdapter(getActivity().getApplicationContext(), mParkingLog, tableView);
//
//        tableView.setDataAdapter(parkingLogAdapter);
//        tableView.setSwipeToRefreshEnabled(false);
//        tableView.setLongClickable(false);
//        tableView.setClickable(false);

        mLinearScroll = (LinearLayout) view.findViewById(R.id.linear_scroll);
        tableView = (SortableTableView) view.findViewById(R.id.tableView);

        if(parkingLogFactory.getParkingLogList().isEmpty()){
            tvNoRecordFound.setVisibility(View.VISIBLE);
            tableView.setVisibility(View.GONE);
        }

        initializeTableView(tableView, getActivity().getApplicationContext());

        /**
         * add item into arraylist
         */
        mParkingLog = parkingLogFactory.getParkingLogList();

        /**
         * create dynamic button according the size of array
         */

        int rem = mParkingLog.size() % rowSize;
        if (rem > 0) {

            for (int i = 0; i < rowSize - rem; i++) {
                mParkingLog.add(new ParkingLog(" ", " ", " ", " "));
            }
        }

        /**
         * add arraylist item into list on page load
         */
        addItem(0);

        int size = mParkingLog.size() / rowSize;

        for (int j = 0; j < size; j++) {
            final int k;
            k = j;
            final Button btnPage = new Button(getActivity());
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(120,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(5, 2, 2, 2);
            btnPage.setHeight(match_parent);
            btnPage.setTextColor(Color.BLACK);
            btnPage.setTextSize(18.0f);
            btnPage.setId(j);
            btnPage.setText(String.valueOf(j + 1));
            mLinearScroll.addView(btnPage, lp);

            btnPage.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    /**
                     * add arraylist item into list
                     */
                    addItem(k);

                }
            });
        }

        return view;


    }

    public void initializeTableView(SortableTableView tableView, Context context){

        final SimpleTableHeaderAdapter simpleTableHeaderAdapter = new SimpleTableHeaderAdapter(context, R.string.timestamp, R.string.event, R.string.area, R.string.slot_id);
        simpleTableHeaderAdapter.setTextColor(ContextCompat.getColor(context, R.color.table_header_text));
        if(simpleTableHeaderAdapter == null){
            System.out.println("simpleTableHeaderAdapter is null");
        }else{
            System.out.println("simpleTableHeaderAdapter is not null");
        }
        if(tableView == null){
            System.out.println("tableView is null");
        }else{
            System.out.println("tableView is not null");
        }
        tableView.setHeaderAdapter(simpleTableHeaderAdapter);

        final int rowColorEven = ContextCompat.getColor(context, R.color.table_data_row_even);
        final int rowColorOdd = ContextCompat.getColor(context, R.color.table_data_row_odd);
        tableView.setDataRowBackgroundProvider(TableDataRowBackgroundProviders.alternatingRowColors(rowColorEven, rowColorOdd));
        tableView.setHeaderSortStateViewProvider(SortStateViewProviders.brightArrows());


        final TableColumnWeightModel tableColumnWeightModel = new TableColumnWeightModel(4);
        tableColumnWeightModel.setColumnWeight(0, 3);
        tableColumnWeightModel.setColumnWeight(1, 2);
        tableColumnWeightModel.setColumnWeight(2, 3);
        tableColumnWeightModel.setColumnWeight(3, 2);
        tableView.setColumnModel(tableColumnWeightModel);

        tableView.setColumnComparator(0, ParkingLogComparator.getTimestampComparator());
        tableView.setColumnComparator(1, ParkingLogComparator.getEventComparator());
        tableView.setColumnComparator(2, ParkingLogComparator.getParkingAreaComparator());
        tableView.setColumnComparator(3, ParkingLogComparator.getSlotIdComparator());


    }

    public void addItem(int count) {
        holder.clear();
        count = count * rowSize;
        /**
         * fill temp array list to set on page change
         */
        for (int j = 0; j < rowSize; j++) {
            holder.add(j, mParkingLog.get(count));
            count = count + 1;
        }
        // set view
        setView();
    }

    //set view method
    public void setView() {
        if(mParkingLog.isEmpty()){
            System.out.println("mParkingLogTemp is null");
        }else{
            System.out.println("mParkingLogTemp is not null");
        }
        parkingLogAdapter = new ParkingLogAdapter(getActivity().getApplicationContext(), holder, tableView);

        if(parkingLogAdapter.isEmpty()){
            System.out.println("parkingLogAdapter is null");
        }else{
            System.out.println("parkingLogAdapter is not null");
        }

        tableView.setDataAdapter(parkingLogAdapter);
        tableView.setSwipeToRefreshEnabled(false);
        tableView.setLongClickable(false);
        tableView.setClickable(false);

    }


}
