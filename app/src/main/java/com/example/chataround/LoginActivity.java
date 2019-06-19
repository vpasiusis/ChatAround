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

    EditText editText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);


        Button enterChat = findViewById(R.id.enterChat1);
        enterChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doCheck();
            }
        });


    }
    private void doCheck(){
        editText = findViewById(R.id.editText11);
        String edittext = editText.getText().toString().trim();
        if(TextUtils.isEmpty(edittext)){
            Toast.makeText(LoginActivity.this, "Please enter username!", Toast.LENGTH_SHORT).show();

        }else{
            Toast.makeText(LoginActivity.this, "Jungiamasi...", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.putExtra("username", edittext);
            startActivity(intent);
        }

    }
}
