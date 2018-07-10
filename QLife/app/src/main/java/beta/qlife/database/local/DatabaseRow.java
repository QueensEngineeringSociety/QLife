package beta.qlife.database.local;

import java.util.ArrayList;

/**
 * Created by Carson on 27/07/2017.
 * <p>
 * Abstract class that defines common table schema and methods. Also used
 * so the DatabaseManager class can define abstract methods for all managers.
 */
public abstract class DatabaseRow {
    public static final String TABLE_NAME = ""; //must be overridden in children
    public static final String ID = "ID";
    public static final int ID_POS = 0;

    private long id;

    public DatabaseRow(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public abstract ArrayList<String> publicFieldsAsStringList();

    protected String boolFieldToString(boolean field) {
        return field ? "Can " : "Cannot ";
    }

    protected String truncateEmailField(String email) {
        int i = email.indexOf('@');
        if (i < 0) {
            return email;
        }
        return email.substring(0, i);
    }
}
