package com.chatmessenger.android.view.ym;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.chatmessenger.android.R;
import com.chatmessenger.android.ym.Buddy;
import com.chatmessenger.android.ym.Constants;
import com.chatmessenger.android.ym.Conversation;
import com.chatmessenger.android.ym.Message;
import com.chatmessenger.android.ym.User;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

/**
 * 友達リストを表示するアクティビティです。
 *
 * @author Teruhiko Kusunoki&lt;<a
 *         href="teru.kusu@gmail.com">teru.kusu@gmail.com</a>&gt;
 *
 */
public class BuddyListActivity extends Activity implements Constants {
    private YmsgService ymsgService;
    private final List<Buddy> buddyList = new ArrayList<Buddy>();
    private BuddyListAdapter buddyListAdapter;
    private final Handler handler = new Handler();

    private final BroadcastReceiver ymsgServiceReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.v(LOG_TAG, "BuddyListActivity.onReceive() called with action: " + action);

            // 非同期イベントの受信
            if (ACTION_LOGIN.equals(action)) {
                updateBuddyList();
            } else if (ACTION_BUDDY_OFFLINE.equals(action)) {
                updateBuddyList();
            } else if (ACTION_BUDDY_ONLINE.equals(action)) {
                updateBuddyList();
            } else if (ACTION_BUDDY_STATUS_CHANGE.equals(action)) {
                updateBuddyList();
            } else if (ACTION_MESSAGE_RECEIVED.equals(action)
                    || ACTION_MESSAGE_SENT.equals(action)) {
                updateBuddyList();
            } else if (ACTION_DISCONNECT.equals(action)) {
                finish();
                Intent newIntent = new Intent(getApplicationContext(), BuddyListActivity.class);
                newIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(newIntent);
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
            setYmsgService(((YmsgService.LocalBinder) service).getService());

            if (!getYmsgService().isLogin()) {
                Log.v(LOG_TAG, "not yet logged in. showing login form.");
                // 未ログイン
                Intent intent = new Intent(BuddyListActivity.this, LoginActivity.class);
                startActivity(intent);
            } else {
                updateBuddyList();
            }
        }
    };

    /*
     * (non-Javadoc)
     *
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.v(LOG_TAG, "BuddyListActivity.onCreate() called: intent=" + getIntent());
        Intent causeIntent = getIntent();
        if (causeIntent != null && causeIntent.getIntExtra("notifyId", -1) != -1) {
            Log.v(LOG_TAG, "notifyId: " + causeIntent.getIntExtra("notifyId", -1));
            NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
            notificationManager.cancel(causeIntent.getIntExtra("notifyId", -1));
        }

        setContentView(R.layout.buddy_list);
        buddyListAdapter = new BuddyListAdapter(this, R.layout.buddy_list, getBuddyList());

        ListView buddyList = (ListView) findViewById(R.id.buddyList);
        buddyList.setAdapter(getBuddyListAdapter());
        buddyList.setOnItemClickListener(new OnItemClickListener() {
            /* (non-Javadoc)
             * @see android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget.AdapterView, android.view.View, int, long)
             */
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.v(LOG_TAG, "onItemClick() called at ListView in BuddyListActivity");

                TextView t = (TextView) view.findViewById(R.id.buddy_id_text);
                Intent intent = new Intent(BuddyListActivity.this, MessageListActivity.class);
                intent.putExtra(EXTRA_RECIPIENT_ID, t.getText().toString());

                startActivity(intent);
            }
        });

        Intent serviceIntent = new Intent(this, YmsgService.class);
        startService(serviceIntent);

        // 受け取りたいアクション
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_LOGIN);
        filter.addAction(ACTION_BUDDY_OFFLINE);
        filter.addAction(ACTION_BUDDY_ONLINE);
        filter.addAction(ACTION_BUDDY_STATUS_CHANGE);
        filter.addAction(ACTION_MESSAGE_SENT);
        filter.addAction(ACTION_MESSAGE_RECEIVED);
        filter.addAction(ACTION_DISCONNECT);
        registerReceiver(getYmsgServiceReceiver(), filter);

        bindService(serviceIntent, getYmsgServiceConnection(), BIND_AUTO_CREATE);
    }

    /*
     * (non-Javadoc)
     *
     * @see android.app.Activity#onRestart()
     */
    @Override
    protected void onRestart() {
        super.onRestart();
        updateBuddyList();
    }

    /*
     * (non-Javadoc)
     *
     * @see android.app.Activity#onDestroy()
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(getYmsgServiceConnection());
        unregisterReceiver(getYmsgServiceReceiver());
    }

    /*
     * (non-Javadoc)
     *
     * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.buddy_list_menu, menu);
        return true;
    }

    /*
     * (non-Javadoc)
     *
     * @see android.app.Activity#onPrepareOptionsMenu(android.view.Menu)
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        SharedPreferences p = getPreferences(MODE_PRIVATE);
        boolean showOffline = p.getBoolean(PREF_BUDDYLIST_SHOW_OFFLINE, false);

        MenuItem menuItem = menu.findItem(R.id.buddy_list_menu_show_offline);
        menuItem.setChecked(showOffline);

        MenuItem logoutMenuItem = menu.findItem(R.id.buddy_list_menu_logout);
        if (getYmsgService() == null || !getYmsgService().isLogin()) {
            logoutMenuItem.setTitle(R.string.login);
        } else {
            logoutMenuItem.setTitle(R.string.logout);
        }
        return true;
    }

    /*
     * (non-Javadoc)
     *
     * @see android.app.Activity#onMenuItemSelected(int, android.view.MenuItem)
     */
    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        YmsgService ymsgService = getYmsgService();
        switch (item.getItemId()) {
        case R.id.buddy_list_menu_logout:
            if (ymsgService != null) {
                ymsgService.logout();
            }

            getBuddyList().clear();
            getBuddyListAdapter().notifyDataSetChanged();

            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            return false;
        case R.id.buddy_list_menu_show_offline:
            item.setChecked(!item.isChecked());
            boolean showOffline = item.isChecked();
            SharedPreferences pref = getPreferences(MODE_PRIVATE);
            pref.edit().putBoolean(PREF_BUDDYLIST_SHOW_OFFLINE, showOffline).commit();
            updateBuddyList();
            return false;
        case R.id.buddy_list_menu_exit:
            if (ymsgService != null) {
                if (ymsgService.isLogin()) {
                    ymsgService.logout();
                }
                ymsgService.stopSelf();
            }
            finish();
            return false;
        case R.id.buddy_list_menu_change_status:
            AlertDialog.Builder builder = getChangeStatsuMessageDialogBuilder();
            builder.show();
            return false;
        default:
            return super.onMenuItemSelected(featureId, item);
        }
    }

    public synchronized void updateBuddyList() {
        YmsgService ys = getYmsgService();
        if (ys == null || !ys.isLogin()) {
            return;
        }

        SharedPreferences pref = getPreferences(MODE_PRIVATE);
        final boolean showOffline = pref.getBoolean(PREF_BUDDYLIST_SHOW_OFFLINE, false);

        List<Buddy> list = getBuddyList();
        list.clear();
        if (showOffline) {
            list.addAll(ys.getBuddyList().getAll());
        } else {
            list.addAll(ys.getBuddyList().getAllOnline());
        }

        // 友達リストに入ってないけど会話ている相手も表示する
        List<Conversation> cl = ys.getConversationList();
        if (cl != null && !cl.isEmpty()) {
            Set<String> buddyIndex = new HashSet<String>();
            for (Buddy buddy : ys.getBuddyList().getAll()) {
                buddyIndex.add(buddy.getId());
            }

            Message lastMessage = null;
            Date lastMessageDate = null;
            for (Conversation c : cl) {
                if (!buddyIndex.contains(c.getRecipientId())) {
                    lastMessage = c.getLasMessage();
                    if (lastMessage != null) {
                        lastMessageDate = lastMessage.getDate();
                    }
                    list.add(new Buddy(c.getRecipientId(), null, YMSG_STATUS_OFFLINE, lastMessageDate));
                }
            }
        }

        Collections.sort(list, new Comparator<Buddy>() {

            /* (non-Javadoc)
             * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
             */
            @Override
            public int compare(Buddy object1, Buddy object2) {
                Date date1 = object1.getLastContactDate();
                Date date2 = object2.getLastContactDate();
                if (date1 != null && date2 != null) {
                    return date1.compareTo(date2);
                } else if (date1 == null && date2 != null) {
                    return -1;
                } else if (date1 != null && date2 == null) {
                    return 1;
                }
                return object2.getId().toLowerCase().compareTo(object1.getId().toLowerCase());
            }
        });

        getHandler().post(new Runnable() {
            @Override
            public void run() {
                getBuddyListAdapter().notifyDataSetChanged();
            }
        });
    }

    /**
     * ymsgService を取得します。
     *
     * @return ymsgService
     */
    protected YmsgService getYmsgService() {
        return ymsgService;
    }

    /**
     * ymsgService を設定します。
     *
     * @param ymsgService
     *            セットする ymsgService
     */
    protected void setYmsgService(YmsgService ymsgService) {
        this.ymsgService = ymsgService;
    }

    /**
     * ymsgServiceReceiver を取得します。
     *
     * @return ymsgServiceReceiver
     */
    protected BroadcastReceiver getYmsgServiceReceiver() {
        return ymsgServiceReceiver;
    }

    /**
     * ymsgServiceConnection を取得します。
     *
     * @return ymsgServiceConnection
     */
    protected ServiceConnection getYmsgServiceConnection() {
        return ymsgServiceConnection;
    }

    /**
     * buddyListAdapter を取得します。
     *
     * @return buddyListAdapter
     */
    protected synchronized BuddyListAdapter getBuddyListAdapter() {
        return buddyListAdapter;
    }

    /**
     * buddyList を取得します。
     *
     * @return buddyList
     */
    protected List<Buddy> getBuddyList() {
        return buddyList;
    }

    /**
     * handler を取得します。
     *
     * @return handler
     */
    protected Handler getHandler() {
        return handler;
    }

    public class BuddyListAdapter extends ArrayAdapter<Buddy> {
        private final SimpleDateFormat dateFormat = new SimpleDateFormat("h:mm a");
        private LayoutInflater layoutInflater;

        /**
         * @param context
         * @param textViewResourceId
         * @param objects
         */
        public BuddyListAdapter(Context context, int textViewResourceId, List<Buddy> objects) {
            super(context, textViewResourceId, objects);

            this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        /*
         * (non-Javadoc)
         *
         * @see android.widget.ArrayAdapter#getView(int, android.view.View,
         * android.view.ViewGroup)
         */
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = this.layoutInflater.inflate(R.layout.buddy_list_item, null);
            }

            Buddy m = getItem(position);

            TextView buddyText = (TextView) convertView.findViewById(R.id.buddy_id_text);
            buddyText.setText(m.getId());

            TextView buddyStatusMessageText = (TextView) convertView.findViewById(R.id.buddy_status_message_text);
            buddyStatusMessageText.setText(m.getStatusMessage());

            TextView buddyLastAccessText = (TextView) convertView.findViewById(R.id.buddy_lasst_access_text);
            StringBuilder sb = new StringBuilder();
            if (m.getLastContactDate() != null) {
                sb.append(getText(R.string.buddy_list_last_access));
                sb.append(dateFormat.format(m.getLastContactDate()));
            }
            buddyLastAccessText.setText(sb.toString());

            TextView buddyOnlineText = (TextView) convertView.findViewById(R.id.buddy_online_text);
            if(m.getStatus() != YMSG_STATUS_OFFLINE) {
                buddyOnlineText.setText(R.string.buddy_list_online);
            } else {
                buddyOnlineText.setText(R.string.buddy_list_offline);
            }

            return convertView;
        }
    }

    protected AlertDialog.Builder getChangeStatsuMessageDialogBuilder() {
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        final View view = layoutInflater.inflate(R.layout.status_setting, null);

        RadioButton onlineRadio = (RadioButton) view.findViewById(R.id.status_setting_online_radio);
        RadioButton invisibleRadio = (RadioButton) view.findViewById(R.id.status_setting_invisible_radio);
        RadioButton customRadio = (RadioButton) view.findViewById(R.id.status_setting_custom_radio);
        EditText customEdit = (EditText) view.findViewById(R.id.status_setting_custom_edit);

        YmsgService ymsgService = getYmsgService();
        if (ymsgService != null && ymsgService.isLogin()) {
            User u = ymsgService.getUser();
            int status = u.getStatus();
            if (status == YMSG_STATUS_OFFLINE) {
                invisibleRadio.setChecked(true);
            } else if (status == YMSG_STATUS_CUSTOM) {
                customRadio.setChecked(true);
                customEdit.setText(u.getStatusMessage());
            } else {
                onlineRadio.setChecked(true);
            }
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.buddy_list_dialog_change_status);
        builder.setView(view);

        builder.setPositiveButton(R.string.change, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                RadioGroup radioGroup = (RadioGroup) view.findViewById(R.id.status_setting_radio_group);
                int checked = radioGroup.getCheckedRadioButtonId();
                int newStatus = YMSG_STATUS_OFFLINE;
                switch (checked) {
                case R.id.status_setting_online_radio:
                    newStatus = YMSG_STATUS_ONLINE;
                    break;
                case R.id.status_setting_custom_radio:
                    newStatus = YMSG_STATUS_CUSTOM;
                    break;
                case R.id.status_setting_invisible_radio:
                    newStatus = YMSG_STATUS_OFFLINE;
                    break;
                }

                EditText customEdit = (EditText) view.findViewById(R.id.status_setting_custom_edit);
                String statusMessage = customEdit.getText().toString();

                changeStatus(newStatus, statusMessage);
            }
        });

        builder.setNegativeButton(R.string.cancel, null);

        return builder;
    }

    protected void changeStatus(int status, String statusMessage) {
        YmsgService y = getYmsgService();
        if (y != null) {
            y.changeStatus(status, statusMessage);
        }
    }

}
