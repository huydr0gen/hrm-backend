package com.tlu.hrm.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class AttendanceDayResponseDTO {

	private LocalDate date;

    private LocalTime checkIn;
    private LocalTime checkOut;

    private Integer workedMinutes;
    private Integer paidMinutes;
    
    private Integer otMinutes;

    // ===== DISPLAY FIELDS =====
    private String display;     // "p:8", "p:4 x:4"
    private String color;       // optional: FULL / HALF / OFF
    
    private List<SpecialScheduleResponseDTO> specialSchedules;
    
	public LocalDate getDate() {
		return date;
	}
	public void setDate(LocalDate date) {
		this.date = date;
	}
	public LocalTime getCheckIn() {
		return checkIn;
	}
	public void setCheckIn(LocalTime checkIn) {
		this.checkIn = checkIn;
	}
	public LocalTime getCheckOut() {
		return checkOut;
	}
	public void setCheckOut(LocalTime checkOut) {
		this.checkOut = checkOut;
	}
	public Integer getWorkedMinutes() {
		return workedMinutes;
	}
	public void setWorkedMinutes(Integer workedMinutes) {
		this.workedMinutes = workedMinutes;
	}
	public Integer getPaidMinutes() {
		return paidMinutes;
	}
	public void setPaidMinutes(Integer paidMinutes) {
		this.paidMinutes = paidMinutes;
	}
	public Integer getOtMinutes() {
		return otMinutes;
	}
	public void setOtMinutes(Integer otMinutes) {
		this.otMinutes = otMinutes;
	}
	public String getDisplay() {
		return display;
	}
	public void setDisplay(String display) {
		this.display = display;
	}
	public String getColor() {
		return color;
	}
	public void setColor(String color) {
		this.color = color;
	}
	public List<SpecialScheduleResponseDTO> getSpecialSchedules() {
		return specialSchedules;
	}
	public void setSpecialSchedules(List<SpecialScheduleResponseDTO> specialSchedules) {
		this.specialSchedules = specialSchedules;
	}

    
}
