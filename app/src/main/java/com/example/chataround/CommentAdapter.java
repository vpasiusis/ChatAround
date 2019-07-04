package com.example.chataround;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class CommentAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<ListViewComment> commentList;

    public CommentAdapter(Activity activity, List<ListViewComment> commentList){
        this.commentList = commentList;
        this.activity = activity;
    }

    @Override
    public int getCount() {
        return commentList.size();
    }

    @Override
    public Object getItem(int i) {
        return commentList.get(i);
    }

    public ListViewComment getListViewComment(int i){ return commentList.get(i); }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        if (inflater == null){
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        if (view == null){
            view = inflater.inflate(R.layout.listview_comment, null);
        }
        TextView name = view.findViewById(R.id.commentName);
        TextView time = view.findViewById(R.id.commentTime);
        TextView message = view.findViewById(R.id.commentMessage);
        final ListViewComment comment = commentList.get(i);
        name.setText(comment.getName());
        time.setText(comment.getTime());

        // Check for empty message
        if (!TextUtils.isEmpty(comment.getMessage())) {
            message.setText(comment.getMessage());
            message.setVisibility(View.VISIBLE);
        } else {
            // message is empty, remove from view
            message.setVisibility(View.GONE);
        }
        return view;
    }
}
