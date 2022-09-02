package pri;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAdjusters;

public class EpochTool {

	public enum Format {

		/**
		 * format yyyy/MM/dd HH:mm:ss
		 */
		STD_SLASH_FULL("yyyy/MM/dd HH:mm:ss"),

		/**
		 * format yyyy/MM/dd
		 */
		STD_SLASH_DAY("yyyy/MM/dd"),

		/**
		 * format yyyy-MM-dd HH:mm:ss
		 */
		STD_DASH_FULL("yyyy-MM-dd HH:mm:ss"),

		/**
		 * format yyyy-MM-dd HH:mm:ss
		 */
		STD_DASH_MINUTE("yyyy-MM-dd HH:mm"),
		
		/**
		 * format yyyyMMddHHmmss
		 * to use with request along with oracle format yyyymmddhh24miss
		 */
		ORACLE_YYYYMMDDHH24MISS("yyyyMMddHHmmss"),

		/**
		 * format yyyy-MM-dd E(e) HH:mm:ss
		 * to use with request along with oracle format yyyymmddhh24miss
		 */
		CALENDAR("yyyy-MM-dd E(e) HH:mm:ss"),
		
		/**
		 * format yyyy-MM-dd'T'HH:mm'Z'
		 * standard iso 8601 date format
		 */
		ISO8601("yyyy-MM-dd'T'HH:mm'Z'");

		private String name = "";

		Format( String name ) {
			this.name = name;
		}

		public String toString() {
			return name;
		}
	}

	public enum Direction {

		forward, backward
	}

	public enum Span {
		Seconds, Minutes, Hours, Days;
	}

	//--------------------------------------------------------------------------
	
	public static Long getNowEpoch() {
				
		Instant instant = Instant.now();
		return instant.getEpochSecond();
	}

	//-------------------------------- String -> Epoch -------------------------

	/** parse a date string to epoch long
	 * 
	 * @param date
	 * @param zoneId if null is given will use "UTC" time zone
	 * @param format enum item
	 * @return an epoch or null (is date is null)
	 */
	public static Long convertToEpoch( String date, Format format, ZoneId zoneId ) {

		if( date == null ) {
			return null;
		}
		
		ZoneId zoneIdUsed = null;
		if( zoneId == null ) {
			zoneIdUsed = ZoneId.of( "UTC" );
		}
		else {
			zoneIdUsed = zoneId;
		}

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern( format.toString() );
		LocalDateTime ldt = LocalDateTime.parse( date, formatter );
		ZonedDateTime zdt = ZonedDateTime.of( ldt, zoneIdUsed );
		return zdt.toEpochSecond();
	}


	/** parse a date string to epoch long
	 * 
	 * @param date
	 * @param zoneId if null is given will use "UTC" time zone
	 * @param customFormat string format
	 * @return an epoch or null (is date is null)
	 */
	public static Long convertToEpoch( String date, String customFormat, ZoneId zoneId ) {

		if( date == null ) {
			return null;
		}
		
		ZoneId zoneIdUsed = null;
		if( zoneId == null ) {
			zoneIdUsed = ZoneId.of( "UTC" );
		}
		else {
			zoneIdUsed = zoneId;
		}

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern( customFormat );
		LocalDateTime ldt = LocalDateTime.parse( date, formatter );
		ZonedDateTime zdt = ZonedDateTime.of( ldt, zoneIdUsed );
		return zdt.toEpochSecond();
	}

	/**
	 * convert an iso instant string to an epoch
	 * Obtains an epoch from a text string such as 2007-12-03T10:15:30.00Z. (ISO-8601 instant format) 
	 * 
	 * @param isoInstant
	 * @return an epoch
	 */
	public static Long convertIsoInstantToEpoch( String isoInstant ) {

		if( isoInstant == null ) {
			return null;
		}

		Instant instant = Instant.parse( isoInstant );
		return instant.getEpochSecond();
	}

	// -------------------------------- Epoch -> String -------------------------

	/**
	 * convert an epoch to a formatted string ( in "UTC" time zone )
	 * @param epoch
	 * @param format enum item
	 * @return
	 * @throws IllegalArgumentException
	 */
	public static String convertToString( Long epoch, Format format ) throws IllegalArgumentException {

		return convertToString( epoch, ZoneId.of( "UTC" ), format );
	}

	/**
	 * convert an epoch from DateTimeFormatter model ( in "UTC" time zone )
	 * @param epoch
	 * @param customFormat custom string format
	 * @return
	 * @throws IllegalArgumentException
	 */
	public static String convertToString( Long epoch, DateTimeFormatter format ) throws IllegalArgumentException {

		return convertToString( epoch, ZoneId.of( "UTC" ), format );
	}

