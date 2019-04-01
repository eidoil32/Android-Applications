package com.idohayun.mybusiness;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class SpinnerAdapter extends ArrayAdapter<appointment> {
    private final Context context;
    private final LayoutInflater layoutInflater;
    private final List<appointment> list;
    private final int resources;

    public SpinnerAdapter(Context context, int resource, List<appointment> objects) {
        super(context, resource, objects);

        this.context = context;
        this.resources = resource;
        this.layoutInflater = LayoutInflater.from(context);
        this.list = objects;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return createItemView(position,convertView,parent);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return createItemView(position,convertView,parent);
    }

    private View createItemView(int position, View convertView, ViewGroup parent){
        final View view = layoutInflater.inflate(resources, parent, false);

        TextView description = (TextView) view.findViewById(R.id.spinner_adapter_description),
                price = (TextView) view.findViewById(R.id.spinner_adapter_price);

        description.setText(list.get(position).getDescription());
        if(list.get(position).getPrice() != 0)
            price.setText(Integer.toString(list.get(position).getPrice()));

        return view;
    }
}
