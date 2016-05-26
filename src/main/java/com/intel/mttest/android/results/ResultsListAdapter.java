package com.intel.mttest.android.results;

import java.util.ArrayList;
import java.util.Collections;

import com.intel.mttest.reporter.ActivityReporter;
import com.intel.mttest.reporter.ActivityReporter.Result;
import com.intel.mttest.representation.Summary;
import com.intel.mttest.representation.TestExecutionStatus;
import com.intel.mttest.representation.TestSetSummary;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.intel.mttest.android.R;

public class ResultsListAdapter extends BaseAdapter {

	private final Context ctx;
	private float commonSize;
	private float headerSize;
	private int offsetSize;
	private ArrayList<Result> groups;
	private ArrayList<Result> tests;
	private ArrayList<Result> finalResults;
	private int sepNum;
	private ArrayList<Integer> type;

	public ResultsListAdapter(Context context) {
		this.ctx = context;
		this.commonSize = ctx.getResources().getDimensionPixelSize(
				R.dimen.text_size_common);
		this.headerSize = ctx.getResources().getDimensionPixelSize(
				R.dimen.text_size_header);
		this.offsetSize = (int) ctx.getResources().getDimension(
				R.dimen.default_padding);
		this.groups = new ArrayList<>();
		this.tests = new ArrayList<>();
		type = new ArrayList<>();
		finalResults = null;
		sepNum = 0;
	}

	public void update(ArrayList<Result> groups, ArrayList<Result> tests) {
		this.type = new ArrayList<>();
		this.groups = sortResults(groups);
		this.tests = sortResults(tests);
		finalResults = null;
		for (int i = 0; i < groups.size(); i++) {
			type.add(1);
		}
		type.add(0);
		sepNum = 1;
		for (int i = 0; i < tests.size(); i++) {
			type.add(2);
		}
		notifyDataSetChanged();
	}

	public void update(TestSetSummary rootSummary, ActivityReporter reporter) {
		sepNum = 0;
		this.type = new ArrayList<>();
		finalResults = new ArrayList<>();
		boolean first = true;
		for (Summary summaryIt : rootSummary.getSubSummaries()) {
			if (!(summaryIt instanceof TestSetSummary))
				continue;

			if (first) {
				first = false;
			} else {
				type.add(0);
				sepNum++;
			}

			TestSetSummary summary = (TestSetSummary) summaryIt;
			ArrayList<Result> tests = reporter.getTests(summary);
			finalResults.add(reporter.getGroup(summary));
			type.add(1);
			finalResults.addAll(tests);
			for (int i = 0; i < tests.size(); i++) {
				type.add(2);
			}
		}
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		if (finalResults == null) {
			return groups.size() + tests.size() + sepNum;
		} else {
			return finalResults.size() + sepNum;
		}
	}

	@Override
	public Object getItem(int position) {
		if (finalResults == null) {
			if (position < groups.size()) {
				return groups.get(position);

			} else if (position == groups.size()) {
				return null;
			} else {
				return tests.get(position - groups.size() - 1);
			}
		} else {
			if (getItemViewType(position) == 0) {
				return null;
			}
			int pos = 0;
			for (int i = 0; i < position; i++) {
				if (getItemViewType(i) != 0) {
					pos++;
				}
			}
			return finalResults.get(pos);
		}
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isEnabled(int position) {
		return getItemViewType(position) != 0;
	}

	@Override
	public int getViewTypeCount() {
		return 3;
	}

	@Override
	public int getItemViewType(int position) {
		return type.get(position);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final int t = getItemViewType(position);

		LayoutInflater inflater = (LayoutInflater) ctx
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = convertView;
		if (v == null) {
			if (t == 0) {
				v = inflater.inflate(R.layout.line_break, parent, false);
			} else {
				v = inflater.inflate(R.layout.row_with_results, parent, false);
			}
		}

		if (t != 0) {
			Result r = (Result) getItem(position);
			TextView testName = (TextView) v.findViewById(R.id.rwrl_test);
			TextView testScore = (TextView) v.findViewById(R.id.rwrl_score);
			String testNameStr = r.getName();
			String testScoreStr = r.getResult();
			if (finalResults != null) {
				if (testScoreStr.equals("waiting")) {
					testScoreStr = "-  ";
				}
			}
			if (t == 1) {
				testName.setTextSize(TypedValue.COMPLEX_UNIT_PX, headerSize);
			} else {
				testName.setTextSize(TypedValue.COMPLEX_UNIT_PX, commonSize);
				if (finalResults != null) {
					testNameStr = "    " + testNameStr;
				}
			}
			testName.setText(testNameStr);
			testScore.setText(testScoreStr);
		}
		return v;
	}

	private ArrayList<Result> sortResults(ArrayList<Result> data) {
		ArrayList<Result> tmp1 = new ArrayList<Result>();
		ArrayList<Result> tmp2 = new ArrayList<Result>();
		int i = 0;
		for (; i < data.size()
				&& !TestExecutionStatus.Status.NONE.equals(data.get(i)
						.getStatus()); i++) {
			tmp1.add(data.get(i));
		}
		for (; i < data.size(); i++) {
			tmp2.add(data.get(i));
		}
		Collections.reverse(tmp1);
		Collections.reverse(tmp2);
		tmp1.addAll(tmp2);
		return tmp1;
	}

}
