package com.example.chataround;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private DatabaseReference myDatabase;
    EditText editText;
    ArrayList<String> arrayList = new ArrayList<>();
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1, arrayList);

        myDatabase = FirebaseDatabase.getInstance().getReference("Messages");
        listView = (ListView)findViewById(R.id.listview1);
        listView.setAdapter(arrayAdapter);
        myDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                arrayAdapter.clear();
                arrayList.clear();
                listView.clearChoices();
                for (DataSnapshot dst : dataSnapshot.getChildren()) {
                    String message = dst.child("message").getValue(String.class);
                    String username = dst.child("username").getValue(String.class);
                    String time = dst.child("time").getValue(String.class);
                    arrayList.add(time + "\n" + username + "\n" + message);
                    arrayAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        /*
        myDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String message = dataSnapshot.child("message").getValue(String.class);
                String username = dataSnapshot.child("username").getValue(String.class);
                String time = dataSnapshot.child("time").getValue(String.class);
                System.out.println(time);
                arrayList.add(time+" \n"+username+" "+message);
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });*/
    }

    public void sendMessage(View view) {

        Bundle bundle = getIntent().getExtras();
        String username1 = bundle.getString("username");
        String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
        currentDateTimeString = currentDateTimeString.replaceAll("\\s+", " ");
        editText = findViewById(R.id.enterTextid);
        String edittext = editText.getText().toString().trim();
        if(!TextUtils.isEmpty(edittext)) {
            final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("Messages");
            DatabaseReference currentUserDB = mDatabase.child(currentDateTimeString+"__"+username1);
            currentUserDB.child("username").setValue(username1);
            currentUserDB.child("message").setValue(edittext);
            currentUserDB.child("type").setValue("Unknown");
            currentUserDB.child("time").setValue(currentDateTimeString);
            editText.setText("");
        }else
        {
            //check
            Toast.makeText(MainActivity.this, "Empty message", Toast.LENGTH_SHORT).show();
        }

    }


}
