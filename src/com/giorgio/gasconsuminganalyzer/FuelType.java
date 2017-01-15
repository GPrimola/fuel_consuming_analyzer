package com.giorgio.gasconsuminganalyzer;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public enum FuelType {
	
	GASOLINA_ADITIVADA ("Gasolina Aditivada"),
	GASOLINA_COMUM ("Gasolina Comum"),
	ALCOHOL ("Álcool"),
	DIESEL ("Diesel");
	
	private String name;
	
	private FuelType(String name) {
		this.name = name;
	}
	
	public static FuelType getFuelType(int ordinal) {
		switch(ordinal) {
		case 0 : return GASOLINA_ADITIVADA;
		case 1 : return GASOLINA_COMUM;
		case 2 : return ALCOHOL;
		case 3 : return DIESEL;
		default : return null;
		}
	}
	
	public static FuelType getFuelType(String name) {
		String lower = name.toLowerCase(Locale.getDefault());
		if(lower.contains("aditivada")) {
			return GASOLINA_ADITIVADA;
		} else if(lower.contains("comum") || lower.contains("gasolina")) {
			return GASOLINA_COMUM;
		}
		
		if(lower.contains("lcool")) {
			return ALCOHOL;
		}
		if(lower.contains("diesel")) {
			return DIESEL;
		}
		
		return null;
	}
	
	public String getName() {
		return name;
	}
	
	@Override
	public String toString() {
		return name;
	}
	
	public List<FuelType> getFuelTypes() {
		List<FuelType> fuelTypes = new ArrayList<FuelType>();
		for(FuelType ft : getClass().getEnumConstants()) {
			fuelTypes.add(ft);
		}
		
		return fuelTypes;
	}
	
}
