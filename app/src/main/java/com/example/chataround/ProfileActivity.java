
package com.example.chataround;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;

import java.io.FileNotFoundException;
import java.io.InputStream;

import static java.lang.String.format;

public class ProfileActivity extends AppCompatActivity {
    private Activity activity;
    private Button descriptionButton;
    private EditText descriptionText;
    private FirebaseController firebaseController;
    private TextView likeCount;
    private TextView postCount;
    private TextView userName;
    private Button myPostActivity;
    private UserClass user;
    private Button mySettings;
    private CardView settingsCardView;
    private ImageView imageView;
    private byte[] image;
    private Uri imageUri;
    ProgressDialog progressDialog;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.profile_activity);
        Toolbar settingsToolbar = (Toolbar) findViewById(R.id.posting_toolbar);
        firebaseController= FirebaseController.getInstance();
        activity = ProfileActivity.this;
        progressDialog=new ProgressDialog(activity);
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);
        bottomNav.setSelectedItemId(R.id.nav_settings);
        descriptionText=findViewById(R.id.descriptionText);
        descriptionButton=findViewById(R.id.description_button);
        userName=findViewById(R.id.usernameProfile);
        likeCount=findViewById(R.id.gotLikes);
        mySettings=findViewById(R.id.settings_button);
        postCount=findViewById(R.id.gotPosts);
        myPostActivity=findViewById(R.id.myPostButton);
        settingsCardView=findViewById(R.id.cardView7);
        imageView=findViewById(R.id.imageViewAvatar);
        descriptionText.setCursorVisible(false);

        final boolean isCurrentUser = getIntent().getExtras().getBoolean("currentUser");
        if (isCurrentUser) {
            user = firebaseController.getCurrentUser();
            myPostActivity.setText("My posts");
            settingsToolbar.setTitle("My profile");
            mySettings.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(activity,SettingsActivity.class);
                    startActivity(intent);
                }
            });
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

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                    if(user.getAvatarId()!=null) {
                        builder.setTitle("Change existing avatar");
                    }else
                    {
                        builder.setTitle("Set avatar");
                    }
                    builder.setItems(new CharSequence[]
                                    {"Gallery", "Camera","Delete avatar"},
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (which) {
                                        case 0:
                                            uploadImage();
                                            break;
                                        case 1:
                                            requestPermissions();
                                            break;
                                        case 2:
                                            StorageReference ref = FirebaseStorage.getInstance().getReferenceFromUrl(user.getAvatarId());
                                            ref.delete();
                                            user.setAvatarId(null);
                                            firebaseController.setAvatarId(null);
                                            imageView.setImageResource(R.drawable.ic_person_black_24dp);
                                            break;
                                    }
                                }
                            });
                    builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    builder.show();
                }

            });

        }else{
            user = firebaseController.getClickedUser();
            settingsToolbar.setTitle("User profile");
            myPostActivity.setText("User posts");
            settingsCardView.setVisibility(View.GONE);
            descriptionText.setKeyListener(null);
        }
        myPostActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity,MyPostsActivity.class);
                intent.putExtra("currentUser",isCurrentUser);
                startActivity(intent);
            }
        });
        setSupportActionBar(settingsToolbar);
        loadData();
        super.onCreate(savedInstanceState);

    }

    public void loadData() {
        descriptionButton.setVisibility(View.GONE);
        userName.setText(" "+user.getName()+" ");
        if(user.getDescription()!=null) {
            descriptionText.setText(" " + user.getDescription() + " ");
        }
        descriptionButton.setVisibility(View.GONE);
        likeCount.setText(String.valueOf(user.getLikes()));
        postCount.setText(String.valueOf(user.getPosts()));
        if(user.getType()==99){
            userName.setBackgroundColor(getResources().getColor(R.color.admin));
        }
        if(user.getAvatarId()!=null) {
            loadPicture();
        }
    }
    private void loadPicture(){
        progressDialog.show();
        imageView.setBackground(null);
        PicassoCache.getPicassoInstance(activity).load(user.getAvatarId()).
                networkPolicy(NetworkPolicy.OFFLINE).into(imageView, new Callback() {
            @Override
            public void onSuccess() {
                progressDialog.dismiss();
            }

            @Override
            public void onError() {
                PicassoCache.getPicassoInstance(activity).load(user.getAvatarId()).
                        into(imageView, new Callback() {
                            @Override
                            public void onSuccess() {
                                progressDialog.dismiss();
                            }

                            @Override
                            public void onError() {
                            }
                        });
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

    public void uploadImage(){
        Intent i = new Intent(Intent.ACTION_PICK);
        i.setType("image/*");
        startActivityForResult(i, 1);
    }
    public void uploadAvatar(byte[] image){
        String key = user.getName()+"_avatar";
        final StorageReference file = firebaseController.getMyStorage().child("Avatars").child(key);
        file.putBytes(image).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                file.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        firebaseController.setAvatarId(uri.toString());
                        user.setAvatarId(uri.toString());
                        progressDialog.dismiss();
                        loadPicture();
                    }
                });
            }
        });
    }
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
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            final Uri imageUri = data.getData();
            try{
                InputStream imageStream = getContentResolver().openInputStream(imageUri);
                Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                selectedImage = ImageController.ResizeImage(selectedImage,1300);
                image = ImageController.BitmapToBytes(selectedImage);
                uploadAvatar(image);
                progressDialog.show();
            }catch (FileNotFoundException e){
                Log.d("Exception", e.getMessage());
            }
        }
        if(requestCode==2 && resultCode == Activity.RESULT_OK) {
            try{
                Bitmap selectedImage = MediaStore.Images.Media.getBitmap(
                        getContentResolver(), imageUri);
                selectedImage = ImageController.ResizeImage(selectedImage, 1300);
                image = ImageController.BitmapToBytes(selectedImage);
                uploadAvatar(image);
                progressDialog.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


}

