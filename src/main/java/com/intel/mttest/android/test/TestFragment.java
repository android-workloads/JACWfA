package com.intel.mttest.android.test;

import java.util.ArrayList;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.intel.mttest.android.R;
import com.intel.mttest.android.StarterActivity;
import com.intel.mttest.android.adapters.RunModesListAdapter;
import com.intel.mttest.android.adapters.TestGroupsListAdapter;
import com.intel.mttest.cmd.MttestModel;
import com.intel.mttest.config.ConfigParams.Field;
import com.intel.mttest.exception.MTTestException;
import com.intel.mttest.reporter.ActivityReporter;
import com.intel.mttest.reporter.EventType;
import com.intel.mttest.reporter.MttestMessage;
import com.intel.mttest.reporter.Observable;
import com.intel.mttest.reporter.Observer;
import com.intel.mttest.representation.Summary;
import com.intel.mttest.representation.TestSet;
import com.intel.mttest.representation.TestSetSummary;

public class TestFragment extends Fragment implements Observer {

	private static final String TESTING_FINISHED_MESSAGE = "We finish";
	public static boolean testSetWasChanged = true;
	private static boolean pick[];
	protected TestGroupsListAdapter adapter;
	protected MttestModel model;
	private ProgressBar progress;
	private Button start;
	private ListView list;
	private ExpandableListView listRunningResults;

	private TextView additionalInfo;
	protected RunModesListAdapter intermediateAdapter;
	private static ActivityReporter reporter = new ActivityReporter();
	static Bundle staticBundle;
	
	protected Handler updaterHandler = new Handler(Looper.getMainLooper()) {
		@Override
		public void handleMessage(Message arg) {
			TestFragment.this.updateStatus();
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);

		adapter = new TestGroupsListAdapter(getActivity(), updaterHandler);
		intermediateAdapter = new RunModesListAdapter(this.getActivity());

		model = MttestModel.instance();
		model.addObserver(this);
		model.addSuiteObserver(this);
		staticBundle = new Bundle();
	};

