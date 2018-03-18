package com.codeworm.barkapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.zxing.client.android.Intents;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static android.support.v4.content.ContextCompat.startActivity;

/**
 * Created by Harvie Marcelino on 01/27/2018.
 */

public class StringRequestController{
    //public static boolean flag;
    static ValidationFlag validationFlag = new ValidationFlag();

    public static void validateQR(final Context context, final String inputData) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_SCANNED_QR,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        //progressDialog.dismiss();
                        System.out.println("Inside Response on validateQR");
                        try {
                            System.out.println(response);
                            JSONObject jsonObject = new JSONObject(response);
                            //Toast.makeText(getApplicationContext(), jsonObject.getString("type"), Toast.LENGTH_SHORT).show();

                            if(jsonObject.getString("type").equals("Success")){
                                System.out.println("Qr code matched!!");
                                validationFlag.setValidateQr(true);
                                //flag = true;

                            }else{
                                Toast.makeText(context, jsonObject.getString("type"), Toast.LENGTH_LONG).show();
                                //flag= false;
                                validationFlag.setValidateQr(false);
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
                        Toast.makeText(context, "WAG KANG EPAL", Toast.LENGTH_SHORT).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("code", inputData);
                return params;
            }
        };

        RequestHandler.getInstance(context).addToRequestQueue(stringRequest);
        System.out.println("IM SAD");
        //return flag;
    }

    public static void setParkingDetails(final Context context, final String inputData) {
        System.out.println("Inside setParkingDetails");

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_PARKING_DETAILS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //progressDialog.dismiss();
                        try {
                            System.out.println(response);
                            JSONObject jsonObject = new JSONObject(response);
                            //Toast.makeText(getApplicationContext(), jsonObject.getString("type"), Toast.LENGTH_SHORT).show();
                            System.out.println("Getting there...");
                            if(!jsonObject.getBoolean("error")){
                                SharedPreferencesManager.getInstance(context).setParkingDetails(jsonObject.getString("slot_id"), jsonObject.getString("rack_location"), jsonObject.getString("address"));
                                System.out.println("Value of slot ID that will be transfered in shared preference: " + jsonObject.getString("slot_id"));

                            }else{
                                System.out.println("Napunta sa else");
                                Toast.makeText(context, jsonObject.getString("type"), Toast.LENGTH_LONG).show();;
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
                params.put("code", inputData);
                return params;
            }
        };
        System.out.println("The decision of momshie");
        RequestHandler.getInstance(context).addToRequestQueue(stringRequest);
//        RequestQueue requestQueue = Volley.newRequestQueue(this);
//        requestQueue.add(stringRequest);
//
//        decision = validationFlag.isCheckUser();
//        System.out.print("The decision of momshie is " + decision);
    }
}
