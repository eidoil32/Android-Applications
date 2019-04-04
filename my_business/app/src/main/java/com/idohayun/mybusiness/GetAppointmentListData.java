package com.idohayun.mybusiness;

import android.app.DatePickerDialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

public class GetAppointmentListData {
    private static final String TAG = "GetAppointmentListData";
    private static List<DateArray> dateArrayList = new ArrayList<>();
    private static int func_day, func_month, func_year, func_hour, func_min, func_personID, func_tempAvailable, func_userId, func_type;
    private DatePickerDialog.OnDateSetListener dateSetListener;
    private static String convertTypeToInt_ifNull;
    private static boolean available;
    private static StringBuilder sb = new StringBuilder();

    public static void getData(final Context context, int selected_day, int selected_month, int selected_year, final ListView listView, final ProgressBar progressBar) {
        RequestQueue queue = Volley.newRequestQueue(context);
        progressBar.setVisibility(View.VISIBLE);
        Map<String, String> map = new HashMap<>();
        map.put("Day", Integer.toString(selected_day));
        map.put("Month", Integer.toString(selected_month));
        map.put("Year", Integer.toString(selected_year));
        Log.d(TAG, "onDateSet: " + map.toString());
        final JSONObject jsonObject = new JSONObject(map);
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST, // the request method
                ServerURLSManager.Appointment_show_list, jsonObject,
                new Response.Listener<JSONObject>() { // the response listener
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            dateArrayList.clear();
                            String s = response.getString("data");
                            sb.append(s);
                            JSONArray jsonArray = new JSONArray(s);
                            Log.d(TAG, "onResponse: " + jsonArray.toString());
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject obj = jsonArray.getJSONObject(i);
                                Log.d(TAG, "onResponse: " + obj.toString());
                                func_day = obj.getInt("Day");
                                func_month = obj.getInt("Month");
                                func_year = obj.getInt("Year");
                                func_hour = obj.getInt("Hour");
                                func_min = obj.getInt("Min");
                                func_personID = obj.getInt("PersonID");
                                func_tempAvailable = obj.getInt("Available");
                                func_userId = obj.getInt("OrderPersonAppID");
                                convertTypeToInt_ifNull = obj.getString("Type");
                                if(convertTypeToInt_ifNull == null || convertTypeToInt_ifNull.equals(" ") || convertTypeToInt_ifNull.equals("")) {
                                    func_type = -1;
                                } else {
                                    func_type = Integer.parseInt(convertTypeToInt_ifNull);
                                }
                                available = (func_tempAvailable == 1 ? TRUE : FALSE);
                                DateArray dateArray = new DateArray(func_day, func_month, func_year, func_hour, func_min, func_type, func_personID, available,func_userId);
                                dateArrayList.add(dateArray);
                                Log.d(TAG, "onResponse: creating (" + i + ") dates dateArray= " +  dateArray.toString() + " type: " + func_type);
                            }
                            progressBar.setVisibility(View.INVISIBLE);
                            DatesListAdapter datesListAdapter = new DatesListAdapter(context, R.layout.order_list_adapter, dateArrayList,listView,progressBar);
                            listView.setAdapter(datesListAdapter);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() { // the error listener
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "onErrorResponse: error " + error.toString());
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(context, "Oops! Got error from server!", Toast.LENGTH_SHORT).show();
                    }
                });

        queue.add(request);
    }
}
