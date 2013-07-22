package com.layercake.admobwrapper;

import android.location.Location;

interface IRemoteEmbeddedAd {

	/* Create AdView */
	void createAdView(String adSize, String adDevId);
	
	/* Create and load adRequest. Leave testDevice as null if not testing. */
	void loadAdRequest(String gender, String birthdate, 
		in java.util.List<String> keywords, in Location location, String testDevice);

}