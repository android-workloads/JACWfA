package com.intel.mttest.android.configure;

import java.util.ArrayList;

import com.intel.mttest.android.R;
import com.intel.mttest.config.ConfigParams;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class AccuracyChangedListener implements OnCheckedChangeListener {

	ArrayList<ConfigParams> values;
	Handler parentUpdater;

	public AccuracyChangedListener(Handler parentUpdater,
			ArrayList<ConfigParams> args, ConfigParams picked) {
		super();
		this.parentUpdater = parentUpdater;
		values = args;
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int id) {
		RadioButton variant = (RadioButton) group.findViewById(id);
		ConfigParams checked = null;
		for (ConfigParams cfg : values) {
			if (cfg.getName().equals(variant.getText())) {
				checked = cfg;
			}
		}
		if (checked != null) {
			Message msg = new Message();
			msg.obj = checked;
			parentUpdater.sendMessage(msg);
		}
	}

}
