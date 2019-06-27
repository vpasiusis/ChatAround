package com.example.chataround;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.List;

public class CommentsActivity extends AppCompatActivity {

    private ListViewItem item;
    private ListView listView;
    private List<ListViewComment> comments;
    private CommentAdapter adapter;
    private TextView message,time,user;
    private EditText editMessage;
    private FirebaseController firebaseController;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comments_activity);

        Toolbar mainToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(mainToolbar);
        firebaseController = FirebaseController.getInstance();

        item = (ListViewItem) getIntent().getSerializableExtra("Item");
        listView = findViewById(R.id.listViewComments);
        message = findViewById(R.id.itemMessage1);
        time = findViewById(R.id.itemTime1);
        user = findViewById(R.id.itemName1);
        editMessage = findViewById(R.id.enterTextid);

        comments = new ArrayList<>();
        adapter = new CommentAdapter(CommentsActivity.this,comments);
        listView.setAdapter(adapter);

        message.setText(item.getMessage());
        time.setText(item.getTime());
        user.setText(item.getName());

    }

    public void sendComment(View view) {

    }
}
