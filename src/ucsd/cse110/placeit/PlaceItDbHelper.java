package ucsd.cse110.placeit;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.android.gms.maps.model.LatLng;


/**
 * @author danielloza
 *
 */
public class PlaceItDbHelper extends SQLiteOpenHelper {
	
	///////////////////////// Static variables //////////////////////////
    
	// Database Version
    private static final int DATABASE_VERSION = 1;
 
    // Database Name
    private static final String DATABASE_NAME = "PlaceItsManager";
 
    // PlaceIts table name
    private static final String TABLE_PLACEITS = "placeIts";
 
    // PlaceIts Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_TITLE = "title";
    private static final String KEY_STATUS = "status";
    private static final String KEY_DESC = "description";
    private static final String KEY_LAT = "latitude";
    private static final String KEY_LNG = "long";
    private static final String KEY_EXP = "expiration";
    private static final String KEY_SCHED_DATE = "scheduled_date";
 

    ///////////////////////// Required Methods ////////////////////////////
    
	public PlaceItDbHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	// Creates the PlaceIt database schema
	public void onCreate(SQLiteDatabase db) {
		String CREATE_PLACEITS_TABLE = "CREATE TABLE " + 
										TABLE_PLACEITS + 
										"(" +
										    KEY_ID + " INTEGER PRIMARY KEY," +
										    KEY_TITLE + " TEXT," +
										    KEY_STATUS + " TEXT," +
										    KEY_DESC + " TEXT," + 
										    KEY_LAT + " REAL," + 
										    KEY_LNG + " REAL," + 
										    KEY_EXP + " TEXT," +
										    KEY_SCHED_DATE + " TEXT" +
									    ")";
        db.execSQL(CREATE_PLACEITS_TABLE);
		
	}

	// defines what should be done on upgrade of SQlite version
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PLACEITS);
 
        // Create tables again
        onCreate(db);
    }
	
	///////////////////////////// CRUD Operations ///////////////////////////
	
	// Adding new PlaceIt
	public void addPlaceIt(PlaceIt placeIt) {
		
		// get a writable instance of our database 
		SQLiteDatabase db = this.getWritableDatabase();
		 
		// insert all the firelds of the PlaceIt into the db
	    ContentValues values = new ContentValues();
	    values.put(KEY_TITLE, placeIt.getTitle()); 					// PlaceIt Title
	    values.put(KEY_STATUS, placeIt.getStatus()); 				// PlaceIt Status
	    values.put(KEY_DESC, placeIt.getDescription()); 			// PlaceIt Description
	    values.put(KEY_LAT, placeIt.getPlaceItLoc().latitude); 		// PlaceIt Latitude
	    values.put(KEY_LNG, placeIt.getPlaceItLoc().longitude); 	// PlaceIt Longitude
	    values.put(KEY_EXP, placeIt.getExpiration()); 				// PlaceIt Expiration
	    values.put(KEY_SCHED_DATE, placeIt.getScheduled_date()); 	// PlaceIt Scheduled Date
	 
	    // Inserting Row
	    db.insert(TABLE_PLACEITS, null, values);
	    db.close(); // Closing database connection
	}
	
	// Getting single PlaceIt
	public PlaceIt getPlaceIt(int id) throws NumberFormatException, ParseException {

		// get a readable instance of our database 
		SQLiteDatabase db = this.getReadableDatabase();
		
		// Date formatter for the expiration and schedule_date
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
		 
		// Cursor to go through the table
	    Cursor cursor = db.query(TABLE_PLACEITS, new String[] { KEY_ID,
	    		KEY_TITLE, KEY_STATUS, KEY_DESC, KEY_LAT, KEY_LNG, KEY_EXP, KEY_SCHED_DATE }, KEY_ID + "=?",
	            new String[] { String.valueOf(id) }, null, null, null, null);
	    if (cursor != null)
	        cursor.moveToFirst();
	 
	    // create the PlaceIt that'll be returned
	    PlaceIt placeIt = new PlaceIt(Integer.parseInt(cursor.getString(0)),
	            cursor.getString(1), cursor.getString(2), cursor.getString(3),
	            new LatLng(cursor.getDouble(4),cursor.getDouble(5)), 
	            formatter.parse(cursor.getString(6)), formatter.parse(cursor.getString(7)));
	    
	    // return placeIt
	    return placeIt;
	}
	 
	// Getting PlaceIts based on status
	public List<PlaceIt> getAllPlaceIts(String placeIt_status) {
		List<PlaceIt> placeItList = new ArrayList<PlaceIt>();
	    // Select All Query
	    String selectQuery = "SELECT  * " +
	    					 "FROM " + TABLE_PLACEITS + " " +
	    					 "WHERE status = " + placeIt_status;
	 
	    // get a writable instance of our database 
	    SQLiteDatabase db = this.getWritableDatabase();
	    Cursor cursor = db.rawQuery(selectQuery, null);
	 
	    // looping through all rows and adding to list
	    if (cursor.moveToFirst()) {
	        do {
	        	PlaceIt placeIt = new PlaceIt();
	            placeIt.setTitle(cursor.getString(0));
	            placeIt.setStatus(cursor.getString(1));
	            placeIt.setDescription(cursor.getString(2));
	            placeIt.setPlaceItLocation(new LatLng(cursor.getDouble(3),cursor.getDouble(4)));
	            placeIt.setExpiration(cursor.getString(5));
	            placeIt.setScheduled_date(cursor.getString(6));
	            
	            // Adding placeIt to list
	            placeItList.add(placeIt);
	        } while (cursor.moveToNext());
	    }
	 
	    // return placeIt list
	    return placeItList;
	}
	 
	// Getting PlaceIts Count
	public int getPlaceItsCount() {
		String countQuery = "SELECT  * FROM " + TABLE_PLACEITS;
		
		// get a readable instance of our database 
        SQLiteDatabase db = this.getReadableDatabase();
        
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();
 
        // return count
        return cursor.getCount();
	}
	
	// Updating single PlaceIt
	public int updatePlaceIt(PlaceIt placeIt) {
		
		// get a writable instance of our database 
		SQLiteDatabase db = this.getWritableDatabase();
		 
	    ContentValues values = new ContentValues();
	    values.put(KEY_TITLE, placeIt.getTitle()); 					// PlaceIt Title
	    values.put(KEY_STATUS, placeIt.getStatus()); 				// PlaceIt Status
	    values.put(KEY_DESC, placeIt.getDescription()); 			// PlaceIt Description
	    values.put(KEY_LAT, placeIt.getPlaceItLoc().latitude); 		// PlaceIt Latitude
	    values.put(KEY_LNG, placeIt.getPlaceItLoc().longitude); 	// PlaceIt Longitude
	    values.put(KEY_EXP, placeIt.getExpiration()); 				// PlaceIt Expiration
	    values.put(KEY_SCHED_DATE, placeIt.getScheduled_date()); 	// PlaceIt Scheduled Date
	 
	 
	    // updating row
	    return db.update(TABLE_PLACEITS, values, KEY_ID + " = ?",
	            new String[] { String.valueOf(placeIt.getId()) });
	    
	}
	 
	// Deleting single PlaceIt
	public void deletePlaceIt(PlaceIt placeIt) {
		
		 SQLiteDatabase db = this.getWritableDatabase();
		    db.delete(TABLE_PLACEITS, KEY_ID + " = ?",
		            new String[] { String.valueOf(placeIt.getId()) });
		    db.close();
	}
}