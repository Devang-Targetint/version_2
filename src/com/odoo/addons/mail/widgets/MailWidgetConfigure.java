package com.odoo.addons.mail.widgets;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.odoo.support.OUser;
import com.odoo.R;

public class MailWidgetConfigure extends Activity implements
		OnItemClickListener {
	public static final String KEY_INBOX = "inbox";
	public static final String KEY_TODO = "to-do";
	public static final String KEY_TOME = "to:me";
	public static final String KEY_ARCHIVE = "archive";

	private static final String PREFS_NAME = "com.odoo.widgetsWidgetProvider";

	List<String> mOptionsList = new ArrayList<String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.widget_mail_configure_layout);
		setTitle("Widget Configure");
		setResult(RESULT_CANCELED);
		mOptionsList.add("Inbox");
		mOptionsList.add("To-Do");
		mOptionsList.add("To:me");
		mOptionsList.add("Archive");
		initListView();

		if (OUser.current(this) == null) {
			Toast.makeText(this, "No account found", Toast.LENGTH_LONG).show();
			finish();
		}

	}

	private void initListView() {
		ListView lstOptions = (ListView) findViewById(R.id.widgetMessageConfigure);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, mOptionsList);
		lstOptions.setAdapter(adapter);
		lstOptions.setOnItemClickListener(this);
	}

	static void savePref(Context context, int appWidgetId, String key,
			String value) {
		SharedPreferences.Editor prefs = context.getSharedPreferences(
				PREFS_NAME, 0).edit();
		prefs.putString(key + "_" + appWidgetId, value);
		prefs.commit();
	}

	static String getPref(Context context, int appWidgetId, String key) {
		SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
		String value = prefs.getString(key + "_" + appWidgetId, "");
		return value;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long arg3) {
		Intent intent = getIntent();
		Bundle extras = intent.getExtras();
		int mAppWidgetId = 0;
		if (extras != null) {
			mAppWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID,
					AppWidgetManager.INVALID_APPWIDGET_ID);
		}
		savePref(this, mAppWidgetId, "message_filter",
				mOptionsList.get(position).toString());
		AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
		MailWidget.updateMessageWidget(this, appWidgetManager,
				new int[] { mAppWidgetId });

		Intent resultValue = new Intent();
		resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
		setResult(RESULT_OK, resultValue);
		finish();
	}

}
