package com.example.chataround;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Switch;

public class SettingsActivity extends AppCompatActivity {
    private Activity activity;
    private FirebaseController firebaseController;
    private UserClass user;
    private Switch aSwitch;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.settings_activity);
        Toolbar eventsToolbar = (Toolbar) findViewById(R.id.posting_toolbar);
        eventsToolbar.setTitle("My settings");
        firebaseController=FirebaseController.getInstance();
        firebaseController.initialize();
        user=firebaseController.getCurrentUser();
        setSupportActionBar(eventsToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        activity = SettingsActivity.this;
        aSwitch=findViewById(R.id.switch1);
        if(user.isAnonMode()){
            aSwitch.setChecked(true);
        }else {
            aSwitch.setChecked(false);
        }
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    user.setAnonMode(true);
                    firebaseController.setUserAnon(true);
                }else {
                    user.setAnonMode(false);
                    firebaseController.setUserAnon(false);
                }
            }
        });
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);
        super.onCreate(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.topbar_posting, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            firebaseController.updateCurrentUser(true, SettingsActivity.this);
            finish();
        }
        if (item.getItemId() == R.id.quit) {
            LogOffDialog logOffDialog = new LogOffDialog();
            logOffDialog.show(getSupportFragmentManager(), "Log Off");
        }
        return super.onOptionsItemSelected(item);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Activity selectedActivity = null;

                    switch (item.getItemId()) {
                        case R.id.nav_home:
                            Intent intent = new Intent(activity, MainActivity.class);
                            startActivity(intent);
                            break;
                        case R.id.nav_events:
                            Intent intent1 = new Intent(activity, EventsActivity.class);
                            startActivity(intent1);
                            break;
                        case R.id.nav_settings:
                            firebaseController.updateCurrentUser(true, SettingsActivity.this);
                            break;
                    }


                    return true;
                }
            };




}
