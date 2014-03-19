package com.relayr.core.activity;

import java.util.Date;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.relayr.R;
import com.relayr.core.api.Relayr_ApiCall;
import com.relayr.core.api.Relayr_ApiConnector;
import com.relayr.core.error.Relayr_Exception;
import com.relayr.core.settings.Relayr_SDKSettings;
import com.relayr.core.user.Relayr_User;

public class Relayr_LoginActivity extends Relayr_Activity {

	private final int RELAYR_MISSPARAMETER = 0;
	private final int RELAYR_LOGINFAIL = 1;
	private final int RELAYR_ERROR = 2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		int layoutID = getResources().getIdentifier("login_view", "layout", getPackageName());
		System.out.println(layoutID);
		setContentView(layoutID);
	}


	public void loginAction(View v) {
		new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				int nameTextID = getResources().getIdentifier("nameField", "id", getPackageName());
				int passwordTextID = getResources().getIdentifier("passwordField", "id", getPackageName());
				EditText username = (EditText)findViewById(nameTextID);
				EditText password = (EditText)findViewById(passwordTextID);

				String usernameText = username.getText().toString();
				String passwordText = password.getText().toString();

				if (!usernameText.isEmpty() && !passwordText.isEmpty()) {
					try {
						Object[] parameters = {};
						Relayr_User.setUserID(usernameText);
						String token = (String)Relayr_ApiConnector.doCall(Relayr_ApiCall.UserConnectWithoutToken, parameters);
						if (token != null) {
							Relayr_SDKSettings.setToken(token);
							Relayr_LoginActivity.this.runOnUiThread(new Runnable(){
							    public void run(){
							    	Relayr_LoginActivity.this.onBackPressed();
							    }
							});
						} else {
							openAlert(RELAYR_LOGINFAIL, null);
						}
					} catch (Relayr_Exception e) {
						openAlert(RELAYR_ERROR, e.getMessage());
					}
				} else {
					openAlert(RELAYR_MISSPARAMETER, null);
				}

				return null;
			}
		}.execute();
	}

	  private void openAlert(final int status, final String errorMessage) {
		  Relayr_LoginActivity.this.runOnUiThread(new Runnable(){
			    public void run(){
			    	 AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Relayr_LoginActivity.this);
					  alertDialogBuilder.setTitle(Relayr_LoginActivity.this.getTitle());
					  String message;
					  switch(status) {
					  case RELAYR_MISSPARAMETER: {
						  message = "Please, fill in your username and password";
						  break;
					  }
					  case RELAYR_LOGINFAIL: {
						  message = "Username or password incorrect.";
						  break;
					  }
					  default: message = errorMessage;
					  }

					  alertDialogBuilder.setMessage(message);
					  alertDialogBuilder.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
						  public void onClick(DialogInterface dialog,int id) {
							dialog.cancel();
						  }
					  });

					  AlertDialog alertDialog = alertDialogBuilder.create();
					  alertDialog.show();
			    }
			});

	  }

}
