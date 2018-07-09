package beta.qlife.utility.comparing;

public class StringComparator {
    private String term, search;
    private int proximity;
    private int partialDistances[][];

    StringComparator(String search, String term) {
        this.term = term.toLowerCase();
        this.search = search.toLowerCase();
        initPartialDistances();
        findProximity();
    }

    private void initPartialDistances() {
        partialDistances = new int[term.length() + 1][search.length() + 1];
        for (int i = 0; i <= term.length(); ++i) {
            partialDistances[i][0] = i;
        }
        for (int i = 0; i <= search.length(); ++i) {
            partialDistances[0][i] = i;
        }
    }

    private void findProximity() {
        for (int i = 0; i < search.length(); ++i) {
            for (int j = 0; j < term.length(); ++j) {
                int subCost = getSubCost(search.charAt(j), term.charAt(i));
                partialDistances[j + 1][i + 1] = min(partialDistances[j][i + 1] + 1,
                        partialDistances[j + 1][i] + 1, partialDistances[j][i] + subCost);
            }
        }
        proximity = partialDistances[term.length()][search.length()];
    }

    private int getSubCost(char c1, char c2) {
        if (c1 != c2) {
            return 1;
        }
        return 0;
    }

    private int min(int val1, int val2, int val3) {
        if (val1 < val2) {
            return val1 < val3 ? val1 : val3;
        } else {
            return val2 < val3 ? val2 : val3;
        }
    }

    public int getProximity() {
        return proximity;
    }
}
