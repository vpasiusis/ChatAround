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
                    if(message!=null&&username!=null&&time!=null) {
                        Bundle bundle = getIntent().getExtras();
                        String username1 = bundle.getString("username");

                        if(username.contains(username1)) {
                            arrayList.add(time + "\n" + username + "\n" + message);
                            arrayAdapter.notifyDataSetChanged();
                            arrayAdapter.getContext().setTheme(0);

                        }else{
                            arrayList.add(time + "\n" + username + "\n" + message);
                            arrayAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

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
            String newMessageId  =currentDateTimeString+"__"+username1;
            newMessageId=newMessageId.replace(".","");
            DatabaseReference currentUserDB = mDatabase.child(newMessageId);
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