	/**
	 * convert an epoch to a formatted string ( in "UTC" time zone )
	 * @param epoch
	 * @param customFormat custom string format
	 * @return
	 * @throws IllegalArgumentException
	 */
	public static String convertToString( Long epoch, String customFormat ) throws IllegalArgumentException {

		return convertToString( epoch, ZoneId.of( "UTC" ), customFormat );
	}

	/**
	 * convert an epoch from DateTimeFormatter model
	 * @param epoch
	 * @param zoneId if null is given will use "UTC" time zone 
	 * @param format enum item
	 * @return
	 * @throws IllegalArgumentException
	 */
	public static String convertToString( Long epoch, ZoneId zoneId, DateTimeFormatter format ) throws IllegalArgumentException {

		if( epoch == null ) {
			return null;
		}
		
		ZoneId zoneIdUsed = null;
		if( zoneId == null ) {
			zoneIdUsed = ZoneId.of( "UTC" );
		}
		else {
			zoneIdUsed = zoneId;
		}

		ZonedDateTime zdt = epochToZonedDate( epoch, zoneIdUsed );
		return zdt.format( format );
	}

	/**
	 * convert an epoch to a formatted string
	 * @param epoch
	 * @param zoneId if null is given will use "UTC" time zone 
	 * @param format enum item
	 * @return
	 * @throws IllegalArgumentException
	 */
	public static String convertToString( Long epoch, ZoneId zoneId, Format format ) throws IllegalArgumentException {

		if( epoch == null ) {
			return null;
		}
		
		ZoneId zoneIdUsed = null;
		if( zoneId == null ) {
			zoneIdUsed = ZoneId.of( "UTC" );
		}
		else {
			zoneIdUsed = zoneId;
		}

		ZonedDateTime zdt = epochToZonedDate( epoch, zoneIdUsed );
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern( format.toString() );
		return zdt.format( formatter );
	}

	/**
	 * convert an epoch to a formatted string
	 * @param epoch
	 * @param zoneId if null is given will use "UTC" time zone 
	 * @param customFormat custom string format
	 * @return
	 * @throws IllegalArgumentException
	 */
	public static String convertToString( Long epoch, ZoneId zoneId, String customFormat ) throws IllegalArgumentException {

		if( epoch == null ) {
			return null;
		}
		
		ZoneId zoneIdUsed = null;
		if( zoneId == null ) {
			zoneIdUsed = ZoneId.of( "UTC" );
		}
		else {
			zoneIdUsed = zoneId;
		}

		ZonedDateTime zdt = epochToZonedDate( epoch, zoneIdUsed );
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern( customFormat );
		return zdt.format( formatter );
	}

	public static String convertToPredefinedFormat( Long epoch, ZoneId zoneId, DateTimeFormatter formatter ) throws IllegalArgumentException {

		if( epoch == null ) {
			return null;
		}

		ZoneId zoneIdUsed = null;
		if( zoneId == null ) {
			zoneIdUsed = ZoneId.of( "UTC" );
		}
		else {
			zoneIdUsed = zoneId;
		}

		ZonedDateTime zdt = epochToZonedDate( epoch, zoneIdUsed );
		return zdt.format( formatter );
	}

	// -------------------------------- Adjustment ------------------------------

	/**
	 * set (adjust) some digit of a date (in the given time zone) of the given
	 * epoch
	 * 
	 * @param epoch
	 * @param year
	 *            to set or null if digit has not to be changed
	 * @param month
	 * @param day
	 * @param hour
	 * @param minute
	 * @param second
	 * @param zoneId
	 *            time zone if to translate the epoch into date (if null is
	 *            given, will use "UTC")
	 * @param verbose
	 * @return
	 * @throws Exception
	 */
	public static Long adjust( Long epoch, Integer year, Integer month, Integer day, Integer hour, Integer minute, Integer second, ZoneId zoneId, Boolean verbose ) throws Exception {

		if( epoch == null ) {
			return null;
		}

		ZoneId zoneIdUsed = null;
		if( zoneId == null ) {
			zoneIdUsed = ZoneId.of( "UTC" );
		}
		else {
			zoneIdUsed = zoneId;
		}

		if( verbose == true ) {

			// Tracer.info(String.format( "adjust (%d) %s [%s] ->", epoch, convertToString( epoch, zoneId, Format.STD_SLASH_FULL ), zoneId.getId() ));
		}

		Instant i = Instant.ofEpochSecond( epoch );
		ZonedDateTime zdt = ZonedDateTime.ofInstant( i, zoneIdUsed );

		int yearDigit = zdt.getYear();
		int monthDigit = zdt.getMonthValue();
		int dayOfMonthDigit = zdt.getDayOfMonth();
		int hourDigit = zdt.getHour();
		int minuteDigit = zdt.getMinute();
		int secondDigit = zdt.getSecond();

		if( year != null ) {
			yearDigit = year;
		}
		if( month != null ) {
			monthDigit = month;
		}
		if( day != null ) {
			dayOfMonthDigit = day;
		}
		if( hour != null ) {
			hourDigit = hour;
		}
		if( minute != null ) {
			minuteDigit = minute;
		}
		if( second != null ) {
			secondDigit = second;
		}

		LocalDateTime lt = LocalDateTime.of( yearDigit, monthDigit, dayOfMonthDigit, hourDigit, minuteDigit, secondDigit );

		zdt = ZonedDateTime.of( lt, zoneIdUsed );

		Long output = zdt.toEpochSecond();

		if( verbose == true ) {

			// Tracer.info(String.format( " (%d) %s [%s]", epoch, convertToString( output, zoneId, Format.STD_SLASH_FULL ), zoneId.getId() ));

		}
		return output;
	}

