package com.grabowcommuter.plugindev;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnKeyListener;
import android.webkit.GeolocationPermissions;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity {
	
	// For development you should code the javascripts and save them in the assets folder.
	// Modify the loadWebView() Method so that everything runs as it should.
	// If everything is fine, just code the json-plugin-file, which contains all information.
	// User the PlugInTester-App for final testing.
	// Finally upload to gitHub and add the Tag 'com.grabo.commuter.plugin' to the gitHub description. 
	
	final static String TAG = "HAG";
	final static String CURRENT_VERSION = "2.8";
	WebView wv;	
	
	String lat ;
	String lng ;
	String zoom;
	String zoomD;
	
	boolean reload = true;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
 
		if (wv != null) {
			wv.destroy();
		}
		
		wv = new WebView(this);
		setContentView(wv);
					
		// Must be - security is alwasy an issue		
		wv.getSettings().setJavaScriptEnabled(true);
		wv.getSettings().setDomStorageEnabled(true);
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			setMixedContent(wv);
		}
		
		/* Nerver needed ...
		 if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN){
		      setFileAccess(wv);
		 }
		 */ 
		 
		wv.requestFocusFromTouch();
		
		wv.addJavascriptInterface(new WebAppInterface(this), "grabowCommuter");
		 
		wv.setWebChromeClient(new WebChromeClient() {
 
			// To enable geloaction
			@Override
			public void onGeolocationPermissionsShowPrompt(String origin,
					GeolocationPermissions.Callback callback) {
				callback.invoke(origin, true, false);
			}

			// To enable alerts from javascript
			@Override
			public boolean onJsAlert(WebView view, String url,
					String message, final android.webkit.JsResult result) {
				new AlertDialog.Builder(MainActivity.this)
						.setTitle("Alert")
						.setMessage(message)
						.setPositiveButton(android.R.string.ok,
								new AlertDialog.OnClickListener() {
									public void onClick(
											DialogInterface dialog,
											int which) {
										result.confirm();
									}
								}).setCancelable(false).create().show();

				return true;
			}
			
			// To enable confirms from javascript
			@Override
			public boolean onJsConfirm(WebView view, String url,
					String message, final android.webkit.JsResult result) {
				new AlertDialog.Builder(MainActivity.this)
						.setTitle("Dialog")
						.setMessage(message)
						.setPositiveButton(android.R.string.ok,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										result.confirm();
									}
								})
						.setNegativeButton(android.R.string.cancel,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										result.cancel();
									}
								}).create().show();
				return true;
			}
			
			// To enable promts from javascript
			@Override
			public boolean onJsPrompt(WebView view, String url, String message,
					String defaultValue, final android.webkit.JsPromptResult result) {
				final LayoutInflater factory = LayoutInflater.from(MainActivity.this);
				final View v = factory.inflate(
						R.layout.javascript_p_dialog, null);

				((TextView) v.findViewById(R.id.prompt_message_text))
						.setText(message);
				((EditText) v.findViewById(R.id.prompt_input_field))
						.setText(defaultValue);

				new AlertDialog.Builder(MainActivity.this)
						.setTitle("Dialog")
						.setView(v)
						.setPositiveButton(android.R.string.ok,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int whichButton) {
										String value = ((EditText) v
												.findViewById(R.id.prompt_input_field))
												.getText().toString();
										result.confirm(value);
									}
								})
						.setNegativeButton(android.R.string.cancel,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int whichButton) {
										result.cancel();
									}
								})
						.setOnCancelListener(
								new DialogInterface.OnCancelListener() {
									public void onCancel(DialogInterface dialog) {
										result.cancel();
									}
								}).show();

				return true;
			};
			
		});
 		
	}         
					  	
	@Override
	public void onResume() {
		Log.i(TAG, "onResume");
		super.onResume();
		wv.onResume();
		loadWebView();
	}
	
	@Override
	public void onPause() {
		Log.i(TAG, "onPause");			
		wv.onPause();		
		super.onPause();
	}
	
	@Override
	public void onDestroy() {
		Log.i(TAG, "onDestroy()");
		if (wv != null) {
			wv.destroy();
			wv = null;
		}
		super.onDestroy();
	}
	 
	@TargetApi(21)
	private void setMixedContent(WebView wv) {
		wv.getSettings().setMixedContentMode(
				WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
	}
	
	@TargetApi(16)
	private void setFileAccess(WebView wv) {
		  wv.getSettings().setAllowUniversalAccessFromFileURLs(true);
	      wv.getSettings().setAllowFileAccessFromFileURLs(true);
	}
	
	public void launchBrowser(String link) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setData(Uri.parse(link));
		startActivity(intent);
	}
	    
	public class WebAppInterface {
	    Context mContext;
	     
	    /** Instantiate the interface and set the context */
	    WebAppInterface(Context c) {
	        mContext = c;
	    }

	    /** Show a toast from the web page */
	    @JavascriptInterface
	    public void showToast(String toast) {
	        Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show();
	    }
	    
	    @JavascriptInterface
	    public String getVersion() {
	        return CURRENT_VERSION;
	    }		    
	}
	 
	public void setOptions(WebView wv, boolean zoomCtrl, boolean geoLocEnable, boolean backButton, boolean lockScreenRot, boolean reload) {
		
		if (zoomCtrl)
			wv.getSettings().setBuiltInZoomControls(true);
		if (backButton)
			setBackButton(wv);
		if (!geoLocEnable) {
			wv.getSettings().setGeolocationEnabled(false);
		}
		if (lockScreenRot) {
			int currentOrientation = getResources().getConfiguration().orientation;
			if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
			   setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
			}
			else {
			   setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
			}
		}
		this.reload = reload;		
	}
	
	public void setBackButton(WebView wv) {
		wv.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_DOWN) {
					WebView webView = (WebView) v;

					switch (keyCode) {
					case KeyEvent.KEYCODE_BACK:
						if (webView.canGoBack()) {
							webView.goBack();
							return true;
						}
						break;
					}
				}
				return false;
			}
		});
	}
	
	private String readFileFromAsset(String filename) {
		// Return: String or null
		InputStream fIn = null;

		// Read the file from the assets folder
		try {
			fIn = this.getResources().getAssets().open(filename);
				Log.i(TAG, "Reading assets... ");
		} catch (IOException e1) {
				Log.e(TAG, "Can't open assets: " + Log.getStackTraceString(e1));
			return null;
		}

		// Convert file to string using String Builder
		String result;
		try {
			BufferedReader bReader = new BufferedReader(
					new InputStreamReader(fIn, "utf-8"), 8);
			StringBuilder sBuilder = new StringBuilder();

			String line = null;
			while ((line = bReader.readLine()) != null) {
				sBuilder.append(line + "\n");
			}

			fIn.close();
			result = sBuilder.toString();

		} catch (Exception e) {
				Log.e(TAG,"Error readInString: \n" + Log.getStackTraceString(e));
			result = null;
		}
		return result;
	}
	

	private String replace(String script) {
			if (script != null) {
				String out = script.replace("#lat#", lat);
				out = out.replace("#lng#", lng);
				out = out.replace("#zoom#", zoom);
				out = out.replace("#zoomD#", zoomD);
				return out;
			} else {
				return null;
			}
		}

	// -------------------------------------------------------------------------------------------------
	// This is the method, you should configure for developing your app
	// -------------------------------------------------------------------------------------------------

	public void loadWebView() {
		
		// For testing just set some values, e.g. Berlin
		// In values represent the center of the Master Map
		lat = "52.519432961166046";
		lng = "13.402783870697021";
		zoom = "12";
		zoomD = "12.567";
		
		// Configure the WebView for your needs
		wv.setWebViewClient(new WebViewClient() {
			
			// Always stay in webview ... please modify to your needs
			String shouldOverrideUrlLoading1 = "http";
			String shouldOverrideUrlLoading2 = "https";
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				Log.i(TAG, "URL:" + url);
				if ((url.startsWith(shouldOverrideUrlLoading1))
						|| (url.startsWith(shouldOverrideUrlLoading2))) {
					return false;
				} else {
					launchBrowser(url);
					return true;
				}
			}
			 
			/*
			@Override
			// Use only if needed!!!
			public void onPageFinished(WebView view, String url) {
				if (!((onPageFinishedLoadUrl == null) || onPageFinishedLoadUrl
						.isEmpty())) {
					super.onPageFinished(wv, url);
					wv.loadUrl(onPageFinishedLoadUrl);
				} else {
					return;
				}
			}
			*/
		});
		
 
		// Example 1 (Test Dialogs) ---------------------------------------------------------------------
		// Load your major script or html file (e.g. test.html) form the assets-folder of this project
		String myScript = readFileFromAsset("alert_confirm_promt.html");
		// Replace #lat# #lng# #zoom#, but not required for these scripts
		// myScript = replace(myScript);

		// Specify what your plugin should do: 
		// disable zoomControls, disable GPS, disable backButton, disable ScreenRotationLock, enable reload
		setOptions(wv, false, false, false, false, true); 
		          
		wv.loadDataWithBaseURL(null, myScript, "text/html",null, null);
		
		
		/*
		// Example 2 (Speedometer with jQuery mobile) ---------------------------------------------------		
		// Load your major script or html file (e.g. test.html) form the assets-folder of this project
		String myScript = readFileFromAsset("speedometer_digital.html");
		
		// Replace #lat# #lng# #zoom#, but not required for these scripts
		// myScript = replace(myScript);

		// Specify what your plugin should do: 
		// disable zoomControls, enable GPS, disable backButton, enable ScreenRotationLock, enable reload
		setOptions(wv, false, true, false, true, true);
		          
		// Load script - jQuery mobile requires a bas URL, therefore we took www.google.com
		wv.loadDataWithBaseURL("http://www.google.com", myScript, "text/html",null, null);
		*/
		
		/*
		// Example 3 (Google maps satellite) -----------------------------------------------------------		
		// We don't need a script, we just construct an URL
		String myUrl = "https://www.google.de/maps/@#lat#,#lng#,#zoom#z/data=!3m1!1e3?hl=en";
		
		// Replace #lat# #lng# #zoom# 
		myUrl = replace(myUrl);

		// Specify what your plugin should do: 
		// disable zoomControls, enable GPS, disable backButton, enable ScreenRotationLock, enable reload
		setOptions(wv, false, false, false, false, true);
		          
		// Load Url
		wv.loadUrl(myUrl);
		*/
		
		
		// -----------------------------------------------------------------------------------------
		// Conclusion
		// -----------------------------------------------------------------------------------------
		// Finally after everything runs perfect, you can generate the plugin-json-file
		// containing all the parameters, you set above.
		// Be sure you did replace all " with ' as we insert the script in a JSON-file.
		// Test the plugin-json-file with the PlugInTester-App.
		// -----------------------------------------------------------------------------------------
	}
}
