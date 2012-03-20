package com.chatmessenger.android.view;

import com.chatmessenger.android.R;
import com.chatmessenger.android.XMPPClient;
import com.chatmessenger.android.view.ym.BuddyListActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class HomeActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.home);
	}

	public void onClickYahoo(View v){
		Intent intent = new Intent(HomeActivity.this, BuddyListActivity.class);
		startActivity(intent);
		
	}

	public void onClickFacebook(View v){
		Toast toast = Toast.makeText(HomeActivity.this, "Under Development", Toast.LENGTH_LONG);
		toast.show();
	}

	public void onClickGtalk(View v){
		Intent intent = new Intent(HomeActivity.this, XMPPClient.class);
		startActivity(intent);
	}
}
