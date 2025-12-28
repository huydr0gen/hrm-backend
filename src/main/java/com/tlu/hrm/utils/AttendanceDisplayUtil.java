package com.tlu.hrm.utils;

import com.tlu.hrm.entities.AttendanceRecord;

public class AttendanceDisplayUtil {

	private static final int FULL_DAY_MINUTES = 480;

    public static String buildDisplay(AttendanceRecord r) {

        if (r == null || r.getPaidMinutes() == null) {
            return "";
        }

        int paid = r.getPaidMinutes();

        if (paid >= FULL_DAY_MINUTES) {
            return "p:8";
        }

        if (paid > 0) {
            int p = paid / 60;
            int x = (FULL_DAY_MINUTES - paid) / 60;
            return "p:" + p + " x:" + x;
        }

        return "x:8";
    }
}
