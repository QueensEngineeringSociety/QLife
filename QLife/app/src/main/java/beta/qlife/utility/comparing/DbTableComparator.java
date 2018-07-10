package beta.qlife.utility.comparing;

import java.util.ArrayList;
import java.util.HashMap;

import beta.qlife.database.local.DatabaseRow;

public class DbTableComparator {
    private ArrayList<DatabaseRow> sortedTable = new ArrayList<>();
    private ArrayList<Integer> rowProximities = new ArrayList<>();

    public DbTableComparator(String search, ArrayList<DatabaseRow> table) {
        HashMap<DatabaseRow, Integer> proximities = new HashMap<>();
        for (DatabaseRow row : table) {
            DbRowComparator comp = new DbRowComparator(search, row);
            proximities.put(row, comp.getProximity());
        }
        sortByProximity(proximities);
    }

    private void sortByProximity(HashMap<DatabaseRow, Integer> proximities) {
        DatabaseRow keyForClosest = null;
        int numTerms = proximities.size();
        for (int i = 0; i < numTerms; ++i) {
            int smallestProx = 99999999;
            for (DatabaseRow row : proximities.keySet()) {
                int curVal = proximities.get(row);
                if (curVal < smallestProx) {
                    smallestProx = curVal;
                    keyForClosest = row;
                }
            }
            sortedTable.add(keyForClosest);
            rowProximities.add(smallestProx);
            proximities.remove(keyForClosest);
        }
    }

    public int rowProximity(int index) {
        return rowProximities.get(index);
    }

    public int rowProximity(DatabaseRow row) {
        return rowProximities.get(sortedTable.indexOf(row));
    }

    public ArrayList<DatabaseRow> tableByProximity() {
        return sortedTable;
    }
}
