package com.example.chataround;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private DatabaseReference myDatabase;
    private EditText editText;
    private ArrayList<String> arrayList = new ArrayList<>();
    private ListView listView;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1, arrayList);

        Bundle bundle = getIntent().getExtras();
        username = bundle.getString("username");

        myDatabase = FirebaseDatabase.getInstance().getReference("Messages");
        listView = findViewById(R.id.listview1);
        editText = findViewById(R.id.enterTextid);
        listView.setAdapter(arrayAdapter);
        myDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                arrayAdapter.clear();
                arrayList.clear();
                listView.clearChoices();
                for (DataSnapshot dst : dataSnapshot.getChildren()) {

                    String message = dst.child("message").getValue(String.class);
                    String username1 = dst.child("username").getValue(String.class);
                    String time = dst.child("time").getValue(String.class);

                    if(message!=null&&username1!=null&&time!=null) {
                        arrayList.add(time + "\n" + username1 + "\n" + message);
                        arrayAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void sendMessage(View view) {
        String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
        currentDateTimeString = currentDateTimeString.replaceAll("\\s+", " ");
        String text = editText.getText().toString().trim();

        if(!TextUtils.isEmpty(text)) {
            String newMessageId  = currentDateTimeString+"__"+username;
            newMessageId=newMessageId.replace(".","");
            DatabaseReference currentUserDB = myDatabase.child(newMessageId);
            currentUserDB.child("username").setValue(username);
            currentUserDB.child("message").setValue(text);
            currentUserDB.child("type").setValue("Unknown");
            currentUserDB.child("time").setValue(currentDateTimeString);
            editText.setText("");
        }else{
            //check
            Toast.makeText(MainActivity.this, "Empty message", Toast.LENGTH_SHORT).show();
        }

    }


}
