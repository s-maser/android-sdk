package io.relayr.core.observers;

public interface Observer<T> {
	void notify(T model);
}
