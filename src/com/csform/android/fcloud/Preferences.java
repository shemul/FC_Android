package com.csform.android.fcloud;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

public class Preferences extends AppCompatActivity {

    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Faculty Cloud");
        toolbar.setSubtitle("Preferences");
        this.setSupportActionBar(toolbar);

    }
}
