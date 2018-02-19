package com.manju23reddy.sightclick.db;

import android.net.Uri;
import android.provider.BaseColumns;

import com.manju23reddy.sightclick.util.BTClientConstants;

import java.util.ArrayList;

/**
 * Created by MReddy3 on 2/5/2018.
 */

public final class BTClientDBContract {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "btclient.db";

    public static ArrayList<String> CREATE_TABLE_SCHEMAS = null;

    public static final Uri DEVICE_CONTENT_URI = BTClientConstants.CONTENT_PROVIDER.BASE_CONTENT_URI.
            buildUpon().appendPath(BTClientConstants.CONTENT_PROVIDER.BT_DEVICE).build();

    public static final Uri FRIEND_CONTENT_URI = BTClientConstants.CONTENT_PROVIDER.BASE_CONTENT_URI.
            buildUpon().appendPath(BTClientConstants.CONTENT_PROVIDER.FRIENDS).build();

    private BTClientDBContract() {
        insertCreateSchema(BTDeviceTable.CREATE_TABLE);
        insertCreateSchema(FriendsTable.CREATE_TABLE);
    }

    public static ArrayList<String> getCreateTableSchemas(){
        return CREATE_TABLE_SCHEMAS;
    }

    private void insertCreateSchema(String schema){
        if (null == CREATE_TABLE_SCHEMAS){
            CREATE_TABLE_SCHEMAS = new ArrayList<>();
        }
        CREATE_TABLE_SCHEMAS.add(schema);
    }


    /**
     * Device Table to bond Device with the application and also to know the device mac address
     * for opening serial communication channel for receiving and sending the commands from device
     * and to device.
     */
    public static abstract class BTDeviceTable{
        public static final String TABLE_NAME = "deviceTable";
        public static final String COLUMN_DEVICE_BT_ADDRESS = "BT_ADDRESS";


        public static final String CREATE_TABLE = "CREATE TABLE "+
                TABLE_NAME + " ("+
                COLUMN_DEVICE_BT_ADDRESS + " TEXT PRIMARY KEY NOT NULL);";

        public static final String DELETE_DEVICE_FROM = "DELETE FROM "+TABLE_NAME;

    }

    /**
     * Friends table to save list of contacts as friends. Thus when an incoming call is received
     * the incoming caller number is verified across the entries in the table and notified
     * device about status
     */
    public static abstract class FriendsTable{
        public static final String TABLE_NAME = "friendsTable";
        public static final String COLUMN_FNAME = "fname";
        public static final String COLUMN_LNAME = "lname";
        public static final String COLUMN_PRIMARY_NUMBER = "pnumber";
        public static final String COLUMN_PROFILE_PIC_URL = "picurl";

        public static final String CREATE_TABLE = "CREATE TABLE "+
                TABLE_NAME + " ("+
                COLUMN_PRIMARY_NUMBER + " TEXT PRIMARY KET, "+
                COLUMN_FNAME +" TEXT NOT NULL, "+
                COLUMN_LNAME +" TEXT NOT NULL, "+
                COLUMN_PROFILE_PIC_URL+ " TEXT );";

        public static final String GET_ALL_FRIENDS = " SELECT * FROM "+TABLE_NAME;

    }


}
