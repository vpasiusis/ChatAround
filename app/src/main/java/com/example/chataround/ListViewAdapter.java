package com.example.chataround;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class ListViewAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<ListViewItem> itemList;

    public ListViewAdapter(Activity activity, List<ListViewItem> itemList){
        this.activity = activity;
        this.itemList = itemList;
    }

    @Override
    public int getCount() {
        return itemList.size();
    }

    @Override
    public Object getItem(int location) {
        return itemList.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (inflater == null){
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        if (convertView == null){
            convertView = inflater.inflate(R.layout.listview_item, null);
        }

        TextView name = convertView.findViewById(R.id.itemName);
        TextView time = convertView.findViewById(R.id.itemTime);
        TextView message = convertView.findViewById(R.id.itemMessage);
        ImageView image = convertView.findViewById(R.id.itemImage);

        ListViewItem item = itemList.get(position);

        name.setText(item.getName());
        time.setText(item.getTime());

        // Check for empty message
        if (!TextUtils.isEmpty(item.getMessage())) {
            message.setText(item.getMessage());
            message.setVisibility(View.VISIBLE);
        } else {
            // message is empty, remove from view
            message.setVisibility(View.GONE);
        }

        // Check image
        if (item.getImage() != null) {
            image.setImageBitmap(item.getImage());
            image.setVisibility(View.VISIBLE);
        } else {
            image.setVisibility(View.GONE);
        }

        return convertView;
    }
}
