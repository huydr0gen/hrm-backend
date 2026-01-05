package com.tlu.hrm.utils;

import com.tlu.hrm.entities.AttendanceRecord;

public class AttendanceDisplayUtil {

	private static final int FULL_DAY_MINUTES = 480;

    public static String buildDisplay(AttendanceRecord r) {

        if (r == null || r.getPaidMinutes() == null) {
            return "";
        }

        StringBuilder sb = new StringBuilder();

        int paid = r.getPaidMinutes();
        int otMinutes = r.getOtMinutes() != null ? r.getOtMinutes() : 0;

        // ===== PAID / ABSENT =====
        if (paid >= FULL_DAY_MINUTES) {
            sb.append("x:8");
        } else if (paid > 0) {
            int x = paid / 60;
            int p = (FULL_DAY_MINUTES - paid) / 60;
            sb.append("x:").append(x).append(" p:").append(p);
        } else {
            sb.append("p:8");
        }

        // ===== OT =====
        if (otMinutes > 0) {
            sb.append(" ot:").append(otMinutes / 60);
        }

        return sb.toString();
    }
}
