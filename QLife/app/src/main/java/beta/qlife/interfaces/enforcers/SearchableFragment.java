package beta.qlife.interfaces.enforcers;

import android.support.v7.widget.SearchView;

public interface SearchableFragment {

    /**
     * This method should get the SearchView and set the onQueryTextSubmit() callback. It should be
     * called from onCreateView().
     */
    void setSearchFunction();

    /**
     * This method should get the Activity the implementing Fragment is attached to and
     * call Util.setSearchVisible().
     * Should be called from onResume() after the view has been inflated with true
     * and in onPause() with false.
     */
    void setSearchVisible(boolean isVisible);

    /**
     * This method should get the SearchView and call Util.closeSearchView(). Should be called
     * in onPause().
     */
    void closeSearchView(SearchView searchView);
}
