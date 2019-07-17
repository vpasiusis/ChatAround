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

public class EventsActivity extends AppCompatActivity {

    Activity activity;
    FirebaseController firebaseController;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.events_activity);
        Toolbar eventsToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        eventsToolbar.setTitle("Latest events");
        firebaseController=FirebaseController.getInstance();
        firebaseController.initialize();
        setSupportActionBar(eventsToolbar);
        activity = EventsActivity.this;
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);
        bottomNav.setSelectedItemId(R.id.nav_events);
        super.onCreate(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.topbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.postButton){
            Intent intent = new Intent(this, PostingActivity.class);
            startActivity(intent);
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
                            // selectedActivity = new FavoritesFragment();
                            break;
                        case R.id.nav_settings:
                            firebaseController.updateCurrentUser(true,EventsActivity.this);
                            break;
                    }


                    return true;
                }
            };

}
