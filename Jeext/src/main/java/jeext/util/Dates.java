package jeext.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import jeext.util.exceptions.FailedRequirement;

/**
 * <p>A Class that provides/facilitates {@link LocalDate} functionalities.
 * <p>All methods tolerate <code>null</code> by design.
 */
public class Dates {

	/**
	 * HTML format
	 */
	public static final DateTimeFormatter DATE_FORMATER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	
	/**
	 * @return <code>true</code> if the given {@link LocalDate} plus 
	 * the given periods (years, months and days) have
	 * strictly elapsed (meaning it is now in the past), <code>false</code>
	 * if not and <code>null</code> if the given {@link LocalDate} is <code>null</code>
	 */
	public static Boolean hasElapsed (LocalDate start, int years, int months, int days) {
		if (start == null) {
			return null;
		}
		return start.plusDays(days).plusMonths(months).plusYears(years).isBefore(LocalDate.now());
	}
	
	/**
	 * Calls upon {@link #hasElapsed(LocalDate, int, int, int)}
	 * with the given {@link PeriodHolder} values
	 * @throws NullPointerException if the given {@link PeriodHolder} is <code>null</code>
	 * @see #hasElapsed(LocalDate, int, int, int)
	 */
	public static Boolean hasElapsed (LocalDate start, PeriodHolder period) {
		Objects.requireNonNull(period);
		return hasElapsed(start, period.getYears(), period.getMonths(), period.getDays());
	}
	
	/**
	 * Calls upon {@link #hasElapsed(LocalDate, int, int, int)}
	 * with the given periods (years and months) and with days set
	 * to zero
	 * @see #hasElapsed(LocalDate, int, int, int)
	 */
	public static Boolean hasElapsed (LocalDate start, int years, int months) {
		return hasElapsed(start, years, months, 0);
	}
	
	/**
	 * Calls upon {@link #hasElapsed(LocalDate, int, int, int)}
	 * with the given period of years and with months and days set
	 * to zero
	 * @see #hasElapsed(LocalDate, int, int, int)
	 */
	public static Boolean hasElapsed (LocalDate start, int years) {
		return hasElapsed(start, years, 0, 0);
	}

	/**
	 * @return <code>true</code> if the given {@link LocalDate} date is 
	 * strictly after the given {@link LocalDate} beginning and strictly before
	 * the given {@link LocalDate} end, <code>false</code> if not
	 * and <code>null</code> if any of the parameters are <code>null</code>
	 */
	public static Boolean isWithinStrict (LocalDate date, LocalDate beginning, LocalDate end) {
		if (date == null || beginning == null || end == null) {
			return null;
		}
		return date.isAfter(beginning) && date.isBefore(end);
	}

	/**
	 * Negates {@link #isWithinStrict(LocalDate, LocalDate, LocalDate)} but
	 * preserves <code>null</code>
	 * @see #isWithinStrict(LocalDate, LocalDate, LocalDate)
	 */
	public static Boolean isNotWithinStrict (LocalDate date, LocalDate beginning, LocalDate end) {
		Boolean value = isWithinStrict(date, beginning, end);
		return (value == null) ? null : !value;
	}
	
	/**
	 * Tests if the two date intervals strictly overlap one another. 
	 * Strict here means that only having the same beginning dates
	 * or only having the same end dates does not count as overlapping.
	 * @return <code>true</code> if the date interval from 
	 * the given {@link LocalDate} beginning_1 to the given {@link LocalDate}
	 * end_1 overlaps the date interval from the given
	 * {@link LocalDate} beginning_2 to the given {@link LocalDate} end_2,
	 * <code>false</code> if not and <code>null</code> if any of the
	 * parameters are <code>null</code> 
	 */
	public static Boolean overlapsStrict (LocalDate beginning_1, LocalDate end_1, LocalDate beginning_2, LocalDate end_2) {
		if (beginning_1 == null || end_1 == null || beginning_2 == null || end_2 == null) {
			return null;
		}
		return beginning_1.isBefore(end_2) && end_1.isAfter(beginning_2);
	}
	
	/**
	 * Negates {@link #overlapsStrict(LocalDate, LocalDate, LocalDate, LocalDate)} but
	 * preserves <code>null</code>
	 * @see #overlapsStrict(LocalDate, LocalDate, LocalDate, LocalDate)
	 */
	public static Boolean notOverlapsStrict (LocalDate beginning_1, LocalDate end_1, LocalDate beginning_2, LocalDate end_2) {
		Boolean value = overlapsStrict(beginning_1, end_1, beginning_2, end_2);
		return (value == null) ? null : !value;
	}
	
	/**
	 * <p>A class that represents a period of time in years, months and days
	 * <p>Primarily created to easily group those values
	 * <p>Use {@link #parse(String)} to parse a {@link String} into 
	 * an instance of this class
	 * <p>Negative values are permitted
	 */
	public static class PeriodHolder {
		
		private final int years, months, days;

		/**
		 * @param s	the {@link String} to be parsed. It must follow this format: 'y:m:d', 
		 * with y, m and d being the number of years, months and days respectively.
		 * Negative values are permitted
		 * @return an instance of this class from the given {@link String}
		 * @throws NullPointerException if the given {@link String} is <code>null</code>
		 * @throws FailedRequirement if the given {@link String} doesn't follow the format
		 */
		public static PeriodHolder parse (String s) {
			Objects.requireNonNull(s);
			
			int first_delimiter = s.indexOf(':');
			int last_delimiter = s.lastIndexOf(':');
			if (first_delimiter == -1 || first_delimiter == last_delimiter) {
				throw new FailedRequirement("The given String `" +s +"` does not follow the specified format");
			}
			
			Integer years = Parser.parseInt(s.substring(0, first_delimiter));
			Integer months = Parser.parseInt(s.substring(first_delimiter +1, last_delimiter));
			Integer days = Parser.parseInt(s.substring(last_delimiter +1, s.length()));
			
			if (years == null || months == null || days == null) {
				throw new FailedRequirement("The given String `" +s +"` does not follow the specified format");
			}
			
			return new PeriodHolder(years, months, days);
		}
		
		/**
		 * <p>Tries to parse the {@link String} to a {@link PeriodHolder}
		 * but returns <code>null</code> if it fails
		 * <p>Different than {@link #parse(String)} in that it throws
		 * no exceptions
		 * 
		 * @return	{@link PeriodHolder} representation of the passed {@link String}
		 * or <code>null</code> if the parsing failed
		 * 
		 * @see #parse(String)
		 */
		public static PeriodHolder parseOrNull (String s) {
			try {
				return parse(s);
			} catch (Exception e) {
				return null;
			}
		}
		
		public PeriodHolder(int years, int months, int days) {
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
		public int hashCode() {
			return Objects.hash(days, months, years);
		}

		@Override
		public boolean equals(Object other) {
			if (this == other) {
				return true;
			}
			if (other == null) {
				return false;
			}
			if (other instanceof PeriodHolder _other) {
				return days == _other.days && months == _other.months && years == _other.years;
			} else {
				return false;
			}
		}

		@Override
		public String toString() {
			return "PeriodHolder [years=" + years + ", months=" + months + ", days=" + days + "]";
		}
	}
	
}
