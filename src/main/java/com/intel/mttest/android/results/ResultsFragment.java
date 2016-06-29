package com.intel.mttest.android.results;

import java.util.ArrayList;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.intel.mttest.android.R;
import com.intel.mttest.android.adapters.RunModesListAdapter;
import com.intel.mttest.cmd.MttestModel;
import com.intel.mttest.config.ConfigParams.Field;
import com.intel.mttest.exception.MTTestException;
import com.intel.mttest.reporter.ActivityReporter;
import com.intel.mttest.reporter.ActivityReporter.Result;
import com.intel.mttest.reporter.EventType;
import com.intel.mttest.reporter.MttestMessage;
import com.intel.mttest.reporter.Observable;
import com.intel.mttest.reporter.Observer;
import com.intel.mttest.representation.Summary;
import com.intel.mttest.representation.TestSetSummary;

public class ResultsFragment extends Fragment implements Observer {

	protected RunModesListAdapter resultsAdapter;
	private ExpandableListView resultsList;
	private TextView additionalInfo;
	private TextView totalResult;
	protected MttestModel model;
	private static ActivityReporter reporter = new ActivityReporter();

	protected Handler updaterHandler = new Handler(Looper.getMainLooper()) {
		@Override
		public void handleMessage(Message arg) {
			ResultsFragment.this.updateStatus();
		}
	};

	@Override
	public View onCreateView(android.view.LayoutInflater inflater,
			android.view.ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.running_layout, container, false);
	};

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
        resultsAdapter.clearData(this.getActivity());
		resultsList = (ExpandableListView) getView().findViewById(R.id.rl_results_list);
		resultsList.setAdapter(resultsAdapter);
		resultsList.setEmptyView(getView()
				.findViewById(R.id.rl_when_list_empty));

		totalResult = (TextView) getView().findViewById(R.id.rl_total_results);
		additionalInfo = (TextView) getView().findViewById(
				R.id.rl_additional_info);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		model = MttestModel.instance();
		model.addObserver(this);
		model.addSuiteObserver(this);
        resultsAdapter = new RunModesListAdapter(this.getActivity());
	}

	@Override
	public void onResume() {
		super.onResume();
		updateStatus();
	}

	
	protected static Summary lastSummary = null;
	protected static Bundle staticBundle = new Bundle();
	
	synchronized protected void checkSummary(Summary newSummary) {
        if(newSummary != lastSummary) {
            lastSummary = newSummary;
            staticBundle = new Bundle();
            resultsAdapter.resetMapping();
        }
    }
	
	
	protected void updateStatus() {
		TestSetSummary summary = model.getSummary();
		checkSummary(summary);
		if (summary == null) {
			updateDefault();
			return;
		}
		boolean inProgress = !summary.isDone();
		if (!inProgress) {
			updateFinished(summary);
		}

		ArrayList<Summary> subSummaries = new ArrayList<Summary>(
				summary.getSubSummaries());
		TestSetSummary localRoot = null;
		for (Summary ss : subSummaries) {
			if (ss instanceof TestSetSummary) {
				localRoot = (TestSetSummary) ss;
			}
		}

		if (localRoot != null) {
			if (!inProgress) {
				try {
					String t = summary.getSource()
							.getConfigParams().getValue(Field.threads);
					additionalInfo.setText("num threads: " + t + "\n"
							+ "precision mode: "
							+ model.getPickedConfig().getName());
					additionalInfo.setVisibility(View.VISIBLE);
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (MTTestException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				resultsAdapter.update(summary, reporter);
			}
		}
		try {
            onViewStateRestoredLocal(staticBundle);
        } catch (Throwable e) {
        }
	}

	@Override
	public void onPause() {
        staticBundle = new Bundle();
        onSaveInstanceStateLocal(staticBundle);
	    super.onPause();
	}
	
	public void updateFinished(TestSetSummary summary) {
		Result res = reporter.getGroup(summary);
		if (res.getResult().equals("interrupted")) {
			totalResult.setText(res.getResult());
		} else {
			totalResult
					.setText("Total score  " + res.getResult() + "  ops/sec");
		}
		totalResult.setVisibility(View.VISIBLE);
	}

	public void updateDefault() {
		totalResult.setVisibility(View.GONE);
	}

	@Override
	public void update(Observable subject, EventType eventType) {
		if (subject instanceof Summary) {
			Message msg = new Message();
			msg.obj = subject;
			updaterHandler.sendMessage(msg);
		}
		if (subject instanceof MttestModel) {
			Message msg = new Message();
			msg.obj = subject;
			updaterHandler.sendMessage(msg);
		}
	}

	@Override
	public void update(Observable subject, MttestMessage eventMsg) {
		// Do nothing
	}

	final static String expandedGroupsBundleName = "expandedGroupsResults";
	
    synchronized public void onSaveInstanceStateLocal(Bundle outState) {    
        ArrayList<Integer> expandedGroups = new ArrayList<Integer>();
        if(resultsList != null) {
            int sz = resultsList.getCount();
            for(int groupPos = 0; groupPos < sz; groupPos++) {
                if(resultsList.isGroupExpanded(groupPos)) {
                    expandedGroups.add(groupPos);
                    resultsAdapter.saveExpanded(groupPos);
                }
            }
            outState.putIntegerArrayList(expandedGroupsBundleName, expandedGroups);
        }
    }
    
    synchronized public void onViewStateRestoredLocal(Bundle savedInstanceState) {
        if(resultsAdapter != null) {
            ArrayList<Integer> expandedGroups = savedInstanceState.getIntegerArrayList(expandedGroupsBundleName);
            if(expandedGroups != null) {
                for(int groupPos : expandedGroups) {
                    resultsList.expandGroup(groupPos);
                    resultsAdapter.restoreExpanded(groupPos);
                }
            }
        }
    }
    
}