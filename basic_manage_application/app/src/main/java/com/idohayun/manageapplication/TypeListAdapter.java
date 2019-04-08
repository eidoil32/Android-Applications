package com.idohayun.manageapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TypeListAdapter extends ArrayAdapter {
    private final Context context;
    private final int layoutResource;
    private final LayoutInflater layoutInflater;
    private ListView listView;
    private List<AppointmentTypes> listOfTypes;
    private RequestQueue queue;
    private JsonObjectRequest request;
    private Map<String, String> map = new HashMap<String, String>();

    public TypeListAdapter(Context context, int resource, ListView listView, List<AppointmentTypes> listOfTypes) {
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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;

        if (convertView == null) {
            convertView = layoutInflater.inflate(layoutResource, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final AppointmentTypes current = listOfTypes.get(position);
        viewHolder.textDescription.setText(current.getDescription());
        viewHolder.textLang.setText(context.getString(R.string.text_lang_prefix) + current.getLanguage());
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

    private void button_delete_appointment_type(AppointmentTypes current) {

    }

    private void button_edit_appointment_type(AppointmentTypes current) {

    }

    private class ViewHolder {
        final TextView textPrice, textLang, textDescription;
        final ImageView optionDel, optionEdit;


        ViewHolder(View v) {
            this.textPrice = (TextView) v.findViewById(R.id.text_price);
            this.textDescription = (TextView) v.findViewById(R.id.text_description);
            this.textLang = (TextView) v.findViewById(R.id.text_lang);
            this.optionDel = (ImageView) v.findViewById(R.id.image_btn_delete);
            this.optionEdit = (ImageView) v.findViewById(R.id.image_btn_edit);
        }
    }
}
