package ucsd.cse110.placeit;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.google.android.gms.maps.model.LatLng;

public class PlaceIt {
	
	////////////////////// Private Variables //////////////////////
	
	private int id;						// db id must be unique
	public String title;				// REQUIRED: name of the PlaceIt
	private LatLng location;			// REQUIRED: the lat/lng of the location
	private String location_str;		// The string representation of the LatLng
	private String status; 				// posted (active), pulled down (triggered), expired
	private String description;			// aditional details of our PlaceIt
	//private Date expiration;			// the day the PlaceIt expires
	private Date scheduled_date;		// the day to schedule
	
	////////////////////// constructors //////////////////////
	
	// empty constructor
	public PlaceIt() {}
	
	// for map constructor
	public PlaceIt(LatLng location) {this.location = location;}
	
	// Minimal constructor
	public PlaceIt(String title, String status, LatLng location) {
		
		this.title = title;
		this.status = status;
		this.location = location;
	}
	
	// for Database constructor
	public PlaceIt(int id, String title, String status, String description, 
				   LatLng location, Date scheduled_date) {
		
		this.id = id;
		this.title = title;
		this.status = status;
		this.description  = description;
		this.location = location;
		//this.expiration = expiration;
		this.scheduled_date = scheduled_date;
		
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
		return description;
	}
	
	 //Returns a String for the sake of our db but will possibly 
	 //need to be changed later on
	public String getScheduled_date() {
		return scheduled_date.toString();
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
		
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
		Date scheduled_date;
		
		try {
			scheduled_date = formatter.parse(scheduled_date_string);
		} catch (ParseException e) {
			scheduled_date = null;
			e.printStackTrace();
		}
		
		this.scheduled_date = scheduled_date;
	}
	
	////////////////////// Other methods //////////////////////
	
	public String toString() {
		return this.title;
	}

}
