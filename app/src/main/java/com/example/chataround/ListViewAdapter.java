package com.example.chataround;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import java.util.List;

public class ListViewAdapter extends BaseExpandableListAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<ListViewItem> itemList;

    public ListViewAdapter(Activity activity, List<ListViewItem> itemList){
        this.activity = activity;
        this.itemList = itemList;
    }

    @Override
    public int getGroupCount() {
        return itemList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {

        List<ListViewComment> comments = itemList.get(groupPosition).getComments();
        return comments.size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return itemList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        List<ListViewComment> comments = itemList.get(groupPosition).getComments();
        return comments.get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isLastChild, View view,
                             ViewGroup parent) {

        if (inflater == null){
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        if (view == null){
            view = inflater.inflate(R.layout.listview_item, null);
        }

        TextView name = view.findViewById(R.id.itemName);
        TextView time = view.findViewById(R.id.itemTime);
        TextView message = view.findViewById(R.id.itemMessage);
        ImageView image = view.findViewById(R.id.itemImage);

        ListViewItem item = itemList.get(groupPosition);

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

        ProgressBar progressBar = view.findViewById(R.id.progressBar);

        if(item.getIsLoading()){
            progressBar.setVisibility(View.VISIBLE);
        }else{
            progressBar.setVisibility(View.GONE);
        }

        // Check image
        if (item.getImage() != null) {
            image.setImageBitmap(item.getImage());
            image.setVisibility(View.VISIBLE);
        } else {
            image.setVisibility(View.GONE);
        }

        return view;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
                             View view, ViewGroup parent) {

        if (inflater == null){
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        if (view == null){
            view = inflater.inflate(R.layout.listview_comment, null);
        }

        ListViewComment comment = itemList.get(groupPosition).getComments().get(childPosition);

        TextView comment1 = view.findViewById(R.id.comment);
        comment1.setText(comment.getMessage());

        return view;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
