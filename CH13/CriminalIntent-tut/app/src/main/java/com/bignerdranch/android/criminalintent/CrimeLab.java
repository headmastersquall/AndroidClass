package com.bignerdranch.android.criminalintent;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CrimeLab {
	private static CrimeLab instance;
	private static final List<Crime> crimes = new ArrayList<>();
	private static Context context;

	private CrimeLab(final Context context) {
	}

	public static CrimeLab getInstance(final Context context) {
		if (instance == null) {
			instance = new CrimeLab(context);
		}
		return instance;
	}

	public static void addCrime(final Crime crime) {
		crimes.add(crime);
	}

	public static List<Crime> getCrimes() {
		return crimes;
	}

	public static Crime getCrime(final UUID id) {
		for (final Crime c : crimes) {
			if (c.getId().equals(id)) {
				return c;
			}
		}
		throw new IllegalArgumentException("Crime not found");
	}
}
