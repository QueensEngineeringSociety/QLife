package engsoc.qlife.utility;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;

import engsoc.qlife.R;
import engsoc.qlife.activities.AboutActivity;
import engsoc.qlife.activities.ReviewActivity;
import engsoc.qlife.activities.SettingsActivity;
import engsoc.qlife.interfaces.observers.CallableObj;

/**
 * Created by Carson on 01/08/2017.
 * Class for common methods. All are short and static.
 */
public class Util {
    public static void initMapView(final MapView mapView, Bundle savedInstanceState,
                                   final Activity activity, final CallableObj<Void> callback) {
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        try {
            MapsInitializer.initialize(activity.getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                if (ActivityCompat.checkSelfPermission(activity.getApplicationContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(activity.getApplicationContext(),
                                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    HandlePermissions.requestLocationPermissions(activity);
                } else {
                    googleMap.setMyLocationEnabled(true);
                }
                try {
                    callback.call(googleMap);
                } catch (Exception e) {
                    googleMap.clear();
                    mapView.setVisibility(View.GONE);
                }
            }
        });
    }

    public static void handleOptionsClick(Activity current, int clickedId){
        switch (clickedId) {
            case R.id.settings:
                current.startActivity(new Intent(current, SettingsActivity.class));
                break;
            case R.id.about:
                current.startActivity(new Intent(current, AboutActivity.class));
                break;
        }
    }

    public static void setActionbarTitle(String title, AppCompatActivity activity) {
        ActionBar actionbar = activity.getSupportActionBar();
        if (actionbar != null) {
            actionbar.setTitle(title);
        }
    }

    public static void setDrawerItemSelected(Activity activity, int itemId, boolean isChecked) {
        NavigationView navView = activity.findViewById(R.id.drawer_layout).findViewById(R.id.nav_view);
        navView.getMenu().findItem(itemId).setChecked(isChecked);
    }

    public static void setBackButton(ActionBar actionBar) {
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    public static void inflateOptionsMenu(int menuId, Menu menu, MenuInflater menuInflater) {
        menuInflater.inflate(menuId, menu);
    }

    public static String getHoursBetween(double startHour, double endHour) {
        //check for closed all day flag
        if (startHour < 0) {
            return "Closed";
        }

        String start = getOneTimeBoundary(startHour);
        String end = getOneTimeBoundary(endHour);
        return start + " to " + end;
    }

    /**
     * Helper method to getHoursBetween() that turns one time into h:mm format.
     *
     * @param hour The time to convert.
     * @return String format of the time in h:mm am/pm format.
     */
    private static String getOneTimeBoundary(double hour) {
        String sHour = "";
        if (hour < 1 || hour >= 13) { //24 hour time
            sHour += String.valueOf((int) hour - 12);
        } else {
            sHour += String.valueOf((int) hour);
        }

        double min = (hour - (int) hour) * 60; //convert 0.5, 0.75 to 30, 45 min
        sHour += ":" + String.valueOf((int) min);
        if (min == 0) {
            sHour += "0";
        }

        if (hour < 12) {
            sHour += " am";
        } else {
            sHour += " pm";
        }
        return sHour;
    }
}
