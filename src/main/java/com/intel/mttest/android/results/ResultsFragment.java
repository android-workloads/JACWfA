package com.intel.mttest.android.results;

import java.util.ArrayList;

import com.intel.mttest.android.R;
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
import com.intel.mttest.representation.TestSetSummary;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

public class ResultsFragment extends Fragment implements Observer {

	protected ResultsListAdapter resultsAdapter;
	private ListView resultsList;
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
		resultsAdapter = new ResultsListAdapter(this.getActivity());
		resultsList = (ListView) getView().findViewById(R.id.rl_results_list);
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
	}

	@Override
	public void onResume() {
		super.onResume();
		updateStatus();
	}

	protected void updateStatus() {
		TestSetSummary summary = model.getSummary();
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
					int t = Integer.parseInt(summary.getSource()
							.getConfigParams().getValue(Field.threads));
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
				resultsAdapter.update(localRoot, reporter);
			}
		}
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

}