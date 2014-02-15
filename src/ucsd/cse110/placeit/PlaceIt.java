package ucsd.cse110.placeit;

import com.google.android.gms.maps.model.LatLng;

public class PlaceIt {
	
	////////////////////// Private Variables //////////////////////
	
	private int id;						// db id must be unique
	private	String title;				// REQUIRED: name of the PlaceIt
	private LatLng location;			// REQUIRED: the lat/lng of the location
	private String location_str;		// The string representation of the LatLng
	private String status; 				// posted (active), pulled down (triggered), expired
	private String description;			// aditional details of our PlaceIt
	private Scheduler schedule;			// the PlaceIt schedule if any
	
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
	public PlaceIt(String title, 
				   String status, 
				   String description, 
				   LatLng location, 
				   String location_str,
				   Scheduler schedule) {
		
		this.title = title;
		this.status = status;
		this.description = description;
		this.location = location;
		this.location_str = location_str;
		this.schedule = schedule;

	}
	
	// for Database constructor
	public PlaceIt(int id, 
				   String title, 
				   String status, 
				   String description, 
				   LatLng location, 
				   String location_str,
				   Scheduler schedule) {
		
		this.id = id;
		this.title = title;
		this.status = status;
		this.description  = description;
		this.location = location;
		this.location_str = location_str;
		this.schedule = schedule;
		
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
	
	public Scheduler getSchedule() {
		return schedule;
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
	
	public void setSchedule(Scheduler schedule) {
		this.schedule = schedule;
	}
	
	////////////////////// Other methods //////////////////////
	
	public String toString() {
		return "ID: " + this.id + "\n" +
			   "Title: " + this.title + "\n" +
			   "Status: " + this.status + "\n" +
			   "Description: " + this.description + "\n" +
			   "LatLng: " + this.location.toString() + "\n" +
			   "Location: " + this.location_str + "\n" +
			   "Scheduling_Option: " + this.schedule.getScheduled_option() + "\n" +
			   "Scheduling_DOW: " + this.schedule.getScheduled_dow() + "\n" +
			   "Scheduling_WeekInterval: " + this.schedule.getScheduled_week() + "\n" +
			   "Scheduling_Minutes: " + this.schedule.getScheduled_minutes() + "\n";
				//this.title;
	}
	
	
}
