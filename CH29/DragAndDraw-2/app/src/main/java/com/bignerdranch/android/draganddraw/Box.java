package com.bignerdranch.android.draganddraw;

import android.graphics.PointF;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

public class Box implements Parcelable {
	private static final String CURRENT_X_KEY = "current_x";
	private static final String CURRENT_Y_KEY = "current_y";
	private static final String ORIGIN_X_KEY = "origin_x";
	private static final String ORIGIN_Y_KEY = "origin_y";
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

	public static final Parcelable.Creator<Box> CREATOR
		= new Parcelable.Creator<Box>() {
		@Override
		public Box createFromParcel(final Parcel source) {
			final Bundle bundle = source.readBundle();
			final PointF origin = new PointF(bundle.getFloat(ORIGIN_X_KEY), bundle.getFloat(ORIGIN_Y_KEY));
			final PointF current = new PointF(bundle.getFloat(CURRENT_X_KEY), bundle.getFloat(CURRENT_Y_KEY));
			final Box box = new Box(origin);
			box.setCurrent(current);
			return box;
		}

		@Override
		public Box[] newArray(final int size) {
			return new Box[0];
		}
	};

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(final Parcel dest, final int flags) {

		final Bundle bundle = new Bundle();
		bundle.putFloat(CURRENT_X_KEY, current.x);
		bundle.putFloat(CURRENT_Y_KEY, current.y);
		bundle.putFloat(ORIGIN_X_KEY, origin.x);
		bundle.putFloat(ORIGIN_Y_KEY, origin.y);
		dest.writeBundle(bundle);
	}
}

/*
public class MyParcelable implements Parcelable {
	private int mData;

	public int describeContents() {
		return 0;
	}

	public void writeToParcel(Parcel out, int flags) {
		out.writeInt(mData);
	}

	public static final Parcelable.Creator<MyParcelable> CREATOR
		= new Parcelable.Creator<MyParcelable>() {
		public MyParcelable createFromParcel(Parcel in) {
			return new MyParcelable(in);
		}

		public MyParcelable[] newArray(int size) {
			return new MyParcelable[size];
		}
	};

	private MyParcelable(Parcel in) {
		mData = in.readInt();
	}
}
*/
