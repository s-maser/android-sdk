package io.relayr;

public interface LoginEventListener {
	public void onSuccessUserLogIn();
    public void onErrorLogin(Throwable e);
}
