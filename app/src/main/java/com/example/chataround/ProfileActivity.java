
package com.example.chataround;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import static java.lang.String.format;

public class ProfileActivity extends AppCompatActivity {
    private Activity activity;
    private Button descriptionButton;
    private EditText descriptionText;
    private FirebaseController firebaseController;
    private TextView likeCount;
    private TextView postCount;
    private TextView userName;
    private Button myPostActivity;
    private ProgressDialog progressDialog;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.profile_activity);
        Toolbar settingsToolbar = (Toolbar) findViewById(R.id.posting_toolbar);
        settingsToolbar.setTitle("Profile");
        setSupportActionBar(settingsToolbar);
        firebaseController= FirebaseController.getInstance();
        activity = ProfileActivity.this;
        progressDialog = new ProgressDialog(activity);
        progressDialog.setTitle("Loading data...");
        progressDialog.show();
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);
        bottomNav.setSelectedItemId(R.id.nav_settings);
        descriptionText=findViewById(R.id.descriptionText);
        descriptionButton=findViewById(R.id.description_button);
        userName=findViewById(R.id.usernameProfile);
        likeCount=findViewById(R.id.gotLikes);
        postCount=findViewById(R.id.gotPosts);
        myPostActivity=findViewById(R.id.myPostButton);
        myPostActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity,MyPostsActivity.class);
                startActivity(intent);
            }
        });
        firebaseController.getUsername();
        userName.setText(firebaseController.returnUsername());
        descriptionText.setCursorVisible(false);
        loadData();

        descriptionButton.setVisibility(View.GONE);
        descriptionText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(s.toString().trim().length()==0){
                    descriptionButton.setVisibility(View.GONE);
                } else {
                    descriptionButton.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                descriptionButton.setVisibility(View.GONE);
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        descriptionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(descriptionText.getText().toString().trim()))
                firebaseController.setDescription(descriptionText.getText().toString().trim());
                descriptionText.setCursorVisible(false);
                descriptionText.clearFocus();
                descriptionButton.setVisibility(View.GONE);
                firebaseController.getDescription();
            }
        });
        descriptionText.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                descriptionText.setSelection(descriptionText.getText().toString().length());
                descriptionText.requestFocus();
                descriptionText.requestFocusFromTouch();
                descriptionText.setCursorVisible(true);
                return false;
            }
        });
        super.onCreate(savedInstanceState);

    }

    public void loadData() {
        firebaseController.getMyDatabase().child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    if(userName.getText().toString().trim().equals(snapshot.child("Username").getValue())){
                        final String key = snapshot.getKey();
                        firebaseController.getMyDatabase().child("users").child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                            final String username = dataSnapshot.child("Username").getValue(String.class);
                            final String avatarId = dataSnapshot.child("AvatarId").getValue(String.class);
                            final String email = dataSnapshot.child("Email").getValue(String.class);
                            final int posts = dataSnapshot.child("Posts").getValue(Integer.class);
                            final int likes = dataSnapshot.child("Likes").getValue(Integer.class);
                            final int type = dataSnapshot.child("Type").getValue(Integer.class);
                            final String decription = dataSnapshot.child("Description").getValue(String.class);
                            final UserClass item1 = new UserClass(key,username,
                                    avatarId,decription,email,posts,likes, type);
                            userName.setText(" "+username+" ");
                            if(decription!=null) {
                                descriptionText.setText(" " + item1.getDescription() + " ");
                            }
                            likeCount.setText(format("%d", item1.getLikes()));
                            postCount.setText(format("%d", item1.getPosts()));
                            descriptionButton.setVisibility(View.GONE);
                            if(avatarId==null) {
                                //put a default avatar
                            }
                            else {
                                //upload an image
                            }
                            progressDialog.dismiss();
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                }
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

        if (item.getItemId() == R.id.quit) {
            LogOffDialog logOffDialog = new LogOffDialog();
            logOffDialog.show(getSupportFragmentManager(), "Log Off");
        }
        return super.onOptionsItemSelected(item);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Activity selectedActivity = null;

                    switch (item.getItemId()) {
                        case R.id.nav_home:
                            Intent intent = new Intent(activity, MainActivity.class);
                            startActivity(intent);
                            break;
                        case R.id.nav_events:
                            Intent intent1 = new Intent(activity, EventsActivity.class);
                            startActivity(intent1);
                            break;
                        case R.id.nav_settings:
                            break;
                    }


                    return true;
                }
            };


}

