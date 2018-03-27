package com.codeworm.barkapp;


import android.content.Context;
import android.content.Intent;
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
import android.widget.ListView;
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
import de.codecrafters.tableview.model.TableColumnWeightModel;
import de.codecrafters.tableview.toolkit.SimpleTableHeaderAdapter;
import de.codecrafters.tableview.toolkit.SortStateViewProviders;
import de.codecrafters.tableview.toolkit.TableDataRowBackgroundProviders;

import static com.codeworm.barkapp.R.id.tableView;
import static com.codeworm.barkapp.R.id.view;


/**
 * A simple {@link Fragment} subclass.
 */
public class ParkingLogFragment extends Fragment {
    private ListView lvParkingLog;
    private ParkingLogAdapter adapter;
    private List<ParkingLog> mParkingLog = new ArrayList<ParkingLog>();
    private List<ParkingLog> holder = new ArrayList<ParkingLog>();
    private List<ParkingLog> record = new ArrayList<ParkingLog>();
    public Handler mHandler;
    public boolean isLoading = false;
    public int currentId=0;
    public int ctr = 1;
    public boolean flag = false;
    TextView tvNoRecordFound;
    ParkingLogFactory parkingLogFactory = new ParkingLogFactory();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_parking_log, container, false);

        SortableTableView tableView = (SortableTableView) view.findViewById(R.id.tableView);
        initializeTableView(tableView, getActivity().getApplicationContext());

        //final ParkingLogTableView parkingLogTableView = (ParkingLogTableView) findViewById(R.id.tableView);
        if(tableView != null){
            System.out.println("Value of parkingLogList is " + parkingLogFactory.getParkingLogList());
            final ParkingLogAdapter parkingLogAdapter = new ParkingLogAdapter(getActivity().getApplicationContext(), parkingLogFactory.getParkingLogList(), tableView);
            tableView.setDataAdapter(parkingLogAdapter);
            tableView.setSwipeToRefreshEnabled(false);
            tableView.setLongClickable(false);
            tableView.setClickable(false);

//            parkingLogTableView.setSwipeToRefreshListener(new SwipeToRefreshListener() {
//                @Override
//                public void onRefresh(RefreshIndicator refreshIndicator) {
//
//                }
//            });
        }

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


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

    /*public class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    //Add loading view during search processing
                    lvParkingLog.addFooterView(ftView);
                    break;
                case 1:
                    //Update data adapter and UI
                    adapter.addListItemToAdapter((ArrayList<ParkingLog>)msg.obj);
                    //Remove loading view after update listview
                    lvParkingLog.removeFooterView(ftView);
                    isLoading=false;
                    break;
                default:
                    break;
            }
        }
    }*/

    private ArrayList<ParkingLog> getMoreData() {
        System.out.println("Getting more data..");
        currentId += 10;
        ArrayList<ParkingLog>lst = new ArrayList<>();

        if((holder.size()-currentId)>10){
            for (int i = currentId; i <= 10; i++) {
                lst.add(holder.get(i+currentId));
            }
        }else{
            for (int i = 0; i <= holder.size()-currentId; i++) {
                lst.add(holder.get(currentId));
                currentId++;
            }
        }
        //Sample code get new data :P
//        lst.add(new Product(++currentId, "iPhone4", 200, "Apple iPhone4 16GB"));
//        lst.add(new Product(++currentId, "iPhone4S", 250, "Apple iPhone4S 16GB"));
//        lst.add(new Product(++currentId, "iPhone5", 300, "Apple iPhone5 16GB"));
//        lst.add(new Product(++currentId, "iPhone5S", 350, "Apple iPhone5S 16GB"));
//        lst.add(new Product(++currentId, "iPhone6", 400, "Apple iPhone6 16GB"));
//        lst.add(new Product(++currentId, "iPhone6S", 450, "Apple iPhone6S 16GB"));
//        lst.add(new Product(++currentId, "iPhone7", 500, "Apple iPhone7 16GB"));
        return lst;
    }
    public class ThreadGetMoreData extends Thread {
        @Override
        public void run() {
            //Add footer view after get data
            mHandler.sendEmptyMessage(0);
            //Search more data
            ArrayList<ParkingLog> lstResult = getMoreData();
            //Delay time to show loading footer when debug, remove it when release
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //Send the result to Handle
            Message msg = mHandler.obtainMessage(1, lstResult);
            mHandler.sendMessage(msg);

        }
    }

}
