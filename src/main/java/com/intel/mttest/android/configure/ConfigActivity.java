package com.intel.mttest.android.configure;

import java.util.ArrayList;

import com.intel.mttest.android.R;
import com.intel.mttest.android.test.TestFragment;
import com.intel.mttest.cmd.MttestModel;
import com.intel.mttest.config.ConfigParams;
import com.intel.mttest.representation.TestSet;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import com.intel.mttest.config.ConfigParams.Field;

public class ConfigActivity extends Activity {

	private ArrayList<ConfigParams> configList;
	private ConfigParams pickedConfig;
	private TestSet rootTestSet, pickedTestSet;
	private int threads = 1;

	protected Handler updaterHandler = new Handler(Looper.getMainLooper()) {
		@Override
		public void handleMessage(Message arg) {
			final MttestModel model = MttestModel.instance();
			Object x = arg.obj;
			if (x instanceof Integer) {
				if (x.equals(R.id.cl_threads_increase_button)) {
					threads++;
				}
				if (x.equals(R.id.cl_threads_decrease_button) && threads > 1) {
					threads--;
				}
				pickedConfig.setAbsValue(Field.threads,
						Integer.toString(threads));
				model.setPickedConfig(pickedConfig);
			}
			if (x instanceof ConfigParams) {
				pickedConfig = (ConfigParams) x;
				pickedConfig.setAbsValue(Field.threads,
						Integer.toString(threads));
				model.setPickedConfig(pickedConfig);
			}
			if (x instanceof TestSet) {
				pickedTestSet = (TestSet) x;
				model.setPickedTestSet(pickedTestSet.getFullName());
				TestFragment.testSetWasChanged = true;
			}
			ConfigActivity.this.updateStatus();
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.config_layout);

		final MttestModel model = MttestModel.instance();

		this.configList = model.getAllConfig();
		this.pickedConfig = model.getPickedConfig();
		this.rootTestSet = model.getRootTestSet();
		this.pickedTestSet = model.getPickedTestSet().getTestSubsets().get(0);

		try {
			threads = Integer.parseInt(pickedConfig.getValue(Field.threads));
		} catch (Throwable e) {
			threads = 1;
		}

		initRadioGroups();
		initIncDecButtons();
		updateStatus();
	}

	protected void initRadioGroups() {
		RadioGroup accuracy = (RadioGroup) findViewById(R.id.cl_accuracy_chooser);

		int checkedId = -1;
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		for (ConfigParams conf : configList) {
			RadioButton variant = (RadioButton) inflater.inflate(
					R.layout.config_radio_button, accuracy, false);
			variant.setText(conf.getName());
			accuracy.addView(variant);
			checkedId = conf.getName().equals(pickedConfig.getName()) ? variant
					.getId() : checkedId;
		}
		accuracy.clearCheck();
		accuracy.check(checkedId);

		accuracy.setOnCheckedChangeListener(new AccuracyChangedListener(
				updaterHandler, configList, pickedConfig));

		RadioGroup suite = (RadioGroup) findViewById(R.id.cl_suite_choser);

		checkedId = -1;
		for (TestSet set : rootTestSet.getTestSubsets()) {
			RadioButton variant = (RadioButton) inflater.inflate(
					R.layout.config_radio_button, suite, false);
			variant.setText(set.getShortName());
			suite.addView(variant);
			checkedId = set.getShortName().equals(pickedTestSet.getShortName()) ? variant
					.getId() : checkedId;
		}
		suite.clearCheck();
		suite.check(checkedId);

		suite.setOnCheckedChangeListener(new TestSetChangedListener(
				updaterHandler, rootTestSet, pickedTestSet));

	}

	protected void initIncDecButtons() {
		Button increase = (Button) findViewById(R.id.cl_threads_increase_button);
		Button decrease = (Button) findViewById(R.id.cl_threads_decrease_button);

		increase.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Message msg = new Message();
				msg.obj = R.id.cl_threads_increase_button;
				updaterHandler.sendMessage(msg);
			}
		});
		decrease.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Message msg = new Message();
				msg.obj = R.id.cl_threads_decrease_button;
				updaterHandler.sendMessage(msg);
			}
		});
	}

	protected void updateStatus() {
		{
			TextView view = (TextView) findViewById(R.id.cl_threads_count);
			if (view != null) {
				view.setText(threads + "");
			}
		}

		int totalTests = 0;
		int timePerTest = 0;

		{
			TextView view = (TextView) findViewById(R.id.cl_testset_details);
			if (view != null) {
				int groups = pickedTestSet.getTestSubsets().size();
				int tests = pickedTestSet.getTestCount();
				String ret = "";
				ret += tests + " test" + (tests == 1 ? "" : "s");
				ret += " in " + groups + " group" + (groups == 1 ? "" : "s");
				view.setText(ret);
				totalTests = tests;
			}
		}

		{
			View view = findViewById(R.id.cl_details_stage_warmup);
			if (view != null) {
				TextView title = (TextView) view
						.findViewById(R.id.cl_details_property_title);
				TextView value = (TextView) view
						.findViewById(R.id.cl_details_property_value);
				title.setText("Warming");
				int val = 0;
				try {
					val = Integer.parseInt(pickedConfig.getValue(Field.rampUp));
				} catch (Throwable e) {
				}
				value.setText(val + " ms per test");
				timePerTest += val;
			}
		}

		{
			View view = findViewById(R.id.cl_details_stage_steady);
			if (view != null) {
				TextView title = (TextView) view
						.findViewById(R.id.cl_details_property_title);
				TextView value = (TextView) view
						.findViewById(R.id.cl_details_property_value);
				title.setText("Measurement");
				int val = 0;
				try {
					val = Integer.parseInt(pickedConfig
							.getValue(Field.duration));
				} catch (Throwable e) {
				}
				value.setText(val + " ms per test");
				timePerTest += val;
			}
		}
		{
			View view = findViewById(R.id.cl_details_stage_sync);
			if (view != null) {
				TextView title = (TextView) view
						.findViewById(R.id.cl_details_property_title);
				TextView value = (TextView) view
						.findViewById(R.id.cl_details_property_value);
				title.setText("Synchronization");
				int val = 0;
				try {
					val = Integer.parseInt(pickedConfig
							.getValue(Field.rampDown));
				} catch (Throwable e) {
				}
				value.setText(val + " ms per test");
				timePerTest += val;
			}
		}

		{
			View view = (View) findViewById(R.id.cl_time_expected);
			if (view != null) {
				TextView title = (TextView) view
						.findViewById(R.id.cl_details_property_title);
				TextView value = (TextView) view
						.findViewById(R.id.cl_details_property_value);
				title.setText("Estimated time: ");
				double time = timePerTest * totalTests / 1000.0 / 60;
				String timeStr = " < 1";
				if (time > 1) {
					timeStr = String.format("%.1f", time);
				}
				value.setText(timeStr + " minutes");
			}
		}
	}
}