	/**
	 * set (adjust) the given epoch with the specified strategy (thus the temporal adjuster)
	 * 
	 * e.g. : to get the start of the current week : TemporalAdjusters.previousOrSame( DayOfWeek.MONDAY );
	 * 
	 * @param epoch
	 * @param adjuster
	 * @param zoneId
	 *            time zone if to translate the epoch into date (if null is
	 *            given, will use "UTC")
	 * @param verbose
	 * @return
	 * @throws Exception
	 */
	public static Long adjustWith( Long epoch, TemporalAdjuster adjuster, ZoneId zoneId, Boolean verbose ) {

		if( epoch == null ) {
			return null;
		}

		ZoneId zoneIdUsed = null;
		if( zoneId == null ) {
			zoneIdUsed = ZoneId.of( "UTC" );
		}
		else {
			zoneIdUsed = zoneId;
		}

		if( verbose == true ) {
			// Tracer.info(String.format( "adjust (%d) %s [%s] ->", epoch, convertToString( epoch, zoneId, Format.STD_SLASH_FULL ), zoneId.getId() ));
		}

		Instant i = Instant.ofEpochSecond( epoch );
		ZonedDateTime zdt = ZonedDateTime.ofInstant( i, zoneIdUsed );

		//------------ Active Part --------------------

		zdt = zdt.with( adjuster );

		//---------------------------------------------

		Long output = zdt.toEpochSecond();

		if( verbose == true ) {
			// Tracer.info(String.format( " (%d) %s [%s]", epoch, convertToString( output, zoneId, Format.STD_SLASH_FULL ), zoneId.getId() ));
		}
		return output;
	}

	// << predefined adjustments >>

	/** adjust (truncate) on start of the day (00h00) of the given epoch */
	public static Long adjustOnStartOfDay( Long epoch, ZoneId zoneId, Boolean verbose ) {

		if( epoch == null ) {
			return null;
		}

		ZoneId zoneIdUsed = null;
		if( zoneId == null ) {
			zoneIdUsed = ZoneId.of( "UTC" );
		}
		else {
			zoneIdUsed = zoneId;
		}

		if( verbose == true ) {
			// Tracer.info(String.format( "adjust (%d) %s [%s] ->", epoch, convertToString( epoch, zoneId, Format.CALENDAR ), zoneId.getId() ));
		}

		Instant i = Instant.ofEpochSecond( epoch );
		ZonedDateTime zdt = ZonedDateTime.ofInstant( i, zoneIdUsed );

		//------------ Active Part --------------------

		zdt = zdt.withHour( 0 ).withMinute( 0 ).withSecond( 0 );

		//---------------------------------------------

		Long output = zdt.toEpochSecond();

		if( verbose == true ) {
			// Tracer.info(String.format( " (%d) %s [%s]", epoch, convertToString( output, zoneId, Format.CALENDAR ), zoneId.getId() ));
		}
		return output;
	}

