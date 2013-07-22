package com.layercake.admobwrapper;

import android.os.IBinder;

interface IEmbeddedAdContainer {

	/* Functions for AdListener */
	void onReceiveAd();
	void onFailedToReceiveAd(String errorCode);
	void onPresentScreen();
	void onDismissScreen();
	void onLeaveApplication();
	
	/* Register the child's interface so that parent can call back. */
	void registerChildInterface(IBinder childInterface);

}