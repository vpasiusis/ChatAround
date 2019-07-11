
package com.example.chataround;

import android.annotation.SuppressLint;
import android.app.Activity;
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

public class ProfileActivity extends AppCompatActivity {
    private Activity activity;
    private Button descriptionButton;
    private EditText descriptionText;
    private FirebaseController firebaseController;
    private TextView likeCount;
    private TextView postCount;
    private TextView userName;
    private Button myPostActivity;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.profile_activity);
        Toolbar settingsToolbar = (Toolbar) findViewById(R.id.posting_toolbar);
        settingsToolbar.setTitle("Profile");
        setSupportActionBar(settingsToolbar);
        firebaseController= FirebaseController.getInstance();
        activity = ProfileActivity.this;
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
        String username=firebaseController.returnUsername();
        System.out.println(firebaseController.returnUid(firebaseController.returnUsername()));
        descriptionText.setCursorVisible(false);
        firebaseController.getUserPostNumber(username);
        firebaseController.getUserLiked(username);
        firebaseController.setLikes();
        firebaseController.setPosts();
        likeCount.setText(firebaseController.getLiked()+"");
        postCount.setText(firebaseController.getPosts()+"");
        userName.setText(" "+username+" ");
        if(firebaseController.getDescription()!=null){
            descriptionText.setText(firebaseController.getDescription());
        }

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