	/** adjust (advance forward) on end of the day (24h00)/start of the next day (00h00) of the given epoch */
	public static Long adjustOnEndOfDay( Long epoch, ZoneId zoneId, Boolean verbose ) {

		if( epoch == null ) {
			return null;
		}

		ZoneId zoneIdUsed = null;
		if( zoneId == null ) {
			zoneIdUsed = ZoneId.of( "UTC" );
		}
		else {
			zoneIdUsed = zoneId;
		}

		if( verbose == true ) {
			// Tracer.info(String.format( "adjust (%d) %s [%s] ->", epoch, convertToString( epoch, zoneId, Format.CALENDAR ), zoneId.getId() ));
		}

		Instant i = Instant.ofEpochSecond( epoch );
		ZonedDateTime zdt = ZonedDateTime.ofInstant( i, zoneIdUsed );

		//------------ Active Part --------------------

		zdt = zdt.withHour( 0 ).withMinute( 0 ).withSecond( 0 ).plusDays( 1 );

		//---------------------------------------------

		Long output = zdt.toEpochSecond();

		if( verbose == true ) {
			// Tracer.info(String.format( " (%d) %s [%s]", epoch, convertToString( output, zoneId, Format.CALENDAR ), zoneId.getId() ));
		}
		return output;
	}
	
	/** adjust (truncate) on start of the week (00h00) of the given epoch */
	public static Long adjustOnStartOfWeek( Long epoch, ZoneId zoneId, Boolean verbose ) {

		if( epoch == null ) {
			return null;
		}

		ZoneId zoneIdUsed = null;
		if( zoneId == null ) {
			zoneIdUsed = ZoneId.of( "UTC" );
		}
		else {
			zoneIdUsed = zoneId;
		}

		if( verbose == true ) {
			// Tracer.info(String.format( "adjust (%d) %s [%s] ->", epoch, convertToString( epoch, zoneId, Format.CALENDAR ), zoneId.getId() ));
		}

		Instant i = Instant.ofEpochSecond( epoch );
		ZonedDateTime zdt = ZonedDateTime.ofInstant( i, zoneIdUsed );

		//------------ Active Part --------------------

		zdt = zdt.withHour( 0 ).withMinute( 0 ).withSecond( 0 ).with( TemporalAdjusters.previousOrSame( DayOfWeek.MONDAY ) );

		//---------------------------------------------

		Long output = zdt.toEpochSecond();

		if( verbose == true ) {
			// Tracer.info(String.format( " (%d) %s [%s]", epoch, convertToString( output, zoneId, Format.CALENDAR ), zoneId.getId() ));
		}
		return output;
	}

	/** adjust (advance forward) on end of the week (24h00)/start of the next week (00h00) of the given epoch */
	public static Long adjustOnEndOfWeek( Long epoch, ZoneId zoneId, Boolean verbose ) {

		if( epoch == null ) {
			return null;
		}

		ZoneId zoneIdUsed = null;
		if( zoneId == null ) {
			zoneIdUsed = ZoneId.of( "UTC" );
		}
		else {
			zoneIdUsed = zoneId;
		}

		if( verbose == true ) {
			// Tracer.info(String.format( "adjust (%d) %s [%s] ->", epoch, convertToString( epoch, zoneId, Format.CALENDAR ), zoneId.getId() ));
		}

		Instant i = Instant.ofEpochSecond( epoch );
		ZonedDateTime zdt = ZonedDateTime.ofInstant( i, zoneIdUsed );

		//------------ Active Part --------------------

		zdt = zdt.withHour( 0 ).withMinute( 0 ).withSecond( 0 ).with( TemporalAdjusters.next( DayOfWeek.MONDAY ) );

		//---------------------------------------------

		Long output = zdt.toEpochSecond();

		if( verbose == true ) {
			// Tracer.info(String.format( " (%d) %s [%s]", epoch, convertToString( output, zoneId, Format.CALENDAR ), zoneId.getId() ));
		}
		return output;
	}

	/** adjust (truncate) on start of the month (00h00) of the given epoch */
	public static Long adjustOnStartOfMonth( Long epoch, ZoneId zoneId, Boolean verbose ) {

		if( epoch == null ) {
			return null;
		}

		ZoneId zoneIdUsed = null;
		if( zoneId == null ) {
			zoneIdUsed = ZoneId.of( "UTC" );
		}
		else {
			zoneIdUsed = zoneId;
		}

		if( verbose == true ) {
			// Tracer.info(String.format( "adjust (%d) %s [%s] ->", epoch, convertToString( epoch, zoneId, Format.CALENDAR ), zoneId.getId() ));
		}

		Instant i = Instant.ofEpochSecond( epoch );
		ZonedDateTime zdt = ZonedDateTime.ofInstant( i, zoneIdUsed );

		//------------ Active Part --------------------

		zdt = zdt.withHour( 0 ).withMinute( 0 ).withSecond( 0 ).withDayOfMonth( 1 );

		//---------------------------------------------

		Long output = zdt.toEpochSecond();

		if( verbose == true ) {
			// Tracer.info(String.format( " (%d) %s [%s]", epoch, convertToString( output, zoneId, Format.CALENDAR ), zoneId.getId() ));
		}
		return output;
	}

