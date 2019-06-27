package com.example.chataround;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class PostingActivity extends AppCompatActivity {
    private EditText editText;
    FirebaseController firebaseController;
    Activity activity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        editText = findViewById(R.id.enterTextid2);
        setContentView(R.layout.posting_activity);
        Toolbar postingToolbar = (Toolbar) findViewById(R.id.posting_toolbar);
        setSupportActionBar(postingToolbar);
        activity=PostingActivity.this;
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);
        bottomNav.setSelectedItemId(0);
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
                    Activity selectedActivity = null;

                    switch (item.getItemId()) {
                        case R.id.nav_home:
                            Intent intent = new Intent(activity, MainActivity.class);
                            startActivity(intent);
                            break;
                        case R.id.nav_events:
                            Intent intent2 = new Intent(activity, EventsActivity.class);
                            startActivity(intent2);
                            break;
                        case R.id.nav_settings:
                            Intent intent1 = new Intent(activity, SettingsActivity.class);
                            startActivity(intent1);
                            break;
                    }


                    return true;
                }
            };

    /*public void sendMessage(View view) {
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
    }*/
}
