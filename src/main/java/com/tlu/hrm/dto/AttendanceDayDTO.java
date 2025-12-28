package com.tlu.hrm.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public class AttendanceDayDTO {

	private LocalDate workDate;
    private String display;   // p:8 / p:4 / x:4 p:4
    private LocalTime checkIn;
    private LocalTime checkOut;
	public LocalDate getWorkDate() {
		return workDate;
	}
	public void setWorkDate(LocalDate workDate) {
		this.workDate = workDate;
	}
	public String getDisplay() {
		return display;
	}
	public void setDisplay(String display) {
		this.display = display;
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
    
    
}
