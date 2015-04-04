
import android.app.Activity;
import android.graphics.Color;
import android.os.Handler;
import android.view.View;

public class ColorTransition {
	private static boolean isStarted = false;
	private static Handler handler = new Handler();
	private static Activity activity;
	private static View layout;

	public static boolean isActive = true;
	private static int red = 0;
	private static int green = 128;
	private static int blue = 255;
	private static boolean red_up = true;
	private static boolean green_up = true;
	private static boolean blue_up = false;
	private static Runnable stepTimer = new Runnable() {
		@Override
		public void run() {
			if (isActive) {
				LoopActions();
				handler.postDelayed(this, 30);
			}
		}

	};

	public static void start(Activity view, View lay) {
		if (!isStarted) {

			handler.postDelayed(stepTimer, 0);

			isStarted = true;
		}
		activity = view;
		layout = lay;
	}

	public static void LoopActions() {
		layout.setBackgroundColor(Color.rgb(red, green, blue));

		if (red <= 0)
			red_up = true;
		if (red >= 255)
			red_up = false;

		if (blue <= 0)
			blue_up = true;
		if (blue >= 255)
			blue_up = false;

		if (green <= 0)
			green_up = true;
		if (green >= 255)
			green_up = false;

		if (blue_up)
			blue += 1;
		else
			blue -= 1;
		if (red_up)
			red += 1;
		else
			red -= 1;
		if (green_up)
			green += 1;
		else
			green -= 1;
	}
}
