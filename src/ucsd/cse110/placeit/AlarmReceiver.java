package ucsd.cse110.placeit;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		
	}
	
//	private AlarmManager alarmMgr;
//	private PendingIntent alarmIntent;
//	
//	alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
//	Intent intent = new Intent(context, AlarmReceiver.class);
//	alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
//
//	// Set the alarm to start at 8:30 a.m.
//	Calendar calendar = Calendar.getInstance();
//	@Override
//	public void onReceive(Context arg0, Intent arg1) {
//		// TODO Auto-generated method stub
//		
//	}
//	calendar.setTimeInMillis(System.currentTimeMillis());
//	calendar.set(Calendar.HOUR_OF_DAY, 8);
//	calendar.set(Calendar.MINUTE, 30);
//
//	// setRepeating() lets you specify a precise custom interval--in this case,
//	// 20 minutes.
//	alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
//	        1000 * 60 * 20, alarmIntent);
}