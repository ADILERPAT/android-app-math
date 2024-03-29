package com.vkv.learnmaths;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.support.v4.app.NavUtils;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;

public class LoginActivity extends Activity {

	static String baseURL = "http://learnmathsapp.apphb.com/api/";

	public static String SESSIONKEY = "com.example.myfirstapp.SESSIONKEY";
	private String sessionKeyRecieved = "";
	private String errorMessage = "";
	
	private EditText usernameEditText;
	private EditText nameEditText;
	private EditText passwordEditText;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		usernameEditText = (EditText) findViewById(R.id.usernameEditText);
		nameEditText = (EditText) findViewById(R.id.nameEditText);
		passwordEditText = (EditText) findViewById(R.id.passwordEditText);
		
		// Show the Up button in the action bar.
		setupActionBar();
	}

	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void loginUser(View view) throws ClientProtocolException, IOException{

		String username = usernameEditText.getText().toString();
		String password = passwordEditText.getText().toString();

		JSONObject model = new JSONObject();
		try {
			model.put("username", username);
			model.put("password", password);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		String data = model.toString();
		Log.v("in ", data);
		
		new PostAsyncTask().execute(baseURL + "users/login", data);
	}

	public void registerUser(View view) throws ClientProtocolException, IOException{

		String username = usernameEditText.getText().toString();
		String name = nameEditText.getText().toString();
		String password = passwordEditText.getText().toString();

		JSONObject model = new JSONObject();
		try {
			model.put("username", username);
			model.put("name", name);
			model.put("password", password);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		String data = model.toString();
		Log.v("in ", data);

		new PostAsyncTask().execute(baseURL + "users/register", data);
	}
	
	private class PostAsyncTask extends AsyncTask<String, String, String> {

		@Override
		protected String doInBackground(String... args) {
			String result = "";		
			try {
				result = RequestHelper.PostData(args);
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return result;
		}
		
		protected void onPostExecute(String result){
			Log.v("out ", result);
			JSONObject jsonObject;
			try {
				jsonObject = new JSONObject(result);
				sessionKeyRecieved = jsonObject.getString("sessionKey");
				errorMessage = "";
			} catch (JSONException e) {
				try {
					jsonObject = new JSONObject(result);
					errorMessage = jsonObject.getString("Message");
				} catch (JSONException e1) {
					e1.printStackTrace();
				}				
				e.printStackTrace();
			}
			
			if (errorMessage.length() > 0) {
				AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
				builder.setTitle(R.string.error_title);
				builder.setPositiveButton(R.string.button_ok, null);
				builder.setMessage(errorMessage);
				AlertDialog theAlertDialog = builder.create();
				theAlertDialog.show();
			}
			else {
				Intent intent = new Intent(LoginActivity.this, ProfileActivity.class);
				intent.putExtra(SESSIONKEY, sessionKeyRecieved);
				startActivity(intent);
			}
		}
	}
}