	@Override
	public View onCreateView(android.view.LayoutInflater inflater,
			android.view.ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.test_layout, container, false);
	};

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		start = (Button) getView().findViewById(R.id.tl_start);

		progress = (ProgressBar) getView().findViewById(R.id.tl_progress_bar);

		list = (ListView) getView().findViewById(R.id.tl_list);
		OnItemClickListener itemClickListener = new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int pos,
					long id) {
				if (parent.getAdapter() == adapter) {
					adapter.togglePickedStatus(pos);
					Message msg = new Message();
					msg.obj = adapter;
					updaterHandler.sendMessage(msg);
				}
			}
		};
		list.setOnItemClickListener(itemClickListener);
		
		listRunningResults = (ExpandableListView) getView().findViewById(R.id.tl_list_res);
		View v = getView().findViewById(R.id.tl_when_list_empty);
		listRunningResults.setEmptyView(v);
		listRunningResults.setSaveEnabled(true);
		v.setVisibility(View.GONE);
		
		additionalInfo = (TextView) getView().findViewById(
				R.id.tl_additional_info);
	}

	static Summary latestSummary = null;
	
	synchronized public void checkSummary(Summary newSummary) {
	    if(newSummary != latestSummary) {
	        latestSummary = newSummary;
	        staticBundle = new Bundle();
	        intermediateAdapter.resetMapping();
	    }
	}
	@Override
	public void onResume() {
		super.onResume();
		TestSetSummary summary = model.getSummary();
		checkSummary(summary);
		if (summary == null || summary.isDone()) {
			list.setAdapter(adapter);
			adapter.updateData(model.getPickedTestSet());
			if (!testSetWasChanged) {
				if (pick != null) {
					adapter.setPickedMask(pick);
				}
			} else {
				pick = adapter.getPickedMask();
				testSetWasChanged = false;
			}
			list.setVisibility(View.VISIBLE);
			listRunningResults.setVisibility(View.GONE);
		} else {
			listRunningResults.setAdapter(intermediateAdapter);
			listRunningResults.setVisibility(View.VISIBLE);
			list.setVisibility(View.GONE);
		}
		try {
		    onViewStateRestoredLocal(staticBundle);
		} catch (Throwable e) {
		}
		updateStatus();
	}

	protected void updateStatus() {
		TestSetSummary summary = model.getSummary();
        checkSummary(summary);
		if (summary == null || summary.isDone()) {
			updateDefault();
		} else {
			updateInProgress(summary);
		}
	}

	protected void updateDefault() {
		start.setText(R.string.start_button);
		start.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				ArrayList<TestSet> picked = adapter.getAllPicked();
				Context ctx = TestFragment.this.getContext();
				if (picked.size() == 0) {
					Toast.makeText(ctx, R.string.test_pick_request,
							Toast.LENGTH_LONG).show();
					return;
				}
				listRunningResults.setVisibility(View.VISIBLE);
				list.setVisibility(View.GONE);

				listRunningResults.setAdapter(intermediateAdapter);
				pick = adapter.getPickedMask();

				Toast.makeText(ctx,
						"Starting of " + picked.size() + " group(s)",
						Toast.LENGTH_LONG).show();
				String specificator = "";
				for (TestSet set : picked)
					specificator += "," + set.getFullName();
				model.setRunningTestSet(specificator);
				model.start();
			}
		});
		progress.setProgress(0);
		StarterActivity act = (StarterActivity) getActivity();
		if (act != null) {
			act.setConfigureVisible(true);
		}
	}

	protected void updateInProgress(TestSetSummary summary) {
		start.setText(R.string.stop_button);
		start.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Toast.makeText(TestFragment.this.getContext(),
						R.string.stopping_hint, Toast.LENGTH_LONG).show();

				list.setAdapter(adapter);

				model.stop();
			}
		});
		progress.setProgress((int) summary.getProgress());
		intermediateAdapter.updateWithSorting(summary, reporter);
		try {
			String t = summary.getSource().getConfigParams().getValue(Field.threads);
			additionalInfo.setText("num threads: " + t + "\n"
					+ "precision mode: " + model.getPickedConfig().getName());
			additionalInfo.setVisibility(View.VISIBLE);
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MTTestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		StarterActivity act = (StarterActivity) getActivity();
		if (act != null) {
			act.setConfigureVisible(false);
		}		
	}

	@Override
	public void update(Observable subject, EventType eventType) {
		if (subject instanceof TestSetSummary) {
			TestSetSummary s = (TestSetSummary) subject;
			if (eventType.equals(EventType.FINISHED) && s.isRoot()) {
				pick = null;				
				Message msg = new Message();
				msg.obj = TESTING_FINISHED_MESSAGE;
				updaterHandler.sendMessage(msg);
			}
		}
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

	@Override
	public void onPause() {
		super.onPause();
		pick = adapter.getPickedMask();
		staticBundle = new Bundle();
		onSaveInstanceStateLocal(staticBundle);
	}

	
	//------------------------
	final String expandedGroupsBundleName="expandedGroups";
//	@Override
//    public void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//        onSaveInstanceStateLocal(outState);
//	}


	
    synchronized public void onSaveInstanceStateLocal(Bundle outState) {    
	    ArrayList<Integer> expandedGroups = new ArrayList<Integer>();
	    if(listRunningResults != null) {
	        int sz = listRunningResults.getCount();
	        for(int groupPos = 0; groupPos < sz; groupPos++) {
	            if(listRunningResults.isGroupExpanded(groupPos)) {
	                expandedGroups.add(groupPos);
        	        intermediateAdapter.saveExpanded(groupPos);
	            }
	        }
	        outState.putIntegerArrayList(expandedGroupsBundleName, expandedGroups);
	    }
	}
	
//	@Override
//    public void onViewStateRestored(Bundle savedInstanceState) {
//        super.onViewStateRestored(savedInstanceState);
//        if(savedInstanceState != null)
//            onViewStateRestoredLocal(savedInstanceState);
//	    
//	}
	synchronized public void onViewStateRestoredLocal(Bundle savedInstanceState) {
	    if(intermediateAdapter != null) {
	        ArrayList<Integer> expandedGroups = savedInstanceState.getIntegerArrayList(expandedGroupsBundleName);
	        if(expandedGroups != null) {
    	        for(int groupPos : expandedGroups) {
    	            listRunningResults.expandGroup(groupPos);
    	            intermediateAdapter.restoreExpanded(groupPos);
    	        }
	        }
	    }
	}
}