package com.chatmessenger.android;

import com.chatmessenger.android.R;
import com.chatmessenger.android.R.layout;

import android.app.Activity;
import android.os.Bundle;

public class HomeActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }
}