package com.tlu.hrm.service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;

import org.apache.poi.ss.usermodel.Cell;
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
                if (row == null) {
                    continue;
                }

                result.setTotalRows(result.getTotalRows() + 1);

                try {
                    // ===== EMPLOYEE CODE =====
                    Cell codeCell = row.getCell(0);
                    if (codeCell == null || codeCell.getCellType() != CellType.STRING) {
                        throw new RuntimeException("Employee code is invalid");
                    }
                    String empCode = codeCell.getStringCellValue().trim();

                    // ===== WORK DATE =====
                    LocalDate workDate;
                    try {
                        workDate = row.getCell(2)
                                .getLocalDateTimeCellValue()
                                .toLocalDate();
                    } catch (Exception e) {
                        throw new RuntimeException("Invalid work date");
                    }

                    // ===== CHECK IN / OUT =====
                    LocalTime checkIn = readTime(row.getCell(3));
                    LocalTime checkOut = readTime(row.getCell(4));

                    if (checkIn != null && checkOut != null && checkOut.isBefore(checkIn)) {
                        throw new RuntimeException("Check-out is before check-in");
                    }

                    Employee employee = employeeRepo.findByCode(empCode)
                            .orElseThrow(() ->
                                    new RuntimeException("Employee not found: " + empCode)
                            );

                    int workedMinutes = 0;
                    if (checkIn != null && checkOut != null) {
                        workedMinutes = (int) Duration
                                .between(checkIn, checkOut)
                                .toMinutes();
                    }

                    AttendanceRecord record =
                            attendanceRepo.findByEmployeeIdAndWorkDate(
                                    employee.getId(), workDate
                            ).orElse(new AttendanceRecord());

                    record.setEmployee(employee);
                    record.setWorkDate(workDate);
                    record.setCheckIn(checkIn);
                    record.setCheckOut(checkOut);
                    record.setWorkedMinutes(workedMinutes);
                    record.setNote("Imported from Excel");

                    // ===== TÍNH CÔNG THÔ =====
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
                    result.getErrors().add(
                            "Row " + (i + 1) + ": " + ex.getMessage()
                    );
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("Cannot read excel file", e);
        }

        return result;
    }

    // =============================
    // HELPER: đọc giờ an toàn
    // =============================
    private LocalTime readTime(Cell cell) {
        if (cell == null || cell.getCellType() != CellType.NUMERIC) {
            return null;
        }
        return cell.getLocalDateTimeCellValue().toLocalTime();
    }
}
