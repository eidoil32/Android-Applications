package com.idohayun.manageapplication;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class getInformationFromSQL {
    private static final String TAG = "getInformationFromSQL";
    private static int ID;

    public static int getLastID() {
        GetLastID getLastId = new GetLastID();
        getLastId.execute();
        return ID;
    }

    public static class GetLastID extends AsyncTask <Void, Void, String> {

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                if (!s.isEmpty())
                {
                    JSONArray jsonArray = new JSONArray(s);
                    JSONObject obj = jsonArray.getJSONObject(0);
                    ID = obj.getInt("PersonID");
                    Log.d(TAG, "onPostExecute: id=" + ID);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(Void... voids) {
            String serverUrl = ServerURLManager.Date_get_last_id;
            try {
                java.net.URL url = new URL(serverUrl);
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
}
