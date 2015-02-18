package com.luk.customsplashscreen;

import android.os.Handler;

public class Setup {

	private static boolean isStarted = false;
	private static Handler handler = new Handler();
	private static SplashActivity activity;

	private static Runnable stepTimer = new Runnable() {
		@Override
		public void run() {
			activity.onSetup();
			handler.postDelayed(this, 40);
		}
	};

	public static void start(SplashActivity view) {
		if (!isStarted) {
			handler.postDelayed(stepTimer, 0);
			isStarted = true;
		}
		activity = view;
	}
}
