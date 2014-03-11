package ucsd.cse110.placeit;

import java.util.ArrayList;

import org.json.JSONObject;

public interface AsyncResponse {
	void processFinish(JSONObject data);
}
