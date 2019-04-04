package com.idohayun.mybusiness;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import java.util.Calendar;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.CalendarContract;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CalendarEvent {
    private static ContentResolver contentResolver;
    private static ContentValues contentValues;
    private static StringBuilder sb = new StringBuilder();
    private static List<appointment> typeList;
    private static final String TAG = "CalendarEvent";
    private Context context;
    private int type, hourInMilli = 60*60*1000;
    private DateArray dateArray;
    private static Uri uri;
    private static boolean status_permission = false;

    public void exportEvent(Context context, int i_type_of_event, DateArray dateArray) {
        contentResolver = context.getContentResolver();
        contentValues = new ContentValues();
        this.context = context;
        this.type = i_type_of_event;
        this.dateArray = dateArray;
        exportEventWithCurrentType();
    }

    private void exportEventWithCurrentType() {
        class getData extends AsyncTask<Void,Void,String> {

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                try {
                    if(s != null) {
                        if (!s.isEmpty()) {
                            exportTheEvent(s);
                        }
                    } else {
                        Log.d(TAG, "onPostExecute: error getting information");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected String doInBackground(Void... voids) {
                try {
                    URL url = new URL(ServerURLSManager.Appointment_get_appointment_types);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String json;
                    while ((json = bufferedReader.readLine()) != null) {
                        sb.append(json + "\n");
                        Log.d(TAG, "doInBackground: " + json);
                    }
                    con.disconnect();
                    return sb.toString().trim();
                } catch (Exception e) {
                    Log.d(TAG, "doInBackground: exception " + e.getMessage());
                    return null;
                }
            }
        }

        getData getJSON = new getData();
        getJSON.execute();
    }

    private void exportTheEvent (String JSON_data) throws JSONException {
        JSONArray jsonArray = new JSONArray(JSON_data);
        String description, lang;
        int price;
        String deviceLocale = Locale.getDefault().getLanguage();
        typeList = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject obj = jsonArray.getJSONObject(i);
            description = obj.getString("Description");
            lang = obj.getString("Lang");
            price = obj.getInt("Price");
            if((deviceLocale.equals("iw") && lang.equals("Hebrew"))) {
                typeList.add(new appointment(description,lang,price));
            } else if (deviceLocale.equals("en") && lang.equals("English")) {
                typeList.add(new appointment(description, lang, price));
            } else if (!deviceLocale.equals("en") && !deviceLocale.equals("iw") && lang.equals("English")) {
                typeList.add(new appointment(description, lang, price)); //default language is english
            }
        }

        Calendar beginTime = Calendar.getInstance();
        beginTime.set(dateArray.getYear(), dateArray.getMonth() - 1, dateArray.getDay(), dateArray.getHour(), dateArray.getMin());
        String appointment_name = typeList.get(type).getDescription();
        Intent intent = new Intent(Intent.ACTION_EDIT);
        intent.setType("vnd.android.cursor.item/event");
        intent.putExtra(CalendarContract.Events.TITLE,appointment_name);
        intent.putExtra(CalendarContract.Events.DESCRIPTION,context.getString(R.string.event_desctiption_with_type,appointment_name));
        intent.putExtra(CalendarContract.Events.EVENT_LOCATION,context.getString(R.string.event_location));
        intent.putExtra("beginTime",beginTime.getTimeInMillis());
        intent.putExtra("endTime",beginTime.getTimeInMillis() + hourInMilli);
        intent.putExtra(CalendarContract.Reminders.EVENT_ID, CalendarContract.Events._ID);
        intent.putExtra(CalendarContract.Events.ALLOWED_REMINDERS, "METHOD_DEFAULT");
        intent.putExtra(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT);
        intent.putExtra(CalendarContract.Reminders.MINUTES,5);
        intent.putExtra(CalendarContract.Events.EVENT_TIMEZONE,Calendar.getInstance().getTimeZone().getID());
        ((Activity)context).startActivityForResult(intent,0);
    }
}
