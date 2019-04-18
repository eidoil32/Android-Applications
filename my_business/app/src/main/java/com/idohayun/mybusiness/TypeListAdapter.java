package com.idohayun.mybusiness;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Configuration;
import android.support.constraint.Guideline;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class TypeListAdapter extends ArrayAdapter {
    private static final String TAG = "TypeListAdapter";
    private final Context context;
    private final int layoutResource;
    private final LayoutInflater layoutInflater;
    private ListView listView;
    private List<Treatment> listOfTypes;
    private RequestQueue queue;
    private JsonObjectRequest request;
    private Map<String, String> map = new HashMap<>();

    TypeListAdapter(Context context, int resource, ListView listView, List<Treatment> listOfTypes) {
        super(context, resource);
        this.context = context;
        this.layoutResource = resource;
        this.layoutInflater = LayoutInflater.from(context);
        this.listView = listView;
        this.listOfTypes = listOfTypes;
    }

    @Override
    public int getCount() {
        return listOfTypes.size();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;

        if (convertView == null) {
            convertView = layoutInflater.inflate(layoutResource, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final Treatment current = listOfTypes.get(position);
        viewHolder.textDescription_hebrew.setText(current.getDescription_Hebrew());
        viewHolder.textDescription_english.setText(current.getDescription_English());
        viewHolder.textPrice.setText(context.getString(R.string.text_price_prefix) + current.getPrice());

        viewHolder.optionDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button_delete_appointment_type(current);
                notifyDataSetChanged();
            }
        });

        viewHolder.optionEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button_edit_appointment_type(current);
                notifyDataSetChanged();
            }
        });

        return convertView;
    }

    private void button_edit_appointment_type(final Treatment current) {
        final Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.popup_edit_treatment);


        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity)getContext()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int displayWidth = displayMetrics.widthPixels;
        int displayHeight = displayMetrics.heightPixels;
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(Objects.requireNonNull(dialog.getWindow()).getAttributes());
        int orientation = getContext().getResources().getConfiguration().orientation;
        float multiple_Width = 0, multiple_Height = 0;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            Log.d(TAG, "Portrait");
            multiple_Width = 0.8f;
            multiple_Height = 0.5f;
        }
        else if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Log.d(TAG, "Landscape");
            multiple_Width = 0.7f;
            multiple_Height = 0.8f;
        }
        int dialogWindowWidth = (int)(displayWidth * multiple_Width);
        int dialogWindowHeight = (int)(displayHeight * multiple_Height);

        Guideline leftGuide = dialog.findViewById(R.id.guideline_left),
                rightGuide = dialog.findViewById(R.id.guideline_right),
                bottomGuide = dialog.findViewById(R.id.bottom_guideline);
        leftGuide.setGuidelineBegin(0);
        rightGuide.setGuidelineBegin(dialogWindowWidth - (int)(30*displayMetrics.density));
        bottomGuide.setGuidelineBegin(dialogWindowHeight - (int)(30*displayMetrics.density));

        layoutParams.width = dialogWindowWidth;
        layoutParams.height = dialogWindowHeight;

        Button btnOK = dialog.findViewById(R.id.btn_save_treatment);
        Button btnCancel = dialog.findViewById(R.id.btn_cancel);

        btnOK.setEnabled(true);
        btnCancel.setEnabled(true);

        final EditText hebrewDescription = dialog.findViewById(R.id.edit_text_lang_hebrew);
        final EditText englishDescription = dialog.findViewById(R.id.edit_text_lang_english);
        final EditText price = dialog.findViewById(R.id.edit_price);

        TextView title = dialog.findViewById(R.id.text_title_popup_treatment);
        title.setText(getContext().getString(R.string.title_edit_treatment_type));

        hebrewDescription.setText(current.getDescription_Hebrew());
        englishDescription.setText(current.getDescription_English());
        price.setText(current.getPrice());

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String hebrewInput, englishInput, priceInput;
                hebrewInput = hebrewDescription.getText().toString();
                englishInput = englishDescription.getText().toString();
                priceInput = price.getText().toString();
                if(hebrewInput.isEmpty() || englishInput.isEmpty() || priceInput.isEmpty()) {
                    Log.d(TAG, "onClick: error");
                } else { // every editText is filled
                    hebrewInput = checkHebrewDescription(hebrewInput);
                    Map<String, String> editedMap = new HashMap<>();
                    editedMap.put("TYPE", "UPDATE");
                    editedMap.put("ID", Integer.toString(current.getId()));
                    editedMap.put("Lang", current.getLanguage());
                    editedMap.put("Description_Hebrew", hebrewInput);
                    editedMap.put("Description_English", englishInput);
                    editedMap.put("Price", priceInput);
                    queue = Volley.newRequestQueue(getContext());
                    Log.d(TAG, "onClick: Catch button clicked!");
                    final JSONObject jsonObject = new JSONObject(editedMap);
                    request = new JsonObjectRequest(
                            Request.Method.POST, // the request method
                            ServerURLSManager.Appointment_update_appointment_type, jsonObject,
                            new Response.Listener<JSONObject>() { // the response listener
                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                        if (response.getString("status").equals("true")) {
                                            Toast.makeText(getContext(), getContext().getString(R.string.treatment_edited_successfully), Toast.LENGTH_SHORT).show();
                                            Log.d(TAG, "onResponse: " + response.getString("data"));
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            },
                            new Response.ErrorListener() { // the error listener
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Toast.makeText(getContext(), "Oops! Got error from server!", Toast.LENGTH_SHORT).show();
                                }
                            });

                    queue.add(request);
                    dialog.cancel();
                    ManageTreatmentsTypes.GetInformation getInformation = new ManageTreatmentsTypes.GetInformation();
                    getInformation.execute();
                }
            }
        });
        dialog.show();
        dialog.getWindow().setAttributes(layoutParams);
    }

    static String checkHebrewDescription(String hebrewString) {
        StringBuilder stringBuilder = new StringBuilder(hebrewString);

        if(hebrewString.contains("'")) {
            int length = hebrewString.length() + 1;
            stringBuilder = new StringBuilder();
            for (int i = 0; i < length; i++) {
                if(hebrewString.charAt(i) == '\'') {
                    stringBuilder.append("''");
                    length -= 1;
                } else {
                    stringBuilder.append(hebrewString.charAt(i));
                }
            }
        }

        return stringBuilder.toString();
    }

    private void button_delete_appointment_type(final Treatment current) {
        final Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.popup_simple_yes_or_no);


        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity)getContext()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int displayWidth = displayMetrics.widthPixels;
        int displayHeight = displayMetrics.heightPixels;
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(Objects.requireNonNull(dialog.getWindow()).getAttributes());
        int orientation = getContext().getResources().getConfiguration().orientation;
        float multiple_Width = 0.7f, multiple_Height = 0.3f;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            Log.d(TAG, "Portrait");
            multiple_Width = 0.8f;
            multiple_Height = 0.3f;
        }
        else if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Log.d(TAG, "Landscape");
            multiple_Width = 0.8f;
            multiple_Height = 0.6f;
        }
        int dialogWindowWidth = (int)(displayWidth * multiple_Width);
        int dialogWindowHeight = (int)(displayHeight * multiple_Height);
        layoutParams.width = dialogWindowWidth;
        layoutParams.height = dialogWindowHeight;

        Guideline guideline_left, guideline_right, guideline_bottom;
        guideline_left = dialog.findViewById(R.id.popup_guideline_left);
        guideline_bottom = dialog.findViewById(R.id.popup_guideline_bottom);
        guideline_right = dialog.findViewById(R.id.popup_guideline_right);

        guideline_left.setGuidelineBegin(0);
        guideline_right.setGuidelineBegin(dialogWindowWidth - (int) (30 * displayMetrics.density));
        guideline_bottom.setGuidelineBegin(dialogWindowHeight - (int) (30 * displayMetrics.density));

        Button btnOK = dialog.findViewById(R.id.btn_popup_yes);
        Button btnNo = dialog.findViewById(R.id.btn_popup_no);

        btnOK.setEnabled(true);
        btnNo.setEnabled(true);

        final TextView descriptionOfWarning = dialog.findViewById(R.id.popup_alert_text);

        descriptionOfWarning.setText(getContext().getString(R.string.warning_delete_treatment_types));

        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // every editText is filled
                Map<String, String> editedMap = new HashMap<>();
                editedMap.put("TYPE", "DELETE");
                editedMap.put("ID", Integer.toString(current.getId()));
                editedMap.put("Lang", current.getLanguage());
                editedMap.put("Description_Hebrew", current.getDescription_Hebrew());
                editedMap.put("Description_English", current.getDescription_English());
                editedMap.put("Price", current.getPrice());
                queue = Volley.newRequestQueue(getContext());
                Log.d(TAG, "onClick: 'delete' button clicked!");
                final JSONObject jsonObject = new JSONObject(editedMap);
                request = new JsonObjectRequest(
                        Request.Method.POST, // the request method
                        ServerURLSManager.Appointment_update_appointment_type, jsonObject,
                        new Response.Listener<JSONObject>() { // the response listener
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    if (response.getString("status").equals("true")) {
                                        CustomToast.showToast(getContext(), getContext().getString(R.string.treatment_deleted_successfully), 1);
                                        Log.d(TAG, "onResponse: " + response.getString("data"));
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        },
                        new Response.ErrorListener() { // the error listener
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(getContext(), "Oops! Got error from server!", Toast.LENGTH_SHORT).show();
                            }
                        });

                queue.add(request);
                dialog.cancel();
                ManageTreatmentsTypes.GetInformation getInformation = new ManageTreatmentsTypes.GetInformation();
                getInformation.execute();
            }
        });
        dialog.show();
        dialog.getWindow().setAttributes(layoutParams);
    }

    private class ViewHolder {
        final TextView textPrice, textDescription_english, textDescription_hebrew;
        final ImageView optionDel, optionEdit;


        ViewHolder(View v) {
            this.textPrice = v.findViewById(R.id.text_price);
            this.textDescription_english = v.findViewById(R.id.text_description_english);
            this.textDescription_hebrew = v.findViewById(R.id.text_description_hebrew);
            this.optionDel = v.findViewById(R.id.image_btn_delete);
            this.optionEdit = v.findViewById(R.id.image_btn_edit);
        }
    }
}
