package util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Dates {

	/**
	 * HTML format
	 */
	public static final DateTimeFormatter DATE_FORMATER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	
	public static boolean hasElapsed (LocalDate start, int years, int months, int days) {
		return start.plusDays(days).plusMonths(months).plusYears(years).isBefore(LocalDate.now());
	}
	
	public static boolean hasElapsed (LocalDate start, DateValuesHolder period) {
		return hasElapsed(start, period.getYears(), period.getMonths(), period.getDays());
	}
	
	public static boolean hasElapsed (LocalDate start, int years, int months) {
		return hasElapsed(start, years, months, 0);
	}
	
	public static boolean hasElapsed (LocalDate start, int years) {
		return hasElapsed(start, years, 0, 0);
	}

	public static boolean isWithinStrict (LocalDate x, LocalDate start, LocalDate end) {
		return x.isAfter(start) && x.isBefore(end);
	}
	
	public static boolean isNotWithinStrict (LocalDate x, LocalDate start, LocalDate end) {
		return ! isWithinStrict(x, start, end);
	}
	
	public static boolean intersectsStrict (LocalDate start_1, LocalDate end_1, LocalDate start_2, LocalDate end_2) {
		return start_1.isBefore(end_2) && end_1.isAfter(start_2);
	}
	
	public static boolean notIntersectsStrict (LocalDate start_1, LocalDate end_1, LocalDate start_2, LocalDate end_2) {
		return ! intersectsStrict(start_1, end_1, start_2, end_2);
	}
	
	
	public static class DateValuesHolder {
		
		private final int years, months, days;

		public static DateValuesHolder parse (String s) {
			if (s == null || s.length() != 10) {
				return null;
			}
			
			Integer years = Parser.parseInt(s.substring(0, 4));
			Integer months = Parser.parseInt(s.substring(5, 7));
			Integer days = Parser.parseInt(s.substring(8, 10));
			
			if (years == null || months == null || days == null || years < 0 || months < 0 || days < 0) {
				return null;
			}
			
			return new DateValuesHolder(years, months, days);
		}
		
		public DateValuesHolder(int years, int months, int days) {
			if (years < 0 || months < 0 || days < 0) {
				throw new RuntimeException(String.format("Date values must be positive: %d-%d-%d", years, months, days));
			}
			
			this.years = years;
			this.months = months;
			this.days = days;
		}

		public int getYears() {
			return years;
		}

		public int getMonths() {
			return months;
		}

		public int getDays() {
			return days;
		}

		@Override
		public boolean equals(Object other) {
			if (this == other) {
				return true;
			}
			if (other == null) {
				return false;
			}
			if (getClass() != other.getClass()) {
				return false;
			}
			DateValuesHolder _other = (DateValuesHolder) other;
			return days == _other.days && months == _other.months && years == _other.years;
		}
	}
	
}
