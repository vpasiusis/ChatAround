package com.example.chataround;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
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
    private Activity activity;
    private TextView message,time,user,commentNumber;
    private EditText editMessage;
    private FirebaseController firebaseController;
    private int commentCounter=0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comments_activity);

        Toolbar mainToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        mainToolbar.setTitle("Post");
        setSupportActionBar(mainToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setSupportActionBar(mainToolbar);
        firebaseController = FirebaseController.getInstance();
        activity=CommentsActivity.this;
        item = firebaseController.getCurrentSelectedItem();
        listView = findViewById(R.id.listViewComments);
        message = findViewById(R.id.itemMessage1);
        time = findViewById(R.id.itemTime1);
        user = findViewById(R.id.itemName1);
        editMessage = findViewById(R.id.enterTextid);
        commentNumber = findViewById(R.id.commentNumber);

        comments = new ArrayList<>();
        adapter = new CommentAdapter(CommentsActivity.this,comments);
        listView.setAdapter(adapter);

        message.setText(item.getMessage());
        String realtime = firebaseController.diffTime(item.getTime());
        time.setText(realtime);
        user.setText(item.getName());

        user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!item.getName().equals(firebaseController.getCurrentUser().getName())) {
                    firebaseController.openClickedUser(item.getName(),activity);
                }
                else {
                    firebaseController.updateCurrentUser(true,activity);
                }


            }
        });

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
                    commentCounter++;
                    if(commentCounter==1)commentNumber.setText("Showing 1 comment");
                    else commentNumber.setText(String.format("Showing %d comments", commentCounter));
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.topbar_posting, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        if (item.getItemId() == R.id.quit) {
            LogOffDialog logOffDialog = new LogOffDialog();
            logOffDialog.show(getSupportFragmentManager(), "Log Off");
        }
        return super.onOptionsItemSelected(item);
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
