package com.example.chataround;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

public class CommentsActivity extends AppCompatActivity {

    private ListViewItem item;
    private ListView listView;
    private List<ListViewComment> comments;
    private ArrayAdapter adapter;
    private TextView message,time,user;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comments_activity);

        Toolbar mainToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(mainToolbar);

        item = (ListViewItem) getIntent().getSerializableExtra("Item");
        listView = findViewById(R.id.listViewComments);
        message = findViewById(R.id.itemMessage1);
        time = findViewById(R.id.itemTime1);
        user = findViewById(R.id.itemName1);

        comments = item.getComments();
        adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1, comments);
        listView.setAdapter(adapter);

        message.setText(item.getMessage());
        time.setText(item.getTime());
        user.setText(item.getName());
    }

}
