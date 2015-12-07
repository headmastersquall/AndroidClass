package com.bignerdranch.android.criminalintent;

import android.content.Context;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class CrimeLab {
	private static CrimeLab instance;
	private static final List<Crime> crimes = new ArrayList<>();
	private static Context context;

	private CrimeLab(final Context context) {
		for (int i = 0; i < 100; i++) {
			Crime crime = new Crime();
			crime.setTitle("Crime #" + i);
			crime.setSolved(i % 2 == 0);
			crimes.add(crime);
		}
	}

	public static CrimeLab getInstance(final Context context) {
		if (instance == null) {
			instance = new CrimeLab(context);
		}
		return instance;
	}

	public static List<Crime> getCrimes() {
		return crimes;
	}

	public static Crime getCrime(UUID id) {
		for (Crime c : crimes) {
			if (c.getId() == id) {
				return c;
			}
		}
		throw new IllegalArgumentException("Crime not found");
	}
}
