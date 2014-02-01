package ucsd.cse110.placeit;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.google.android.gms.maps.model.LatLng;

public class PlaceIt {
	
	////////////////////// Private Variables //////////////////////
	
	private int id;						// db id must be unique
	private String title;				// REQUIRED: name of the PlaceIt
	private String status; 				// posted (active), pulled down (triggered), expired
	private String description;			// REQUIRED: aditional details of our PlaceIt
	private LatLng location;			// REQUIRED: the lat/lng of the location
	private Date expiration;			// the day the PlaceIt expires
	private Date scheduled_date;		// the day to schedule
	
	////////////////////// constructors //////////////////////
	
	// empty constructor
	public PlaceIt() {}
	
	// constructor
	public PlaceIt(int id, String title, String status, String description, 
				   LatLng location, Date expiration, Date scheduled_date) {
		
		this.id = id;
		this.title = title;
		this.status = status;
		this.description  = description;
		this.location = location;
		this.expiration = expiration;
		this.scheduled_date = scheduled_date;
		
	}
	
	////////////////////// getters //////////////////////
	
	public int getId() {
		return id;
	}
	
	public String getTitle() {
		return title;
	}
	
	public String getStatus() {
		return status;
	}
	
	public String getDescription() {
		return description;
	}
	
	public LatLng getLocation() {
		return location;
	}
	
	// Returns a String for the sake of our db but will possibly 
	// need to be changed later on
	public String getExpiration() {
		return expiration.toString();
	}
	
	// Returns a String for the sake of our db but will possibly 
	// need to be changed later on
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
	
	public void setStatus(String status) {
		this.status = status;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public void setLocation(LatLng location) {
		this.location = location;
	}
	
	// String parameter to be able to use with SQLite db
	public void setExpiration(String expiration) {
		
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
		Date expirationDate;
		
		try {
			expirationDate = formatter.parse(expiration);
		} catch (ParseException e) {
			expirationDate = null;
			e.printStackTrace();
		}
		
		this.expiration = expirationDate;
	}
	
	// String parameter to be able to use with SQLite db
	public void setScheduled_date(String scheduled_date_string) {
		
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
		Date scheduled_date;
		
		try {
			scheduled_date = formatter.parse(scheduled_date_string);
		} catch (ParseException e) {
			scheduled_date = null;
			e.printStackTrace();
		}
		
		this.scheduled_date = scheduled_date;
	}

}
