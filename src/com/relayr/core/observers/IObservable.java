package com.relayr.core.observers;

public interface IObservable<T> {
	void addObserver(Observer<T> observer);
    void removeObserver(Observer<T> observer);
}
