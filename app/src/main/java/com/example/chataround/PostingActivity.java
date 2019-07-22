package com.example.chataround;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class PostingActivity extends AppCompatActivity {
    private EditText editPostText;
    private FirebaseController firebaseController;
    private Activity activity;
    private Button cameraButton;
    private UserClass user;
    private byte[] image;
    private Uri imageUri;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        setContentView(R.layout.posting_activity);
        Toolbar postingToolbar = (Toolbar) findViewById(R.id.posting_toolbar);
        postingToolbar.setTitle("New post");
        setSupportActionBar(postingToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        activity=PostingActivity.this;
        firebaseController = FirebaseController.getInstance();
        user=firebaseController.getCurrentUser();
        editPostText = findViewById(R.id.enterTextidpost);
        cameraButton  = (Button)findViewById(R.id.PictureButtonId);
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);
        bottomNav.setSelectedItemId(0);
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPermissions();
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
            finish(); // close this activity and return to preview activity (if there is any)
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
                            firebaseController.updateCurrentUser(false,true, PostingActivity.this);
                            break;
                    }


                    return true;
                }
            };

    public void requestPermissions() {
        if (ContextCompat.checkSelfPermission(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    1);
        } else {
            cameraIntent();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    cameraIntent();
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
        }
    }

    public void cameraIntent(){
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
        imageUri = getContentResolver().insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, 2);
    }

    public void sendMessage(View view) {
        String text = editPostText.getText().toString().trim();
        if(!TextUtils.isEmpty(text)||hasImageSpan(editPostText)) {
            if(!user.isAnonMode()) {
                firebaseController.updatePosts(user.getName(), true);
            }
            if (hasImageSpan(editPostText)) {
                firebaseController.sendImage(image,text);
            } else {
                firebaseController.sendMessage(text, null);
                editPostText.setText("");
            }
            Intent intent = new Intent(PostingActivity.this, MainActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0);
            activity.finish();
        }
        else {
            //check
            Toast.makeText(PostingActivity.this, "Empty message", Toast.LENGTH_SHORT).show();
        }
    }
    public boolean hasImageSpan(EditText edit) {
        Editable edittextimage  = edit.getEditableText();
        ImageSpan[] spans = edittextimage.getSpans(0, edittextimage.length(), ImageSpan.class);
        return !(spans.length == 0);
    }

    public void uploadImage(View view){
        Intent i = new Intent(Intent.ACTION_PICK);
        i.setType("image/*");
        startActivityForResult(i, 1);
    }

    private void addImageInEditTextUpload(Drawable drawable) {
        drawable.setBounds(0, 0, (int)(drawable.getIntrinsicWidth()*0.2), (int)(drawable.getIntrinsicHeight()*0.2));
        int selectionCursorPos = editPostText.getSelectionStart();
        editPostText.getText().insert(selectionCursorPos, " ");
        selectionCursorPos = editPostText.getSelectionStart();
        SpannableStringBuilder builder = new SpannableStringBuilder(editPostText.getText());
        int startPos = selectionCursorPos - ".".length();
        builder.setSpan(new ImageSpan(drawable), startPos, selectionCursorPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        editPostText.setText(builder);
        editPostText.setSelection(selectionCursorPos);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            final Uri imageUri = data.getData();
            try{
                InputStream imageStream = getContentResolver().openInputStream(imageUri);
                Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                Drawable d = new BitmapDrawable(getResources(),selectedImage);
                selectedImage = ImageController.ResizeImage(selectedImage,1300);
                image = ImageController.BitmapToBytes(selectedImage);
                addImageInEditTextUpload(d);

            }catch (FileNotFoundException e){
                Log.d("Exception", e.getMessage());
            }
        }
        if(requestCode==2 && resultCode == Activity.RESULT_OK) {
            try{
                Bitmap selectedImage = MediaStore.Images.Media.getBitmap(
                        getContentResolver(), imageUri);
                Drawable d = new BitmapDrawable(getResources(), selectedImage);
                selectedImage = ImageController.ResizeImage(selectedImage, 1300);
                image = ImageController.BitmapToBytes(selectedImage);
                addImageInEditTextUpload(d);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
