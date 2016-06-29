package com.intel.mttest.android.configure;

import java.util.ArrayList;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.intel.mttest.android.R;

public class ThreadPicker{

	public static final int sysCores = Runtime.getRuntime().availableProcessors();
	
	private ArrayList<LinearLayout> pickers;
	private ArrayList<Integer> values;
	
	private LayoutInflater inflater;
	private ViewGroup parent;
	private Handler handler;

	public ThreadPicker(Context context, ViewGroup parent, Handler handler, ArrayList<Integer> values) {
		this.handler = handler;
		this.parent = parent;

		this.values = new ArrayList<>();
		pickers = new ArrayList<>();
		
		inflater = LayoutInflater.from(context);
		
		for(int v : values) {
			addPicker(v);
		}
	}

	public ArrayList<Integer> getValues() {
		return values;
	}

	public String getCommaSeparatedValuesAsString() {
		String result = "";
		ArrayList<Integer> threads = getValues();
		for (int i = 0; i < threads.size(); i++) {
			if(i > 0) {
				result += ",";
			}
			result += threads.get(i).toString();
		}
		return result;
	}
	
	public void addPicker(int value) {
		LinearLayout picker = (LinearLayout) inflater.inflate(R.layout.thread_picker, null, false);
		pickers.add(picker);
		values.add(value);
		parent.addView(picker);
		int index = pickers.size() - 1;
		
		TextView text = (TextView) picker.findViewById(R.id.cl_threads_count);		
		Button increase = (Button) picker.findViewById(R.id.cl_threads_increase_button);
		Button decrease = (Button) picker.findViewById(R.id.cl_threads_decrease_button);

		text.setText(Integer.toString(value));
		increase.setOnClickListener(increaseAction(index));
		decrease.setOnClickListener(decreaseAction(index));
	}
	
	public void deleteLastPicker() {
		int lastIndex = pickers.size() - 1;
		values.remove(lastIndex);
		parent.removeView(pickers.get(lastIndex));
		pickers.remove(lastIndex);
	}
	
	public int numPickers() {
		return values.size();
	}
	
	private OnClickListener increaseAction(final int index) {
		return new OnClickListener() {
			@Override
			public void onClick(View view) {
				if (values.get(index) < sysCores) {
					values.set(index, values.get(index) + 1);
					TextView text = (TextView) pickers.get(index).findViewById(R.id.cl_threads_count);
					text.setText(Integer.toString(values.get(index)));
					Message msg = new Message();
					msg.obj = R.id.cl_threads_increase_button;
					handler.sendMessage(msg);
				}
			}
		};
	}

	private OnClickListener decreaseAction(final int index) {
		return new OnClickListener() {
			@Override
			public void onClick(View view) {
				if (values.get(index) > 1) {
					values.set(index, values.get(index) - 1);
					TextView text = (TextView) pickers.get(index).findViewById(R.id.cl_threads_count);
					text.setText(Integer.toString(values.get(index)));
					Message msg = new Message();
					msg.obj = R.id.cl_threads_decrease_button;
					handler.sendMessage(msg);
				}
			}
		};
	}

}