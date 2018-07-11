package beta.qlife.ui.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ListFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import beta.qlife.R;
import beta.qlife.activities.MainTabActivity;
import beta.qlife.database.local.DatabaseRow;
import beta.qlife.database.local.buildings.Building;
import beta.qlife.database.local.buildings.BuildingManager;
import beta.qlife.database.local.food.Food;
import beta.qlife.database.local.food.FoodManager;
import beta.qlife.interfaces.enforcers.ActionbarFragment;
import beta.qlife.interfaces.enforcers.DrawerItem;
import beta.qlife.interfaces.enforcers.ListFragmentWithChild;
import beta.qlife.interfaces.enforcers.SearchableFragment;
import beta.qlife.utility.Constants;
import beta.qlife.utility.Util;
import beta.qlife.utility.comparing.DbTableComparator;

/**
 * Created by Carson on 26/06/2017.
 * Fragment that displays the buildings in the phone/cloud database. When a building is clicked, it starts
 * OneBuildingFragment that provides details about the building.
 */
public class BuildingsFragment extends ListFragment implements ActionbarFragment, DrawerItem, ListFragmentWithChild, SearchableFragment {

    public static final String TAG_FOOD_NAMES = "FOOD_NAMES";

    private Activity activity;
    private SearchView searchView;
    private ArrayList<DatabaseRow> rawBuildings;
    private BuildingManager buildingManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_list, container, false);
        activity = getActivity();
        setActionbarTitle();
        setSearchFunction();
        initListView();
        return v;
    }

    private void initListView() {
        buildingManager = (new BuildingManager(activity));
        rawBuildings = buildingManager.getTable();
        inflateListView();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        onListItemChosen(v);
    }

    @Override
    public void onResume() {
        super.onResume();
        selectDrawer();
        setSearchVisible(true);
        inflateListView();
    }

    @Override
    public void onPause() {
        super.onPause();
        deselectDrawer();
        setSearchVisible(false);
        closeSearchView(searchView);
    }

    @Override
    public void setActionbarTitle() {
        Util.setActionbarTitle(getString(R.string.fragment_buildings), (AppCompatActivity) activity);
    }

    @Override
    public void deselectDrawer() {
        Util.setDrawerItemSelected(activity, R.id.nav_buildings, false);
    }

    @Override
    public void selectDrawer() {
        Util.setDrawerItemSelected(activity, R.id.nav_buildings, true);
    }

    @Override
    public void onListItemChosen(View view) {
        Bundle args = setDataForOneItem(view);
        OneBuildingFragment oneBuildingFragment = new OneBuildingFragment();
        oneBuildingFragment.setArguments(args);
        if (activity != null) {
            FragmentManager fm = ((FragmentActivity) activity).getSupportFragmentManager();
            fm.beginTransaction().addToBackStack(null).replace(R.id.content_frame, oneBuildingFragment).commit();
        }
    }

    @Override
    public Bundle setDataForOneItem(View view) {
        if (activity != null) {
            Bundle args = new Bundle();
            FoodManager foodManager = new FoodManager(activity.getApplicationContext());
            String sId = ((TextView) view.findViewById(R.id.db_id)).getText().toString();
            Building building = buildingManager.getRow(Integer.parseInt(sId));
            ArrayList<Food> food = foodManager.getFoodForBuilding(Integer.parseInt(sId));

            ArrayList<String> foodNames = new ArrayList<>();
            for (Food oneFood : food) {
                foodNames.add(oneFood.getName());
            }

            //deal with special case building names - common short forms for long names
            switch (building.getName()) {
                case Constants.ARC_FULL:
                    args.putString(Building.COLUMN_NAME, Constants.ARC);
                    break;
                case Constants.JDUC_FULL:
                    args.putString(Building.COLUMN_NAME, Constants.JDUC);
                    break;
                default:
                    args.putString(Building.COLUMN_NAME, building.getName());
                    break;
            }

            args.putString(Building.COLUMN_PURPOSE, building.getPurpose());
            args.putBoolean(Building.COLUMN_BOOK_ROOMS, building.getBookRooms());
            args.putBoolean(Building.COLUMN_ATM, building.getAtm());
            args.putDouble(Building.COLUMN_LAT, building.getLat());
            args.putDouble(Building.COLUMN_LON, building.getLon());
            args.putStringArrayList(TAG_FOOD_NAMES, foodNames);
            return args;
        }
        return null;
    }

    @Override
    public void inflateListView() {
        if (activity != null) {
            setListAdapter(rawBuildings);
        }
    }

    @Override
    public void setSearchFunction() {
        MainTabActivity myActivity = (MainTabActivity) activity;
        if (myActivity != null) {
            Menu menu = myActivity.getOptionsMenu();
            if (menu != null) {
                final MenuItem searchItem = menu.findItem(R.id.action_search);
                searchView = (SearchView) searchItem.getActionView();
                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        DbTableComparator comp = new DbTableComparator(query, rawBuildings);
                        setListAdapter(comp.tableByProximity());
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        return false;
                    }
                });
            }
        }
    }

    private void setListAdapter(ArrayList<DatabaseRow> buildings) {
        if (activity != null) {
            ArrayList<HashMap<String, String>> buildingsList = new ArrayList<>();
            for (DatabaseRow row : buildings) {
                buildingsList.add(packBuildingMap(row));
            }
            ListAdapter adapter = new SimpleAdapter(activity.getApplicationContext(), buildingsList,
                    R.layout.buildings_list_item, new String[]{Building.COLUMN_NAME, Building.COLUMN_PURPOSE, Building.COLUMN_FOOD, FoodFragment.TAG_DB_ID},
                    new int[]{R.id.name, R.id.purpose, R.id.food, R.id.db_id});
            setListAdapter(adapter);
        }
    }

    private HashMap<String, String> packBuildingMap(DatabaseRow row) {
        Building building = (Building) row;
        HashMap<String, String> map = new HashMap<>();
        map.put(Building.COLUMN_NAME, building.getName());
        map.put(Building.COLUMN_PURPOSE, building.getPurpose());
        String food = building.getFood() ? "Yes" : "No";
        map.put(Building.COLUMN_FOOD, food);
        map.put(FoodFragment.TAG_DB_ID, String.valueOf(building.getId()));
        return map;
    }

    @Override
    public void setSearchVisible(boolean isVisible) {
        Util.setSearchVisible((MainTabActivity) activity, isVisible);
    }

    @Override
    public void closeSearchView(SearchView searchView) {
        Util.closeSearchView(searchView);
    }
}