	/** adjust (advance forward) on end of the month (24h00)/start of the next month (00h00) of the given epoch */
	public static Long adjustOnEndOfMonth( Long epoch, ZoneId zoneId, Boolean verbose ) {

		if( epoch == null ) {
			return null;
		}

		ZoneId zoneIdUsed = null;
		if( zoneId == null ) {
			zoneIdUsed = ZoneId.of( "UTC" );
		}
		else {
			zoneIdUsed = zoneId;
		}

		if( verbose == true ) {
			// Tracer.info(String.format( "adjust (%d) %s [%s] ->", epoch, convertToString( epoch, zoneId, Format.CALENDAR ), zoneId.getId() ));
		}

		Instant i = Instant.ofEpochSecond( epoch );
		ZonedDateTime zdt = ZonedDateTime.ofInstant( i, zoneIdUsed );

		//------------ Active Part --------------------

		zdt = zdt.with( TemporalAdjusters.firstDayOfNextMonth()).withHour( 0 ).withMinute( 0 ).withSecond( 0 );
		//---------------------------------------------

		Long output = zdt.toEpochSecond();

		if( verbose == true ) {
			// Tracer.info(String.format( " (%d) %s [%s]", epoch, convertToString( output, zoneId, Format.CALENDAR ), zoneId.getId() ));
		}
		return output;
	}
	
	/** adjust (truncate) on start of the year (00h00) of the given epoch */
	public static Long adjustOnStartOfYear( Long epoch, ZoneId zoneId, Boolean verbose ) {

		if( epoch == null ) {
			return null;
		}

		ZoneId zoneIdUsed = null;
		if( zoneId == null ) {
			zoneIdUsed = ZoneId.of( "UTC" );
		}
		else {
			zoneIdUsed = zoneId;
		}

		if( verbose == true ) {
			// Tracer.info(String.format( "adjust (%d) %s [%s] ->", epoch, convertToString( epoch, zoneId, Format.CALENDAR ), zoneId.getId() ));
		}

		Instant i = Instant.ofEpochSecond( epoch );
		ZonedDateTime zdt = ZonedDateTime.ofInstant( i, zoneIdUsed );

		//------------ Active Part --------------------

		zdt = zdt.withHour( 0 ).withMinute( 0 ).withSecond( 0 ).withDayOfYear( 1 );

		//---------------------------------------------

		Long output = zdt.toEpochSecond();

		if( verbose == true ) {
			// Tracer.info(String.format( " (%d) %s [%s]", epoch, convertToString( output, zoneId, Format.CALENDAR ), zoneId.getId() ));
		}
		return output;
	}

	/** adjust (advance forward) on end of the year (24h00)/start of the next year (00h00) of the given epoch */
	public static Long adjustOnEndOfYear( Long epoch, ZoneId zoneId, Boolean verbose ) {

		if( epoch == null ) {
			return null;
		}

		ZoneId zoneIdUsed = null;
		if( zoneId == null ) {
			zoneIdUsed = ZoneId.of( "UTC" );
		}
		else {
			zoneIdUsed = zoneId;
		}

		if( verbose == true ) {
			// Tracer.info(String.format( "adjust (%d) %s [%s] ->", epoch, convertToString( epoch, zoneId, Format.CALENDAR ), zoneId.getId() ));
		}

		Instant i = Instant.ofEpochSecond( epoch );
		ZonedDateTime zdt = ZonedDateTime.ofInstant( i, zoneIdUsed );

		//------------ Active Part --------------------

		zdt = zdt.with( TemporalAdjusters.firstDayOfNextYear()).withHour( 0 ).withMinute( 0 ).withSecond( 0 );
		//---------------------------------------------

		Long output = zdt.toEpochSecond();

		if( verbose == true ) {
			// Tracer.info(String.format( " (%d) %s [%s]", epoch, convertToString( output, zoneId, Format.CALENDAR ), zoneId.getId() ));
		}
		return output;
	}
	
	// -------------------------------- Shifting --------------------------------

	/**
	 * convert an epoch to ZonedDateTime object with the given ZoneId
	 * @param epoch
	 * @param zoneId if null is given will use "UTC" time zone 
	 * @return
	 */
	public static ZonedDateTime epochToZonedDate( Long epoch, ZoneId zoneId ) {

		if( epoch == null ) {
			return null;
		}

		ZoneId zoneIdUsed = null;

		if( zoneId == null ) {
			zoneIdUsed = ZoneId.of( "UTC" );
		}
		else {
			zoneIdUsed = zoneId;
		}

		Instant i = Instant.ofEpochSecond( epoch );
		ZonedDateTime zdt = ZonedDateTime.ofInstant( i, zoneIdUsed );
		return zdt;
	}

