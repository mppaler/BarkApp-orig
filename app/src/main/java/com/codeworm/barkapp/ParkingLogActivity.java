package com.codeworm.barkapp;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by NgocTri on 11/15/2015.
 */
public class ParkingLogActivity extends Activity {
    private ListView lvProduct;
    private ProductListAdapter adapter;
    private List<Product> mProductList;
    public Handler mHandler;
    public View ftView;
    public boolean isLoading = false;
    public int currentId=11;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking_log);

//        lvProduct = (ListView)findViewById(R.id.listview_product);

        LayoutInflater li = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ftView = li.inflate(R.layout.footer_view, null);
        mHandler = new MyHandler();
        mProductList = new ArrayList<>();
        //Add sample data for list
        //We can get data from DB, webservice here
        mProductList.add(new Product(1, "iPhone4", 200, "Apple iPhone4 16GB"));
        mProductList.add(new Product(3, "iPhone4S", 250, "Apple iPhone4S 16GB"));
        mProductList.add(new Product(4, "iPhone5", 300, "Apple iPhone5 16GB"));
        mProductList.add(new Product(5, "iPhone5S", 350, "Apple iPhone5S 16GB"));
        mProductList.add(new Product(6, "iPhone6", 400, "Apple iPhone6 16GB"));
        mProductList.add(new Product(7, "iPhone6S", 450, "Apple iPhone6S 16GB"));
        mProductList.add(new Product(8, "iPhone7", 500, "Apple iPhone7 16GB"));
        mProductList.add(new Product(9, "iPhone7S", 600, "Apple iPhon7S 16GB"));
        mProductList.add(new Product(10, "iPhone8", 700, "Apple iPhone8 16GB"));
        mProductList.add(new Product(11, "iPhone8S", 800, "Apple iPhone8S 16GB"));

        //Init adapter
        adapter = new ProductListAdapter(getApplicationContext(), mProductList);
        lvProduct.setAdapter(adapter);

        lvProduct.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Do something
                //Ex: display msg with product id get from view.getTag
                Toast.makeText(getApplicationContext(), "Clicked product id =" + view.getTag(), Toast.LENGTH_SHORT).show();
            }
        });
        lvProduct.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                //Check when scroll to last item in listview, in this tut, init data in listview = 10 item
                if(view.getLastVisiblePosition() == totalItemCount-1 && lvProduct.getCount() >=10 && isLoading == false) {
                    isLoading = true;
                    Thread thread = new ThreadGetMoreData();
                    //Start thread
                    thread.start();
                }

            }
        });
    }

    public class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    //Add loading view during search processing
                    lvProduct.addFooterView(ftView);
                    break;
                case 1:
                    //Update data adapter and UI
                    adapter.addListItemToAdapter((ArrayList<Product>)msg.obj);
                    //Remove loading view after update listview
                    lvProduct.removeFooterView(ftView);
                    isLoading=false;
                    break;
                default:
                    break;
            }
        }
    }

    private ArrayList<Product> getMoreData() {
        ArrayList<Product>lst = new ArrayList<>();
        //Sample code get new data :P
        lst.add(new Product(++currentId, "iPhone4", 200, "Apple iPhone4 16GB"));
        lst.add(new Product(++currentId, "iPhone4S", 250, "Apple iPhone4S 16GB"));
        lst.add(new Product(++currentId, "iPhone5", 300, "Apple iPhone5 16GB"));
        lst.add(new Product(++currentId, "iPhone5S", 350, "Apple iPhone5S 16GB"));
        lst.add(new Product(++currentId, "iPhone6", 400, "Apple iPhone6 16GB"));
        lst.add(new Product(++currentId, "iPhone6S", 450, "Apple iPhone6S 16GB"));
        lst.add(new Product(++currentId, "iPhone7", 500, "Apple iPhone7 16GB"));
        return lst;
    }
    public class ThreadGetMoreData extends Thread {
        @Override
        public void run() {
            //Add footer view after get data
            mHandler.sendEmptyMessage(0);
            //Search more data
            ArrayList<Product> lstResult = getMoreData();
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
