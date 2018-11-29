package com.example.mocalatte.project1.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mocalatte.project1.R;
import com.example.mocalatte.project1.item.FriendListMenu;

import java.util.List;

public class FriendListAdapter extends BaseAdapter {
    private Context context;
    private List<FriendListMenu> lstItem;

    public FriendListAdapter(Context context, List<FriendListMenu> lstItem) {
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
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = View.inflate(context, R.layout.item_sliding_menu, null);
        }
        TextView name = (TextView)convertView.findViewById(R.id.friend_name);
        TextView contact = (TextView)convertView.findViewById(R.id.friend_contactnum);
        FriendListMenu item = lstItem.get(position);
        name.setText(item.getName());
        contact.setText(item.getContactnum());

        return convertView;
    }
}
