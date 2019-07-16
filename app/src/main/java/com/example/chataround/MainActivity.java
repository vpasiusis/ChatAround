package com.example.chataround;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    private ListView listView;
    private FirebaseController firebaseController;
    private List<ListViewItem> list;
    private ItemAdapter adapter;
    private int loadedItems = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar mainToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        mainToolbar.setTitle("Latest posts");
        listView = findViewById(R.id.listview1);
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        firebaseController = FirebaseController.getInstance();
        firebaseController.initialize();
        setSupportActionBar(mainToolbar);
        list = new ArrayList<>();
        adapter = new ItemAdapter(this, list);
        listView.setAdapter(adapter);
        bottomNav.setOnNavigationItemSelectedListener(navListener);
        Query query = firebaseController.getMyDatabase().
                child("Messages").orderByKey().limitToLast(10);
        loadItems(loadedItems, query);
        loadedItems+=10;
        updateFeed();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(MainActivity.this, CommentsActivity.class);
                firebaseController.setCurrentSelectedItem(adapter.getListViewItem(i));
                intent.putExtra("keyboard", false);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.topbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.postButton){
            Intent intent = new Intent(this, PostingActivity.class);
            startActivity(intent);
        }

        if(item.getItemId()==R.id.quit){
            LogOffDialog logOffDialog = new LogOffDialog();
            logOffDialog.show(getSupportFragmentManager(),"Log Off");

        }
        return super.onOptionsItemSelected(item);
    }

    public void updateFeed(){
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if(adapter.getCount()>=loadedItems && firstVisibleItem+visibleItemCount==totalItemCount){
                    String oldestItemTime = adapter.getListViewItem(loadedItems-1).getTime();
                    Query query = firebaseController.getMyDatabase().
                            child("Messages").orderByKey().endAt(oldestItemTime).limitToLast(10);
                    loadItems(loadedItems, query);
                    loadedItems+=10;
                }
            }
        });
    }

    public void loadItems(final int start, final Query query) {
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
                Query query1 = firebaseController.getMyDatabase().child("users");
                query1.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                            if(username1.equals(dataSnapshot1.child("Username").getValue())){
                                final String avatarId = dataSnapshot1.child("AvatarId").getValue(String.class);
                                final ListViewItem item1 = new ListViewItem(key,username1,
                                        null,message,imageId,time,commentCount, likeCount,avatarId);
                                list.add(start,item1);
                                adapter.notifyDataSetChanged();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onChildChanged(DataSnapshot dst, String s) {
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

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Activity selectedActivity = null;

                    switch (item.getItemId()) {
                        case R.id.nav_home:
                            break;
                        case R.id.nav_events:
                            Intent intent1 = new Intent(MainActivity.this, EventsActivity.class);
                            startActivity(intent1);
                            break;
                        case R.id.nav_settings:
                            firebaseController.updateCurrentUser(true,MainActivity.this);
                            break;
                    }
                    return true;
                }
            };

    @Override
    public void onBackPressed() {
        if(ImageController.isFullscreen) ImageController.zoomImageOut(this);
    }
}
