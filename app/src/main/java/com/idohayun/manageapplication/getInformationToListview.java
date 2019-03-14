package com.idohayun.manageapplication;

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

public class getInformationToListview {
    private static final String TAG = "getInformation";
    private String databaseURL;
    private ListView listView;
    private int mDay, mMonth, mYear;
    private Context context;

    public getInformationToListview(String databaseURL, ListView listView, int day, int month, int year, Context context) {
        this.databaseURL = databaseURL;
        this.listView = listView;
        this.mDay = day;
        this.mMonth = month;
        this.mYear = year;
        this.context = context;
    }

    public void getJSON() {
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
                    //creating a URL
                    java.net.URL url = new URL(databaseURL);

                    //Opening the URL using HttpURLConnection
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();

                    //StringBuilder object to read the string from the service
                    StringBuilder sb = new StringBuilder();

                    //We will use a buffered reader to read the string from service
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));

                    //A simple string to read values from each line
                    String json;

                    //reading until we don't find null
                    while ((json = bufferedReader.readLine()) != null) {

                        //appending it to string builder
                        sb.append(json + "\n");
                    }

                    //finally returning the read string
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
        if (date.getYear() == year && date.getMonth() == month && date.getDay() == day)
            return true;
        return false;
    }
}
