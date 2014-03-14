package ucsd.cse110.placeit;

/*
 * Validates the user input of the PlaceIt form
 */
public class PlaceItDataChecker {
	
	private PlaceIt theData;
	
	public PlaceItDataChecker() {
		// empty constructor
	}
	
	public PlaceItDataChecker(PlaceIt inputData) {
		this.theData = inputData;
	}
	
	
	//decide to use this or not later on
	public Boolean checkMinimal()
	{
		if (theData.getTitle().toString().length() == 0 || 
				theData.getLocation().toString().length() == 0)
			return false;
		else
			return true;
	}
	
	public Boolean checkNormal()
	{
		if (theData.getTitle().toString().length() == 0 || theData.getLocation().toString().length() == 0 || 
				theData.getDescription().toString().length() == 0 /*|| 
				theData.getScheduled_date().toString().length() == 0 modify this later*/)
			return false;
		else
			return true;
	}
	
	public Boolean checkCategoryNormal()
	{
		String ary[] = theData.getCategories();
		if (theData.getTitle().toString().length() == 0 ||
				theData.getDescription().toString().length() == 0 ||
				(ary[0].length()==0 && ary[1].length()==0 && ary[2].length()==0)) {
			return false;
		}
		else
			return true;
	}
	
	public void setData(PlaceIt data)
	{
		this.theData = data;
	}
	
	public PlaceIt getData()
	{
		return this.theData;
	}
}
