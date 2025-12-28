package com.tlu.hrm.utils;

import java.io.IOException;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelAttendanceTemplateUtil {

	public static byte[] generate() {

        try (Workbook wb = new XSSFWorkbook()) {

            Sheet sheet = wb.createSheet("ATTENDANCE");

            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("EMPLOYEE_CODE");
            header.createCell(1).setCellValue("WORK_DATE (yyyy-MM-dd)");
            header.createCell(2).setCellValue("CHECK_IN (HH:mm)");
            header.createCell(3).setCellValue("CHECK_OUT (HH:mm)");

            sheet.autoSizeColumn(0);
            sheet.autoSizeColumn(1);
            sheet.autoSizeColumn(2);
            sheet.autoSizeColumn(3);

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            wb.write(out);
            return out.toByteArray();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
