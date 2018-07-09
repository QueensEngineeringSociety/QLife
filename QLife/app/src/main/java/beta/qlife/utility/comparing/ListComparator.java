package beta.qlife.utility.comparing;

import java.util.ArrayList;
import java.util.HashMap;

public class ListComparator {
    private String search;
    private ArrayList<String> sortedTerms = new ArrayList<>();

    public ListComparator(String search, ArrayList<String> terms) {
        this.search = search.toLowerCase();
        HashMap<String, Integer> proximities = new HashMap<>();
        for (String term : terms) {
            term = term.toLowerCase();
            StringComparator comp = new StringComparator(this.search, term);
            proximities.put(term, comp.getProximity());
        }
        sortByProximity(proximities);
    }

    private void sortByProximity(HashMap<String, Integer> proximities) {
        String keyForClosest = "";
        int numTerms = proximities.size();
        for (int i = 0; i < numTerms; ++i) {
            double smallestProx = 99999999;
            for (String str : proximities.keySet()) {
                double curVal = proximities.get(str);
                if (curVal < smallestProx) {
                    smallestProx = curVal;
                    keyForClosest = str;
                }
            }
            sortedTerms.add(keyForClosest);
            proximities.remove(keyForClosest);
        }
    }

    public ArrayList<String> termsByProximity() {
        return sortedTerms;
    }

    public String proximityReference() {
        return search;
    }
}
