package com.example.chataround;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
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
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.channels.FileLockInterruptionException;
import java.nio.file.FileSystemNotFoundException;

public class PostingActivity extends AppCompatActivity {
    private EditText editPostText;
    private FirebaseController firebaseController;
    private Activity activity;
    private Button cameraButton;
    private byte[] image;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        setContentView(R.layout.posting_activity);
        Toolbar postingToolbar = (Toolbar) findViewById(R.id.posting_toolbar);
        setSupportActionBar(postingToolbar);
        activity=PostingActivity.this;
        firebaseController = FirebaseController.getInstance();
        editPostText = findViewById(R.id.enterTextidpost);
        cameraButton  = (Button)findViewById(R.id.PictureButtonId);
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);
        bottomNav.setSelectedItemId(0);
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, 2);
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

    public void sendMessage(View view) {
        String text = editPostText.getText().toString().trim();
        if(!TextUtils.isEmpty(text)) {
            if (hasImageSpan(editPostText)) {
                firebaseController.sendImage(image,text);
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
            } else {
                firebaseController.sendMessage(text, null);
                editPostText.setText("");
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
            }
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
    private void addImageInEditText(Drawable drawable) {

        drawable.setBounds(0, 0, (int)(drawable.getIntrinsicWidth()*2), (int)(drawable.getIntrinsicHeight()*2));
        int selectionCursorPos = editPostText.getSelectionStart();
        editPostText.getText().insert(selectionCursorPos, ".");
        selectionCursorPos = editPostText.getSelectionStart();
        SpannableStringBuilder builder = new SpannableStringBuilder(editPostText.getText());
        int startPos = selectionCursorPos - ".".length();
        builder.setSpan(new ImageSpan(drawable), startPos, selectionCursorPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        editPostText.setText(builder);
        editPostText.setSelection(selectionCursorPos);
    }
    private void addImageInEditTextUpload(Drawable drawable) {

        drawable.setBounds(0, 0, (int)(drawable.getIntrinsicWidth()*0.2), (int)(drawable.getIntrinsicHeight()*0.2));
        int selectionCursorPos = editPostText.getSelectionStart();
        editPostText.getText().insert(selectionCursorPos, ".");
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
            Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
            Drawable d = new BitmapDrawable(getResources(), imageBitmap);
            imageBitmap = ImageController.ResizeImage(imageBitmap, 1600);
            image = ImageController.BitmapToBytes(imageBitmap);
            addImageInEditText(d);


        }
    }
}