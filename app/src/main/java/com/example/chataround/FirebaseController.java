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
import java.util.HashMap;
import java.util.Map;

public class FirebaseController {
    private static FirebaseController instance = null;
    private FirebaseUser currentFirebaseUser;
    private DatabaseReference myDatabase;
    private StorageReference myStorage;
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

    public DatabaseReference getMyDatabase(){
        return myDatabase;
    }

    public StorageReference getMyStorage(){
        return myStorage;
    }

    public void sendMessage(String message, String imageId){
        String time = getTime();
        String key = getKey(time);

        Map<String, Object> newMessage = new HashMap<>();
        newMessage.put("message", message);
        newMessage.put("imageId",imageId);
        newMessage.put("time", time);
        newMessage.put("comments", 0);
        newMessage.put("likes", 0);
        newMessage.put("username", currentFirebaseUser.getEmail());

        DatabaseReference messageDb = myDatabase.child("Messages").child(key);
        messageDb.setValue(newMessage);
    }

    public void sendComment(String message, String postId, int comments){
        String time = getTime();
        String key = getKey(time);

        Map<String, Object> newMessage = new HashMap<>();
        newMessage.put("message", message);
        newMessage.put("time", time);
        newMessage.put("username", currentFirebaseUser.getEmail());
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

    public ListViewItem getCurrentSelectedItem(){
        return currentSelectedItem;
    }
    public void setCurrentSelectedItem(ListViewItem item){
        currentSelectedItem = item;
    }
}
