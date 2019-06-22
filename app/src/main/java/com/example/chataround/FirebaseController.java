package com.example.chataround;

import android.net.Uri;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
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
import java.util.HashMap;
import java.util.Map;

public class FirebaseController {
    private static FirebaseController instance = null;
    private FirebaseUser currentFirebaseUser;
    private DatabaseReference myDatabase;
    private StorageReference myStorage;

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

        Map<String, Object> newMessage = new HashMap<>();
        newMessage.put("message", message);
        newMessage.put("type", type);
        newMessage.put("time", time);
        newMessage.put("username", currentFirebaseUser.getEmail());

        DatabaseReference currentUserDB = myDatabase.child(key);
        currentUserDB.setValue(newMessage);
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
