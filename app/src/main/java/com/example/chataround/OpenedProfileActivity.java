package com.example.chataround;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import static java.lang.String.format;

public class OpenedProfileActivity extends AppCompatActivity {
    private Activity activity;
    private Button descriptionButton;
    private TextView descriptionText;
    private FirebaseController firebaseController;
    private TextView likeCount;
    private TextView postCount;
    private TextView userName;
    private Button myPostActivity;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.opened_profile_activity);
        Toolbar settingsToolbar = (Toolbar) findViewById(R.id.posting_toolbar);
        settingsToolbar.setTitle("Profile");
        setSupportActionBar(settingsToolbar);
        firebaseController= FirebaseController.getInstance();
        activity = OpenedProfileActivity.this;
        progressDialog = new ProgressDialog(activity);
        progressDialog.setTitle("Loading data...");
        progressDialog.show();
        Bundle extras = getIntent().getExtras();
        String userData="";
        if (extras != null) {
            userData = extras.getString("username");

        }
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);
        //bottomNav.setSelectedItemId(R.id.nav_settings);
        descriptionText=findViewById(R.id.descriptionText);
        userName=findViewById(R.id.usernameProfile);
        likeCount=findViewById(R.id.gotLikes);
        postCount=findViewById(R.id.gotPosts);
        myPostActivity=findViewById(R.id.userPostButton);
        userName.setText(userData);
        descriptionText.setCursorVisible(false);
        loadData();
        myPostActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity,MyPostsActivity.class);
                intent.putExtra("username",userName.getText().toString().trim());
                startActivity(intent);
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
                                if(type==99){
                                    userName.setBackgroundColor(getResources().getColor(R.color.admin));
                                }

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
                            Intent intent2 = new Intent(activity, ProfileActivity.class);
                            startActivity(intent2);

                            break;
                    }


                    return true;
                }
            };

}

