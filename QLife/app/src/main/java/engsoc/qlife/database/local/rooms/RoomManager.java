package engsoc.qlife.database.local.rooms;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;

import engsoc.qlife.database.local.DatabaseManager;
import engsoc.qlife.database.local.DatabaseRow;

/**
 * Created by Alex on 7/28/2017.
 * Class that handles Room rows in phone database
 */
public class RoomManager extends DatabaseManager {
    /**
     * Created by Alex on 7/28/2017.
     */
    public RoomManager(Context context) {
        super(context);
    }

    @Override
    public void insertRow(DatabaseRow row) {
        if (row instanceof Room) {
            Room room = (Room) row;
            ContentValues values = new ContentValues();
            values.put(Room.ID, room.getId());
            values.put(Room.COLUMN_BUILDING_ID, room.getBuildingId());
            values.put(Room.COLUMN_DESCRIPTION, room.getDescription());
            values.put(Room.COLUMN_MAP_URL, room.getMapUrl());
            values.put(Room.COLUMN_NAME, room.getName());
            values.put(Room.COLUMN_PIC_URL, room.getPicUrl());
            values.put(Room.COLUMN_ROOM_ID, room.getRoomId());
            getDatabase().insert(Room.TABLE_NAME, null, values);
        }
    }

    @Override
    public ArrayList<DatabaseRow> getTable() {
        String[] projection = {
                Room.ID,
                Room.COLUMN_BUILDING_ID,
                Room.COLUMN_DESCRIPTION,
                Room.COLUMN_MAP_URL,
                Room.COLUMN_NAME,
                Room.COLUMN_PIC_URL,
                Room.COLUMN_ROOM_ID
        };
        ArrayList<DatabaseRow> ILCRooms = new ArrayList<>();
        //try with resources - automatically closes cursor whether or not its completed normally
        //order by building name
        try (Cursor cursor = getDatabase().query(Room.TABLE_NAME, projection, null, null, null, null, Room.COLUMN_NAME + " ASC")) {
            while (cursor.moveToNext()) {
                Room room = getRow(cursor.getInt(Room.ID_POS));
                ILCRooms.add(room);
            }
            cursor.close();
            return ILCRooms; //return only when the cursor has been closed
        }
    }

    @Override
    public Room getRow(long id) {
        String[] projection = {
                Room.ID,
                Room.COLUMN_BUILDING_ID,
                Room.COLUMN_DESCRIPTION,
                Room.COLUMN_MAP_URL,
                Room.COLUMN_NAME,
                Room.COLUMN_PIC_URL,
                Room.COLUMN_ROOM_ID
        };
        String selection = Room.ID + " LIKE ?";
        String[] selectionArgs = {String.valueOf(id)};
        try (Cursor cursor = getDatabase().query(Room.TABLE_NAME, projection, selection, selectionArgs, null, null, null)) {
            cursor.moveToNext();
            //getInt()>0 because SQLite doesn't have boolean types - 1 is true, 0 is false
            Room room = new Room(cursor.getInt(Room.ROOM_ID_POS), cursor.getInt(Room.BUILDING_ID_POS), cursor.getString(Room.DESCRIPTION_POS),
                    cursor.getString(Room.MAP_URL_POS), cursor.getString(Room.NAME_POS), cursor.getString(Room.PIC_URL_POS),
                    cursor.getInt(Room.ROOM_ID_POS));
            cursor.close();
            return room; //return only when the cursor has been closed.
            //Return statement never missed, try block always finishes this.
        }
    }

    @Override
    public void updateRow(DatabaseRow oldRow, DatabaseRow newRow) {
        if (oldRow instanceof Room && newRow instanceof Room) {
            Room oldRoom = (Room) oldRow;
            Room newRoom = (Room) newRow;

            ContentValues values = new ContentValues();
            values.put(Room.ID, newRoom.getId());
            values.put(Room.COLUMN_BUILDING_ID, newRoom.getBuildingId());
            values.put(Room.COLUMN_DESCRIPTION, newRoom.getDescription());
            values.put(Room.COLUMN_MAP_URL, newRoom.getMapUrl());
            values.put(Room.COLUMN_NAME, newRoom.getName());
            values.put(Room.COLUMN_PIC_URL, newRoom.getPicUrl());
            values.put(Room.COLUMN_ROOM_ID, newRoom.getRoomId());

            String selection = Room.ID + " LIKE ?";
            String selectionArgs[] = {String.valueOf(oldRoom.getId())};
            getDatabase().update(Room.TABLE_NAME, values, selection, selectionArgs);
        }
    }
}


