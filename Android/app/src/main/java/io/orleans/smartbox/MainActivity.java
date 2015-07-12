package io.orleans.smartbox;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
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
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.ViewFlipper;
import android.net.wifi.ScanResult;

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

		checkIfSettingUpSmartbox();
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
				System.out.println(results);
				int size = results.size();

				try {
					size = size - 1;
					while (size >= 0) {
//						HashMap<String, String> item = new HashMap<String, String>();
//						item.put("key", results.get(size).SSID + "  " + results.get(size).capabilities);


						System.out.println("SSID: " + results.get(size).SSID);

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
//		Toast.makeText(this, "Scanning...." + size, Toast.LENGTH_SHORT).show();








//		getlistOfWifi();
//		foreach (wifinetwork) {
//			if (networkName.equals("Smartbox")) {
//				String ip = Utils.getIPAddress(true);;
//				String[] splitIP = ip.split(".");
//				splitIP[3] = "1";
//				ip = splitIP[0] + "." + splitIP[1] + "." + splitIP[2] + "." + splitIP[3];
//				webView.loadUrl("http://" + ip + ":81");
//			}
//		}
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