	/**
	 * add/subtract some digit from a date (in the given time zone) of the given
	 * epoch
	 * 
	 * @param epoch
	 * @param year
	 *            to set or null if digit has not to be changed
	 * @param month
	 * @param day
	 * @param hour
	 * @param minute
	 * @param second
	 * @param zoneId
	 *            time zone if to translate the epoch into date (if null is
	 *            given, will use "UTC")
	 * @param verbose
	 * @return
	 * @throws Exception
	 */
	public static Long add( Long epoch, Integer years, Integer months, Integer days, Integer hours, Integer minutes, Integer seconds, ZoneId zoneId, Boolean verbose ) throws Exception {

		if( epoch == null ) {
			return null;
		}

		ZoneId zoneIdUsed = null;
		if( zoneId == null ) {
			zoneIdUsed = ZoneId.of( "UTC" );
		}
		else {
			zoneIdUsed = zoneId;
		}

		if( verbose == true ) {

			// Tracer.info(String.format( "add input  (%d) %s [%s]", epoch, convertToString( epoch, zoneId, Format.STD_SLASH_FULL ), zoneId.getId() ));
		}

		Instant i = Instant.ofEpochSecond( epoch );
		ZonedDateTime zdt = ZonedDateTime.ofInstant( i, zoneIdUsed );

		if( years != null ) {
			zdt = zdt.plusYears( years );
		} ;
		if( months != null ) {
			zdt = zdt.plusMonths( months );
		}
		if( days != null ) {
			zdt = zdt.plusDays( days );
		}
		if( hours != null ) {
			zdt = zdt.plusHours( hours );
		}
		if( minutes != null ) {
			zdt = zdt.plusMinutes( minutes );
		}
		if( seconds != null ) {
			zdt = zdt.plusSeconds( seconds );
		}

		Long output = zdt.toEpochSecond();

		if( verbose == true ) {

			// Tracer.info(String.format( "add output (%d) %s [%s]", epoch, convertToString( output, zoneId, Format.STD_SLASH_FULL ), zoneId.getId() ));

		}
		return output;
	}

	/**
	 * shift forward or backward an epoch of a data from it's own period
	 * 
	 * @param dataEpoch the epoch of a data of the same { period + periodUnit + zoneId }  
	 * @param direction { forward | backward } 
	 * @param period
	 * @param periodUnit { 's' for seconds, 'H' for hours, 'D' for days }
	 * @param periodZoneId optional : time zone to use when computing the shift, if set to null will use UTC
	 * @param verbose (if set to null count as false) 
	 * @return the shifted epoch
	 * @throws Exception
	 */
	public static Long shiftFromPeriod( Long dataEpoch, Direction direction, Long period, char periodUnit, ZoneId periodZoneId, Boolean verbose ) throws Exception {

		if( dataEpoch == null ) {
			return null;
		}

		Long output = null;

		ZoneId zoneIdUsed = null;
		if( periodZoneId == null ) {
			zoneIdUsed = ZoneId.of( "UTC" );
		}
		else {
			zoneIdUsed = periodZoneId;
		}

		if( verbose == true ) {

			// Tracer.info(String.format( "shiftByPeriod (%d%c) input  (%d) %s [%s]", period, periodUnit, dataEpoch, convertToString( dataEpoch, zoneIdUsed, Format.STD_SLASH_FULL ), zoneIdUsed.getId() ));
		}

		switch( periodUnit )
		{
			case 's': {

				if( direction == Direction.forward ) {
					output = dataEpoch + period;
				}
				else {
					output = dataEpoch - period;
				}
				break;
			}

			case 'H': {

				Instant i = Instant.ofEpochSecond( dataEpoch );
				ZonedDateTime zdt = ZonedDateTime.ofInstant( i, zoneIdUsed );

				if( direction == Direction.forward ) {
					zdt = zdt.plusHours( period );
				}
				else {
					zdt = zdt.minusHours( period );
				}
				output = zdt.toEpochSecond();
				break;
			}

			case 'D': {

				Instant i = Instant.ofEpochSecond( dataEpoch );
				ZonedDateTime zdt = ZonedDateTime.ofInstant( i, zoneIdUsed );

				if( direction == Direction.forward ) {
					zdt = zdt.plusDays( period );
				}
				else {
					zdt = zdt.minusDays( period );
				}
				output = zdt.toEpochSecond();
				break;
			}
			
			case 'M': {

				Instant i = Instant.ofEpochSecond( dataEpoch );
				ZonedDateTime zdt = ZonedDateTime.ofInstant( i, zoneIdUsed );

				if( direction == Direction.forward ) {
					zdt = zdt.plusMonths( period );
				}
				else {
					zdt = zdt.minusMonths( period );
				}
				output = zdt.toEpochSecond();
				break;
			}
			
			case 'A': {

				Instant i = Instant.ofEpochSecond( dataEpoch );
				ZonedDateTime zdt = ZonedDateTime.ofInstant( i, zoneIdUsed );

				if( direction == Direction.forward ) {
					zdt = zdt.plusYears( period );
				}
				else {
					zdt = zdt.minusYears( period );
				}
				output = zdt.toEpochSecond();
				break;
			}
			
			default : {
				throw new Exception( "unexpected periodUnit value" );
			}
		}

		if( verbose == true ) {

			// Tracer.info(String.format( "shiftByPeriod (%d%c) output (%d) %s [%s]", period, periodUnit, output, convertToString( output, zoneIdUsed, Format.STD_SLASH_FULL ), zoneIdUsed.getId() ));
		}

		return output;
	}

