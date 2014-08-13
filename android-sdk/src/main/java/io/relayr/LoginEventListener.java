package io.relayr;

public interface LoginEventListener {
	void onSuccessUserLogIn();
    void onErrorLogin(Throwable e);
}
