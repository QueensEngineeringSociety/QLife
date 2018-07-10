package beta.qlife.utility.comparing;

import java.util.ArrayList;
import java.util.HashMap;

public class StringListComparator {
    private ArrayList<String> sortedTerms = new ArrayList<>();
    private ArrayList<Integer> termProximities = new ArrayList<>();

    StringListComparator(String search, ArrayList<String> terms) {
        search = search.toLowerCase();
        HashMap<String, Integer> proximities = new HashMap<>();
        for (String term : terms) {
            term = term.toLowerCase();
            StringComparator comp = new StringComparator(search, term);
            proximities.put(term, comp.getProximity());
        }
        sortByProximity(proximities);
    }

    private void sortByProximity(HashMap<String, Integer> proximities) {
        String keyForClosest = "";
        int numTerms = proximities.size();
        for (int i = 0; i < numTerms; ++i) {
            int smallestProx = 99999999;
            for (String str : proximities.keySet()) {
                int curVal = proximities.get(str);
                if (curVal < smallestProx) {
                    smallestProx = curVal;
                    keyForClosest = str;
                }
            }
            sortedTerms.add(keyForClosest);
            termProximities.add(smallestProx);
            proximities.remove(keyForClosest);
        }
    }

    public int termProximity(int index) {
        return termProximities.get(index);
    }

    public int termProximity(String term) {
        return termProximities.get(sortedTerms.indexOf(term));
    }

    public ArrayList<String> termsByProximity() {
        return sortedTerms;
    }
}
