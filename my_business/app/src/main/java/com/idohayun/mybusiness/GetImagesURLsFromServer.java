package com.idohayun.mybusiness;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class GetImagesURLsFromServer {
    private static final String TAG = "GetImagesURLsFromServer";
    private static String dataURL = "http://eidoil32.myhf.in/getImages.php",numURL = "http://eidoil32.myhf.in/getImages.php";
    private List<ImageURL> imageURLList;
    private static StringBuilder sb = new StringBuilder();
    private Context context;
    private GridView gridView;
    private static int NUMOFIMAGES;



    public void CreateImageGridView(Context context, GridView gridView) {

        this.context = context;
        this.gridView = gridView;

        GetJSON_data();
    }


    private void GetJSON_data() {
        class getJSON_data extends AsyncTask<Void, Void, String> {

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                try {
                    if(s != null) {
                        if (!s.isEmpty()) {
                            loadIntoGridView(s);
                        } else {
                            Log.d(TAG, "onPostExecute: s is empty");
                            CustomToast.showToast(context,context.getString(R.string.error_download_images),0);
                        }
                    } else {
                        Log.d(TAG, "onPostExecute: s is null");
                        CustomToast.showToast(context,context.getString(R.string.error_download_images),0);
                    }
                } catch (JSONException e) {
                    Log.d(TAG, "onPostExecute: error in JSON exception " + e.getMessage());
                    e.printStackTrace();
                }
            }

            @Override
            protected String doInBackground(Void... voids) {
                try {
                    URL url = new URL(dataURL);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String json;
                    while ((json = bufferedReader.readLine()) != null) {
                        sb.append(json + "\n");
                    }
                    con.disconnect();
                    return sb.toString().trim();
                } catch (Exception e) {
                    Log.d(TAG, "doInBackground: exception error! " + e.toString());
                    return null;
                }
            }
        }

        getJSON_data getJSON = new getJSON_data();
        getJSON.execute();
    }

    private void loadIntoGridView(String JSON_Data) throws JSONException {
            JSONArray jsonArray = new JSONArray(JSON_Data);
            imageURLList = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                imageURLList.add(new ImageURL(
                        obj.getString("pathToFile"),
                        obj.getInt("ID"),
                        obj.getInt("Width"),
                        obj.getInt("Height")));
                Log.d(TAG, "loadIntoListView: " + imageURLList.get(i).toString());
            }

            ImageAdapter imageGridAdapter = new ImageAdapter(context,imageURLList);
            gridView.setAdapter(imageGridAdapter);
    }
}
