package engsoc.qlife.database.dibs;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import engsoc.qlife.interfaces.AsyncTaskObserver;
import engsoc.qlife.utility.Constants;
import engsoc.qlife.utility.async.DownloadTextTask;

/**
 * Created by Alex Ruffo on 21/06/2017.
 * Gets the D!bs API data for the selected day
 */
public class GetRooms extends DownloadTextTask<Void, Void> {

    public GetRooms(AsyncTaskObserver observer) {
        super(observer);
    }

    @Override
    protected Void backgroundTask(Void val) {
        try {
            //call php script on server that gets info from cloud database
            String jsonStr = getText(Constants.GET_DIBS_ROOMS);
            mObserver.duringTask(new JSONArray(jsonStr));
        } catch (JSONException e) {
            Log.d("HELLOTHERE", "BAD: " + e);
        }
        return null;
    }
}
