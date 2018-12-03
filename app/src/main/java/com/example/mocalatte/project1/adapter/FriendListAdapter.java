package com.example.mocalatte.project1.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = View.inflate(context, R.layout.item_sliding_menu, null);
        }
        TextView name = (TextView)convertView.findViewById(R.id.friend_name);
        TextView contact = (TextView)convertView.findViewById(R.id.friend_contactnum);
        FriendListMenu item = lstItem.get(position);
        name.setText(item.getName());
        contact.setText(item.getContactnum());

        ImageButton friendDeleteBtn = (ImageButton)convertView.findViewById(R.id.friendDeleteBtn);
        friendDeleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(context)
                        .setMessage("연락처를 정말 삭제하시겠습니까?")
                        .setPositiveButton("확인",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        DBManager dbManager = new DBManager(context);
                                        SQLiteDatabase db = dbManager.getWritableDatabase();
                                        db.execSQL("DELETE FROM " + dbManager.ContactTB + " WHERE " + "phone" + "= '" + lstItem.get(position).getContactnum() + "'");
                                        //Close the database
                                        db.close();

                                        lstItem.remove(position);
                                        notifyDataSetChanged();

                                        dialog.dismiss();
                                    }
                                })
                        .setNegativeButton("취소",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                }).show();
            }
        });

        return convertView;
    }
}
