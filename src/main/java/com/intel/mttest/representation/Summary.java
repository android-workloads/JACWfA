package com.intel.mttest.representation;

import java.util.ArrayList;
import java.util.List;

import com.intel.mttest.reporter.EventType;
import com.intel.mttest.reporter.Observable;
import com.intel.mttest.reporter.Observer;
import com.intel.mttest.util.Identifiable;


public abstract class Summary extends Identifiable implements Observable {
	public Summary(MttestTest source, TestSetSummary owner) {
		this.source = source;
		this.owner = owner;
		this.status = new TestExecutionStatus(TestExecutionStatus.Status.NONE, null);
		this.agregatedStatus = new TestExecutionStatus(TestExecutionStatus.Status.NONE, null);
		this.observers = new ArrayList<Observer>();
	}

	public MttestTest getSource() {
		return source;
	}
	
	public String getShortName() {
		return source.getShortName();
	}
	
	public TestExecutionStatus getExecutionStatus() {
		return status;
	}
	
	public abstract int getDurationMs();
	public abstract int getTestsCompletedCount();
	public abstract double getMeanIterationMs();
	public abstract double getScore();

	public boolean isRoot() {
		return owner == null;
	}
	
	public void setInProgress()  {
		totalTimeInProgress = System.currentTimeMillis();
		status = TestExecutionStatus.getWorst(status, new TestExecutionStatus(TestExecutionStatus.Status.IN_PORGRESS, null));
		notifyObservers(EventType.STARTED);
	}
	public void setSuccesful() {
		updateAgregatedExecutionStatus(new TestExecutionStatus(TestExecutionStatus.Status.SUCCESS, null));
	}
	
	public void setWarning(String reason) {
		updateAgregatedExecutionStatus(new TestExecutionStatus(TestExecutionStatus.Status.WARNING, reason));
	}
	
	public void setFailed(String reason) {
		updateAgregatedExecutionStatus(new TestExecutionStatus(TestExecutionStatus.Status.FAIL, reason));
	}
	public void setInterrupted() {
		updateAgregatedExecutionStatus(new TestExecutionStatus(TestExecutionStatus.Status.INTERRUPT, null));
	}
	
	public void setDone() {
		status = TestExecutionStatus.getWorst(agregatedStatus, status);
		totalTimeInProgress = System.currentTimeMillis() - totalTimeInProgress;
		recalcOwner();
		notifyObservers(EventType.FINISHED);
	}
	public boolean isDone() {
		return status.isDone();
	}
	public boolean isInProgress() {
		return status.inProgress();
	}
	
	public TestSetSummary getOwner() {
		return owner;
	}
	public long getTotalTimeInProgressMs() {
		return totalTimeInProgress;
	}
	
	private volatile MttestTest source;
	private volatile long totalTimeInProgress;
	private volatile TestExecutionStatus status;
	private volatile TestExecutionStatus agregatedStatus;
	protected volatile TestSetSummary owner;
	
	protected void updateAgregatedExecutionStatus(TestExecutionStatus newStatus) {
		agregatedStatus = TestExecutionStatus.getWorst(agregatedStatus, newStatus);
	}
	protected void joinStatus(TestExecutionStatus newStatus) {
		status = TestExecutionStatus.getWorst(agregatedStatus, status);
	}
	protected void recalcOwner() {
		if(owner!=null) {
			owner.recalc(this);
		}
	}
		
	@Override
	public void addObserver(Observer observer) {
		observers.add(observer);
	}
	
	public void sendEvent(Observable source, EventType event) {
		for(Observer obs : observers) {
			obs.update(source, event);
		}
		if(owner != null && event.isCritical()) {
			owner.sendEvent(source, event);
		}
	}
	protected List<Observer> observers;
	
	protected void notifyObservers(EventType event) {
		sendEvent(this, event);
	}
	
	@Override
	public int compareTo(Identifiable ie) {
		if(ie instanceof Summary){
			Summary e = (Summary) ie;
			MttestTest src1 = getSource();
			MttestTest src2 = e.getSource();
			int x = src1.compareTo(src2);
			if(x != 0) return x;
		}
		return super.compareTo(ie);
	}
}
