package beta.qlife.utility.comparing;

import java.util.ArrayList;

import beta.qlife.database.local.DatabaseRow;

public class DbRowComparator {
    private int proximity;

    DbRowComparator(String search, DatabaseRow row) {
        ArrayList<String> fieldsAsStrings = row.publicFieldsAsStringList();
        StringListComparator stringListComparator = new StringListComparator(search, fieldsAsStrings);
        proximity = stringListComparator.termProximity(0);
    }

    public int getProximity() {
        return proximity;
    }
}
