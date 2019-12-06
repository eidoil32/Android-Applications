package com.idohayun.mybusiness;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PhoneList {
    private static List<String> phoneList;
    private static int attempt = 0;
    private static final String TAG = "PhoneList";

    public static void setPhoneList(final Context context) {
        phoneList = new ArrayList<>();
        JsonObjectRequest request;
        RequestQueue queue = Volley.newRequestQueue(context);
        Map<String,String> map = new HashMap<>();
        map.put("PhoneList"," ");
        final JSONObject jsonObject = new JSONObject(map);
        Log.d(TAG, "onClick: " + jsonObject.toString());
        request = new JsonObjectRequest(
                Request.Method.POST, ServerURLSManager.General_get_phone_list, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(response.getString("status").equals("true")) {
                                String s = response.getString("data");
                                JSONArray jsonArray = new JSONArray(s);
                                for (int i = 0; i < jsonArray.length(); i++ ) {
                                    JSONObject obj = jsonArray.getJSONObject(i);
                                    phoneList.add(obj.getString("Phone"));
                                }
                                Log.d(TAG, "onResponse: " + phoneList.size());
                            } else {
                                if(attempt < 3) {
                                    setPhoneList(context);
                                    Log.d(TAG, "onResponse: try again... attempt number: " + attempt++);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            if(attempt < 3) {
                                setPhoneList(context);
                                Log.d(TAG, "onResponse: try again... attempt number: " + attempt++);
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "onResponse: Failed!!");
                if(attempt < 3) {
                    setPhoneList(context);
                    Log.d(TAG, "onResponse: try again... attempt number: " + attempt++);
                }
            }
        });
        queue.add(request);
    }

    public static List<String> getPhoneList() {
        return phoneList;
    }

    public static boolean checkIF_PhoneIsAlreadyInDB(String s_phoneNumber) {
        List<String> phoneList = PhoneList.getPhoneList();
        for (int i = 0 ; i < phoneList.size(); i++) {
            if(s_phoneNumber.equals(phoneList.get(i)))
                return false;
        }

        return true;
    }
}
