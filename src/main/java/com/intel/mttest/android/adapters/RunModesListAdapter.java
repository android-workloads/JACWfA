package com.intel.mttest.android.adapters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;

import android.content.Context;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.intel.mttest.android.R;
import com.intel.mttest.android.adapters.data.GroupData;
import com.intel.mttest.android.adapters.data.RunModeData;
import com.intel.mttest.android.results.ResultsListAdapter;
import com.intel.mttest.reporter.ActivityReporter;
import com.intel.mttest.reporter.ActivityReporter.Result;
import com.intel.mttest.representation.Summary;
import com.intel.mttest.representation.TestExecutionStatus;
import com.intel.mttest.representation.TestSetSummary;

public class RunModesListAdapter extends BaseExpandableListAdapter {
	
	private Context ctx;
	private float headerSize;
	private ArrayList<RunModeData> runmodes;
    private ConcurrentHashMap<Integer, ExpandableListView> viewMapping = new ConcurrentHashMap<Integer, ExpandableListView>(); 
    private ConcurrentHashMap<Integer, ArrayList<Integer>> expandMapping = new ConcurrentHashMap<Integer, ArrayList<Integer>>(); 
	
	
	public RunModesListAdapter(Context context) {
		this.ctx = context;
		this.headerSize = ctx.getResources().getDimensionPixelSize(
				R.dimen.text_size_header1);
		runmodes = new ArrayList<>();
	}

	private void updateData(TestSetSummary rootSummary, ActivityReporter reporter) {
		ArrayList<Result> runmodes = new ArrayList<>();
		ArrayList<ArrayList<Result>> groups = new ArrayList<>();
		ArrayList<ArrayList<ArrayList<Result>>> tests = new ArrayList<>();
		runmodes = reporter.getGroups(rootSummary);
		groups = new ArrayList<>();
		tests= new ArrayList<>();
		for (Summary runModeSummary : rootSummary.getSubSummaries()) {
			if (runModeSummary instanceof TestSetSummary) {
				TestSetSummary summary = (TestSetSummary) runModeSummary;
				groups.add(reporter.getGroups(summary));
				tests.add(reporter.getTestsHierachicaly(summary));
			}
		}
		this.runmodes.clear();
		for(int i = 0; i < runmodes.size(); i++) {
			ArrayList<GroupData> g = new ArrayList<>();
			for (int j = 0; j < groups.get(i).size(); j++) {
				g.add(new GroupData(groups.get(i).get(j), tests.get(i).get(j)));
			}
			this.runmodes.add(new RunModeData(runmodes.get(i), g));
		}
	}
	
	public void updateWithSorting(TestSetSummary rootSummary, ActivityReporter reporter) {
		updateData(rootSummary, reporter);
		runmodes = sortResults(runmodes);
		notifyDataSetChanged();
	}
	public void update(TestSetSummary rootSummary, ActivityReporter reporter) {
		updateData(rootSummary, reporter);
		notifyDataSetChanged();
	}

	@Override
	public GroupData getChild(int groupPos, int childPos) {
		return runmodes.get(groupPos).getGroupResult(childPos);
	}

	@Override
	public long getChildId(int groupPos, int childPos) {
		return groupPos*getGroupCount() + childPos;
	}

