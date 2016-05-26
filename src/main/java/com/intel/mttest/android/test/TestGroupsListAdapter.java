package com.intel.mttest.android.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import com.intel.mttest.android.R;
import com.intel.mttest.representation.TestSet;

import android.content.Context;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ListAdapter;
import android.widget.TextView;

public class TestGroupsListAdapter extends BaseAdapter implements ListAdapter {

	Context ctx;
	TestSet root;
	Handler parentUpdater;
	private ArrayList<TestSet> sets;
	private boolean isChecked[];

	public TestGroupsListAdapter(Context ctx, Handler parentUpdater) {
		super();
		this.ctx = ctx;
		this.parentUpdater = parentUpdater;
		sets = new ArrayList<TestSet>();
		isChecked = new boolean[0];
	}

	synchronized public boolean isPicked(int index) {
		return isChecked[index];
	}

	synchronized ArrayList<TestSet> getAllPicked() {
		ArrayList<TestSet> ret = new ArrayList<TestSet>();
		for (int i = 0; i < sets.size(); i++)
			if (isChecked[i])
				ret.add(sets.get(i));
		return ret;
	}

	@Override
	synchronized public int getCount() {
		return sets.size();
	}

	@Override
	synchronized public String getItem(int index) {
		return sets.get(index).getName();
	}

	@Override
	synchronized public long getItemId(int arg) {
		return sets.indexOf(arg);
	}

	@Override
	synchronized public View getView(final int pos, View curView,
			ViewGroup parent) {
		View view = curView;
		if (view == null) {
			LayoutInflater inflater = (LayoutInflater) ctx
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.row_with_checkbox, parent, false);
		}
		final String item = getItem(pos);
		TextView name = (TextView) view.findViewById(R.id.rwc_name);
		name.setText(item);
		boolean checked = isChecked[pos];

		CheckBox check = (CheckBox) view.findViewById(R.id.rwc_check);
		check.setChecked(checked);
		updateLineView(name, checked);
		return view;
	}

	public void updateLineView(TextView view, boolean checked) {
		
		if (checked) {
			view.setTextColor(ContextCompat.getColor(ctx, R.color.active_text));
		} else {
			view.setTextColor(ContextCompat.getColor(ctx, R.color.default_text));
		}
	}

	synchronized public void updateData(TestSet suite) {
		root = suite;
		ArrayList<TestSet> sets = new ArrayList<TestSet>(suite.getTestSubsets());
		ArrayList<TestSet> subsets = new ArrayList<TestSet>();
		for (TestSet set : sets)
			subsets.addAll(set.getTestSubsets());
		Collections.sort(subsets);
		isChecked = new boolean[subsets.size()];
		Arrays.fill(isChecked, true);
		this.sets = subsets;
		this.notifyDataSetChanged();
	}

	synchronized public boolean[] getPickedMask() {
		return Arrays.copyOf(isChecked, isChecked.length);
	}

	synchronized public void setPickedMask(boolean[] picked) {
		if (isChecked.length != picked.length) {
			// Absence of it cost about 2 hours of debugging
			throw new IllegalArgumentException(
					"Picked mask should have the same dimentions with list len. "
							+ isChecked.length + "!=" + picked.length);
		}
		isChecked = Arrays.copyOf(picked, picked.length);
		this.notifyDataSetChanged();
	}

	synchronized public void togglePickedStatus(int pos) {
		isChecked[pos] ^= true;
		this.notifyDataSetChanged();
	}

	public TestSet getRootTestSet() {
		return root;
	}
}
