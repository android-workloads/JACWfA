package com.intel.mttest.android.configure;

import java.util.ArrayList;
import java.util.List;

import com.intel.mttest.android.R;
import com.intel.mttest.config.ConfigParams;
import com.intel.mttest.representation.TestSet;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class TestSetChangedListener implements OnCheckedChangeListener {
	Activity ctx;
	List<TestSet> values;
	Handler parentUpdater;

	public TestSetChangedListener(Handler parentUpdater, TestSet root,
			TestSet picked) {
		this.parentUpdater = parentUpdater;
		values = root.getTestSubsets();
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int id) {
		RadioButton variant = (RadioButton) group.findViewById(id);
		TestSet checked = null;
		for (TestSet set : values) {
			if (set.getShortName().equals(variant.getText())) {
				checked = set;
			}
		}
		if (checked != null) {
			Message msg = new Message();
			msg.obj = checked;
			parentUpdater.sendMessage(msg);
		}
	}

}
