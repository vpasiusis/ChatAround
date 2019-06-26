package com.example.chataround;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.storage.StorageReference;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import android.view.Menu;

public class MainActivity extends AppCompatActivity {

    private EditText editText;
    private ExpandableListView listView;
    private long backPressedTime;
    private Toast backToast;
    private FirebaseController firebaseController;
    private List<ListViewItem> list;
    private ListViewAdapter adapter;
    private int loadedItems = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar mainToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(mainToolbar);
        firebaseController = FirebaseController.getInstance();
        firebaseController.initialize();
        listView = findViewById(R.id.listview1);
        editText = findViewById(R.id.enterTextid);
        list = new ArrayList<>();
        adapter = new ListViewAdapter(this, list);
        listView.setAdapter(adapter);

        Query query = firebaseController.getMyDatabase().orderByKey().limitToLast(10);
        loadItems(loadedItems, query);
        loadedItems+=10;
        updateFeed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.temporary_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.item1){
            Toast.makeText(MainActivity.this, "Sorry, not available yet..",
                    Toast.LENGTH_SHORT).show();
        }
        if(item.getItemId()==R.id.item2) {
            Toast.makeText(MainActivity.this, "Sorry, not available yet..",
                    Toast.LENGTH_SHORT).show();
        }
        if(item.getItemId()==R.id.quit){
            FirebaseAuth.getInstance().signOut();
            LoginManager.getInstance().logOut();
            Intent i = new Intent(this, LoginActivity.class); //if under this dialog you do not have your MainActivity
            i.addFlags(i.FLAG_ACTIVITY_CLEAR_TOP);
            i.addFlags(i.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            finish();

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
                if(adapter.getGroupCount()==loadedItems && firstVisibleItem+visibleItemCount==totalItemCount){
                    String oldestItemTime = adapter.getItem(loadedItems-1).getTime();
                    Query query = firebaseController.getMyDatabase().orderByKey().endAt(oldestItemTime).limitToLast(10);
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
                final String username1 = dst.child("username").getValue(String.class);
                final String time = dst.child("time").getValue(String.class);
                final String type = dst.child("type").getValue(String.class);

                //comments to be added to this list
                final List<ListViewComment> comments = new ArrayList<>();
                ListViewComment comment = new ListViewComment(null,null,"el comentario");
                comments.add(comment);

                final ListViewItem item1 = new ListViewItem(key,username1,null,message,time, comments);

                if(type.equals("image")){
                    StorageReference ref = firebaseController.getMyStorage().child(message);
                    final long megabyte = 1024*1024;
                    item1.setIsLoading(true);
                    ref.getBytes(megabyte).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {
                            Bitmap image = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            item1.setImage(image);
                            item1.setIsLoading(false);
                            adapter.notifyDataSetChanged();
                        }
                    });
                    list.add(start,item1);

                    adapter.notifyDataSetChanged();
                }else if(type.equals("message")){
                    list.add(start,item1);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {/*
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).getId().equals(dataSnapshot.getKey())) {
                        list.remove(i);
                        break;
                    }
                }
                list.remove(dataSnapshot.getChildren().iterator());*/
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

    public void sendMessage(View view) {
        String text = editText.getText().toString().trim();
        if(!TextUtils.isEmpty(text)) {
            firebaseController.sendMessage(text,"message");
            editText.setText("");
        }else{
            //check
            Toast.makeText(MainActivity.this, "Empty message", Toast.LENGTH_SHORT).show();
        }

    }

    public void uploadImage(View view){
        Intent i = new Intent(Intent.ACTION_PICK);
        i.setType("image/*");
        startActivityForResult(i, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            final Uri imageUri = data.getData();
            try{
                InputStream imageStream = getContentResolver().openInputStream(imageUri);
                Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                selectedImage = ImageController.ResizeImage(selectedImage,1300);
                byte[] image = ImageController.BitmapToBytes(selectedImage);
                firebaseController.sendImage(image);
            }catch (FileNotFoundException e){
                Log.d("Exception", e.getMessage());
            }
        }
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


}
