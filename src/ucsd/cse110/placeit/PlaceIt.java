package ucsd.cse110.placeit;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.google.android.gms.maps.model.LatLng;

public class PlaceIt {
	
	////////////////////// Private Variables //////////////////////
	
	private int id;					// db id must be unique
	private String title;			// name of the PlaceIt
	private String status; 			// posted (active), pulled down (triggered), expired
	private String description;		// limited to 1000 characters
	private LatLng location;		// the lat/lng of the location
	
	// NOTE: we might want to separate into another class
	private Date expiration;		// the day the PlaceIt expires
	private Date scheduled_date;	// the day to schedule
	
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
	
	public LatLng getPlaceItLoc() {
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
	
	public void setPlaceItLocation(LatLng location) {
		this.location = location;
	}
	
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
