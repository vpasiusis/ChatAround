package com.example.chataround;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    private EditText editText;
    private Button enterChat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        editText = findViewById(R.id.editText11);
        enterChat = findViewById(R.id.enterChat1);
        enterChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doCheck();
            }
        });
    }

    private void doCheck(){
        String text = editText.getText().toString().trim();

        if(TextUtils.isEmpty(text)){
            Toast.makeText(LoginActivity.this, "Please enter username!", Toast.LENGTH_SHORT).show();

        }else{
            Toast.makeText(LoginActivity.this, "Jungiamasi...", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.putExtra("username", text);
            startActivity(intent);
        }
    }
}
