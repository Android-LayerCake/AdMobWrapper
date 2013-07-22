package com.layercake.admobwrapper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.widget.LinearLayout;
import android.app.Activity;
import android.content.Intent;

import com.google.ads.*;
import com.google.ads.AdRequest.ErrorCode;
import com.google.ads.AdRequest.Gender;

public class EmbeddedAd extends Activity {
	
	public static String TAG = "EmbeddedAd";
	
	private AdView adView;
	
	private IEmbeddedAdContainer containerInterface;
	
	@Override
	  public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_embedded_ad);
	    
	    // Get client interface
        Intent i = getIntent();
        IBinder b = i.getIBinderExtra("parentBinder");
        containerInterface = IEmbeddedAdContainer.Stub.asInterface(b);
        
        if (containerInterface == null) {
        	Log.e(TAG, "No containerInterface!");
        	return;
        }
        
        // Register child (own) interface
        IBinder ownInterface = new IRemoteEmbeddedAd.Stub() {

			@Override
			public void createAdView(String adSize, String adDevId)
					throws RemoteException {
				// Create the adView
				AdSize size;
				if (adSize.equals("BANNER")) {
					size = AdSize.BANNER;
				} else {
					size = AdSize.BANNER; // Actually just supporting banners for now
				}
				adView = new AdView(EmbeddedAd.this, size, adDevId);

				// Add the adView to layout
			    LinearLayout layout = (LinearLayout)findViewById(R.id.mainLayout);
			    layout.addView(adView);
			    
			    // Hook up container interface as AdListener to it
			    adView.setAdListener(new AdListener() {
					@Override
					public void onDismissScreen(Ad a) {
						try { containerInterface.onDismissScreen(); } 
						catch (RemoteException e) { Log.e(TAG, "Failed to call remote: " + e.getMessage());}
					}
					@Override
					public void onFailedToReceiveAd(Ad a, ErrorCode ec) {
						try { containerInterface.onFailedToReceiveAd(ec.toString()); } 
						catch (RemoteException e) { Log.e(TAG, "Failed to call remote: " + e.getMessage());}
					}
					@Override
					public void onLeaveApplication(Ad a) {
						try { containerInterface.onLeaveApplication(); } 
						catch (RemoteException e) { Log.e(TAG, "Failed to call remote: " + e.getMessage());}
					}
					@Override
					public void onPresentScreen(Ad a) {
						try { containerInterface.onPresentScreen(); } 
						catch (RemoteException e) { Log.e(TAG, "Failed to call remote: " + e.getMessage());}
					}
					@Override
					public void onReceiveAd(Ad a) {
						try { containerInterface.onReceiveAd(); } 
						catch (RemoteException e) { Log.e(TAG, "Failed to call remote: " + e.getMessage());}
					}			    	
			    });
			}

			@Override
			public void loadAdRequest(String gender, String birthdate, 
					List<String> keywords, Location location,
					String testDevice) throws RemoteException {
				
		        AdRequest adRequest = new AdRequest();        
		        
		        if (gender != null) {
		        	if (gender.toUpperCase().equals("FEMALE")) {
		        		adRequest.setGender(Gender.FEMALE);
		        	} else if (gender.toUpperCase().equals("MALE")) {
		        		adRequest.setGender(Gender.MALE);
		        	} else {
		        		adRequest.setGender(Gender.UNKNOWN);
		        	}
		        }
		        
		        if (birthdate != null) {
		        	adRequest.setBirthday(birthdate);
		        }
		        
		        if (keywords != null) {
		        	Set<String> keywordSet = new HashSet<String>();
		        	for (String keyword : keywords) {
		        		keywordSet.add(keyword);
		        	}
		        	adRequest.setKeywords(keywordSet);
		        }
		        
		        if (location != null) {
		        	adRequest.setLocation(location);
		        }
		        
		        if (testDevice != null) {
		        	if (testDevice.equals("TEST_EMULATOR")) {
		        		adRequest.addTestDevice(AdRequest.TEST_EMULATOR);
		        	} else {
		        		adRequest.addTestDevice(testDevice);
		        	}
		        }
		        
		        adView.loadAd(adRequest);
			}
        };
        try {
        	containerInterface.registerChildInterface(ownInterface);
        } catch (RemoteException e) {
        	Log.e(TAG, "Error trying to register child interface: " + e.getMessage());
        }

	  }

	  @Override
	  public void onDestroy() {
	    if (adView != null) {
	      adView.destroy();
	    }
	    super.onDestroy();
	  }

}
