package com.example.manavb.voicealarm;

import android.content.Intent;
import android.net.Uri;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.View;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        Toolbar mToolbar = findViewById(R.id.toolbar_about);
        setSupportActionBar(mToolbar);
        ActionBar ab = getSupportActionBar();
        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);
    }

    public void goToEmail(View view) {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setData(Uri.parse("mailto:manavbatra26@gmail.com"));
        //emailIntent.putExtra(Intent.EXTRA_EMAIL, "manavbatra26@gmail.com");
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Hey there! I found you on Voice Alarm.");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Dear Manav, \n");
        if (emailIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(emailIntent);
        }
        startActivity(Intent.createChooser(emailIntent, "Choose an email app: "));
    }
}
