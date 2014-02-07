package ucsd.cse110.placeit;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.google.android.gms.maps.model.LatLng;

public class PlaceIt {
	
	////////////////////// Private Variables //////////////////////
	
	private int id;						// db id must be unique
	private	String title;				// REQUIRED: name of the PlaceIt
	private LatLng location;			// REQUIRED: the lat/lng of the location
	private String location_str;		// The string representation of the LatLng
	private String status; 				// posted (active), pulled down (triggered), expired
	private String description;			// aditional details of our PlaceIt
	private Date scheduled_date;		// the day to schedule
	
	////////////////////// constructors //////////////////////
	
	// empty constructor
	public PlaceIt() {}
	
	// for map constructor
	public PlaceIt(LatLng location) {this.location = location;}
	
	// Minimal constructor
	public PlaceIt(String title, String status, LatLng location, String location_str) {
		
		this.title = title;
		this.status = status;
		this.location = location;
		this.location_str = location_str;
	}
	
	// Minimal + description if exist constructor
	public PlaceIt(String title, String status, String description, 
					LatLng location, String location_str, String scheduled_date) {
		
		this.title = title;
		this.status = status;
		this.description = description;
		this.location = location;
		this.location_str = location_str;
		this.scheduled_date = stringToDate(scheduled_date);
	}
	
	// for Database constructor
	public PlaceIt(int id, String title, String status, String description, 
				   LatLng location, String location_str, String scheduled_date) {
		
		this.id = id;
		this.title = title;
		this.status = status;
		this.description  = description;
		this.location = location;
		this.location_str = location_str;
		this.scheduled_date = stringToDate(scheduled_date);
		
	}
	
	////////////////////// getters //////////////////////
	
	public int getId() {
		return id;
	}
	
	public String getTitle() {
		return title;
	}
	
	public LatLng getLocation() {
		return location;
	}
	
	public String getLocation_str() {
		return location_str;
	}
	
	public String getStatus() {
		return status;
	}
	
	public String getDescription() {
		if (description == null)
			return "";
		else
			return description;
	}
	
	 //Returns a String for the sake of our db but will possibly 
	 //need to be changed later on
	public String getScheduled_date() {
		if (scheduled_date != null ) {
			return scheduled_date.toString();
		}else return "";
	}
	
	////////////////////// setters //////////////////////
	
	public void setId(int id) {
		this.id = id;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public void setLocation(LatLng location) {
		this.location = location;
	}
	
	public void setLocation_str(String location_str) {
		this.location_str = location_str;
	}
	
	public void setStatus(String status) {
		this.status = status;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	// String parameter to be able to use with SQLite db
	public void setScheduled_date(String scheduled_date_string) {
		
		this.scheduled_date = stringToDate(scheduled_date_string);
	}
	
	////////////////////// Other methods //////////////////////
	
	public String toString() {
		return this.title;
	}
	
	// converts a string to a Date object
	private static Date stringToDate(String date_str) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
		Date date;
		
		try {
			date = formatter.parse(date_str);
		} catch (ParseException e) {
			date = null;
			e.printStackTrace();
		}
		return date;
	}

}