	/** 
	 *  this function behave as shiftFromPeriod excepting that the input param is one of the typeBase (P2) letter ('1', '2', ... 'Q', 'D', ... 'J' )
	 */
	public static Long shiftFromTypeBase( Long dataEpoch, Direction direction, char typeBase, ZoneId periodZoneId, Boolean verbose ) throws Exception {
		
		Long period = 60L;
		char periodUnit = 's';
		
		switch( typeBase ) {

			case '1' : { period =  60L; periodUnit = 's'; break; }
			case '2' : { period = 120L; periodUnit = 's'; break; }
			case '3' : { period = 180L; periodUnit = 's'; break; }
			case '4' : { period = 240L; periodUnit = 's'; break; }
			case '5' : { period = 300L; periodUnit = 's'; break; }
			case '6' : { period = 360L; periodUnit = 's'; break; }
			case 'X' : { period = 600L; periodUnit = 's'; break; }
			case 'Q' : { period = 900L; periodUnit = 's'; break; }
			case 'D' : { period =1800L; periodUnit = 's'; break; }	
			case 'H' : { period =   1L; periodUnit = 'H'; break; }	
			case 'J' : { period =   1L; periodUnit = 'D'; break; }
	
			default : {
				throw new Exception( "unexpected typeBase value" );
			}
		}

		return shiftFromPeriod( dataEpoch, direction, period, periodUnit, periodZoneId, verbose );
	}	
	
