package com.codeworm.barkapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;

public class UserManualActivity extends AppCompatActivity {

    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_manual);
        mListView = (ListView) findViewById(R.id.listView_UserManual);

        ArrayList<Card> list = new ArrayList<>();

        list.add(new Card("drawable://" + R.drawable.um_map, "In the homepage, click the 'Find a Rack' button to view the map and to look for racks that are available for parking the user's bicycle. The user can also search for a specific area.", "1. Navigate nearby racks"));
//        list.add(new Card("drawable://" + R.drawable.bamf1, "On the map, click the information button('i') in order to track realtime slots. This will show if the racks have available slots or if the slot is already full","2. Track realtime slots"));
//        list.add(new Card("drawable://" + R.drawable.colorado_mountains, "In the homepage, click the 'Scan QR Code' button in order to scan the qr codes that are displayed on each slot. In this way, the bike will be registered in the slot","3. Scan QR code to get registered"));
//        list.add(new Card("drawable://" + R.drawable.cork, "CLAIM YOUR BIKE RIGHT! ","4. Scan QR code to claim"));

        CardAdapter adapter = new CardAdapter(this, R.layout.activity_user_manual, list);
        mListView.setAdapter(adapter);
    }
}
