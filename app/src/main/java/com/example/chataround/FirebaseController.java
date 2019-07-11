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

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class FirebaseController {
    private static FirebaseController instance = null;
    private FirebaseUser currentFirebaseUser;
    private DatabaseReference myDatabase;
    private StorageReference myStorage;
    private String username;
    private String description = null;
    private int type = -1;
    private int userLikes;
    private int userPosts;
    private AtomicBoolean done;
    private String postUserUid;
    private ListViewItem currentSelectedItem = null;

    private FirebaseController() {
    }

    public static FirebaseController getInstance() {
        if (instance == null) {
            instance = new FirebaseController();
        }
        return instance;
    }

    public void initialize() {
        currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        myDatabase = FirebaseDatabase.getInstance().getReference();
        myStorage = FirebaseStorage.getInstance().getReference().child("Messages");
    }

    public void setDefaultData() {
        username = null;
        type = -1;
    }

    public String currentUser() {
        String Email;
        Email = currentFirebaseUser.getEmail();
        return Email;
    }

    public void getUsername() {

        getMyDatabase().child("users").child(currentFirebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                username = dataSnapshot.child("Username").getValue(String.class);

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
    public String returnUsername(){
        return username;
    }



    public int getMyType() {
        if (type == -1) {
            getMyDatabase().child("users").child(currentFirebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    type = dataSnapshot.child("Type").getValue(Integer.class);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }
        return type;
    }

    public DatabaseReference getMyDatabase() {
        return myDatabase;
    }

    public StorageReference getMyStorage() {
        return myStorage;
    }


    public void sendMessage(String message, String imageId) {
        String time = getTime();
        String key = getKey(time);
        String name = username;
        Map<String, Object> newMessage = new HashMap<>();
        newMessage.put("message", message);
        newMessage.put("imageId", imageId);
        newMessage.put("time", time);
        newMessage.put("comments", 0);
        newMessage.put("likes", 0);
        newMessage.put("username", name);

        DatabaseReference messageDb = myDatabase.child("Messages").child(key);
        messageDb.setValue(newMessage);
    }

    public void sendComment(String message, String postId, int comments) {
        String time = getTime();
        String key = getKey(time);

        Map<String, Object> newMessage = new HashMap<>();
        newMessage.put("message", message);
        newMessage.put("time", time);
        newMessage.put("username", username);
        newMessage.put("postId", postId);

        DatabaseReference commentDb = myDatabase.child("Comments").child(key);
        commentDb.setValue(newMessage);

        DatabaseReference messageDb = myDatabase.child("Messages").child(postId);
        Map<String, Object> changedMessage = new HashMap<>();
        changedMessage.put("comments", comments + 1);
        messageDb.updateChildren(changedMessage);

    }

    public void sendImage(byte[] image, String message) {
        String time = getTime();
        String key = getKey(time);
        StorageReference file = myStorage.child(key);
        sendMessage(message, key);
        file.putBytes(image);
    }

    public void setDescription(String description) {
        myDatabase.child("users").child(currentFirebaseUser.getUid()).child("Description").setValue(description);
    }

    public String getDescription() {
        myDatabase.child("users").child(currentFirebaseUser.getUid()).child("Description").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                description = dataSnapshot.getValue(String.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return description;
    }
    public int getUserLiked(final String name) {
        if(name!=null) {
            myDatabase.child("Messages").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    int countingUserPost = 0;
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        if (name.equals(snapshot.child("username").getValue())) {
                            countingUserPost += snapshot.child("likes").getValue(Integer.class);
                        }
                    }
                    userLikes = countingUserPost;
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        return userLikes;
    }

    public int getUserPostNumber(final String name) {
        if(name!=null) {
            myDatabase.child("Messages").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    int countingUserLikes = 0;
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        if (name.equals(snapshot.child("username").getValue())) {
                            countingUserLikes += 1;
                        }
                    }
                    userPosts = countingUserLikes;
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        return userPosts;
    }

    //
    //
    //
    //
    //man cia reikia paduot username posto, bet kai paduotu  neveikia.
    public String returnUid(String name){
        final String name2 = name;
        myDatabase.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    if(name2.equals(snapshot.child("Username").getValue())){
                       postUserUid= snapshot.getKey();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return postUserUid;
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

    public String diffTime(String MessageTime) {
        long difference;
        String time = "Just now";
        try {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date1 = df.parse(MessageTime);
            Date date2 = df.parse(getTime());
            difference = (date2.getTime() - date1.getTime()) / 1000; // to seconds
            if(!MessageTime.substring(0,4).equals(getTime().substring(0,4))){
                SimpleDateFormat df1 = new SimpleDateFormat("dd MMM YY");
                String date = df1.format(date1);
                return date;
            }
            long day = difference / (24 * 3600);
            if(day==1){
                String days = day + " day ago";
                return days;
            }else if(day>1 && day<7){
                String days = day + " days ago";
                return days;
            }else if(day>=7){
                SimpleDateFormat df1 = new SimpleDateFormat("dd MMM");
                String date = df1.format(date1);
                return date;
            }
            difference = difference % (24 * 3600);
            long hour = difference / 3600;
            if(hour==1){
                String hours = hour + " hour ago";
                return hours;
            }else if(hour>1 && hour<24){
                String hours = hour + " hours ago";
                return hours;
            }
            difference %= 3600;
            long minute = difference / 60 ;
            if(minute==1){
                String minutes = minute + " minute ago";
                return minutes;
            }else if(minute>1 && minute<60){
                String minutes = minute + " minutes ago";
                return minutes;
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return time;
    }

    public String diffCount(int count){
        DecimalFormat df = new DecimalFormat("#.#");
        if(count<1000) return String.valueOf(count);
        else if(count<1000000){
            String value = String.valueOf(df.format((float)count/1000));
            return value + "K";
        }else{
            String value = String.valueOf(df.format((float)count/1000000));
            return  value + "M";
        }
    }

    public ListViewItem getCurrentSelectedItem(){
        return currentSelectedItem;
    }
    public void setCurrentSelectedItem(ListViewItem item){
        currentSelectedItem = item;
    }
}
