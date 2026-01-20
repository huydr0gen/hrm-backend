package com.tlu.hrm.utils;

import com.tlu.hrm.dto.AttendanceDayResponseDTO;
import com.tlu.hrm.entities.AttendanceRecord;
import com.tlu.hrm.entities.LeaveRequest;
import com.tlu.hrm.enums.LeaveDuration;
import com.tlu.hrm.enums.LeaveType;

public class AttendanceDisplayUtil {

	private static final int FULL_DAY_MINUTES = 480;
    private static final int HALF_DAY_MINUTES = 240;

    public static void applyLeaveLogic(
            AttendanceDayResponseDTO dto,
            AttendanceRecord r,
            LeaveRequest leave
    ) {

        if (leave == null) {
            applyNormalAttendance(dto, r);
            appendOT(dto, r);
            return;
        }

        LeaveType type = leave.getType();
        LeaveDuration duration = leave.getDuration();

        switch (type) {
            case PERSONAL:
                applyRV(dto);
                break;
            case ANNUAL:
                applyAnnual(dto, duration);
                break;
            case UNPAID:
                applyUnpaid(dto, duration);
                break;
            default:
                applyNormalAttendance(dto, r);
                break;
        }

        appendOT(dto, r);
    }

    // ===== RV =====
    private static void applyRV(AttendanceDayResponseDTO dto) {
        dto.setDisplay("RV:8");
        dto.setWorkedMinutes(0);
        dto.setPaidMinutes(FULL_DAY_MINUTES);
    }

    // ===== ANNUAL =====
    private static void applyAnnual(AttendanceDayResponseDTO dto, LeaveDuration duration) {
        if (duration == LeaveDuration.FULL_DAY) {
            dto.setDisplay("P:8");
            dto.setWorkedMinutes(0);
            dto.setPaidMinutes(FULL_DAY_MINUTES);
        } else {
            dto.setDisplay("X:4 - P:4");
            dto.setWorkedMinutes(HALF_DAY_MINUTES);
            dto.setPaidMinutes(FULL_DAY_MINUTES);
        }
    }

    // ===== UNPAID =====
    private static void applyUnpaid(AttendanceDayResponseDTO dto, LeaveDuration duration) {
        if (duration == LeaveDuration.FULL_DAY) {
            dto.setDisplay("Ro:8");
            dto.setWorkedMinutes(0);
            dto.setPaidMinutes(0);
        } else {
            dto.setDisplay("X:4 - Ro:4");
            dto.setWorkedMinutes(HALF_DAY_MINUTES);
            dto.setPaidMinutes(HALF_DAY_MINUTES);
        }
    }

    // ===== NORMAL =====
    private static void applyNormalAttendance(AttendanceDayResponseDTO dto, AttendanceRecord r) {
        if (r == null) {
            dto.setDisplay("");
            dto.setWorkedMinutes(0);
            dto.setPaidMinutes(0);
            return;
        }

        dto.setCheckIn(r.getCheckIn());
        dto.setCheckOut(r.getCheckOut());
        dto.setWorkedMinutes(r.getWorkedMinutes());
        dto.setPaidMinutes(r.getPaidMinutes());
        dto.setOtMinutes(r.getOtMinutes());

        // Cả hai đều null → không hiển thị gì
        if (r.getCheckIn() == null && r.getCheckOut() == null) {
            dto.setDisplay("");
            dto.setWorkedMinutes(0);
            dto.setPaidMinutes(0);
            return;
        }

        // Thiếu 1 trong 2 → X:0
        if (r.getCheckIn() == null || r.getCheckOut() == null) {
            dto.setDisplay("X:0");
            dto.setWorkedMinutes(0);
            dto.setPaidMinutes(0);
            return;
        }

        if (r.getWorkedMinutes() != null) {
            double hours = r.getWorkedMinutes() / 60.0;

            // ===== GIỚI HẠN TỐI ĐA 8 GIỜ =====
            if (hours > 8) {
                hours = 8;
            }

            // Làm tròn 2 chữ số thập phân
            double rounded = Math.round(hours * 100.0) / 100.0;

            // Nếu là số nguyên thì bỏ .00
            if (rounded == Math.floor(rounded)) {
                dto.setDisplay("X:" + (int) rounded);
            } else {
                dto.setDisplay("X:" + rounded);
            }
        } else {
            dto.setDisplay("X:0");
        }
    }

    // ===== APPEND OT =====
    private static void appendOT(AttendanceDayResponseDTO dto, AttendanceRecord r) {
        if (r == null || r.getOtMinutes() == null || r.getOtMinutes() <= 0) {
            return;
        }

        double otHours = r.getOtMinutes() / 60.0;
        double roundedOT = Math.round(otHours * 100.0) / 100.0;

        String current = dto.getDisplay();
        if (current == null || current.isBlank()) {
            dto.setDisplay("OT:" + roundedOT);
        } else {
            dto.setDisplay(current + " - OT:" + roundedOT);
        }
    }
}