	/**
	 * return the epoch of the nearest data of such { period + periodUnit + periodZoneId } forward or backward the given input epoch
	 * note if the given epoch already match a data time stamp, the given input epoch will be returned
	 * 
	 * @param epoch to start the search from
	 * @param direction { forward | backward } 
	 * @param period
	 * @param periodUnit { 's' for seconds, 'H' for hours, 'D' for days }
	 * @param periodZoneId optional : time zone to use when computing the shift, if set to null will use UTC
	 * @param verbose (if set to null count as false) 
	 * @return the nearest data epoch
	 * @throws Exception
	 */
	public static Long setToNearestData( Long epoch, Direction direction, Long period, char periodUnit, ZoneId periodZoneId, Boolean verbose ) throws Exception {

		if( epoch == null ) {
			return null;
		}

		ZoneId zoneIdUsed = null;
		if( periodZoneId == null ) {
			zoneIdUsed = ZoneId.of( "UTC" );
		}
		else {
			zoneIdUsed = periodZoneId;
		}

		if( verbose == true ) {

			// Tracer.info(String.format( "setToNearestData (%d%c) input  (%d) %s [%s]", period, periodUnit, epoch, convertToString( epoch, zoneIdUsed, Format.STD_SLASH_FULL ), zoneIdUsed.getId() ));
		}

		Long output = null;

		switch( periodUnit )
		{
			case 's': {

				Long reminder = epoch % period;

				if( reminder == 0 ) {
					// the epoch is already pointing a data
					output = epoch;
				}
				else {
					if( direction == Direction.forward ) {
						output = ((epoch / period) + 1) * period;
					}
					else {
						output = (epoch / period) * period;
					}
				}
				break;
			}
			case 'H': {

				if( period > 1 ) { throw new Exception( "setToNearestData : unsupported input parameters" ); }

				Long epoch0m00sLT = adjust( epoch, null, null, null, null, 0, 0, zoneIdUsed, verbose );

				if( epoch0m00sLT.equals( epoch ) ) {

					// epoch is already pointing a hourly data
					output = epoch;
				}
				else {
					if( direction == Direction.forward ) {
						output = shiftFromPeriod( epoch0m00sLT, direction, period, periodUnit, periodZoneId, verbose );
					}
					else {
						output = epoch0m00sLT;
					}
				}
				break;
			}
			case 'D': {

				if( period > 1 ) { throw new Exception( "setToNearestData : unsupported input parameters" ); }

				Long epoch0h00m00sLT = adjust( epoch, null, null, null, 0, 0, 0, zoneIdUsed, verbose );

				if( epoch0h00m00sLT.equals( epoch ) ) {

					// epoch is already pointing a daily data
					output = epoch;
				}
				else {
					if( direction == Direction.forward ) {
						output = shiftFromPeriod( epoch0h00m00sLT, direction, period, periodUnit, periodZoneId, verbose );
					}
					else {
						output = epoch0h00m00sLT;
					}
				}
				break;
			}
			case 'M': {
				if( period > 1 ) { throw new Exception( "setToNearestData : unsupported input parameters" ); }
			
				// Epoch must be start of month ?
				Long epoch0h00m00sLT = adjust( epoch, null, null, 1, 0, 0, 0, zoneIdUsed, verbose );

				if( epoch0h00m00sLT.equals( epoch ) ) {

					// epoch is already pointing a daily data
					output = epoch;
				}
				else {	// si la l'époch correspond pas a la premiere date (debut mois?) alors on avance d'un mois?
					if( direction == Direction.forward ) {
						output = shiftFromPeriod( epoch0h00m00sLT, direction, period, periodUnit, periodZoneId, verbose );
					}
					else {
						output = epoch0h00m00sLT;
					}
				}
				break;
			}
			case 'A': {
				if( period > 1 ) { throw new Exception( "setToNearestData : unsupported input parameters" ); }
				
				// Epoch must be start of month ?
				Long epoch0h00m00sLT = adjust( epoch, null, 1, 1, 0, 0, 0, zoneIdUsed, verbose );

				if( epoch0h00m00sLT.equals( epoch ) ) {

					// epoch is already pointing a daily data
					output = epoch;
				}
				else {	// si la l'époch correspond pas a la premiere date (debut mois?) alors on avance d'un mois?
					if( direction == Direction.forward ) {
						output = shiftFromPeriod( epoch0h00m00sLT, direction, period, periodUnit, periodZoneId, verbose );
					}
					else {
						output = epoch0h00m00sLT;
					}
				}
				break;
				
			}
			default : {
				throw new Exception( "setToNearestData : unsupported input parameters" );
			}
		}

		if( verbose == true ) {

			// Tracer.info(String.format( "setToNearestData (%d%c) output (%d) %s [%s]", period, periodUnit, output, convertToString( output, zoneIdUsed, Format.STD_SLASH_FULL ), zoneIdUsed.getId() ));
		}
		return output;
	}

	/**
	 * give seconds to next month on day 01 at 00:00
	 * 
	 * @param epoch
	 * @param zoneId
	 * @return
	 * @throws Exception 
	 */
	public static Long getSecondsToNextMonth( Long epoch, ZoneId zoneId ) throws Exception {

		if( epoch == null ) {
			return null;
		}

		ZonedDateTime zdt = epochToZonedDate( epoch, zoneId );
		ZonedDateTime zdtNextMonth = epochToZonedDate( zdt.plusMonths( 1 ).toEpochSecond(), zoneId );

		Long epochNextMonthAdjusted = adjust( zdtNextMonth.toEpochSecond(), null, null, 1, 0, 0, 0, zoneId, false );

		return epochNextMonthAdjusted - zdt.toEpochSecond();
	}

	/**
	 * give seconds to next day at 00:00
	 * 
	 * @param epoch
	 * @param zoneId
	 * @return
	 * @throws Exception 
	 */
	public static Long getSecondsToNextDay( Long epoch, ZoneId zoneId ) throws Exception {

		if( epoch == null ) {
			return null;
		}

		ZonedDateTime zdt = epochToZonedDate( epoch, zoneId );
		ZonedDateTime zdtNextDay = epochToZonedDate( zdt.plusDays( 1 ).toEpochSecond(), zoneId );

		Long epochNextDayAdjusted = adjust( zdtNextDay.toEpochSecond(), null, null, null, 0, 0, 0, zoneId, false );

		return epochNextDayAdjusted - zdt.toEpochSecond();
	}
}
