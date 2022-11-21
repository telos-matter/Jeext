package util;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class Dates {

	public static final String DATE_FORMAT = "yyyy-MM-dd"; // HTML format
	
	public static String toString (Date date) {
		if (date == null) {
			date = new Date(0);
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		String month = "" +calendar.get(Calendar.MONTH);
		String day = "" +calendar.get(Calendar.DAY_OF_MONTH);
		if (calendar.get(Calendar.MONTH) < 10) {
			month = "0" +month;
		}
		if (calendar.get(Calendar.DAY_OF_MONTH) < 10) {
			day = "0" +day;
		}
		return "" +calendar.get(Calendar.YEAR) +'-' +month +'-' +day;
	}
	
	public static Date getDateFromNow (int years) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date ());
		calendar.add(Calendar.YEAR, -years);
		return calendar.getTime();
		
	}
	
	public static Integer getYearFrom (Date date) {
		if (date == null) {
			return null;
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar.get(Calendar.YEAR);
	}
	
	public static Date getCurrentDate () {
		return new Date ();
	}
	
	public static int getCurrentYear () {
		return new GregorianCalendar().get(Calendar.YEAR);
	}
	
	public static boolean intersectsYear (Date date, Date start_date, Date finish_date) {
		int year = getYearFrom(date);
		return year >= getYearFrom(start_date) && year <= getYearFrom(finish_date);
	}
	
	public static boolean isAgeInferior (int years, String birthdate) {
		if (birthdate == null) {
			return false;
		}
		Date now = new Date ();
		Date birth = Strings.parseDate(birthdate);
		long age = now.getTime() -birth.getTime();
		return (age / (1000 * 60 * 60 * 24 * 365.25)) < years;
	}
	
	public static boolean isAgeSuperior (int years, String birthdate) {
		return !isAgeInferior(years, birthdate);
	}
	
	public static boolean equalsDate (Date date_1, Date date_2) {
		if (date_1 == null || date_2 == null) {
			return false;
		}
		Calendar calendar_1 = Calendar.getInstance();
		calendar_1.setTime(date_1);
		Calendar calendar_2 = Calendar.getInstance();
		calendar_2.setTime(date_2);
		return calendar_1.get(Calendar.YEAR) == calendar_2.get(Calendar.YEAR) && calendar_1.get(Calendar.MONTH) == calendar_2.get(Calendar.MONTH) && calendar_1.get(Calendar.DAY_OF_MONTH) == calendar_2.get(Calendar.DAY_OF_MONTH);
	}
	
	public static boolean intersectsStrict (Date x, Date start, Date finish) {
		return x.after(start) && x.before(finish);
	}
	
	public static boolean notIntersectsStrict (Date x, Date start, Date finish) {
		return ! intersectsStrict(x, start, finish);
	}
	
	public static boolean intersectsStrict (Date start_1, Date finish_1, Date start_2, Date finish_2) {
		return start_1.before(finish_2) && finish_1.after(start_2);
	}
	
	public static boolean notIntersectsStrict (Date start_1, Date finish_1, Date start_2, Date finish_2) {
		return ! intersectsStrict(start_1, finish_1, start_2, finish_2);
	}
	
}
