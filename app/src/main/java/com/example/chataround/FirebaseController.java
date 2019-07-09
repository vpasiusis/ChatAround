package com.example.chataround;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class FirebaseController {
    private static FirebaseController instance = null;
    private FirebaseUser currentFirebaseUser;
    private DatabaseReference myDatabase;
    private StorageReference myStorage;
    private String username;
    private int type;
    private ListViewItem currentSelectedItem = null;

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
    public String currentUser(){
        String Email;
        Email=currentFirebaseUser.getEmail();
        return Email;
    }
    public String getUsername(){
        getMyDatabase().child("users").child(currentFirebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                username = dataSnapshot.child("Username").getValue(String.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return username;
    }

    public DatabaseReference getMyDatabase(){
        return myDatabase;
    }

    public StorageReference getMyStorage(){
        return myStorage;
    }

    public int getMyType() {
        getMyDatabase().child("users").child(currentFirebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                type = dataSnapshot.child("Type").getValue(Integer.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return type;
    }


    public void sendMessage(String message, String imageId){
        String time = getTime();
        String key = getKey(time);
        String name= getUsername();
        Map<String, Object> newMessage = new HashMap<>();
        newMessage.put("message", message);
        newMessage.put("imageId",imageId);
        newMessage.put("time", time);
        newMessage.put("comments", 0);
        newMessage.put("likes", 0);
        newMessage.put("username", getUsername());

        DatabaseReference messageDb = myDatabase.child("Messages").child(key);
        messageDb.setValue(newMessage);
    }

    public void sendComment(String message, String postId, int comments){
        String time = getTime();
        String key = getKey(time);

        Map<String, Object> newMessage = new HashMap<>();
        newMessage.put("message", message);
        newMessage.put("time", time);
        newMessage.put("username", getUsername());
        newMessage.put("postId", postId);

        DatabaseReference commentDb = myDatabase.child("Comments").child(key);
        commentDb.setValue(newMessage);

        DatabaseReference messageDb = myDatabase.child("Messages").child(postId);
        Map<String, Object> changedMessage = new HashMap<>();
        changedMessage.put("comments", comments+1);
        messageDb.updateChildren(changedMessage);

    }

    public void sendImage(byte[] image, String message){
        String time = getTime();
        String key = getKey(time);
        StorageReference file = myStorage.child(key);
        sendMessage(message,key);
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
    //Cj galima optimaliau padaryt, bet veikia
    public String diffTime(String MessageTime) {
        long difference ;
        String time="";
        String days="", hours="", minute="";
        try {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date1 = df.parse(MessageTime);
            Date date2 = df.parse(getTime());
            difference = (date2.getTime() - date1.getTime()) / 1000;
            long day = difference / (24 * 3600);
            if(day==1) {days = day + " day ";}else {days = day + " days ";}
            difference = difference % (24 * 3600);
            long hour = difference / 3600;
            if(hour==1) {hours = hour + " hour ";}else { hours = hour + " hours ";}
            difference %= 3600;
            long minutes = difference / 60 ;
            if(minutes==1) {minute = minutes + " minute ";}else {minute = minutes + " minutes ";}
            difference %= 60;
            long seconds = difference;
            if(day==0){ time = hours + minute +"ago"; }
            if(day==0&&hour==0){ time = minute + seconds + " Seconds ago";}
            if(day==0&&hour==0&&minutes==0) { time = seconds + " Seconds ago";}
            if(day==0&&hour==0&&minutes==0&&seconds<20) { time="Few second ago";}
            if(day!=0){ time = days + hours + "ago" ;}
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return time;
    }

    public ListViewItem getCurrentSelectedItem(){
        return currentSelectedItem;
    }
    public void setCurrentSelectedItem(ListViewItem item){
        currentSelectedItem = item;
    }
}
