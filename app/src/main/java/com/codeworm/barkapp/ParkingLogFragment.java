package com.codeworm.barkapp;


import android.content.Context;
import android.content.Intent;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
    public View ftView;
    public boolean isLoading = false;
    public int currentId=0;
    public int ctr = 1;
    public boolean flag = false;
    TextView tvNoRecordFound;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ftView = inflater.inflate(R.layout.fragment_parking_log, container, false);
        // Inflate the layout for this fragment
        return ftView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        lvParkingLog = getActivity().findViewById(R.id.listview_log);
        tvNoRecordFound = getActivity().findViewById(R.id.tv_NoRecordFound);

        //System.out.println("Value of arrayListOfLog inside Fragment is --> " + bundle);
        mParkingLog = getArguments().getParcelableArrayList("arrayListOfLog");

        if(mParkingLog.isEmpty()){
            tvNoRecordFound.setVisibility(View.VISIBLE);
        }else {

            LayoutInflater li = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            ftView = li.inflate(R.layout.footer_view, null);
            mHandler = new MyHandler();
            System.out.println("Inside onActivityCreated");
            Iterator iterator = mParkingLog.iterator();

            while (iterator.hasNext()) {
                ParkingLog pl = (ParkingLog) iterator.next();
                System.out.println("Value of timestamp --> " + pl.getTimestamp());
                holder.add(new ParkingLog(ctr, pl.getTimestamp(), pl.getEvent(), pl.getParking_area(), pl.getSlot_id()));
                ctr++;
            }

            if (holder.size() > 10) {
                for (int i = 0; i <= 9; i++) {
                    record.add(holder.get(i));
                }
            } else {
                record = holder;
            }

            //Init adapter
            //ArrayAdapter<Product> arrayAdapter = new ArrayAdapter<Product>(getActivity().getApplicationContext(), android.R.layout.simple_list_item_1, mProductList);
            adapter = new ParkingLogAdapter(getActivity().getApplicationContext(), record);
            System.out.println("LAMAN OF ADAPTER IS " + adapter);
            if (!adapter.isEmpty()) {
                System.out.println("ADAPTER NOT EMPTY");
            } else {
                System.out.println("ADAPTER EMPTY");
            }
            lvParkingLog.setAdapter(adapter);

            if (lvParkingLog != null) {
                System.out.println("lvProduct NOT EMPTY");
            } else {
                System.out.println("lvProduct EMPTY");
            } //HANGGANG DITO
        }

        lvParkingLog.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                //Check when scroll to last item in listview, in this tut, init data in listview = 10 item
                if (view.getLastVisiblePosition() == totalItemCount - 1 && lvParkingLog.getCount() >= 10 && isLoading == false) {
                    System.out.println("Last item detected. Getting more data...");
                    isLoading = true;
                    Thread thread = new ThreadGetMoreData();
                    //Start thread
                    thread.start();
                }

            }
        });

        super.onActivityCreated(savedInstanceState);
    }

    public class MyHandler extends Handler {
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
    }

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

    public void getJSONResponse(final String sUsername) {
        /*JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.POST, Constants.URL_GET_PARKING_LOGS,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        //progressDialog.dismiss();
                        try {
                            for(int i=0;i<response.length();i++){
                                // Get current json object
                                JSONObject record = response.getJSONObject(i);

                                // Get the current student (json object) data
                                String timestamp = record.getString("timestamp");
                                String event = record.getString("event");
                                String parking_area = record.getString("parking_area");
                                String slot_id = record.getString("slot_id");

                                ParkingLog parkingLog = new ParkingLog(0,null,null,null,null);
                                parkingLog.setTimestamp(timestamp);
                                parkingLog.setEvent(event);
                                parkingLog.setParking_area(parking_area);
                                parkingLog.setSlot_id(slot_id);

                                mParkingLog.add(parkingLog);
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //progressDialog.hide();
                        // Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("username", sUsername);
                return params;
            }
        };
        System.out.print("The decision of momshie");
        RequestHandler.getInstance(getActivity().getApplicationContext()).addToRequestQueue(jsonArrayRequest);
    }*/
        /*System.out.println("Inside getJSONResponse");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_GET_PARKING_LOGS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //progressDialog.dismiss();
                        System.out.println("Value of response " + response);
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            System.out.println("Inside Response");
                            System.out.println("Value of jsonArray " + jsonArray);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                // Get current json object
                                JSONObject record = jsonArray.getJSONObject(i);

                                // Get the current student (json object) data
                                String timestamp = record.getString("timestamp");
                                String event = record.getString("event");
                                String parking_area = record.getString("parking_area");
                                String slot_id = record.getString("slot_id");

                                System.out.println("Printing values of each log: ");
                                System.out.println("timestamp : " + timestamp);
                                System.out.println("event : " + event);
                                System.out.println("parking area : "+ parking_area);
                                System.out.println("slot id : " + slot_id);

//                                ParkingLog parkingLog = new ParkingLog();
//                                parkingLog.setTimestamp(timestamp);
//                                parkingLog.setEvent(event);
//                                parkingLog.setParking_area(parking_area);
//                                parkingLog.setSlot_id(slot_id);
                                System.out.println("Add data to ArrayList");
//                                System.out.println("Value of parkingLog is " + parkingLog);
                                mParkingLog.add(new ParkingLog(i, timestamp, event, parking_area, slot_id));
                            }
                        flag = true;
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //progressDialog.hide();
                        // Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("username", sUsername);
                return params;
            }
        };
        System.out.println("The decision of momshie");
        RequestHandler.getInstance(getActivity().getApplicationContext()).addToRequestQueue(stringRequest);
        System.out.println("I ended up here");*/

    }

    @Override
    public void onPause() {
//        record.clear();
//        adapter.notifyDataSetChanged();
        super.onPause();
    }
}
