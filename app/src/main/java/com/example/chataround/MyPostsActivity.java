package com.example.chataround;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MyPostsActivity extends AppCompatActivity {

    private Activity activity;
    private ListView listView;
    private FirebaseController firebaseController;
    private List<ListViewItem> list;
    private ItemAdapter adapter;
    private int loadedItems = 0, userItems=0, itemsToLoad=10;
    private Button LikeButton;
    private UserClass user;
    private String latestItemTime=null, avatarId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
        activity=MyPostsActivity.this;

        listView = findViewById(R.id.listview1);
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);
        //bottomNav.setSelectedItemId(R.id.nav_settings);
        firebaseController = FirebaseController.getInstance();
        Toolbar mainToolbar = (Toolbar) findViewById(R.id.main_toolbar);

        final boolean isCurrentUser = getIntent().getExtras().getBoolean("currentUser");
        if (!isCurrentUser) {
            user = firebaseController.getClickedUser();
            mainToolbar.setTitle("User posts");

        }else {
            user = firebaseController.getCurrentUser();
            mainToolbar.setTitle("My posts");
            setSupportActionBar(mainToolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        avatarId = user.getAvatarId();
        setSupportActionBar(mainToolbar);
        list = new ArrayList<>();
        adapter = new ItemAdapter(this, list);
        listView.setAdapter(adapter);

        Query query = firebaseController.getMyDatabase().
                child("Messages").orderByKey().limitToLast(10);
        loadItems(query);
        updateFeed();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(MyPostsActivity.this, CommentsActivity.class);
                firebaseController.setCurrentSelectedItem(adapter.getListViewItem(i));
                intent.putExtra("keyboard", false);
                startActivity(intent);
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

        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        if(item.getItemId()==R.id.quit){
            LogOffDialog logOffDialog = new LogOffDialog();
            logOffDialog.show(getSupportFragmentManager(),"Log Off");

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
                            firebaseController.updateCurrentUser(false,true,MyPostsActivity.this);
                            break;
                    }
                    return true;
                }
            };

    public void updateFeed(){
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if(totalItemCount!=0 && loadedItems==itemsToLoad && firstVisibleItem+visibleItemCount==totalItemCount){
                    itemsToLoad+=10;
                    Query query = firebaseController.getMyDatabase().
                            child("Messages").orderByKey().endAt(latestItemTime).limitToLast(10);
                    loadItems(query);
                }
            }
        });
    }

    public void loadItems(Query query) {
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dst, String s) {
                final String key = dst.getKey();
                final String message = dst.child("message").getValue(String.class);
                final String imageId = dst.child("imageId").getValue(String.class);
                final String username1 = dst.child("username").getValue(String.class);
                final String time = dst.child("time").getValue(String.class);
                final int commentCount = dst.child("comments").getValue(Integer.class);
                final int likeCount = dst.child("likes").getValue(Integer.class);

                if(user.getName().equals(username1)) {
                    int i=0;
                    while(adapter.getCount()>i &&
                            Long.parseLong(time)<Long.parseLong(adapter.getListViewItem(i).getTime())){
                        i++;
                    }
                    final ListViewItem item1 = new ListViewItem(key,username1,
                            null,message,imageId,time,commentCount, likeCount,avatarId);
                    list.add(i, item1);
                    adapter.notifyDataSetChanged();
                    userItems++;
                }
                if(loadedItems==(itemsToLoad-10)) latestItemTime = time;
                if(loadedItems==itemsToLoad){
                    latestItemTime=time;
                    loadedItems--;
                }
                loadedItems++;
                if(loadedItems==itemsToLoad&&userItems==0){
                    Query query = firebaseController.getMyDatabase().
                            child("Messages").orderByKey().endAt(latestItemTime).limitToLast(10);
                    loadItems(query);
                }

            }

            @Override
            public void onChildChanged(DataSnapshot dst, String s) {
                final String username = dst.child("username").getValue(String.class);
                if(username == user.getName()){
                    final String key = dst.getKey();
                    final int commentCount = dst.child("comments").getValue(Integer.class);
                    final int likeCount = dst.child("likes").getValue(Integer.class);
                    for(int i = 0; i< list.size();i++){
                        if(list.get(i).getId().equals(key)){
                            ListViewItem item = list.get(i);
                            item.setComments(commentCount);
                            item.setLikes(likeCount);
                            adapter.notifyDataSetChanged();
                        }
                    }
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                for(int i = 0; i< list.size();i++){
                    if(list.get(i).getId().equals(dataSnapshot.getKey())){
                        ListViewItem item = list.get(i);
                        list.remove(item);
                        adapter.notifyDataSetChanged();
                        if(item.getName()==user.getName())userItems--;
                    }
                }
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
    public void onBackPressed() {
        if(ImageController.isFullscreen) ImageController.zoomImageOut(this);
        else super.onBackPressed();
    }

}
