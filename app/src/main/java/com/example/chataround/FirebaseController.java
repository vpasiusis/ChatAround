package com.example.chataround;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class FirebaseController {
    private static FirebaseController instance = null;
    private FirebaseUser currentFirebaseUser;
    private DatabaseReference myDatabase;
    private StorageReference myStorage;
    private Bitmap image;


    private FirebaseController(){ }

    public static FirebaseController getInstance(){
        if(instance==null){
            instance = new FirebaseController();
        }
        return instance;
    }

    public void initialize(){
        currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        myDatabase = FirebaseDatabase.getInstance().getReference("Messages");
        myStorage = FirebaseStorage.getInstance().getReference().child("Messages");
    }

    public DatabaseReference getMyDatabase(){
        return myDatabase;
    }

    public StorageReference getMyStorage(){
        return myStorage;
    }

    public void sendMessage(String message, String type){
        String time = getTIme();
        String key = time + "__" + currentFirebaseUser.getEmail();
        key=key.replace(".","");

        DatabaseReference currentUserDB = myDatabase.child(key);
        currentUserDB.child("username").setValue(currentFirebaseUser.getEmail());
        currentUserDB.child("message").setValue(message);
        currentUserDB.child("type").setValue(type);
        currentUserDB.child("time").setValue(time);
    }

    public void sendImage(Uri imageUri){
        String time = getTIme();
        String key1 = time+"__"+currentFirebaseUser.getEmail();
        final String key=key1.replace(".","");

        StorageReference file = myStorage.child(key);
        file.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful()){
                    sendMessage(key, "image");
                }
            }
        });
    }

    public String getTIme(){
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = df.format(c.getTime());
        return date;
    }
}
