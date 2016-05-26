package com.intel.mttest.android.test;

import java.util.ArrayList;
import java.util.Arrays;

import com.intel.mttest.android.R;
import com.intel.mttest.android.StarterActivity;
import com.intel.mttest.android.results.ResultsListAdapter;
import com.intel.mttest.cmd.MttestModel;
import com.intel.mttest.config.ConfigParams.Field;
import com.intel.mttest.exception.MTTestException;
import com.intel.mttest.reporter.ActivityReporter;
import com.intel.mttest.reporter.EventType;
import com.intel.mttest.reporter.MttestMessage;
import com.intel.mttest.reporter.Observable;
import com.intel.mttest.reporter.Observer;
import com.intel.mttest.reporter.ActivityReporter.Result;
import com.intel.mttest.representation.Summary;
import com.intel.mttest.representation.TestSet;
import com.intel.mttest.representation.TestSetSummary;

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
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class TestFragment extends Fragment implements Observer {

	private static final String TESTING_FINISHED_MESSAGE = "We finish";
	public static boolean testSetWasChanged = true;
	private static boolean pick[];
	protected TestGroupsListAdapter adapter;
	protected MttestModel model;
	private ProgressBar progress;
	private Button start;
	private ListView list;

	private TextView additionalInfo;
	protected ResultsListAdapter resultsAdapter;
	private static ActivityReporter reporter = new ActivityReporter();

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
		resultsAdapter = new ResultsListAdapter(this.getActivity());

		model = MttestModel.instance();
		model.addObserver(this);
		model.addSuiteObserver(this);
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
		list.setEmptyView(getView().findViewById(R.id.rl_when_list_empty));
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

		additionalInfo = (TextView) getView().findViewById(
				R.id.tl_additional_info);
	}

	@Override
	public void onResume() {
		super.onResume();
		TestSetSummary summary = model.getSummary();
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
		} else {
			list.setAdapter(resultsAdapter);
		}
		updateStatus();
	}

	protected void updateStatus() {
		TestSetSummary summary = model.getSummary();
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

				list.setAdapter(resultsAdapter);
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

		ArrayList<Summary> subSummaries = new ArrayList<Summary>(
				summary.getSubSummaries());
		TestSetSummary localRoot = null;
		for (Summary ss : subSummaries) {
			if (ss instanceof TestSetSummary) {
				localRoot = (TestSetSummary) ss;
			}
		}

		if (localRoot != null) {
			ArrayList<Result> resGroups = reporter.getGroups(localRoot);
			ArrayList<Result> resTests = reporter.getTests(localRoot);
			resultsAdapter.update(resGroups, resTests);
		}
		try {
			int t = Integer.parseInt(summary.getSource().getConfigParams()
					.getValue(Field.threads));
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
	}

}