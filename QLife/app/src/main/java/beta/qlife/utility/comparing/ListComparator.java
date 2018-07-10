package beta.qlife.utility.comparing;

import java.util.ArrayList;
import java.util.HashMap;

abstract class ListComparator<T> {
    private ArrayList<Integer> proximities = new ArrayList<>();
    private ArrayList<T> sortedTerms = new ArrayList<>();

    void sortByProximity(HashMap<T, Integer> proximitiesMap) {
        T keyForClosest = null;
        int numTerms = proximitiesMap.size();
        for (int i = 0; i < numTerms; ++i) {
            int smallestProx = 99999999;
            for (T row : proximitiesMap.keySet()) {
                int curVal = proximitiesMap.get(row);
                if (curVal < smallestProx) {
                    smallestProx = curVal;
                    keyForClosest = row;
                }
            }
            sortedTerms.add(keyForClosest);
            proximities.add(smallestProx);
            proximitiesMap.remove(keyForClosest);
        }
    }

    public int rowProximity(int index) {
        return proximities.get(index);
    }

    public int rowProximity(T row) {
        return proximities.get(sortedTerms.indexOf(row));
    }

    public ArrayList<T> tableByProximity() {
        return sortedTerms;
    }
}
