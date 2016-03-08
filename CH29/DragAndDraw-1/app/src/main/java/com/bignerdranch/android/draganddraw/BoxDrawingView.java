package com.bignerdranch.android.draganddraw;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

public class BoxDrawingView extends View {
	private final String TAG = "BoxDrawingView";
	private final String BOXES_KEY = "savedBoxes";
	private final String VIEW_PARCELABLE = "view_parcelable";

	private Box currentBox;
	private ArrayList<Box> boxen = new ArrayList<>();
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

	@Override
	protected Parcelable onSaveInstanceState() {
		final Parcelable viewParselable = super.onSaveInstanceState();
		Log.i(TAG, "Saving " + boxen.size() + " boxes");
		final Bundle savedState = new Bundle();
		savedState.putParcelableArrayList(BOXES_KEY, boxen);
		savedState.putParcelable(VIEW_PARCELABLE, viewParselable);
		return savedState;
	}

	@Override
	protected void onRestoreInstanceState(Parcelable state) {
		final Bundle bundle = (Bundle) state;
		final Parcelable viewState = bundle.getParcelable(VIEW_PARCELABLE);
		super.onRestoreInstanceState(viewState);

		Log.i(TAG, "Restoring " + bundle.getParcelableArrayList(BOXES_KEY).size() + " items");
		for (final Parcelable p : bundle.getParcelableArrayList(BOXES_KEY)) {
			boxen.add((Box)p);
		}
	}
}
