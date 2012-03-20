package com.chatmessenger.android.view.gmail;

import org.jivesoftware.smack.XMPPException;

import com.chatmessenger.android.R;
import com.chatmessenger.android.gmail.ConnectionHelper;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends Activity {	
	
	ProgressDialog pd;
	EditText txfUser, txfPass;
	public boolean isLogin;
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_gmail);
		
		Button btnLogin = (Button)findViewById(R.id.btnLogin);
		txfUser = (EditText)findViewById(R.id.txfUser);
		txfPass = (EditText)findViewById(R.id.txfPassword);
		btnLogin.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				String user = txfUser.getText().toString();
				String pass = txfPass.getText().toString();
				new DoLogin().execute(user, pass);
			}
		});
	}
	
	class DoLogin extends AsyncTask<String, Boolean, Boolean> {

		
		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			pd.dismiss();
			if(result) {
				LoginActivity.this.finish();
				startService(new Intent(LoginActivity.this, NChatService.class));
				startActivity(new Intent(LoginActivity.this, ContactListActivity.class));
			}
			else {
				AlertDialog.Builder adBuilder = new AlertDialog.Builder(LoginActivity.this);
				adBuilder
						.setMessage("Login unsuccessful.\nPlease try again.")
						.setCancelable(false)
						.setPositiveButton("OK",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										dialog.cancel();
									}
								});
				AlertDialog alert = adBuilder.create();
				alert.show();
			}
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pd = ProgressDialog.show(LoginActivity.this, "Please Wait", "Connecting...");
		}

		@Override
		protected Boolean doInBackground(String... params) {
			boolean result = false;
			String user = params[0];
			String pass = params[1];
			try {
				ConnectionHelper.connect();
				ConnectionHelper.login(user, pass);
				result = Boolean.TRUE;
				isLogin = Boolean.TRUE;
			} catch (XMPPException e) {
				result = Boolean.FALSE;
				isLogin = Boolean.FALSE;
				e.printStackTrace();
			}
			return result;
		}
		
	}
}