package com.bignerdranch.android.criminalintent;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.bignerdranch.android.criminalintent.database.CrimeBaseHelper;
import com.bignerdranch.android.criminalintent.database.CrimeCursorWrapper;
import com.bignerdranch.android.criminalintent.database.CrimeDbSchema.CrimeTable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CrimeLab {
	private static CrimeLab instance;
	private Context context;
	private SQLiteDatabase database;

	private CrimeLab(final Context context) {
		this.context = context.getApplicationContext();
		database = new CrimeBaseHelper(this.context).getWritableDatabase();
	}

	public static CrimeLab getInstance(final Context context) {
		if (instance == null) {
			instance = new CrimeLab(context);
		}
		return instance;
	}

	public void addCrime(final Crime crime) {
		final ContentValues values = getContentValues(crime);
		database.insert(CrimeTable.NAME, null, values);
	}

	public void remove(final Crime crime) {
	}

	public List<Crime> getCrimes() {
		List<Crime> crimes = new ArrayList<>();

		CrimeCursorWrapper cursor = queryCrimes(null, null);
		try {
			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				crimes.add(cursor.getCrime());
				cursor.moveToNext();
			}
		} finally {
			cursor.close();
		}
		return crimes;
	}

	public Crime getCrime(final UUID id) {
		final CrimeCursorWrapper cursor = queryCrimes(
			CrimeTable.Cols.UUID + " = ?",
			new String[] { id.toString() });

		try {
			if (cursor.getCount() == 0) {
				throw new IllegalArgumentException("Crime not found");
			}
			cursor.moveToFirst();
			return cursor.getCrime();
		} finally {
			cursor.close();
		}

	}

	public int updateCrime(final Crime crime) {
		final String uuidString = crime.getId().toString();
		final ContentValues values = getContentValues(crime);
		return database.update(
			CrimeTable.NAME,
			values,
			CrimeTable.Cols.UUID + " = ?",
			new String[] { uuidString });
	}

	public int deleteCrime(final UUID id) {
		return database.delete(
			CrimeTable.NAME,
			CrimeTable.Cols.UUID + " = ?",
			new String[] { id.toString() });
	}

	private static ContentValues getContentValues(final Crime crime) {
		final ContentValues values = new ContentValues();
		values.put(CrimeTable.Cols.UUID, crime.getId().toString());
		values.put(CrimeTable.Cols.TITLE, crime.getTitle());
		values.put(CrimeTable.Cols.DATE, crime.getDate().getTime());
		values.put(CrimeTable.Cols.SOLVED, crime.isSolved() ? 1 : 0);
		return values;
	}

	private CrimeCursorWrapper queryCrimes(final String whereClause, final String[] whereArgs) {
		final Cursor cursor = database.query(
			CrimeTable.NAME,
			null,
			whereClause,
			whereArgs,
			null,
			null,
			null
		);
		return new CrimeCursorWrapper(cursor);
	}
}
