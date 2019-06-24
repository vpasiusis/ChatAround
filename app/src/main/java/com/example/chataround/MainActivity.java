package com.example.chataround;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.storage.StorageReference;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private EditText editText;
    private ExpandableListView listView;
    private long backPressedTime;
    private Toast backToast;
    private FirebaseController firebaseController;
    private List<ListViewItem> list;
    private ListViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseController = FirebaseController.getInstance();
        firebaseController.initialize();
        listView = findViewById(R.id.listview1);
        editText = findViewById(R.id.enterTextid);

        list = new ArrayList<>();
        adapter = new ListViewAdapter(this, list);
        listView.setAdapter(adapter);
        updateFeed();
    }

    public void updateFeed(){
        firebaseController.getMyDatabase().addChildEventListener(new ChildEventListener() {
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

                if(type.equals("image")){
                    StorageReference ref = firebaseController.getMyStorage().child(message);
                    final long megabyte = 1024*1024;
                    final ListViewItem item1 = new ListViewItem(key,username1,null,message,time, comments);
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
                    list.add(item1);
                    adapter.notifyDataSetChanged();
                }else if(type.equals("message")){
                    ListViewItem item1 = new ListViewItem(key,username1,null,message,time, comments);
                    list.add(item1);
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
            firebaseController.sendImage(imageUri);
        }
    }

    @Override
    public void onBackPressed() {
        if (backPressedTime + 1000 > System.currentTimeMillis() ) {
            backToast.cancel();
            FirebaseAuth.getInstance().signOut();
            Intent i = new Intent(this, LoginActivity.class); //if under this dialog you do not have your MainActivity
            i.addFlags(i.FLAG_ACTIVITY_CLEAR_TOP);
            i.addFlags(i.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            return;
        } else {
            backToast = Toast.makeText(getBaseContext(), "Press one more time to exit", Toast.LENGTH_SHORT);
            backToast.show();
        }
        backPressedTime = System.currentTimeMillis();
    }


}
