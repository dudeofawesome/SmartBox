package io.orleans.smartbox;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.ViewFlipper;
import android.net.wifi.ScanResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity
		implements NavigationDrawerFragment.NavigationDrawerCallbacks {

	/**
	 * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
	 */
	private NavigationDrawerFragment mNavigationDrawerFragment;

	/**
	 * Used to store the last screen title. For use in {@link #restoreActionBar()}.
	 */
	private CharSequence mTitle;
	ViewFlipper viewFlipper;
	WebView webView;
	ImageView envelope;
	RelativeLayout letter;
	TextView letterText;

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mNavigationDrawerFragment = (NavigationDrawerFragment)
				getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
		mTitle = getTitle();
		viewFlipper = (ViewFlipper) findViewById(R.id.viewFlipper);

		// Set up the drawer.
		mNavigationDrawerFragment.setUp(
				R.id.navigation_drawer,
				(DrawerLayout) findViewById(R.id.drawer_layout));

		webView = (WebView) findViewById(R.id.webView);
		letter = (RelativeLayout) findViewById(R.id.letter);
		letterText = (TextView) findViewById(R.id.letterText);
		envelope = (ImageView) findViewById(R.id.mailStateImg);

		checkIfSettingUpSmartbox();

		updateFromMailbox();
	}











	private void updateFromMailbox () {
		new DownloadWebpageTask().execute("getData");
	}

	public class DownloadWebpageTask extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... urls) {

			// params comes from the execute() call: params[0] is the url.
			try {
				return downloadUrl(urls[0]);
			} catch (IOException e) {
				return "Unable to retrieve web page. URL may be invalid.";
			}
		}
		// onPostExecute displays the results of the AsyncTask.
		@Override
		protected void onPostExecute(String result) {
			System.out.println(result);
			JSONObject data;
			try {
				data = new JSONObject(result);

				if (data.getBoolean("mail")) {
					envelope.setBackgroundResource(R.drawable.mail_open);
					letter.setVisibility(View.VISIBLE);
				} else {
					envelope.setBackgroundResource(R.drawable.mail_open);
					letter.setVisibility(View.INVISIBLE);
				}
//				data.getJSONObject("mailPosition") = {front: true, middle:true, back: false};
//				data.getBoolean("flagUp") = false;
//				data.getBoolean("doorOpen") = false;
			} catch (JSONException err) { err.printStackTrace(); }

//			if (result.contains("Turning")) {
//				txtConnect.setText("Connected");
//				if (result.contains("on")) {
//					txtStatus.setText("Sprinklers are on");
//					imgStatus.setImageResource(R.drawable.watering);
//				} else if (result.contains("off")) {
//					txtStatus.setText("Sprinklers are off");
//					if (result.contains("rain")) {
//						imgStatus.setImageResource(R.drawable.rain);
//					} else {
//						imgStatus.setImageResource(R.drawable.off);
//					}
//				}
//			} else if (result.contains("Status")) {
//				txtConnect.setText("Connected");
//				if (result.contains("on")) {
//					txtStatus.setText("Sprinklers are on");
//					imgStatus.setImageResource(R.drawable.watering);
//				} else if (result.contains("off")) {
//					txtStatus.setText("Sprinklers are off");
//					if (result.contains("rain")) {
//						imgStatus.setImageResource(R.drawable.rain);
//					} else {
//						imgStatus.setImageResource(R.drawable.off);
//					}
//				}
//			} else if (result.contains("Analytics")) {
//				txtConnect.setText("Connected");
//				if (result.contains("watering data")) {
//
//				}
//			}
		}
	}

	private String downloadUrl(String myurl) throws IOException {
		InputStream is = null;
		// Only display the first 500 characters of the retrieved
		// web page content.
		int len = 500;

		try {
			URL url = new URL("http://joshs-edison.local:8080/" + myurl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(10000 /* milliseconds */);
			conn.setConnectTimeout(15000 /* milliseconds */);
			conn.setRequestMethod("GET");
			conn.setDoInput(true);
			// Starts the query
			conn.connect();
			int response = conn.getResponseCode();
			System.out.println("The response is: " + response);
			is = conn.getInputStream();

			// Convert the InputStream into a string
			String contentAsString = readIt(is, len);
			return contentAsString;

			// Makes sure that the InputStream is closed after the app is
			// finished using it.
		} finally {
			if (is != null) {
				is.close();
			}
		}
	}

	public String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
		Reader reader = null;
		reader = new InputStreamReader(stream, "UTF-8");
		char[] buffer = new char[len];
		reader.read(buffer);
		return new String(buffer);
	}









	private void checkIfSettingUpSmartbox () {
		final ArrayList<HashMap<String, String>> arraylist = new ArrayList<HashMap<String, String>>();
		final WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		if (wifi.isWifiEnabled() == false) {
			wifi.setWifiEnabled(true);
		}

		registerReceiver(new BroadcastReceiver() {
			@Override
			public void onReceive (Context c, Intent intent) {
				List<ScanResult>results = wifi.getScanResults();
				int size = results.size();

				try {
					size = size - 1;
					while (size >= 0) {
						if (results.get(size).SSID.equals("Smartbox")) {
//						if (results.get(size).SSID.equals("Intel 5GHz")) {
							System.out.println("FOUND WIFI");
							String ip = Utils.getIPAddress(true);
							String[] splitIP = ip.split("\\.");
							splitIP[3] = "1";
							ip = splitIP[0] + "." + splitIP[1] + "." + splitIP[2] + "." + splitIP[3];
							webView.loadUrl("http://" + ip + ":81");

							System.out.println("IP4: " + ip);

							break;
						}

						size--;
					}
				} catch (Exception e) {

				}
			}
		}, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
		wifi.startScan();
	}

	@Override
	public void onNavigationDrawerItemSelected (int position) {
		// update the main content by replacing fragments
		FragmentManager fragmentManager = getSupportFragmentManager();
		fragmentManager.beginTransaction()
				.replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
				.commit();
	}

	public void onSectionAttached (int number) {
		switch (number) {
			case 1:
				mTitle = getString(R.string.title_section1);
				viewFlipper.setDisplayedChild(1);
				break;
			case 2:
				mTitle = getString(R.string.title_section2);
				viewFlipper.setDisplayedChild(0);
				break;
		}
	}

	public void restoreActionBar () {
		ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(mTitle);
	}


	@Override
	public boolean onCreateOptionsMenu (Menu menu) {
		if (!mNavigationDrawerFragment.isDrawerOpen()) {
			// Only show items in the action bar relevant to this screen
			// if the drawer is not showing. Otherwise, let the drawer
			// decide what to show in the action bar.
			getMenuInflater().inflate(R.menu.main, menu);
			restoreActionBar();
			return true;
		}
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected (MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.action_settings) {
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		private static final String ARG_SECTION_NUMBER = "section_number";

		/**
		 * Returns a new instance of this fragment for the given section
		 * number.
		 */
		public static PlaceholderFragment newInstance (int sectionNumber) {
			PlaceholderFragment fragment = new PlaceholderFragment();
			Bundle args = new Bundle();
			args.putInt(ARG_SECTION_NUMBER, sectionNumber);
			fragment.setArguments(args);
			return fragment;
		}

		public PlaceholderFragment () {
		}

		@Override
		public void onAttach (Activity activity) {
			super.onAttach(activity);
			((MainActivity) activity).onSectionAttached(
					getArguments().getInt(ARG_SECTION_NUMBER));
		}
	}

}
