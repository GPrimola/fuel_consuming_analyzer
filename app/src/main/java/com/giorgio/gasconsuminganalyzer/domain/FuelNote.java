package com.giorgio.gasconsuminganalyzer.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.giorgio.gasconsuminganalyzer.FuelType;

public class FuelNote extends DomainObject {
	
	public static final String DB_TABLE = "fuel_note_table";
	
	public static final String KEY_KM = "mileage";
	public static final String KEY_FUEL_TYPE = "type_of_fuel";
	public static final String KEY_FUEL_AMOUNT_CASH = "quantity_of_fuel";
	public static final String KEY_FUEL_AMOUNT_VOLUME = "volume";
	public static final String KEY_FULL_TANK = "full_tank";
	public static final String KEY_REFUEL_DATE = "refuel_date";
	
	public static final int KM_CSV_INDEX = 0;
	public static final int FUEL_TYPE_CSV_INDEX 			= KM_CSV_INDEX+1;
	public static final int FUEL_AMOUNT_CASH_CSV_INDEX 		= KM_CSV_INDEX+2;
	public static final int FUEL_AMOUNT_VOLUME_CSV_INDEX 	= KM_CSV_INDEX+3;
	public static final int DISTANCE_LAST_REFUEL_CSV_INDEX	= KM_CSV_INDEX+4;
	public static final int FULL_TANK_CSV_INDEX 			= KM_CSV_INDEX+5;
	public static final int REFUEL_DATE_CSV_INDEX 			= KM_CSV_INDEX+6;
	
	public static final List<String> tableColumns = new ArrayList<String>();

	private Double mileage;
	private FuelType fuelType;
	private BigDecimal fuelAmountCash;
	private Double fuelAmountVolume;
	private Calendar refuelDate;
	private Boolean fullTank;
	
	public FuelNote() {
		fullTank = true;
		refuelDate = Calendar.getInstance();
	}
	
	public FuelNote(double mileage, FuelType fuelType, double fuelAmountCash, double fuelAmountVolume, boolean fullTank, Calendar refuelDate) {
		this(mileage,fuelType, fuelAmountCash, fuelAmountVolume, fullTank, refuelDate.getTime().getTime());
	}
	
	public FuelNote(double mileage, FuelType fuelType, double fuelAmountCash, double fuelAmountVolume, boolean fullTank, long refuelDate) {
		this.mileage = mileage;
		this.fuelType = fuelType;
		this.fuelAmountCash = new BigDecimal(fuelAmountCash).setScale(4, RoundingMode.HALF_UP);
		this.fuelAmountVolume = fuelAmountVolume;
		this.refuelDate = Calendar.getInstance();
		this.refuelDate.setTime(new Date(refuelDate));
		this.fullTank = fullTank;
		
		tableColumns.clear();
		tableColumns.add(FuelNote.KEY_ID);
		tableColumns.add(FuelNote.KEY_KM);
		tableColumns.add(FuelNote.KEY_FUEL_TYPE);
		tableColumns.add(FuelNote.KEY_FUEL_AMOUNT_VOLUME);
		tableColumns.add(FuelNote.KEY_FUEL_AMOUNT_CASH);
		tableColumns.add(FuelNote.KEY_FULL_TANK);
		tableColumns.add(FuelNote.KEY_REFUEL_DATE);
	}

	public Double getMileage() {
		return mileage;
	}

	public void setMileage(Double mileage) {
		this.mileage = mileage;
	}

	public FuelType getFuelType() {
		return fuelType;
	}

	public void setFuelType(FuelType fuelType) {
		this.fuelType = fuelType;
	}

	public BigDecimal getFuelAmountCash() {
		return fuelAmountCash;
	}

	public void setFuelAmountCash(BigDecimal fuelAmountCash) {
		this.fuelAmountCash = fuelAmountCash;
	}

	public Double getFuelAmountVolume() {
		return fuelAmountVolume;
	}

	public void setFuelAmountVolume(Double fuelAmountVolume) {
		this.fuelAmountVolume = fuelAmountVolume;
	}

	public Calendar getRefuelDate() {
		return refuelDate;
	}

	public void setRefuelDate(Calendar refuelDate) {
		this.refuelDate = refuelDate;
	}
	
	public Boolean getFullTank() {
		return fullTank;
	}
	
	public void setFullTank(Boolean fullTank) {
		this.fullTank = fullTank;
	}
	
	public Boolean isFullTank() {
		return fullTank;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((mileage == null) ? 0 : mileage.hashCode());
		result = prime * result
				+ ((refuelDate == null) ? 0 : refuelDate.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FuelNote other = (FuelNote) obj;
		if (mileage == null) {
			if (other.mileage != null)
				return false;
		} else if (!mileage.equals(other.mileage))
			return false;
		return true;
	}



	/**
	 * Generated
	 */
	private static final long serialVersionUID = -8586173760319354483L;

	public static List<String> getTableColumns() {
		return tableColumns;
	}
	
}
