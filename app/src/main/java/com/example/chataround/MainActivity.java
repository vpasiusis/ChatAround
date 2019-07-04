package com.example.chataround;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.storage.StorageReference;
import java.util.ArrayList;
import java.util.List;
import android.view.Menu;

public class MainActivity extends AppCompatActivity {

    private ListView listView;
    private long backPressedTime;
    private Toast backToast;
    private FirebaseController firebaseController;
    private List<ListViewItem> list;
    private ItemAdapter adapter;
    private int loadedItems = 0;
    private Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar mainToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        listView = findViewById(R.id.listview1);
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);

        firebaseController = FirebaseController.getInstance();
        firebaseController.initialize();
        setSupportActionBar(mainToolbar);
        list = new ArrayList<>();
        activity=MainActivity.this;
        firebaseController.getUsername();
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
                if(adapter.getCount()==loadedItems && firstVisibleItem+visibleItemCount==totalItemCount){
                    String oldestItemTime = adapter.getListViewItem(loadedItems-1).getTime();
                    Query query = firebaseController.getMyDatabase().
                            child("Messages").orderByKey().endAt(oldestItemTime).limitToLast(10);
                    loadItems(loadedItems, query);
                    loadedItems+=10;
                }
            }
        });
    }

    public void loadItems(final int start, Query query) {
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

                final ListViewItem item1 = new ListViewItem(key,username1,
                        null,message,imageId,time,commentCount, likeCount);

                if(imageId!=null){
                    getImage(item1,imageId);
                    list.add(start,item1);
                    adapter.notifyDataSetChanged();
                }else{
                    list.add(start,item1);
                    adapter.notifyDataSetChanged();
                }
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
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).getId().equals(dataSnapshot.getKey())) {
                        list.remove(i);
                        break;
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void getImage(final ListViewItem item, final String message){
        StorageReference ref = firebaseController.getMyStorage().child(message);
        final long megabyte = 1024*1024;
        item.setIsLoading(true);
        ref.getBytes(megabyte).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap image = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                item.setImage(image);
                item.setIsLoading(false);
                adapter.notifyDataSetChanged();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                getImage(item,message);
            }
        });
    }


    @Override
    public void onBackPressed() {
        if (backPressedTime + 1000 > System.currentTimeMillis() ) {
            backToast.cancel();
            FirebaseAuth.getInstance().signOut();
            LoginManager.getInstance().logOut();
            Intent i = new Intent(this, LoginActivity.class); //if under this dialog you do not have your MainActivity
            i.addFlags(i.FLAG_ACTIVITY_CLEAR_TOP);
            i.addFlags(i.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            finish();
            return;
        } else {
            backToast = Toast.makeText(getBaseContext(), "Press one more time to exit", Toast.LENGTH_SHORT);
            backToast.show();
        }
        backPressedTime = System.currentTimeMillis();
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
                            Intent intent1 = new Intent(activity, EventsActivity.class);
                            startActivity(intent1);
                            break;
                        case R.id.nav_settings:
                            Intent intent = new Intent(activity, SettingsActivity.class);
                            startActivity(intent);
                            break;
                    }
                    return true;
                }
            };

}
