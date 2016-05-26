package com.intel.mttest.android;

import java.util.ArrayList;

import com.intel.mttest.android.configure.ConfigActivity;
import com.intel.mttest.android.results.ResultsFragment;
import com.intel.mttest.android.test.TestFragment;
import com.intel.mttest.cmd.MttestModel;
import com.intel.mttest.loaders.CmdArgs;
import com.intel.mttest.reporter.EventType;
import com.intel.mttest.reporter.ILog;
import com.intel.mttest.reporter.MttestMessage;
import com.intel.mttest.reporter.Observable;
import com.intel.mttest.reporter.Observer;
import com.intel.mttest.representation.OS;
import com.intel.mttest.representation.TestSetSummary;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.PersistableBundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.TabHost.TabSpec;
import android.widget.TabWidget;

public class StarterActivity extends FragmentActivity implements Observer { // FragmentActivity
																			// ActivityGroup
	final public static String TEST_TAG = "TEST_FRAGMENT";
	final public static String RESULTS_TAG = "RESULTS_FRAGMENT";

	private FragmentTabHost tabHost;
	private TabWidget tabWidget;
	private boolean configureVisible;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// turn screen on and lock
		{
			this.getWindow()
					.addFlags(
							WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
									| WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
									| WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON
									| WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
									| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

			PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
			// may be replaced with `shell input keyevent 26`
			WakeLock wl = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK
					| PowerManager.ACQUIRE_CAUSES_WAKEUP, "turn screen on");
			wl.acquire();
		}
		init();

		setContentView(R.layout.starter_layout);

		tabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
		tabWidget = (TabWidget) findViewById(android.R.id.tabs);
		tabHost.setup(this, getSupportFragmentManager(),
				android.R.id.tabcontent);

		{
			TabSpec test = tabHost.newTabSpec(TEST_TAG);
			test.setIndicator("Test");
			tabHost.addTab(test, TestFragment.class, null);
		}

		{
			TabSpec results = tabHost.newTabSpec(RESULTS_TAG);
			results.setIndicator("Results");
			tabHost.addTab(results, ResultsFragment.class, null);
		}
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState,
			PersistableBundle persistentState) {
		super.onRestoreInstanceState(savedInstanceState, persistentState);
		configureVisible = savedInstanceState.getBoolean("confVisible", true);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putBoolean("confVisible", configureVisible);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.findItem(R.id.configure).setVisible(configureVisible);
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case R.id.configure:
			startActivity(new Intent(this, ConfigActivity.class));
			return true;
		default:
			return super.onMenuItemSelected(featureId, item);
		}
	}

	private void init() {
		final ILog logger = new ILog(OS.ANDROID, null);
		Intent argsSrc = getIntent();
		ArrayList<String> args = new ArrayList<String>();
		String[] keys = CmdArgs.getKeys();
		for (String key : keys)
			addFromIntent(key, args, argsSrc);

		MttestModel.init(OS.ANDROID, this, null, args);
		final MttestModel model = MttestModel.instance();
		model.addObserver(this);
		model.addSuiteObserver(this);
		boolean start = false;
		try {
			start = Boolean.parseBoolean(argsSrc.getStringExtra("autostart"));
		} catch (Throwable e) {
			logger.e(e.getMessage());
		}
		if (start) {
			model.start();
		}
	}

	static private void addFromIntent(String argKey, ArrayList<String> list,
			Intent src) {
		if (src.hasExtra(argKey)) {
			list.add(argKey);
			list.add(src.getStringExtra(argKey));
		}
	}

	protected Handler updaterHandler = new Handler(Looper.getMainLooper()) {
		@Override
		public void handleMessage(Message arg) {
			Object e = arg.obj;
			if (e instanceof EventType && e.equals(EventType.FINISHED)) {
				tabHost.setCurrentTabByTag(RESULTS_TAG);
			}
		}
	};

	@Override
	public void update(Observable subject, EventType eventType) {
		if (subject instanceof TestSetSummary) {
			TestSetSummary s = (TestSetSummary) subject;
			if (s.isRoot()) {
				Message msg = new Message();
				msg.obj = eventType;
				updaterHandler.sendMessage(msg);
			}
		}

		if (subject instanceof MttestModel) {
			Message msg = new Message();
			msg.obj = eventType;
			updaterHandler.sendMessage(msg);
		}
	}

	@Override
	public void update(Observable subject, MttestMessage eventMsg) {
		// Do nothing
	}

	public void setConfigureVisible(boolean state) {
		configureVisible = state;
		invalidateOptionsMenu();
	}

}