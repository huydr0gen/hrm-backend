package com.tlu.hrm.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.tlu.hrm.entities.AttendanceRecord;
import com.tlu.hrm.entities.Employee;

public class ExcelAttendanceMonthlyExporter {

	private static final String[] HEADERS = {
		    "EMPLOYEE_CODE",
		    "EMPLOYEE_NAME",
		    "WORK_DATE",
		    "CHECK_IN",
		    "CHECK_OUT",
		    "OT_HOURS"
		};

    public static byte[] generate(
            List<Employee> employees,
            Map<Long, Map<LocalDate, AttendanceRecord>> attendanceMap,
            YearMonth month) {

        try (Workbook workbook = new XSSFWorkbook()) {

            Sheet sheet = workbook.createSheet("ATTENDANCE_" + month);

            // =========================
            // Style cho date & time
            // =========================
            CreationHelper helper = workbook.getCreationHelper();

            CellStyle dateStyle = workbook.createCellStyle();
            dateStyle.setDataFormat(
                    helper.createDataFormat().getFormat("yyyy-MM-dd")
            );

            CellStyle timeStyle = workbook.createCellStyle();
            timeStyle.setDataFormat(
                    helper.createDataFormat().getFormat("HH:mm")
            );

            // =========================
            // Header
            // =========================
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < HEADERS.length; i++) {
                headerRow.createCell(i).setCellValue(HEADERS[i]);
            }

            int rowIndex = 1;

            // =========================
            // Data rows
            // =========================
            for (Employee employee : employees) {

                for (int day = 1; day <= month.lengthOfMonth(); day++) {

                    LocalDate date = month.atDay(day);

                    AttendanceRecord record = attendanceMap
                            .getOrDefault(employee.getId(), Map.of())
                            .get(date);

                    Row row = sheet.createRow(rowIndex++);

                    // EMPLOYEE_CODE
                    row.createCell(0).setCellValue(employee.getCode());

                    // EMPLOYEE_NAME
                    row.createCell(1).setCellValue(employee.getFullName());

                    // WORK_DATE
                    Cell dateCell = row.createCell(2);
                    dateCell.setCellValue(java.sql.Date.valueOf(date));
                    dateCell.setCellStyle(dateStyle);

	                 // =========================
	                 // CHECK_IN / CHECK_OUT
	                 // =========================
	                 LocalTime checkIn;
	                 LocalTime checkOut;
	
	                 if (record != null && record.getCheckIn() != null && record.getCheckOut() != null) {
	                     checkIn = record.getCheckIn();
	                     checkOut = record.getCheckOut();
	                 } else {
	                     // 👉 GIỜ MẪU CHO NGÀY CHƯA CÓ DỮ LIỆU
	                     checkIn = LocalTime.of(8, 0);
	                     checkOut = LocalTime.of(17, 0);
	                 }
	
	                 // CHECK_IN
	                 Cell inCell = row.createCell(3);
	                 inCell.setCellValue(java.sql.Time.valueOf(checkIn));
	                 inCell.setCellStyle(timeStyle);
	
	                 // CHECK_OUT
	                 Cell outCell = row.createCell(4);
	                 outCell.setCellValue(java.sql.Time.valueOf(checkOut));
	                 outCell.setCellStyle(timeStyle);
	                 
	                 double otHours = 0;

	                 if (record != null && record.getOtMinutes() != null) {
	                     otHours = record.getOtMinutes() / 60.0;
	                 }

	                 row.createCell(5).setCellValue(otHours);
                }
            }
            
            // =========================
            // Auto size
            // =========================
            for (int i = 0; i < HEADERS.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // =========================
            // Write file
            // =========================
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return out.toByteArray();

        } catch (IOException e) {
            throw new RuntimeException("Failed to generate attendance excel", e);
        }
    }
}
