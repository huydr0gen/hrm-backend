package com.tlu.hrm.service;

import java.time.LocalDate;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.tlu.hrm.dto.SalaryImportResultDTO;
import com.tlu.hrm.entities.Employee;
import com.tlu.hrm.entities.SalaryRecord;
import com.tlu.hrm.repository.EmployeeRepository;
import com.tlu.hrm.repository.SalaryRecordRepository;

@Service
public class SalaryImportServiceImpl implements SalaryImportService {

	private final EmployeeRepository employeeRepo;
    private final SalaryRecordRepository salaryRepo;
    
	public SalaryImportServiceImpl(EmployeeRepository employeeRepo, SalaryRecordRepository salaryRepo) {
		super();
		this.employeeRepo = employeeRepo;
		this.salaryRepo = salaryRepo;
	}
    
	@Override
    public SalaryImportResultDTO importExcel(MultipartFile file, int month, int year) {

        SalaryImportResultDTO result = new SalaryImportResultDTO();

        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {

            Sheet sheet = workbook.getSheetAt(0);

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {

                Row row = sheet.getRow(i);
                if (row == null) continue;

                try {
                    // A: EMP_CODE
                    String empCode = row.getCell(0).getStringCellValue().trim();

                    // B: EMP_NAME → IGNORE
                    // row.getCell(1)

                    Employee emp = employeeRepo.findByCode(empCode)
                            .orElseThrow(() ->
                                new RuntimeException("Không tìm thấy nhân viên: " + empCode));

                    if (salaryRepo.existsByEmployeeIdAndMonthAndYear(
                            emp.getId(), month, year)) {
                        throw new RuntimeException("Đã có lương tháng này");
                    }

                    SalaryRecord r = new SalaryRecord();
                    r.setEmployee(emp);
                    r.setMonth(month);
                    r.setYear(year);

                    r.setBasicSalary(getLong(row, 2));
                    r.setAllowance(getLong(row, 3));
                    r.setOtPay(getLong(row, 4));
                    r.setBonus(getLong(row, 5));
                    r.setDeduction(getLong(row, 6));
                    r.setTotalSalary(getLong(row, 7));

                    r.setImportedAt(LocalDate.now());

                    salaryRepo.save(r);
                    result.setSuccessCount(result.getSuccessCount() + 1);

                } catch (Exception e) {
                    result.setFailedCount(result.getFailedCount() + 1);
                    result.getErrors().add(
                        "Dòng " + (i + 1) + ": " + e.getMessage());
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("Import Excel thất bại", e);
        }

        return result;
    }

    private Long getLong(Row row, int index) {
        Cell cell = row.getCell(index);
        if (cell == null) return 0L;
        return (long) cell.getNumericCellValue();
    }
}
