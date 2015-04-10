package com.luk.DrawerToy;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class DrawView extends View implements OnTouchListener {
	List<Point> points = new ArrayList<Point>();
	List<Integer> brushes = new ArrayList<Integer>();
	List<Integer> colors = new ArrayList<Integer>();

	Paint paint = new Paint();
	int BrushSize = 20;
	Boolean EpilepsyMode = false;
	boolean clear = false;
	Random generator = new Random();

	public DrawView(Context context) {
		super(context);
		setFocusable(true);
		setFocusableInTouchMode(true);
		this.setOnTouchListener(this);
		paint.setAntiAlias(true);
	}

	public int newColor() {
		return generator.nextInt();
	}

	public int newBrushSize() {
		return 1 + generator.nextInt(15);
	}

	@Override
	public void onDraw(Canvas canvas) {
		if (!clear) {
			if (!EpilepsyMode) {
				for (int i = 0; i < points.size(); i++) {
					paint.setColor(colors.get(i));
					canvas.drawCircle(points.get(i).x, points.get(i).y,
							brushes.get(i), paint);
				}
			} else {
				paint.setColor(newColor());
				for (int i = 0; i < points.size(); i++) {
					canvas.drawCircle(points.get(i).x, points.get(i).y,
							newBrushSize(), paint);
				}
			}
		} else if (clear) {
			points.removeAll(points);
			colors.removeAll(colors);
			brushes.removeAll(brushes);
			clear = !clear;
		}
		paint.setColor(Color.BLACK);
		canvas.drawRect(0, 0, 50, 50, paint);
		paint.setColor(Color.RED);
		canvas.drawRect(100, 0, 50, 50, paint);
	}

	public boolean onTouch(View view, MotionEvent event) {
		// return super.onTouchEvent(event);
		Point point = new Point();
		point.x = event.getX();
		point.y = event.getY();// - FingerSize;
		points.add(point);
		brushes.add(newBrushSize());
		colors.add(newColor());

		if (event.getAction() != MotionEvent.ACTION_DOWN) {
			if (event.getX() < 50 && event.getY() < 50)
				EpilepsyMode = (EpilepsyMode) ? false : true;

			if (event.getX() > 50 && event.getX() < 100 && event.getY() < 50)
				clear = true;
		}
		invalidate();
		return true;
	}
}

class Point {
	float x, y;

	public Point() {
	}

	public Point(float dx, float dy) {
		x = dx;
		y = dy;
	}
	// @Override
	// public String toString() {
	// return x + ", " + y;
	// }
}
