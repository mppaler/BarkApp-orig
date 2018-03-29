package com.codeworm.barkapp;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class UserManualFragment extends Fragment {
    private ListView mListView;
    public View ftView;

    public UserManualFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        ftView= inflater.inflate(R.layout.user_manual, container, false);
        return ftView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mListView = getActivity().findViewById(R.id.listView_UserManual);

        ArrayList<Card> list = new ArrayList<>();

        list.add(new Card("drawable://" + R.drawable.um_map, "In the homepage, click the 'Find a Rack' button to view the map and to look for racks that are available for parking the user's bicycle. The user can also search for a specific area.", "1. Navigate nearby racks"));
        list.add(new Card("drawable://" + R.drawable.um_realtime, "On the map, click the information button('i') in order to track realtime slots. This will show if the racks have available slots or if the slot is already full","2. Track realtime slots"));
        list.add(new Card("drawable://" + R.drawable.scan_image, "In the homepage, click the 'Scan QR Code' button in order to scan the qr codes that are displayed on each slot. In this way, the bike will be registered in the slot","3. Scan QR code to get registered"));
        list.add(new Card("drawable://" + R.drawable.bicycle_logo, "If you are registered in the slot, always make sure that BEFORE you claim your bike, you must first scan the QR code again. ","4. Claim your bike right! \n(for registered only)"));
        

        CardAdapter adapter = new CardAdapter(getActivity(), R.layout.fragment_user_manual, list);
        mListView.setAdapter(adapter);
    }
}
