package ucsd.cse110.placeit;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;


/**
 * The database that stores all the Placeit information
 *
 */
public class PlaceItDbHelper extends SQLiteOpenHelper{
	
	///////////////////////// Static variables //////////////////////////
    
	// Database Version
    private static final int DATABASE_VERSION = 15;
 
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
    private static final String KEY_LNG = "longitude";
    private static final String KEY_LOC = "location_str";
    private static final String KEY_SCHED_OPTION = "scheduled_option";
    private static final String KEY_SCHED_DOW = "scheduled_dow";
    private static final String KEY_SCHED_WEEK = "scheduled_week_interval";
    private static final String KEY_SCHED_MINUTES = "scheduled_minutes";
    private static final String KEY_USER = "username";
    private static final String KEY_CAT1 = "category1";
    private static final String KEY_CAT2 = "category2";
    private static final String KEY_CAT3 = "category3";
 

    ///////////////////////// Required Methods ////////////////////////////
    
	public PlaceItDbHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	

	// Creates the PlaceIt database schema
	public void onCreate(SQLiteDatabase db) {
		String CREATE_PLACEITS_TABLE = "CREATE TABLE " + 
										TABLE_PLACEITS + 
										"(" +
										    KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
										    KEY_TITLE + " TEXT," +
										    KEY_STATUS + " TEXT," +
										    KEY_DESC + " TEXT," + 
										    KEY_LAT + " REAL," + 
										    KEY_LNG + " REAL," + 
										    KEY_LOC + " TEXT," +
										    KEY_SCHED_OPTION + " TEXT," +
										    KEY_SCHED_DOW + " TEXT," +
										    KEY_SCHED_WEEK + " TEXT," +
										    KEY_SCHED_MINUTES + " INTEGER," +
										    KEY_USER + " TEXT," +
										    KEY_CAT1 + " TEXT," +
										    KEY_CAT2 + " TEXT," +
										    KEY_CAT3 + " TEXT" +
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
	public long addPlaceIt(PlaceIt placeIt) {

		// get a writable instance of our database 
		SQLiteDatabase db = this.getWritableDatabase();
		 
	    // Inserting Row
	    long placeItId = db.insert(TABLE_PLACEITS, null, fillContentValues(placeIt));
	    db.close(); // Closing database connection
	    return placeItId;

	}
	
	// Getting single PlaceIt by its ID
	public PlaceIt getPlaceIt(long placeIt_id) {

		// get a readable instance of our database 
		SQLiteDatabase db = this.getReadableDatabase();
				 
		// Cursor to go through the table
	    Cursor cursor = db.query(TABLE_PLACEITS, 
	    						 new String[] { KEY_ID, 			// 0
	    									   	KEY_TITLE, 			// 1
	    									   	KEY_STATUS,			// 2
	    									   	KEY_DESC, 			// 3
	    									   	KEY_LAT, 			// 4
	    									   	KEY_LNG, 			// 5
	    									   	KEY_LOC, 			// 6
	    									   	KEY_SCHED_OPTION, 	// 7
	    									   	KEY_SCHED_DOW,		// 8
	    									   	KEY_SCHED_WEEK, 	// 9
	    									   	KEY_SCHED_MINUTES,  // 10
	    									   	KEY_USER,			// 11
	    									   	KEY_CAT1,			// 12
	    									   	KEY_CAT2,			// 13
	    									   	KEY_CAT3,			// 14
	    									   }, KEY_ID + "=?",
	    						 new String[] { String.valueOf(placeIt_id) }, 
	    						 null, null, null, null);
	    if (cursor != null && cursor.getCount() > 0) {
	        cursor.moveToFirst();
	 
		    // create the PlaceIt that'll be returned
		    PlaceIt placeIt = new PlaceIt(cursor.getInt(0),					// id
		            					  cursor.getString(1), 				// title
		            					  cursor.getString(2), 				// status
		            					  cursor.getString(3),				// description
		            					  new LatLng(cursor.getDouble(4),	// lat
		            							  	 cursor.getDouble(5)), 	// lng
		            					  cursor.getString(6),				// location_str
		            					  new Scheduler(cursor.getString(7),// scheduled_option							
		            							  		cursor.getString(8),// scheduled_dow
		            							  		cursor.getString(9),// scheduled_week
		            							  		cursor.getInt(10)	// scheduled_minutes
		            							  		),
		            					  new String[]{cursor.getString(12),			// category 1
		            					   			   cursor.getString(13),			// category 2	
		            					   			   cursor.getString(14)}			// category 3
		    					);
		    return placeIt;
	    }
	    
	    // if cursor was null
	    return null;
	    
	}
	
	
	// Getting all placeit for synchronization
		public ArrayList<PlaceIt> getAllPlaceIts() {
			ArrayList<PlaceIt> placeItList = new ArrayList<PlaceIt>();
		 
			 // Select All Query
		    String selectQuery = "SELECT  * " +
		    					 "FROM " + TABLE_PLACEITS + " " +
		    					 "WHERE "+ KEY_TITLE + " != \"" + null +"\"";
		 
		    // get a writable instance of our database 
		    SQLiteDatabase db = this.getWritableDatabase();
		    Cursor cursor = db.rawQuery(selectQuery, null);
		 
		    // looping through all rows and adding to list
		    if (cursor.moveToFirst()) {
		        do {
		        	PlaceIt placeIt = new PlaceIt();
		        	placeIt.setId(cursor.getInt(0));
		            placeIt.setTitle(cursor.getString(1));
		            placeIt.setStatus(cursor.getString(2));
		            placeIt.setDescription(cursor.getString(3));
		            placeIt.setLocation(new LatLng(cursor.getDouble(4),
		            							   cursor.getDouble(5)));
		            placeIt.setLocation_str(cursor.getString(6));
		            placeIt.setSchedule( new Scheduler(cursor.getString(7), 
		            								   cursor.getString(8),
		            								   cursor.getString(9),
		            								   cursor.getInt(10)));
		            placeIt.setCategories(new String[]{cursor.getString(12),
								            		   cursor.getString(13),
								            		   cursor.getString(14)});
		            // Adding placeIt to list
		            placeItList.add(placeIt);
		        } while (cursor.moveToNext());
		    }
		 
		    // return placeIt list
		    return placeItList;
		}
	
	
	// Getting placeit by username and status
	public ArrayList<PlaceIt> getAllPlaceItsByUsernameAndStatus(String username, String status) {
		ArrayList<PlaceIt> placeItList = new ArrayList<PlaceIt>();
	 
		 // Select All Query
	    String selectQuery = "SELECT  * " +
	    					 "FROM " + TABLE_PLACEITS + " " +
	    					 "WHERE "+ KEY_USER + " = \"" + username +"\"" +
	    					 " AND "  + KEY_STATUS + "= \"" + status +"\"";
	 
	    // get a writable instance of our database 
	    SQLiteDatabase db = this.getWritableDatabase();
	    Cursor cursor = db.rawQuery(selectQuery, null);
	 
	    // looping through all rows and adding to list
	    if (cursor.moveToFirst()) {
	        do {
	        	PlaceIt placeIt = new PlaceIt();
	        	placeIt.setId(cursor.getInt(0));
	            placeIt.setTitle(cursor.getString(1));
	            placeIt.setStatus(cursor.getString(2));
	            placeIt.setDescription(cursor.getString(3));
	            placeIt.setLocation(new LatLng(cursor.getDouble(4),
	            							   cursor.getDouble(5)));
	            placeIt.setLocation_str(cursor.getString(6));
	            placeIt.setSchedule( new Scheduler(cursor.getString(7), 
	            								   cursor.getString(8),
	            								   cursor.getString(9),
	            								   cursor.getInt(10)));
	            placeIt.setCategories(new String[]{cursor.getString(12),
							            		   cursor.getString(13),
							            		   cursor.getString(14)});
	            // Adding placeIt to list
	            placeItList.add(placeIt);
	        } while (cursor.moveToNext());
	    }
	 
	    // return placeIt list
	    return placeItList;
	}
	
	
	// Getting PlaceIts by username
	public ArrayList<PlaceIt> getAllPlaceItsByUsername(String username) {
		ArrayList<PlaceIt> placeItList = new ArrayList<PlaceIt>();
	 
		 // Select All Query
	    String selectQuery = "SELECT  * " +
	    					 "FROM " + TABLE_PLACEITS + " " +
	    					 "WHERE "+ KEY_USER + " = \"" + username +"\"";
	 
	    // get a writable instance of our database 
	    SQLiteDatabase db = this.getWritableDatabase();
	    Cursor cursor = db.rawQuery(selectQuery, null);
	 
	    // looping through all rows and adding to list
	    if (cursor.moveToFirst()) {
	        do {
	        	PlaceIt placeIt = new PlaceIt();
	        	placeIt.setId(cursor.getInt(0));
	            placeIt.setTitle(cursor.getString(1));
	            placeIt.setStatus(cursor.getString(2));
	            placeIt.setDescription(cursor.getString(3));
	            placeIt.setLocation(new LatLng(cursor.getDouble(4),
	            							   cursor.getDouble(5)));
	            placeIt.setLocation_str(cursor.getString(6));
	            placeIt.setSchedule( new Scheduler(cursor.getString(7), 
	            								   cursor.getString(8),
	            								   cursor.getString(9),
	            								   cursor.getInt(10)));
	            placeIt.setCategories(new String[]{cursor.getString(12),
							            		   cursor.getString(13),
							            		   cursor.getString(14)});
	            // Adding placeIt to list
	            placeItList.add(placeIt);
	        } while (cursor.moveToNext());
	    }
	 
	    // return placeIt list
	    return placeItList;
	}
	
	
	// Getting PlaceIts based on status
	public ArrayList<PlaceIt> getAllPlaceIts(String placeIt_status) {
		ArrayList<PlaceIt> placeItList = new ArrayList<PlaceIt>();
	    // Select All Query
	    String selectQuery = "SELECT  * " +
	    					 "FROM " + TABLE_PLACEITS + " " +
	    					 "WHERE "+ KEY_STATUS + " = \"" + placeIt_status +"\"";
	 
	    // get a writable instance of our database 
	    SQLiteDatabase db = this.getWritableDatabase();
	    Cursor cursor = db.rawQuery(selectQuery, null);
	 
	    // looping through all rows and adding to list
	    if (cursor.moveToFirst()) {
	        do {
	        	PlaceIt placeIt = new PlaceIt();
	        	placeIt.setId(cursor.getInt(0));
	            placeIt.setTitle(cursor.getString(1));
	            placeIt.setStatus(cursor.getString(2));
	            placeIt.setDescription(cursor.getString(3));
	            placeIt.setLocation(new LatLng(cursor.getDouble(4),
	            							   cursor.getDouble(5)));
	            placeIt.setLocation_str(cursor.getString(6));
	            placeIt.setSchedule( new Scheduler(cursor.getString(7), 
	            								   cursor.getString(8),
	            								   cursor.getString(9),
	            								   cursor.getInt(10)));
	            placeIt.setCategories(new String[]{cursor.getString(12),
							            		   cursor.getString(13),
							            		   cursor.getString(14)});
	            // Adding placeIt to list
	            placeItList.add(placeIt);
	        } while (cursor.moveToNext());
	    }
	 
	    // return placeIt list
	    return placeItList;
	}
	
	// Getting Category PlaceIts 
	public ArrayList<PlaceIt> getCategoryPlaceIts(String username) {
		ArrayList<PlaceIt> placeItList = new ArrayList<PlaceIt>();
	    // Select All Query
	    String selectQuery = "SELECT  * " +
	    					 "FROM " + TABLE_PLACEITS + " " +
	    					 "WHERE "+ KEY_USER + " = \"" + username +"\" " +
	    					 "AND " + KEY_CAT1 + " <> \"\" " + 
	    					 "AND " + KEY_STATUS + " = \"" + PlaceItUtil.ACTIVE +"\"";
	    					 
	 
	    // get a writable instance of our database 
	    SQLiteDatabase db = this.getWritableDatabase();
	    Cursor cursor = db.rawQuery(selectQuery, null);
	 
	    // looping through all rows and adding to list
	    if (cursor.moveToFirst()) {
	        do {
	        	PlaceIt placeIt = new PlaceIt();
	        	placeIt.setId(cursor.getInt(0));
	            placeIt.setTitle(cursor.getString(1));
	            placeIt.setStatus(cursor.getString(2));
	            placeIt.setDescription(cursor.getString(3));
	            placeIt.setLocation(new LatLng(cursor.getDouble(4),
	            							   cursor.getDouble(5)));
	            placeIt.setLocation_str(cursor.getString(6));
	            placeIt.setSchedule( new Scheduler(cursor.getString(7), 
	            								   cursor.getString(8),
	            								   cursor.getString(9),
	            								   cursor.getInt(10)));
	            placeIt.setCategories(new String[]{cursor.getString(12),
							            		   cursor.getString(13),
							            		   cursor.getString(14)});
	            // Adding placeIt to list
	            placeItList.add(placeIt);
	        } while (cursor.moveToNext());
	    }
	 
	    // return placeIt list
	    return placeItList;
	}
	
	// Getting Schedulers based on status
	public ArrayList<Scheduler> getAllSchedules(String status) {
		ArrayList<Scheduler> schedulerList = new ArrayList<Scheduler>();
	    // Select All Query
	    String selectQuery = "SELECT " + KEY_SCHED_OPTION + ", "
									   + KEY_SCHED_DOW + ", " 
									   + KEY_SCHED_WEEK + ", " 
									   + KEY_SCHED_MINUTES + 
							 " FROM "  + TABLE_PLACEITS +
	    					 " WHERE " + KEY_STATUS + " = \"" + status +"\"";
	 
	    // get a writable instance of our database 
	    SQLiteDatabase db = this.getWritableDatabase();
	    Cursor cursor = db.rawQuery(selectQuery, null);
	 
	    // looping through all rows and adding to list
	    if (cursor.moveToFirst()) {
	        do {
	        	Scheduler schedule = new Scheduler(cursor.getString(0),	// Scheduling option
	        									   cursor.getString(1),	// Scheduling DOW
	            								   cursor.getString(2), // Scheduling week interval
	            								   cursor.getInt(3)); 	// Scheduling minutes
	            // Adding placeIt to list
	        	schedulerList.add(schedule);
	        } while (cursor.moveToNext());
	    }
	 
	    // return placeIt list
	    return schedulerList;
	}
	 
	// Getting PlaceIts Count
	public int getPlaceItsCount() {
		String countQuery = "SELECT  * FROM " + TABLE_PLACEITS;
		
		// get a readable instance of our database 
        SQLiteDatabase db = this.getReadableDatabase();
        
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
 
        // return count
        return count;
	}
	
	// Updating single PlaceIt
	public long updatePlaceIt(PlaceIt placeIt) {
		
		// get a writable instance of our database 
		SQLiteDatabase db = this.getWritableDatabase();
		 
	 
	    // updating row
	    return db.update(TABLE_PLACEITS, fillContentValues(placeIt), KEY_ID + " = ?",
	            new String[] { String.valueOf(placeIt.getId()) });
	    
	}
	 
	// Deleting single PlaceIt
	public void deletePlaceIt(PlaceIt placeIt) {
		
		 SQLiteDatabase db = this.getWritableDatabase();
		    db.delete(TABLE_PLACEITS, KEY_ID + " = ?",
		            new String[] { String.valueOf(placeIt.getId()) });
		    db.close();
	}
	
	//////////////////////Helper functions //////////////////////
	
	private ContentValues fillContentValues(PlaceIt placeIt) {
		
		Scheduler schedule = placeIt.getSchedule();
		
		// insert all the fields of the PlaceIt into the db
	    ContentValues values = new ContentValues();
	    values.put(KEY_TITLE, placeIt.getTitle()); 						// Title
	    values.put(KEY_STATUS, placeIt.getStatus()); 					// Status
	    values.put(KEY_DESC, placeIt.getDescription()); 				// Description
	    values.put(KEY_LAT, placeIt.getLocation().latitude); 			// Latitude
	    values.put(KEY_LNG, placeIt.getLocation().longitude); 			// Longitude
	    values.put(KEY_LOC, placeIt.getLocation_str()); 				// Location String
	    values.put(KEY_SCHED_OPTION, schedule.getScheduled_option());	// Scheduled Option
	    values.put(KEY_SCHED_DOW, schedule.getScheduled_dow());			// Scheduled DOW
	    values.put(KEY_SCHED_WEEK, schedule.getScheduled_week());		// Scheduled Week
	    values.put(KEY_SCHED_MINUTES, schedule.getScheduled_minutes());	// Scheduled Minutes
	    Log.i("Debugging DB","1");
	    String[] categories = placeIt.getCategories();  
	    Log.i("Debugging DB","2");
	    values.put(KEY_CAT1, categories[0]);							// Category 1
	    values.put(KEY_CAT2, categories[1]);							// Category 2
	    values.put(KEY_CAT3, categories[2]);							// Category 3
	    Log.i("Debugging DB","3");
	    values.put(KEY_USER, placeIt.getUsername());					// Username
	    
	    return values;
	}
	
}
