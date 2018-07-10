package beta.qlife.utility.comparing;

import java.util.ArrayList;

import beta.qlife.database.local.DatabaseRow;

class DbRowComparator extends Comparator {
    DbRowComparator(String search, DatabaseRow row) {
        search = search.toLowerCase();
        ArrayList<String> fieldsAsStrings = row.publicFieldsAsStringList();
        StringListComparator stringListComparator = new StringListComparator(search, fieldsAsStrings);
        proximity = stringListComparator.rowProximity(0);
    }
}
