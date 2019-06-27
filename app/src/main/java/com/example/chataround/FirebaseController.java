package com.example.chataround;

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
        myDatabase = FirebaseDatabase.getInstance().getReference();
        myStorage = FirebaseStorage.getInstance().getReference().child("Messages");
    }

    public DatabaseReference getMyDatabase(){
        return myDatabase;
    }

    public StorageReference getMyStorage(){
        return myStorage;
    }

    public void sendMessage(String message, String type){
        String time = getTime();
        String key = getKey(time);

        Map<String, Object> newMessage = new HashMap<>();
        newMessage.put("message", message);
        newMessage.put("type", type);
        newMessage.put("time", time);
        newMessage.put("username", currentFirebaseUser.getEmail());

        DatabaseReference currentUserDB = myDatabase.child("Messages").child(key);
        currentUserDB.setValue(newMessage);
    }

    public void sendImage(byte[] image){
        String time = getTime();
        String key = getKey(time);

        StorageReference file = myStorage.child(key);
        sendMessage(key, "image");
        file.putBytes(image);
    }

    public String getTime(){
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = df.format(c.getTime());
        return time;
    }

    public String getKey(String time){
        String key = time + "__" + currentFirebaseUser.getEmail();
        key=key.replace(".","");
        return key;
    }
}
