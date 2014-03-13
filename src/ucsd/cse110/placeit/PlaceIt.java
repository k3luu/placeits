package ucsd.cse110.placeit;

import com.google.android.gms.maps.model.LatLng;

/*
 * Defines the PlaceIt object using a LatLng object for the location and 
 * Scheduler object for it's schedule
 */
public class PlaceIt {
	
	////////////////////// Private Variables //////////////////////
	
	private int id;						// db id must be unique *
	private	String title;				// REQUIRED: name of the PlaceIt*
	private LatLng location;			// REQUIRED: the lat/lng of the location
	private String location_str;		// The string representation of the LatLng
	private String status; 				// posted (active), pulled down (triggered), expired *
	private String description;			// Additional details of our PlaceIt *
	private Scheduler schedule;			// the PlaceIt schedule if any *
	private String username;			// the username *
	private String[] categories;		

	////////////////////// constructors //////////////////////
	
	// empty constructor
	public PlaceIt() {this.username = PlaceItUtil.USERNAME;}
	
	// for map constructor
	public PlaceIt(LatLng location) {
		this.location = location;
		this.username = PlaceItUtil.USERNAME;
	}
	
	// Minimal constructor
//	public PlaceIt(String title, String status, LatLng location, String location_str) {
//		
//		this.title = title;
//		this.status = status;
//		this.location = location;
//		this.location_str = location_str;
//		this.username = PlaceItUtil.USERNAME;
//	}
	
	// Minimal + description if exist constructor
	public PlaceIt(String title, 
				   String status, 
				   String description, 
				   LatLng location, 
				   String location_str,
				   Scheduler schedule,
				   String[] categories) {
		
		this.title = title;
		this.status = status;
		this.description = description;
		this.location = location;
		this.location_str = location_str;
		this.schedule = schedule;
		this.categories = categories;
		this.username = PlaceItUtil.USERNAME;
	}
	
	
	
	// for Database constructor
	public PlaceIt(int id, 
				   String title, 
				   String status, 
				   String description, 
				   LatLng location, 
				   String location_str,
				   Scheduler schedule,
				   String[] categories) {
		
		this.id = id;
		this.title = title;
		this.status = status;
		this.description  = description;
		this.location = location;
		this.location_str = location_str;
		this.schedule = schedule;
		this.username = PlaceItUtil.USERNAME;
		this.categories = categories;
		
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
	
	public String getShortDescription() {
		if (description == null)
			return "";
		
		String desc = description;
		if (desc.length() > PlaceItUtil.MAX_DESC) {
			desc = desc.substring(0, PlaceItUtil.MAX_DESC) + "...";
		}
		
		return desc;
	}
	
	public Scheduler getSchedule() {
		return schedule;
	}
	
	public String getUsername() {
		return username;
	}
	
	public String[] getCategories() {
		return categories;
	}
	
	////////////////////// setters //////////////////////
	
	public void setId(long id) {
		this.id = (int) id;
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
	
	public void setUsername(String name) {
		this.username = name;
	}
	
	public void setCategories(String[] categories) {
		this.categories = categories;
	}
	////////////////////// Other methods //////////////////////
	
	public String toString() {
		return "Description:  "
	    		+ description+"\n\n"
	    		+ "Address:  "
	    		+ location_str+"\n\n"
	    		+ "Schedule:\n"
	    		+ schedule.toString();
	}
	
	public boolean comparePlaceIt(PlaceIt source) {
		if (this.getTitle().equals(source.getTitle())
				&& this.getStatus().equals(source.getStatus())
				&& this.getDescription().equals(source.getDescription())
				&& this.getLocation().latitude == source.getLocation().latitude
				&& this.getLocation().longitude == source.getLocation().longitude
				&& this.getLocation_str().equals(source.getLocation_str())
				&& this.getSchedule().getScheduled_option().equals(source.getSchedule().getScheduled_option())
				&& this.getSchedule().getScheduled_dow().equals(source.getSchedule().getScheduled_dow())
				&& this.getSchedule().getScheduled_week().equals(source.getSchedule().getScheduled_week())
				&& this.getSchedule().getScheduled_minutes() == source.getSchedule().getScheduled_minutes()
				) {
			return true;
		}
		else {
			return false;
		}		
	}
}
