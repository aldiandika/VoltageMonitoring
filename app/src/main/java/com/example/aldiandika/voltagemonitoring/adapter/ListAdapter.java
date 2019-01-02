package com.example.aldiandika.voltagemonitoring.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.aldiandika.voltagemonitoring.R;
import com.example.aldiandika.voltagemonitoring.data.monitor;

import java.util.ArrayList;

public class ListAdapter extends BaseAdapter {

    private Activity activity;
    private ArrayList<monitor> list_data;
    private  static LayoutInflater inflater = null;

    public ListAdapter(Activity act, ArrayList<monitor> dat){
        activity = act;
        list_data = dat;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        return list_data.size();
    }

    @Override
    public Object getItem(int i) {
        return list_data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View v = view;

        if(view == null){
            v = inflater.inflate(R.layout.listdata,null);

            TextView txt_listid = (TextView) v.findViewById(R.id.txt_listid);
            TextView txt_listv1 = (TextView) v.findViewById(R.id.txt_listv1);
            TextView txt_listv2 = (TextView) v.findViewById(R.id.txt_listv2);
            TextView txt_listv3 = (TextView) v.findViewById(R.id.txt_listv3);
            TextView txt_listv4 = (TextView) v.findViewById(R.id.txt_listv4);
            TextView txt_listarus = (TextView) v.findViewById(R.id.txt_listarus);
            TextView txt_listdaya = (TextView) v.findViewById(R.id.txt_listdaya);
            TextView txt_listTgl = (TextView) v.findViewById(R.id.txt_listTgl);

            monitor data = list_data.get(i);
            txt_listid.setText(data.getId());
            txt_listv1.setText(data.getV_satu());
            txt_listv2.setText(data.getV_dua());
            txt_listv3.setText(data.getV_tiga());
            txt_listv4.setText(data.getV_empat());
            txt_listarus.setText(data.getArus());
            txt_listdaya.setText(data.getDaya());
            txt_listTgl.setText(data.getTgl());
        }
        return v;
    }
}
