package com.codeworm.barkapp;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.codeworm.barkapp.m_FireBase.FirebaseHelper;
import com.codeworm.barkapp.m_Model.LocationModel;
import com.codeworm.barkapp.m_UI.ParkingListAdapter;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;
import java.util.List;

public class ParkingListActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference db =  database.getReferenceFromUrl("https://barkapp-cc121.firebaseio.com/");
    FirebaseHelper helper;
    ParkingListAdapter adapter;
    ArrayList<LocationModel> locationModels=new ArrayList<>();
    ArrayList<String> mKeys= new ArrayList<>();
    ListView lv;
    EditText nameEditTxt, propTxt, descTxt;
    SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking_list);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Parking List");

        if(toolbar == null){
            System.out.println("I am null");
        }else{
            System.out.println("I am not null");
        }

        //for Search View

        toolbar.setTitleTextColor(Color.parseColor("#FFFFFF"));

        searchView = (SearchView) findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(this);
        searchView.setIconifiedByDefault(false);
        searchView.setSubmitButtonEnabled(true);
        searchView.setQueryHint("Search here..");

        lv = findViewById(R.id.lv);

        //INITIALIZE FIREBASE DB
        db = FirebaseDatabase.getInstance().getReference();
        helper = new FirebaseHelper(db);

        //ADAPTER
        adapter = new ParkingListAdapter(this, helper.retrieve());

        lv.setAdapter(adapter);
        //add for search
        lv.setTextFilterEnabled(false);

        adapter.notifyDataSetChanged();



//        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
//            @Override
//            public void onSearchViewShown() {
//
//            }
//
//            @Override
//            public void onSearchViewClosed() {
//                lv = findViewById(R.id.lv);
//                adapter = new ParkingListAdapter(ParkingListActivity.this, helper.retrieve());
//                lv.setAdapter(adapter);
//
//            }
//        });
//
//        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                if(newText != null && !newText.isEmpty()){
//                    List<String> lstFound = new ArrayList<String>();
//                    for(LocationModel item: locationModels){
//                        if (item.contains(newText))
//                            lstFound.add(String.valueOf(item));
//                        }
//                        adapter = new ParkingListAdapter(ParkingListActivity.this, helper.retrieve());
//                        lv.setAdapter(adapter);
//                    }
//                    else{
//                    adapter = new ParkingListAdapter(ParkingListActivity.this, helper.retrieve());
//                    lv.setAdapter(adapter);
//                }
//                return true;
//                }
//
//        });
        db.child("locs").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


//        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
//        DatabaseReference businessRef = rootRef.child("locs");
//
//        ValueEventListener eventListener = new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                for(DataSnapshot ds : dataSnapshot.getChildren()) {
//                    String name = ds.child("name").getValue(String.class);
//                    Long ratio = ds.child("ratio").getValue(Long.class);
//                    Long total_slot = ds.child("total_slot").getValue(Long.class);
//                    Log.d("POTA", name + " / " + ratio + " / " + total_slot + " / ");
//                }
//
//            }
//
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        };
//        businessRef.addListenerForSingleValueEvent(eventListener);

    }




    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        Filter filter = adapter.getFilter();
        if (TextUtils.isEmpty(newText)) {
            filter.filter("");


        } else {
            filter.filter(newText);

        }
        return true;
    }
}
