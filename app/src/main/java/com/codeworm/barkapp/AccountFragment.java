package com.codeworm.barkapp;


import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.DialogPreference;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;


/**
 * A simple {@link Fragment} subclass.
 */
public class AccountFragment extends Fragment {
    String sFullname, sUsername, sMobilenum;
    TextView txtFullname, txtUsername, txtMobilenum;
    Button btnChangeUsername, btnChangePassword, btnChangeMobilenum, btnLogout;
    ListView listView;

    public AccountFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //SharedPreferencesManager manager = (SharedPreferencesManager) getActivity().getSharedPreferences("mysharedpref", Context.MODE_PRIVATE);
//        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
//        sFullname = preferences.getString("fullname", "");
//        sUsername = preferences.getString("username", "");
//        sMobilenum = preferences.getString("mobilenum", "");
        View view = inflater.inflate(R.layout.fragment_account, container, false);


        txtFullname = (TextView) view.findViewById(R.id.fullname);
        txtUsername = (TextView) view.findViewById(R.id.username);
        txtMobilenum = (TextView) view.findViewById(R.id.mobilenum);
        btnLogout = (Button) view.findViewById(R.id.btn_logout);
        listView = (ListView) view.findViewById(R.id.listAccountDetails);

        System.out.println("Finish Initialization");
        System.out.println("Value of Fullname in SharedPreference ---> " + SharedPreferencesManager.getInstance(getActivity()).getFullname());
        txtFullname.setText(SharedPreferencesManager.getInstance(getActivity()).getFullname());
        txtUsername.setText(SharedPreferencesManager.getInstance(getActivity()).getUsername());
        txtMobilenum.setText(SharedPreferencesManager.getInstance(getActivity()).getMobilenum());


        final String details[] = {
                "FULLNAME : " + SharedPreferencesManager.getInstance(getActivity()).getFullname(),
                "USERNAME : " + SharedPreferencesManager.getInstance(getActivity()).getUsername(),
                "PASSWORD : N/A",
                "MOBILE NUMBER : " + SharedPreferencesManager.getInstance(getActivity()).getMobilenum()
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, details);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                System.out.println("You clicked " + position);
                System.out.println("You clicked " + details[position]);
                switch (position){
                    case 0: Toast.makeText(getActivity(), "Fullname cannot be changed.", Toast.LENGTH_SHORT).show(); break;
                    case 1: startActivity(new Intent(getActivity().getApplicationContext(), ChangeUsernameActivity.class)); break;
                    case 2: Toast.makeText(getActivity(), "You clicked password", Toast.LENGTH_SHORT).show(); break;
                    case 3: Toast.makeText(getActivity(), "You clicked mobile number", Toast.LENGTH_SHORT).show(); break;

                }
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createConfirmation();
            }
        });

        // Inflate the layout for this fragment
        return view;
    }

    public void createConfirmation(){
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
            alertDialog.setMessage("Are you sure you want to logout?");
            alertDialog.setCancelable(false);

            alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    SharedPreferencesManager.getInstance(getActivity()).logout();
                    getActivity().finish();
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                }
            });
            alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        alertDialog.create().show();
    }

}
