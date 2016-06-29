package com.intel.mttest.android.results;

import java.util.ArrayList;

import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.intel.mttest.android.R;
import com.intel.mttest.android.adapters.data.GroupData;
import com.intel.mttest.reporter.ActivityReporter.Result;

public class ResultsListAdapter extends BaseExpandableListAdapter {

	private final Context ctx;
	private float commonSize;
	private float headerSize;
	private int offsetSize;
	private ArrayList<GroupData> groups;

	public ResultsListAdapter(Context context) {
		this.ctx = context;
		this.commonSize = ctx.getResources().getDimensionPixelSize(
				R.dimen.text_size_common);
		this.headerSize = ctx.getResources().getDimensionPixelSize(
				R.dimen.text_size_header);
		this.offsetSize = (int) ctx.getResources().getDimension(
				R.dimen.default_padding);
		this.groups = new ArrayList<>();
	}

	synchronized public void update(ArrayList<GroupData> groups) {
		this.groups = groups;
		notifyDataSetChanged();
	}

	@Override
	synchronized public Result getChild(int groupPos, int childPos) {
		return groups.get(groupPos).getTestCaseResult(childPos);
	}

	@Override
	synchronized public long getChildId(int groupPos, int childPos) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	synchronized public View getChildView(int groupPos, int childPos, boolean isLastChild, View convertView,
			ViewGroup parent) {
		View v = convertView;
		if (v == null) {
			LayoutInflater inflater = (LayoutInflater) ctx
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = inflater.inflate(R.layout.row_with_results, parent, false);
		}
		Result r = (Result) getChild(groupPos, childPos);
		TextView testName = (TextView) v.findViewById(R.id.rwrl_test);
		testName.setPadding(2 * offsetSize, testName.getPaddingTop(), testName.getPaddingEnd(), testName.getPaddingBottom());
		TextView groupScore = (TextView) v.findViewById(R.id.rwrl_score);
		testName.setText(r.getName());
		testName.setTextSize(TypedValue.COMPLEX_UNIT_PX, commonSize);
		groupScore.setText(r.getResult());
		return v;
	}

	@Override
	synchronized public int getChildrenCount(int groupPos) {
		return groups.get(groupPos).testCases.size();
	}

	@Override
	synchronized public GroupData getGroup(int groupPos) {
		return groups.get(groupPos);
	}

	@Override
	synchronized public int getGroupCount() {
		return groups.size();
	}

	@Override
	synchronized public long getGroupId(int groupPos) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	synchronized public View getGroupView(int groupPos, boolean isExpanded, View convertView, ViewGroup parent) {
		View v = convertView;
		if (v == null) {
			LayoutInflater inflater = (LayoutInflater) ctx
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = inflater.inflate(R.layout.row_with_results, parent, false);
		}
		Result r = getGroup(groupPos).get();
		TextView groupName = (TextView) v.findViewById(R.id.rwrl_test);
		groupName.setPadding(offsetSize, groupName.getPaddingTop(), groupName.getPaddingEnd(), groupName.getPaddingBottom());
		TextView groupScore = (TextView) v.findViewById(R.id.rwrl_score);
		groupName.setText(r.getName());
		groupName.setTextSize(TypedValue.COMPLEX_UNIT_PX, headerSize);
		groupScore.setText(r.getResult());
		return v;
	}

	@Override
	synchronized public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	synchronized public boolean isChildSelectable(int groupPos, int childPos) {
		return true;
	}

}
