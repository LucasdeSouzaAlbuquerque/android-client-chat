package com.chatmessenger.android.view.ym;

import com.chatmessenger.android.R;
import com.chatmessenger.android.ym.Constants;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

/**
 *
 * @author Teruhiko Kusunoki&lt;<a href="teru.kusu@gmail.com">teru.kusu@gmail.com</a>&gt;
 *
 */
public class LoginActivity extends Activity implements Constants {

    private ProgressDialog progressDialog;
    private YmsgService ymsgService;

	private final BroadcastReceiver ymsgServiceReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
		    String action = intent.getAction();
			if(ACTION_LOGIN.equals(action)) {
				// ログイン完了
                progressDialog.dismiss();
				finish();
			} else if (ACTION_LOGIN_FAILED.equals(action)) {
                progressDialog.dismiss();
                Toast.makeText(LoginActivity.this, R.string.login_failed, Toast.LENGTH_LONG).show();
			}
		}
	};

	private final ServiceConnection ymsgServiceConnection = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {
			setYmsgService(null);
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			setYmsgService(((YmsgService.LocalBinder)service).getService());
		}
	};

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.login);

		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(ACTION_LOGIN);
		intentFilter.addAction(ACTION_LOGIN_FAILED);
		registerReceiver(getYmsgServiceReceiver(), intentFilter);

		Intent serviceIntent = new Intent(this, YmsgService.class);
		bindService(serviceIntent, getYmsgServiceConnection(), BIND_AUTO_CREATE);

		SharedPreferences pref = getPreferences(MODE_PRIVATE);

		if (pref.getBoolean(PREF_LOGIN_REMEMBER_ID, false)) {
			EditText loginIdEdit = (EditText) findViewById(R.id.loginIdEdit);
			loginIdEdit.setText(pref.getString(PREF_LOGIN_ID, null));

			EditText loginPassEdit = (EditText) findViewById(R.id.loginPassEdit);
			loginPassEdit.setText(pref.getString(PREF_LOGIN_PASSWORD, null));
		}

		CheckBox loginRememberIdCheck  = (CheckBox) findViewById(R.id.loginRemberIdCheck);
		loginRememberIdCheck.setChecked(pref.getBoolean(PREF_LOGIN_REMEMBER_ID, false));

		CheckBox loginAutoCheck  = (CheckBox) findViewById(R.id.loginAutoCheck);
		loginAutoCheck.setChecked(pref.getBoolean(PREF_LOGIN_AUTO, false));

		CheckBox loginHiddenCheck  = (CheckBox) findViewById(R.id.loginHiddenCheck);
		loginHiddenCheck.setChecked(pref.getBoolean(PREF_LOGIN_HIDDEN, false));

		Button loginButton = (Button) findViewById(R.id.loginButton);
		loginButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				EditText loginIdEdit = (EditText) findViewById(R.id.loginIdEdit);
				EditText loginPassEdit = (EditText) findViewById(R.id.loginPassEdit);
				
				
				String loginId = loginIdEdit.getText().toString().trim();
				if(loginId.contains("@")){
					String [] login = loginId.split("@");
					loginId = login[0];
				}
				String loginPass = loginPassEdit.getText().toString().trim();

				if (loginId == null || "".equals(loginId)
						|| loginPass == null || "".equals(loginPass)) {
					Toast.makeText(LoginActivity.this, R.string.login_empty_id, Toast.LENGTH_LONG).show();
					return;
				}

				// 設定保存
				SharedPreferences pref = getPreferences(MODE_PRIVATE);
				Editor editor = pref.edit();

				CheckBox loginRememberIdCheck  = (CheckBox) findViewById(R.id.loginRemberIdCheck);
				editor.putBoolean(PREF_LOGIN_REMEMBER_ID, loginRememberIdCheck.isChecked());
				if (loginRememberIdCheck.isChecked()) {
				    editor.putString(PREF_LOGIN_ID, loginId);
                    editor.putString(PREF_LOGIN_PASSWORD, loginPass);
				} else {
                    editor.remove(PREF_LOGIN_ID);
                    editor.remove(PREF_LOGIN_PASSWORD);
				}

				CheckBox loginAutoCheck  = (CheckBox) findViewById(R.id.loginAutoCheck);
                editor.putBoolean(PREF_LOGIN_AUTO, loginAutoCheck.isChecked());

                CheckBox loginHiddenCheck  = (CheckBox) findViewById(R.id.loginHiddenCheck);
                editor.putBoolean(PREF_LOGIN_HIDDEN, loginHiddenCheck.isChecked());

                editor.commit();

                progressDialog = new ProgressDialog(LoginActivity.this);
                progressDialog.setMessage(getText(R.string.sysmsg_now_login));
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.show();

                getYmsgService().login(loginId, loginPass, loginHiddenCheck.isChecked());
			}
		});
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(getYmsgServiceReceiver());
		unbindService(getYmsgServiceConnection());
		setYmsgService(null);
	}

	/**
	 * ymsgService を取得します。
	 * @return ymsgService
	 */
	protected YmsgService getYmsgService() {
		return ymsgService;
	}

	/**
	 * ymsgService を設定します。
	 * @param ymsgService セットする ymsgService
	 */
	protected void setYmsgService(YmsgService ymsgService) {
		this.ymsgService = ymsgService;
	}

	/**
	 * ymsgServiceConnection を取得します。
	 * @return ymsgServiceConnection
	 */
	protected ServiceConnection getYmsgServiceConnection() {
		return ymsgServiceConnection;
	}

	/**
	 * ymsgServiceReceiver を取得します。
	 * @return ymsgServiceReceiver
	 */
	protected BroadcastReceiver getYmsgServiceReceiver() {
		return ymsgServiceReceiver;
	}
}
