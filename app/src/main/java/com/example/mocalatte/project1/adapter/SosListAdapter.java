package com.example.mocalatte.project1.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.mocalatte.project1.R;
import com.example.mocalatte.project1.item.ContactItem;
import com.example.mocalatte.project1.item.SosItem;

import java.util.List;

public class SosListAdapter  extends BaseAdapter {
    private Context context;
    private List<SosItem> lstItem;

    public SosListAdapter(Context context, List<SosItem> lstItem) {
        this.context = context;
        this.lstItem = lstItem;
    }

    @Override
    public int getCount() {
        return lstItem.size();
    }

    @Override
    public Object getItem(int position) {
        return lstItem.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.sos_view, null);
        }
        TextView name = (TextView) convertView.findViewById(R.id.tv_sos_name);
        TextView num = (TextView) convertView.findViewById(R.id.tv_sos_num);
        SosItem item = lstItem.get(position);
        name.setText(item.getSosName());
        num.setText("(" + item.getSosNum() + ")");
        return convertView;
    }
}