	@Override
	synchronized public View getChildView(int groupPos, int childPos, boolean isLastChild, View convertView,
			ViewGroup parent) {
		ExpandableListView v = (ExpandableListView) convertView;
		if (v == null) {
			v = new ExpandableListView(ctx) {
			
				@Override
				protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
					heightMeasureSpec = MeasureSpec.makeMeasureSpec(999999, MeasureSpec.AT_MOST);
					super.onMeasure(widthMeasureSpec, heightMeasureSpec);
				}
			
			};
			ResultsListAdapter adapter = new ResultsListAdapter(ctx);
			v.setAdapter(adapter);
		}
		v.setSaveEnabled(true);
		ResultsListAdapter adapter = (ResultsListAdapter) v.getExpandableListAdapter();
		adapter.update(runmodes.get(groupPos).groups);
		ExpandableListView old = viewMapping.get(groupPos);
		viewMapping.put(groupPos, v);
		setExpandedList(v, expandMapping.get(groupPos));
		return v;
	}

	@Override
	public int getChildrenCount(int groupPos) {
		return 1;
	}

	@Override
	public RunModeData getGroup(int groupPos) {
		return runmodes.get(groupPos);
	}

	@Override
	public int getGroupCount() {
		return runmodes.size();
	}

	@Override
	public long getGroupId(int groupPos) {
		return groupPos;
	}

	@Override
	public View getGroupView(int groupPos, boolean isExpanded, View convertView, ViewGroup parent) {
		View v = convertView;
		if (v == null) {
			LayoutInflater inflater = (LayoutInflater) ctx
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = inflater.inflate(R.layout.row_with_results, parent, false);
		}
		Result r = getGroup(groupPos).get();
		TextView modeName = (TextView) v.findViewById(R.id.rwrl_test);
		TextView modeScore = (TextView) v.findViewById(R.id.rwrl_score);
		modeName.setText(r.getName());
		modeName.setTextSize(TypedValue.COMPLEX_UNIT_PX, headerSize);
		modeScore.setText(r.getResult());
		return v;
	}

	@Override
	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPos, int childPos) {
		return true;
	}

	synchronized private ArrayList<RunModeData> sortResults(ArrayList<RunModeData> data) {
		
		ArrayList<RunModeData> tmp1 = new ArrayList<>();
		ArrayList<RunModeData> tmp2 = new ArrayList<>();
		int i = 0;
		for (; i < data.size() && !TestExecutionStatus.Status.NONE.equals(data.get(i).get().getStatus()); i++) {
			tmp1.add(data.get(i));
		}
		for (; i < data.size(); i++) {
			tmp2.add(data.get(i));
		}
		Collections.reverse(tmp1);
		Collections.reverse(tmp2);
		tmp1.addAll(tmp2);
		for(int k = 0; k < tmp1.size(); k++) {
			tmp1.get(k).groups = sortGroups(tmp1.get(k).groups);
		}
		return tmp1;
	}

	private ArrayList<GroupData> sortGroups(ArrayList<GroupData> data) {
		
		ArrayList<GroupData> tmp1 = new ArrayList<>();
		ArrayList<GroupData> tmp2 = new ArrayList<>();
		int i = 0;
		for (; i < data.size() && !TestExecutionStatus.Status.NONE.equals(data.get(i).get() .getStatus()); i++) {
			tmp1.add(data.get(i));
		}
		for (; i < data.size(); i++) {
			tmp2.add(data.get(i));
		}
		Collections.reverse(tmp1);
		Collections.reverse(tmp2);
		tmp1.addAll(tmp2);
		for(int k = 0; k < tmp1.size(); k++) {
			tmp1.get(k).testCases = sortTests(tmp1.get(k).testCases);
		}
		return tmp1;
	}

	private ArrayList<Result> sortTests(ArrayList<Result> data) {
		
		ArrayList<Result> tmp1 = new ArrayList<>();
		ArrayList<Result> tmp2 = new ArrayList<>();
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

    synchronized ArrayList<Integer> getExpandedList(ExpandableListView list) {
        ArrayList<Integer> expanded = new ArrayList<Integer>();
        if(list != null) {
            int sz = list.getCount();
            for(int groupPos = 0; groupPos < sz; groupPos++)
                if(list.isGroupExpanded(groupPos))
                    expanded.add(groupPos);
        }
        return expanded;
    }
    synchronized void setExpandedList(ExpandableListView list, ArrayList<Integer> expanded) {
        try {
        if(list != null && expanded != null) {
            for(int elemId : expanded) {
                list.expandGroup(elemId);
            }
        }
        } catch (Throwable e) {
        }
    }
	
    public void saveExpanded(int groupPos) {
        ExpandableListView list = viewMapping.get(groupPos);
        ArrayList<Integer> expandedGroups = getExpandedList(list);
        expandMapping.put(groupPos, expandedGroups);
    }

    public void restoreExpanded(int groupPos) {
        ExpandableListView list = viewMapping.get(groupPos);
        ArrayList<Integer> expandedGroups = expandMapping.get(groupPos);
        setExpandedList(list, expandedGroups);
    }

    synchronized public void resetMapping() {
        expandMapping.clear();
        viewMapping.clear();
    }

    public void clearData(Context context) {
        this.ctx = context;
        this.headerSize = ctx.getResources().getDimensionPixelSize(
                R.dimen.text_size_header1);
        runmodes = new ArrayList<>();
        
    }
}
