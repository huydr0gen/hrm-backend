package com.tlu.hrm.service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.tlu.hrm.dto.AttendanceImportResultDTO;
import com.tlu.hrm.entities.AttendanceRecord;
import com.tlu.hrm.entities.Employee;
import com.tlu.hrm.enums.AttendanceWorkType;
import com.tlu.hrm.repository.AttendanceRecordRepository;
import com.tlu.hrm.repository.EmployeeRepository;
import com.tlu.hrm.utils.ExcelAttendanceTemplateUtil;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class AttendanceImportServiceImpl implements AttendanceImportService {

	private final EmployeeRepository employeeRepo;
    private final AttendanceRecordRepository attendanceRepo;

    public AttendanceImportServiceImpl(
            EmployeeRepository employeeRepo,
            AttendanceRecordRepository attendanceRepo) {
        this.employeeRepo = employeeRepo;
        this.attendanceRepo = attendanceRepo;
    }

    @Override
    public AttendanceImportResultDTO importExcel(
            MultipartFile file,
            YearMonth month) {

        AttendanceImportResultDTO result = new AttendanceImportResultDTO();

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {

            Sheet sheet = workbook.getSheetAt(0);

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {

                Row row = sheet.getRow(i);
                if (row == null || row.getCell(0) == null) {
                    continue;
                }

                result.setTotalRows(result.getTotalRows() + 1);

                try {
                    String empCode = row.getCell(0).getStringCellValue().trim();
                    if (empCode.isEmpty()) {
                        throw new RuntimeException("Employee code is empty");
                    }

                    LocalDate date;
                    try {
                        date = row.getCell(1)
                                  .getLocalDateTimeCellValue()
                                  .toLocalDate();
                    } catch (Exception e) {
                        throw new RuntimeException("Invalid work date");
                    }

                    LocalTime in = null;
                    LocalTime out = null;

                    if (row.getCell(2) != null 
                            && row.getCell(2).getCellType() == CellType.NUMERIC) {
                        in = row.getCell(2)
                                .getLocalDateTimeCellValue()
                                .toLocalTime();
                    }

                    if (row.getCell(3) != null 
                            && row.getCell(3).getCellType() == CellType.NUMERIC) {
                        out = row.getCell(3)
                                 .getLocalDateTimeCellValue()
                                 .toLocalTime();
                    }

                    if (in != null && out != null && out.isBefore(in)) {
                        throw new RuntimeException("Check-out is before check-in");
                    }

                    Employee emp = employeeRepo.findByCode(empCode)
                            .orElseThrow(() ->
                                    new RuntimeException("Employee code not found: " + empCode)
                            );

                    int workedMinutes = 0;
                    if (in != null && out != null) {
                        workedMinutes = (int) Duration.between(in, out).toMinutes();
                    }

                    AttendanceRecord record =
                            attendanceRepo.findByEmployeeIdAndWorkDate(emp.getId(), date)
                                    .orElse(new AttendanceRecord());

                    record.setEmployee(emp);
                    record.setWorkDate(date);
                    record.setCheckIn(in);
                    record.setCheckOut(out);
                    record.setWorkedMinutes(workedMinutes);
                    record.setNote("Imported from Excel");

                    if (workedMinutes >= 480) {
                        record.setPaidMinutes(480);
                        record.setWorkType(AttendanceWorkType.FULL_DAY);
                    } else if (workedMinutes >= 240) {
                        record.setPaidMinutes(240);
                        record.setWorkType(AttendanceWorkType.HALF_DAY);
                    } else {
                        record.setPaidMinutes(0);
                        record.setWorkType(AttendanceWorkType.ABSENT);
                    }

                    attendanceRepo.save(record);
                    result.setSuccessRows(result.getSuccessRows() + 1);

                } catch (Exception ex) {
                    result.getErrors().add("Row " + (i + 1) + ": " + ex.getMessage());
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("Cannot read excel file", e);
        }

        return result;
    }

    @Override
    public byte[] exportTemplate() {
        return ExcelAttendanceTemplateUtil.generate();
    }
}
