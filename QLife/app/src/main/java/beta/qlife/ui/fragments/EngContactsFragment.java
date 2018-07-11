package beta.qlife.ui.fragments;


import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;

import beta.qlife.R;
import beta.qlife.activities.MainTabActivity;
import beta.qlife.database.local.DatabaseRow;
import beta.qlife.database.local.contacts.engineering.EngineeringContact;
import beta.qlife.database.local.contacts.engineering.EngineeringContactsManager;
import beta.qlife.interfaces.enforcers.ActionbarFragment;
import beta.qlife.interfaces.enforcers.DrawerItem;
import beta.qlife.interfaces.enforcers.ListFragment;
import beta.qlife.interfaces.enforcers.SearchableFragment;
import beta.qlife.utility.Util;
import beta.qlife.utility.comparing.DbTableComparator;

/**
 * Created by Carson on 12/06/2017.
 * Activity that displays engineering contact information held in cloud database
 */
public class EngContactsFragment extends android.support.v4.app.ListFragment implements ActionbarFragment, DrawerItem, ListFragment, SearchableFragment {

    private ArrayList<DatabaseRow> rawContacts = new ArrayList<>();
    private Activity activity;
    private SearchView searchView;

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
        rawContacts = (new EngineeringContactsManager(activity)).getTable();
        inflateListView();
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
        Util.setActionbarTitle(getString(R.string.fragment_eng_contacts), (AppCompatActivity) activity);
    }

    @Override
    public void deselectDrawer() {
        Util.setDrawerItemSelected(getActivity(), R.id.nav_tools, false);
    }

    @Override
    public void selectDrawer() {
        Util.setDrawerItemSelected(getActivity(), R.id.nav_tools, true);
    }

    @Override
    public void inflateListView() {
        if (activity != null) {
            setListAdapter(rawContacts);
        }
    }

    /**
     * Helper method that packs a hash-map containing a key and value for each piece of contact information.
     *
     * @param row The contact information to pack for.
     * @return The packed map.
     */
    private static HashMap<String, String> packEngContactsMap(DatabaseRow row) {
        EngineeringContact contact = (EngineeringContact) row;
        HashMap<String, String> map = new HashMap<>();
        map.put(EngineeringContact.COLUMN_NAME, contact.getName());
        map.put(EngineeringContact.COLUMN_EMAIL, contact.getEmail());
        map.put(EngineeringContact.COLUMN_POSITION, contact.getPosition());
        map.put(EngineeringContact.COLUMN_DESCRIPTION, contact.getDescription());
        return map;
    }

    @Override
    public void setSearchVisible(boolean isVisible) {
        Util.setSearchVisible((MainTabActivity) activity, isVisible);
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
                        DbTableComparator comp = new DbTableComparator(query, rawContacts);
                        setListAdapter(comp.tableByProximity());
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        return false;
                    }
                });
                searchView.setOnCloseListener(new SearchView.OnCloseListener() {
                    @Override
                    public boolean onClose() {
                        inflateListView();
                        return false;
                    }
                });
            }
        }
    }

    @Override
    public void closeSearchView(SearchView searchView) {
        Util.closeSearchView(searchView);
    }

    private void setListAdapter(ArrayList<DatabaseRow> listContent) {
        if (activity != null) {
            ArrayList<HashMap<String, String>> engContactsList = new ArrayList<>();
            for (DatabaseRow row : listContent) {
                engContactsList.add(packEngContactsMap(row));
            }
            ListAdapter adapter = new SimpleAdapter(activity.getApplicationContext(), engContactsList,
                    R.layout.eng_contacts_list_item, new String[]{EngineeringContact.COLUMN_NAME, EngineeringContact.COLUMN_EMAIL,
                    EngineeringContact.COLUMN_POSITION, EngineeringContact.COLUMN_DESCRIPTION}, new int[]{R.id.name, R.id.email, R.id.position, R.id.description});
            setListAdapter(adapter);
        }
    }
}
