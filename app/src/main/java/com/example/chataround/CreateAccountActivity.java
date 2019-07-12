package com.example.chataround;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class CreateAccountActivity extends AppCompatActivity {


    private EditText Name;
    private EditText Password;
    private EditText UserName;
    private String Type = "";
    private Button Create;
    private Boolean userNameAvaillable=true;
    private FirebaseAuth mAuth;
    private ProgressDialog mProgress;
    private FirebaseController firebaseController;
    private FirebaseAuth.AuthStateListener mAuthListener;


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_account);
        firebaseController = FirebaseController.getInstance();
        firebaseController.initialize();
        Name = (EditText) findViewById(R.id.edusername);
        Password = (EditText) findViewById(R.id.atpass);
        UserName = findViewById(R.id.edusername2);
        mAuth = FirebaseAuth.getInstance();
        Create = (Button) findViewById(R.id.btnCreate);
        mProgress = new ProgressDialog(this);
        Create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRegister();
            }
        });

    }
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseApp.initializeApp(this);

    }



    private void startRegister() {

        final String email = Name.getText().toString().trim();
        final String password = Password.getText().toString().trim();
        final String userName = UserName.getText().toString().trim();
        userNameAvaillable=true;
        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)&& !TextUtils.isEmpty(userName)) {
            mProgress.setMessage("Checking information...");
            mProgress.show();
            firebaseController.getMyDatabase().child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        if (userName.equals(snapshot.child("Username").getValue(String.class))) {
                            userNameAvaillable = false;
                        }
                    }
                    if (userNameAvaillable|| userName.contains(".")) {
                        if(!mProgress.isShowing()) {
                            mProgress.show();
                        }
                        mAuth.createUserWithEmailAndPassword(email, password)
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        mProgress.dismiss();
                                        if (task.isSuccessful()) {
                                            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("users");
                                            DatabaseReference currentUserDB = mDatabase.child(mAuth.getCurrentUser().getUid());
                                            Toast.makeText(CreateAccountActivity.this, " Acount " + email + " was created ", Toast.LENGTH_SHORT).show();
                                            currentUserDB.child("Type").setValue(0);
                                            currentUserDB.child("Email").setValue(email);
                                            currentUserDB.child("Username").setValue(userName);
                                            currentUserDB.child("Likes").setValue(0);
                                            currentUserDB.child("Posts").setValue(0);
                                            currentUserDB.child("RegisterData").setValue(getTime());
                                            currentUserDB.child("AvatarId").setValue(null);
                                        } else
                                            Toast.makeText(CreateAccountActivity.this, "error registering user", Toast.LENGTH_SHORT).show();

                                    }
                                });

                    } else {
                        mProgress.dismiss();
                        Toast.makeText(CreateAccountActivity.this, "User with that username exist or incorrect", Toast.LENGTH_SHORT).show();

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }else
            Toast.makeText(CreateAccountActivity.this, "Enter all data", Toast.LENGTH_SHORT).show();
    }
    public String getTime(){
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = df.format(c.getTime());
        return time;
    }

}



