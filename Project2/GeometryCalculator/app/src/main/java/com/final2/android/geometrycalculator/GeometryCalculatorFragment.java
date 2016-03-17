package com.final2.android.geometrycalculator;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import static com.final2.android.geometrycalculator.GeometryCalculator.rectangleArea;
import static com.final2.android.geometrycalculator.GeometryCalculator.rectangleDiagonal;
import static com.final2.android.geometrycalculator.GeometryCalculator.rectanglePerimeter;
import static com.final2.android.geometrycalculator.GeometryCalculator.rectangleSolidSurfaceArea;
import static com.final2.android.geometrycalculator.GeometryCalculator.rectangleSolidVolume;
import static com.final2.android.geometrycalculator.GeometryCalculator.triangleArea;
import static com.final2.android.geometrycalculator.GeometryCalculator.trianglePerimeter;

public class GeometryCalculatorFragment extends Fragment {

	private View view;
	private EditText rectHeight;
	private EditText rectWidth;
	private EditText rectSolidHeight;
	private EditText rectSolidWidth;
	private EditText rectSolidLength;
	private EditText triangle_a;
	private EditText triangle_b;
	private EditText triangle_c;

	public static Fragment getInstance() {
		return new GeometryCalculatorFragment();
	}

	@Override
	public void onCreate(final @Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public void onResume() {
		super.onResume();

		calculateRectangleResults();
		calculateRectangleSolidResults();
		calculateTriangleResults();
	}

	@Nullable
	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_geometry_calculator, container, false);
		rectHeight = (EditText)view.findViewById(R.id.rectangle_height_edittext);
		rectWidth = (EditText)view.findViewById(R.id.rectangle_width_edittext);
		rectSolidHeight = (EditText)view.findViewById(R.id.rectangle_solid_height_edittext);
		rectSolidWidth = (EditText)view.findViewById(R.id.rectangle_solid_width_edittext);
		rectSolidLength = (EditText)view.findViewById(R.id.rectangle_solid_length_edittext);
		triangle_a = (EditText)view.findViewById(R.id.triangle_a_edittext);
		triangle_b = (EditText)view.findViewById(R.id.triangle_b_edittext);
		triangle_c = (EditText)view.findViewById(R.id.triangle_c_edittext);

		rectHeight.setOnKeyListener(new RectangleKeyListener());
		rectWidth.setOnKeyListener(new RectangleKeyListener());
		rectSolidHeight.setOnKeyListener(new RectangleSolidKeyListener());
		rectSolidWidth.setOnKeyListener(new RectangleSolidKeyListener());
		rectSolidLength.setOnKeyListener(new RectangleSolidKeyListener());
		triangle_a.setOnKeyListener(new TriangleKeyListener());
		triangle_b.setOnKeyListener(new TriangleKeyListener());
		triangle_c.setOnKeyListener(new TriangleKeyListener());
		return view;
	}

	@Override
	public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.fragment_geometry_calculator, menu);

		final MenuItem searchItem = menu.findItem(R.id.menu_item_clear);
		searchItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				rectHeight.setText("");
				rectWidth.setText("");
				rectSolidHeight.setText("");
				rectSolidWidth.setText("");
				rectSolidLength.setText("");
				triangle_a.setText("");
				triangle_b.setText("");
				triangle_c.setText("");

				calculateRectangleResults();
				calculateRectangleSolidResults();
				calculateTriangleResults();
				return false;
			}
		});
	}

	private void calculateRectangleResults() {
		final TextView rectAreaResult = (TextView)view.findViewById(R.id.rectangle_area_result);
		final TextView rectPerimeterResult = (TextView)view.findViewById(R.id.rectangle_perimeter_result);
		final TextView rectDiagonalResult = (TextView)view.findViewById(R.id.rectangle_diagonal_result);

		final double height = getNumberFromEditText(rectHeight);
		final double width = getNumberFromEditText(rectWidth);

		setResult(rectAreaResult, rectangleArea(width, height));
		setResult(rectPerimeterResult, rectanglePerimeter(width, height));
		setResult(rectDiagonalResult, rectangleDiagonal(width, height));
	}

	private void calculateRectangleSolidResults() {
		final TextView rectSolidVolumeResult = (TextView)view.findViewById(R.id.rectangle_solid_volume_result);
		final TextView rectSolidSurfaceAreaResult = (TextView)view.findViewById(R.id.rectangle_solid_surface_area_result);

		final double height = getNumberFromEditText(rectSolidHeight);
		final double width = getNumberFromEditText(rectSolidWidth);
		final double length = getNumberFromEditText(rectSolidLength);

		setResult(rectSolidVolumeResult, rectangleSolidVolume(width, height, length));
		setResult(rectSolidSurfaceAreaResult, rectangleSolidSurfaceArea(width, height, length));
	}

	private void calculateTriangleResults() {
		final TextView trianglePerimeterResult = (TextView)view.findViewById(R.id.triangle_perimiter_result);
		final TextView triangleAreaResult = (TextView)view.findViewById(R.id.triangle_area_result);

		final double a = getNumberFromEditText(triangle_a);
		final double b = getNumberFromEditText(triangle_b);
		final double c = getNumberFromEditText(triangle_c);

		setResult(trianglePerimeterResult, trianglePerimeter(a, b, c));
		setResult(triangleAreaResult, triangleArea(a, b, c));
	}

	private double getNumberFromEditText(final EditText editText) {
		return editText.getText().length() == 0
			? 0.0
			: Double.valueOf(editText.getText().toString());
	}

	private void setResult(final TextView textView, final double value) {
		textView.setText(String.valueOf(value));
	}

	private class RectangleKeyListener implements View.OnKeyListener {
		@Override
		public boolean onKey(final View v, final int keyCode, final KeyEvent event) {
			calculateRectangleResults();
			return false;
		}
	}

	private class RectangleSolidKeyListener implements View.OnKeyListener {
		@Override
		public boolean onKey(final View v, final int keyCode, final KeyEvent event) {
			calculateRectangleSolidResults();
			return false;
		}
	}

	private class TriangleKeyListener implements View.OnKeyListener {
		@Override
		public boolean onKey(final View v, final int keyCode, final KeyEvent event) {
			calculateTriangleResults();
			return false;
		}
	}
}
