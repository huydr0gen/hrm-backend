package com.tlu.hrm.utils;

import com.tlu.hrm.entities.AttendanceRecord;

public class AttendanceDisplayUtil {

	private static final int FULL_DAY_MINUTES = 480;

    public static String buildDisplay(AttendanceRecord r) {

    	if (r == null || r.getPaidMinutes() == null || r.getWorkType() == null) {
            return "";
        }

        StringBuilder sb = new StringBuilder();

        int paid = r.getPaidMinutes();
        int otMinutes = r.getOtMinutes() != null ? r.getOtMinutes() : 0;
        int hours = paid / 60;

        switch (r.getWorkType()) {

            case FULL_DAY:
                sb.append("x:8");
                break;

            case HALF_DAY:
            case PARTIAL:
                sb.append("x:").append(hours);
                break;

            case LEAVE:
                if (hours == 0) {
                    sb.append("p:8");
                } else {
                    sb.append("x:").append(hours)
                      .append(" p:").append(8 - hours);
                }
                break;

            default:
                // ABSENT, SPECIAL_OFF, UNPAID → không hiển thị gì
                return "";
        }

        if (otMinutes > 0) {
            sb.append(" ot:").append(otMinutes / 60);
        }

        return sb.toString();
    }
}
