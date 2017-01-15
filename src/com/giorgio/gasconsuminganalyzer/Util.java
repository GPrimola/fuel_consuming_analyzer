package com.giorgio.gasconsuminganalyzer;

import java.io.File;
import java.io.FilenameFilter;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Util {
	
	static public Double roundDouble(double d) {
    	BigDecimal bd = new BigDecimal(d);
    	return Double.valueOf(bd.setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue());
    }
	
	static public Double roundDouble4(double d) {
    	BigDecimal bd = new BigDecimal(d);
    	return Double.valueOf(bd.setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue());
    }
	
	static public Double roundDouble3(double d) {
    	BigDecimal bd = new BigDecimal(d);
    	return Double.valueOf(bd.setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue());
    }
	
	static public Double roundDouble2(double d) {
    	BigDecimal bd = new BigDecimal(d);
    	return Double.valueOf(bd.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
    }
	
	static public Double roundDouble1(double d) {
    	BigDecimal bd = new BigDecimal(d);
    	return Double.valueOf(bd.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue());
    }
	
	static public String getDateTime(Calendar c) {
		return getDateTime(c.getTime());
    }
	
	static public String getDateTime(Date date) {
    	SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy_-_HH-mm", Locale.getDefault());
    	return sdf.format(date);
    }

	static public String getDate(Date date, Character separator) {
		StringBuilder sb = new StringBuilder();
		sb.append("dd").append(separator).append("MM").append(separator).append("yyyy");
    	SimpleDateFormat sdf = new SimpleDateFormat(sb.toString(), Locale.getDefault());
    	return sdf.format(date);
    }
	
	static public String getFormattedDateTime(Date date, char dateSeparator, char timeSeparator, String dateTimeSeparator) {
		StringBuilder sb = new StringBuilder();
		sb.append("dd").append(dateSeparator).append("MM").append(dateSeparator).append("yyyy").append(dateTimeSeparator).append("HH").append(timeSeparator).append("mm").append(timeSeparator).append("ss");
    	SimpleDateFormat sdf = new SimpleDateFormat(sb.toString(), Locale.getDefault());
    	return sdf.format(date);
    }
	
	static public String getDate(Date date, boolean dayOfMonth, boolean month, boolean year, Character separator) {
		StringBuilder sb = new StringBuilder();
		if(dayOfMonth) sb.append("dd");
		if(month) {
			if(sb.length() > 0) sb.append(separator);
			sb.append("MM");  
		}
		if(year) {
			if(sb.length() > 0) sb.append(separator);
			sb.append("yyyy");
		}
    	SimpleDateFormat sdf = new SimpleDateFormat(sb.toString(), Locale.getDefault());
    	return sdf.format(date);
    }
	
	static public boolean isSameYear(Date date1, Date date2) {
		Calendar c1 = Calendar.getInstance(), c2 = Calendar.getInstance();
		c1.setTime(date1);
		c2.setTime(date2);
		
		return isSameYear(c1, c2);
    }
	
	static public boolean isSameYear(Calendar date1, Calendar date2) {
		return date1.get(Calendar.YEAR) == date2.get(Calendar.YEAR); 
    }
	
	static public FilenameFilter getCSVFilenameFilter() {
		return new FilenameFilter() {
			@Override
			public boolean accept(File dir, String filename) {
				return filename.endsWith(".csv");
			}
		};
	}
	
	static public int getDiferencaDias(Date passado, Date futuro) {
		long p = passado.getTime();
		long f = futuro.getTime();
		
		long d = f - p;
		// 24*60*60*1000 = 86400000
		return (int)(d/86400000);
	}
	
	static public int getDiferencaDias(Calendar passado, Calendar futuro) {
		return getDiferencaDias(passado.getTime(), futuro.getTime());
	}
	
	static public double getDiferencaDiasDouble(Date passado, Date futuro) {
		long p = passado.getTime();
		long f = futuro.getTime();
		
		long d = f - p;
		// 24*60*60*1000 = 86400000
		return d/86400000.0;
	}
	
	static public double getDiferencaDiasDouble(Calendar passado, Calendar futuro) {
		return getDiferencaDias(passado.getTime(), futuro.getTime());
	}
	
}
