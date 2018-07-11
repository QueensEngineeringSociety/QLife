package beta.qlife.utility.comparing;

import java.util.ArrayList;
import java.util.HashMap;

import beta.qlife.database.local.DatabaseRow;

public class DbTableComparator extends ListComparator<DatabaseRow> {

    public DbTableComparator(String search, ArrayList<DatabaseRow> table) {
        search = search.toLowerCase();
        HashMap<DatabaseRow, Integer> proximities = new HashMap<>();
        for (DatabaseRow row : table) {
            DbRowComparator comp = new DbRowComparator(search, row);
            proximities.put(row, comp.getProximity());
        }
        sortByProximity(proximities);
    }
}
