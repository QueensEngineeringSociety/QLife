package beta.qlife.utility.comparing;

import java.util.ArrayList;
import java.util.HashMap;

class StringListComparator extends ListComparator<String> {

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
}
