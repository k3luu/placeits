package ucsd.cse110.placeit;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/*
 * Handles the scheduling of a PlaceIt
 * scheduled_date_millis holds:
 *  (i) the milliseconds until the next scheduled event
 *  (ii) -1 indicating the PlaceIt has no set schedule
 * 
 */
public class Scheduler {
	
	private Calendar scheduleDate = null;
	
	private String scheduled_option;	// the day to schedule e.g. Monday
	private String scheduled_dow;		// the day to schedule e.g. Monday
	private String scheduled_week_interval;// week repeat interval e.g Every week
	private int scheduled_minutes;		// minutes to repost e.g 5 minutes
	
	// for user input
	public Scheduler(String scheduled_option, String scheduled_dow, 
			String scheduled_week_interval, int scheduled_minutes) {
		
		this.scheduled_option = scheduled_option;
		
		if (scheduled_option.equals(PlaceItUtil.WEEKLY_SCHEDULE)) {
			this.scheduled_dow = scheduled_dow;
			this.scheduled_week_interval = scheduled_week_interval;
			this.scheduled_minutes = PlaceItUtil.NOTSET;
		}
		else if (scheduled_option.equals(PlaceItUtil.MINUTE_SCHEDULE)) {
			this.scheduled_dow = null;
			this.scheduled_week_interval = null;
			this.scheduled_minutes = scheduled_minutes;
		}
		else {
			this.scheduled_dow = null;
			this.scheduled_week_interval = null;
			this.scheduled_minutes = PlaceItUtil.NOTSET;
		}
		
	}
	
	// setUp the alarm for this schedule
	public void setRepeatingAlarm(Context context, long placeIt_Id) {
		
		scheduleDate = Calendar.getInstance(); // get the current time
		
		Intent reactivatePlaceIt = new Intent(context, AlarmReceiver.class);
		reactivatePlaceIt.putExtra(PlaceItUtil.PLACEIT_ID, placeIt_Id);
	    PendingIntent recurringActivation = PendingIntent.getBroadcast(context,
	    		(int)placeIt_Id, reactivatePlaceIt, PendingIntent.FLAG_CANCEL_CURRENT);
	    AlarmManager alarms = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		
		// if we are scheduling by weeks
		if (scheduled_option.equals(PlaceItUtil.WEEKLY_SCHEDULE)) {
			
			// convert the user input into usable integers for Calendar and Alarm
			int dowNum = checkDay(scheduled_dow);
			int weekIntervalNum = checkWeek(scheduled_week_interval);
			
			// set the repost date to the given dow with given interval
			if ( dowNum != PlaceItUtil.NOTSET && weekIntervalNum != PlaceItUtil.NOTSET) {
				
				// set the calendar object to the day specifed
				scheduleDate.set(Calendar.DAY_OF_WEEK, dowNum);
			
				// set an alarm to the day repeating with the given interval
				alarms.setInexactRepeating(AlarmManager.RTC_WAKEUP,
										   scheduleDate.getTimeInMillis(),
										   weekIntervalNum*PlaceItUtil.INTERVAL_WEEK, 
										   recurringActivation);
				SimpleDateFormat sdf = new SimpleDateFormat("E yyyy MMM dd HH:mm:ss");
				Log.i("WeeklyAlarm set for", sdf.format(scheduleDate.getTime()) );
			}
		}
		// if we are scheduling by the minute
		else if (scheduled_option.equals(PlaceItUtil.MINUTE_SCHEDULE)) {
			if (scheduled_minutes >= 0)  {
				scheduleDate.add(Calendar.MINUTE, scheduled_minutes);
				alarms.setInexactRepeating(AlarmManager.RTC_WAKEUP,
						scheduleDate.getTimeInMillis(),
						scheduled_minutes*PlaceItUtil.INTERVAL_MINUTE, recurringActivation);
				SimpleDateFormat sdf = new SimpleDateFormat("E yyyy MMM dd HH:mm:ss");
				Log.i("MinuteAlarm set for", sdf.format(scheduleDate.getTime()) );
			}
		}
	}
	
    public void removeAlarm(Context context, long l) {

		Intent reactivatePlaceIt = new Intent(context, AlarmReceiver.class);
		reactivatePlaceIt.putExtra(PlaceItUtil.PLACEIT_ID, l);
	    PendingIntent recurringActivation = PendingIntent.getBroadcast(context,
	    		(int) l, reactivatePlaceIt, PendingIntent.FLAG_CANCEL_CURRENT);
	    AlarmManager alarms = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
	    
	    if (alarms!= null) {
	    	Log.i("REMOVED", "ALARM");
	    	alarms.cancel(recurringActivation);
	    }  
    }
	
	////////////////////// getters //////////////////////
	
	public String getScheduled_option() {
		return scheduled_option;
	}
	
	public String getScheduled_week() {
		return scheduled_week_interval;
	}
	
	public String getScheduled_dow() {
		return scheduled_dow;
	}
	
	public int getScheduled_minutes() {
		return scheduled_minutes;
	}
	
	////////////////////// setters //////////////////////
	
	public void setScheduled_dow(String scheduled_dow_string) {
		this.scheduled_dow = scheduled_dow_string;
	}
	
	public void setScheduled_week(String scheduled_week_interval) {
		this.scheduled_week_interval = scheduled_week_interval;
	}
	
	public void setScheduled_minutes(int scheduled_minutes) {
		this.scheduled_minutes = scheduled_minutes;
	}
	
	
	public void setScheduled_option(String scheduled_option) {
		this.scheduled_option = scheduled_option;
	}
	
	////////////////////// Helper functions //////////////////////
	
	// returns the integer version of the given day
	private static int checkDay(String day)
    {
        Map<String,Integer> dayMap=new HashMap<String,Integer>();
        
        dayMap.put("Sunday",Calendar.SUNDAY);
        dayMap.put("Monday",Calendar.MONDAY);
        dayMap.put("Tuesday",Calendar.TUESDAY);
        dayMap.put("Wednesday",Calendar.WEDNESDAY);
        dayMap.put("Thrusday",Calendar.THURSDAY);
        dayMap.put("Friday", Calendar.FRIDAY);
        dayMap.put("Saturday",Calendar.SATURDAY);
        
        if (!dayMap.containsKey(day)) {
        	return PlaceItUtil.NOTSET;
        }
        
        return dayMap.get(day).intValue();
    }
	
	// returns the interval of weeks
	private static int checkWeek(String week)
    {
        Map<String,Integer> weekMap=new HashMap<String,Integer>();
        
        weekMap.put("week",1);
        weekMap.put("two weeks",2);
        weekMap.put("three weeks",3);
        weekMap.put("four weeks",4);
        
        if (!weekMap.containsKey(week)) {
        	return PlaceItUtil.NOTSET;
        }
        
        return weekMap.get(week).intValue();
    }
	
	// prints the value of the scheuduled date
	public String toString() {
		if (scheduleDate != null) {
			SimpleDateFormat sdf = new SimpleDateFormat("E yyyy MMM dd HH:mm:ss", Locale.US);
			return sdf.format(scheduleDate.getTime());
		}
		else {
			return "";
		}		
	}
}
