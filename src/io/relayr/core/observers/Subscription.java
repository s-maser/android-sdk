package io.relayr.core.observers;


public class Subscription<T> {

	private Observer<T> observer;
	private Observable<T> observable;

	public Subscription(Observer<T> observer, Observable<T> observable) {
		this.observer = observer;
		this.observable = observable;
	}

	public boolean isSubscribed() {
		return observable.hasObserver(observer);
	}

	public void unsubscribe() {
		observable.removeObserver(observer);
	}

	public void subscrible() {
		observable.addObserver(observer);
	}
}
