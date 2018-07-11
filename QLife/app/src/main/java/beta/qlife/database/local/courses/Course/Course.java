package beta.qlife.database.local.courses.Course;

import java.util.ArrayList;

import beta.qlife.database.local.DatabaseRow;

/**
 * Created by Carson on 19/01/2017.
 * Defines the schema for the Courses table. Simply holds the course code and name, does not
 * differentiate between individual classes like lectures and tutorials.
 */
public class Course extends DatabaseRow {
    //table schema
    public static final String TABLE_NAME = "courses";
    public static final String COLUMN_CODE = "code";
    public static final String COLUMN_NAME = "Name";
    public static final String COLUMN_SET_NAME = "set_name";

    //column number each field ends up in
    public static final int CODE_POS = 1;
    public static final int NAME_POS = 2;
    public static final int SET_NAME_POS = 3;

    //fields in database
    private String code;
    private String name;
    private boolean setName;

    public Course(long id, String code, String name, boolean setName) {
        super(id);
        this.code = code;
        this.name = name;
        this.setName = setName;
    }

    public Course(int id, String code) {
        super(id);
        this.code = code;
        setName = false;
    }

    public Course(int id, String code, String name) {
        super(id);
        this.code = code;
        this.name = name;
        setName = true;
    }

    public Course(int id, String code, boolean setName) {
        super(id);
        this.code = code;
        this.setName = setName;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isSetName() {
        return setName;
    }

    public void setSetName(boolean setName) {
        this.setName = setName;
    }

    @Override
    public ArrayList<String> publicFieldsAsStringList() {
        ArrayList<String> fields = new ArrayList<>();
        fields.add(name);
        fields.add(code);
        return fields;
    }
}
