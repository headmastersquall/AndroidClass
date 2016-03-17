package com.final2.android.geometrycalculator;

public final class GeometryCalculator {
	public static double rectangleArea(final double width, final double height) {
		return width * height;
	}

	public static double rectanglePerimeter(final double width, final double height) {
		return (width * 2) + (height * 2);
	}

	public static double rectangleDiagonal(final double width, final double height) {
		return Math.sqrt((height * height) + (width * width));
	}

	public static double rectangleSolidVolume(final double width, final double height, final double length) {
		return height * width * length;
	}

	public static double rectangleSolidSurfaceArea(final double width, final double height, final double length) {
		return ((length * width) + (height * width) + (height * length)) * 2;
	}

	public static double trianglePerimeter(final double a, final double b, final double c) {
		return a + b + c;
	}

	public static double triangleArea(final double a, final double b, final double c) {
		final double s = (a + b + c) / 2;
		return Math.sqrt(s * (s - a) * (s - b) * (s - c));
	}
}
