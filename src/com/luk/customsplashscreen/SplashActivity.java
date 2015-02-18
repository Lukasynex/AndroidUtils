package com.luk.customsplashscreen;

import android.app.Activity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;

public class SplashActivity extends Activity {

	private static int index = 0;
	private boolean created = false;
	private Animation fade;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		fade = AnimationUtils.loadAnimation(this, R.anim.fade_out);
		Setup.start(this);
	}

	public void onSetup() {
		if (!created) {
			ProgressBar bar = (ProgressBar) findViewById(R.id.RedProgressBar);
			bar.setProgress(index);
			++index;
			if (index == 99) {
				//TODO: animate the fade out between switching layouts
//				View lay = (View) findViewById(R.id.spl);
				//lay.startAnimation(fade);
				
				setContentView(R.layout.activity_main);
				
				created = true;

			}
		}
	}
}
