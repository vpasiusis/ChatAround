package com.example.chataround;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private long backPressedTime;
    private Toast backToast;
    private EditText Name;
    private EditText Password;
    private TextView Info;
    private Button Login;
    private Button Register;
    private ProgressBar pbbar;
    private FirebaseAuth mAuth;

    private ProgressDialog progressDialog;
    private FirebaseAuth.AuthStateListener mAuthListener;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        Name = (EditText)findViewById(R.id.etName);
        progressDialog = new ProgressDialog(this);
        Password = (EditText)findViewById(R.id.etPass);
        Login = (Button)findViewById(R.id.btnLogin) ;
        Register = (Button)findViewById(R.id.btnRegister) ;
        mAuth = FirebaseAuth.getInstance();
       // Info = (TextView)findViewById(R.id.twCheck) ;
       // pbbar = (ProgressBar) findViewById(R.id.pbbar);
        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doLogin();
            }
        });
        Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doRegister();
            }
        });

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null) {
                    Toast.makeText(LoginActivity.this,   firebaseAuth.getCurrentUser().getEmail() +" joined chat", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        };
    }
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseApp.initializeApp(this);
        mAuth.addAuthStateListener(mAuthListener);
    }
    private void doRegister() {
        Intent intent = new Intent(this, CreateAccountActivity.class);
        startActivity(intent);
    }

    private void doLogin() {
        String email = Name.getText().toString().trim();
        String password = Password.getText().toString().trim();

        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
            progressDialog.setMessage("Joining the chat...");
            progressDialog.show();
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            progressDialog.dismiss();
                            if (task.isSuccessful()) {
                                Toast.makeText(LoginActivity.this, "Joined", Toast.LENGTH_SHORT).show();
                            } else
                                Toast.makeText(LoginActivity.this, "Email or password incorrect", Toast.LENGTH_SHORT).show();
                        }
                    });
        }else
            Toast.makeText(LoginActivity.this, "Įveskite visus laukus", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        if (backPressedTime + 1000 > System.currentTimeMillis() ) {
            backToast.cancel();
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
            return;
        } else {
            backToast = Toast.makeText(getBaseContext(), "Paspauskite dar kartą jog išeiti", Toast.LENGTH_SHORT);
            backToast.show();
        }
        backPressedTime = System.currentTimeMillis();
    }
}
