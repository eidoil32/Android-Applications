package com.idohayun.manageapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class getInformationToListView {
    private static final String TAG = "getInformation";
    private String databaseURL;
    private ListView listView;
    private int mDay, mMonth, mYear;
    private Context context;

    getInformationToListView(String databaseURL, ListView listView, int day, int month, int year, Context context) {
        this.databaseURL = databaseURL;
        this.listView = listView;
        this.mDay = day;
        this.mMonth = month;
        this.mYear = year;
        this.context = context;
    }

    public void getJSON() {
        @SuppressLint("StaticFieldLeak")
        class GetJSON extends AsyncTask<Void, Void, String> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                try {
                    if (!s.isEmpty())
                        loadIntoListView(s);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected String doInBackground(Void... voids) {
                try {
                    java.net.URL url = new URL(databaseURL);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    StringBuilder sb = new StringBuilder();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String json;
                    while ((json = bufferedReader.readLine()) != null) {
                        sb.append(json);
                        sb.append("\n");
                    }
                    return sb.toString().trim();

                } catch (Exception e) {
                    return null;
                }
            }
        }
        GetJSON getJSON = new GetJSON();
        getJSON.execute();
    }

    private void loadIntoListView(String json) throws JSONException {
        Log.d(TAG, "loadIntoListView: " + json);
        JSONArray jsonArray = new JSONArray(json);
        List<DateListArray> datesArray = new ArrayList<>();
        int id;
        DateListArray tempDate;
        int available;

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject obj = jsonArray.getJSONObject(i);
            id = obj.getInt("PersonID");
            available = obj.getInt("Available");
            //(String name, String type, int day, int month, int year, int hour, int min, boolean available)
            tempDate = new DateListArray(
                    obj.getString("FullName"),
                    obj.getString("Type"),
                    obj.getInt("Day"),
                    obj.getInt("Month"),
                    obj.getInt("Year"),
                    obj.getInt("Hour"),
                    obj.getInt("Min"),
                    obj.getInt("Phone"),
                    id,
                    available == 1);
            if (checkDate(tempDate, mDay, mMonth, mYear))
                datesArray.add(tempDate);
            Log.d(TAG, "loadIntoListView: " + datesArray.toString());
        }
        FeedAdapter feedAdapter = new FeedAdapter(context, R.layout.adapter_available, datesArray,listView);
        listView.setAdapter(feedAdapter);
    }

    private boolean checkDate(DateListArray date, int day, int month, int year) {
        return (date.getYear() == year && date.getMonth() == month && date.getDay() == day);
    }
}
