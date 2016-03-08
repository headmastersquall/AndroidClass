package com.bignerdranch.android.draganddraw;

import android.graphics.PointF;

public class Box {
	private final PointF origin;
	private PointF current;

	public Box(final PointF origin) {
		this.origin = origin;
	}

	public PointF getCurrent() {
		return current;
	}

	public void setCurrent(final PointF current) {
		this.current = current;
	}

	public PointF getOrigin() {
		return origin;
	}
}
