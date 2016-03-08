package com.bignerdranch.android.draganddraw;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class BoxDrawingView extends View {
	private final String TAG = "BoxDrawingView";

	private Box currentBox;
	private List<Box> boxen = new ArrayList<>();
	private final Paint boxPaint;
	private final Paint backgroundPaint;

	// Used when creating the view in code
	public BoxDrawingView(final Context context) {
		this(context, null);
	}

	// Used when inflating the view from XML
	public BoxDrawingView(final Context context, final AttributeSet attrs) {
		super(context, attrs);

		// Paint the boxes a nice semitransparent red (ARGB)
		boxPaint = new Paint();
		boxPaint.setColor(0x22ff0000);

		// Paint the background off-white
		backgroundPaint = new Paint();
		backgroundPaint.setColor(0xfff8efe0);
	}

	@Override
	protected void onDraw(final Canvas canvas) {
		// Fill the background
		canvas.drawPaint(backgroundPaint);

		for (final Box box : boxen) {
			final float left = Math.min(box.getOrigin().x, box.getCurrent().x);
			final float right = Math.max(box.getOrigin().x, box.getCurrent().x);
			final float top = Math.min(box.getOrigin().y, box.getCurrent().y);
			final float bottom = Math.max(box.getOrigin().y, box.getCurrent().y);
			canvas.drawRect(left, top, right, bottom, boxPaint);
		}
	}

	@Override
	public boolean onTouchEvent(final MotionEvent event) {
		final PointF current = new PointF(event.getX(), event.getY());
		String action = "";

		switch (event.getAction())
		{
			case MotionEvent.ACTION_DOWN:
				action = "ACTION_DOWN";
				// Reset drawing state
				currentBox = new Box(current);
				boxen.add(currentBox);
				break;
			case MotionEvent.ACTION_MOVE:
				action = "ACTION_MOVE";
				if (currentBox != null) {
					currentBox.setCurrent(current);
					invalidate();
				}
				break;
			case MotionEvent.ACTION_UP:
				action = "ACTION_UP";
				currentBox = null;
				break;
			case MotionEvent.ACTION_CANCEL:
				action = "ACTION_CANCEL";
				currentBox = null;
				break;
		}

		Log.i(TAG, action + " at x=" + current.x + ", y=" + current.y);
		return true;
	}
}
