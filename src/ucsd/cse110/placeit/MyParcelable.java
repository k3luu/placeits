package ucsd.cse110.placeit;

import com.google.android.gms.maps.model.LatLng;

import android.os.Parcel;
import android.os.Parcelable;

public class MyParcelable implements Parcelable {

	// Private Variables
	private int id;						// database id must be unique
	private String title;				// REQUIRED: name of the PlaceIt
	private String status; 				// posted (active), pulled down (triggered), expired
	private String description;			// REQUIRED: additional details of our PlaceIt
	private LatLng location;			// REQUIRED: the lat/lng of the location
	
	// empty constructor
		public MyParcelable() {}
		
	// for map constructor
		public MyParcelable(LatLng location) {this.location = location;}
		
	// Constructor to re-constructing
		public MyParcelable(Parcel in) {
			readFromParcel (in);
		}
	// for Database constructor
		public MyParcelable(int id, String title, String status, String description, 
			LatLng location) {
			
			this.id = id;
			this.title = title;
			this.status = status;
			this.description  = description;
			this.location = location;
		}
	
	// standard getter
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
	
	// standard setter
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

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel out, int flags) {
		out.writeInt(id);
		out.writeString(status);
		out.writeString(description);
		out.writeParcelable(location, flags);
	}
	
	private void readFromParcel(Parcel in) {
		id = in.readInt();
		status = in.readString();
		description = in.readString();
		location = in.readParcelable(LatLng.class.getClassLoader());
	}

	public static final Parcelable.Creator<MyParcelable> CREATOR
    		= new Parcelable.Creator<MyParcelable>() {
		public MyParcelable createFromParcel(Parcel in) {
			return new MyParcelable(in);
		}

		public MyParcelable[] newArray(int size) {
			return new MyParcelable[size];
		}
	};
}
