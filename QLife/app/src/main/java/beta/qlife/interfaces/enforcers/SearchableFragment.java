package beta.qlife.interfaces.enforcers;

public interface SearchableFragment {

    /**
     * This method should get the Activity the implementing Fragment is attached to and
     * call Util.setSearchVisible().
     * Should be called from onCreateView() after the view has been inflated with true
     * and in onPause() with false.
     */
    void setSearchVisible(boolean isVisible);
}
