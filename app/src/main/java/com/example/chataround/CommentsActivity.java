package com.example.chataround;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
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
        mainToolbar.setTitle("Post");
        setSupportActionBar(mainToolbar);
        firebaseController = FirebaseController.getInstance();

        item = firebaseController.getCurrentSelectedItem();
        listView = findViewById(R.id.listViewComments);
        message = findViewById(R.id.itemMessage1);
        time = findViewById(R.id.itemTime1);
        user = findViewById(R.id.itemName1);
        editMessage = findViewById(R.id.enterTextid);

        comments = new ArrayList<>();
        adapter = new CommentAdapter(CommentsActivity.this,comments);
        listView.setAdapter(adapter);

        message.setText(item.getMessage());
        String realtime = firebaseController.diffTime(item.getTime());
        time.setText(realtime);
        user.setText(item.getName());

        boolean keyboard = getIntent().getExtras().getBoolean("keyboard");
        if(keyboard){
            editMessage.requestFocus();
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            Dialog d = builder.create();
            d.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        }

        loadComments();
    }

    public void loadComments(){
        Query query = firebaseController.getMyDatabase().child("Comments").orderByKey();
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dst, String s) {
                final String postId = dst.child("postId").getValue(String.class);
                if(postId.equals(item.getId())){
                    final String key = dst.getKey();
                    final String message = dst.child("message").getValue(String.class);
                    final String username = dst.child("username").getValue(String.class);
                    final String time = dst.child("time").getValue(String.class);
                    final String realTime = firebaseController.diffTime(time);
                    ListViewComment comment = new ListViewComment(key,username,message,realTime);
                    comments.add(comment);
                    adapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void sendComment(View view) {
        String text = editMessage.getText().toString().trim();
        if(!TextUtils.isEmpty(text)) {
            firebaseController.sendComment(text,item.getId(), item.getComments());
            editMessage.setText("");
        }else{
            //check
            Toast.makeText(CommentsActivity.this, "Empty message", Toast.LENGTH_SHORT).show();
        }

    }
}
