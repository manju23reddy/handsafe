package com.manju23reddy.sightclick.providers;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.manju23reddy.sightclick.db.BTClientDBContract;
import com.manju23reddy.sightclick.db.BTClientDBHelper;
import com.manju23reddy.sightclick.util.BTClientConstants;

public class BTClientContentProvider extends ContentProvider {
    private BTClientDBHelper mDBHelper = null;

    private static final int DEVICE = 100;
    private static final int CONTACTS = 200;

    private static UriMatcher sUriMatcher = buildUriMatcher();


    private static UriMatcher buildUriMatcher(){
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(BTClientConstants.CONTENT_PROVIDER.AUTHORITY,
                BTClientConstants.CONTENT_PROVIDER.BT_DEVICE, DEVICE);

        uriMatcher.addURI(BTClientConstants.CONTENT_PROVIDER.AUTHORITY,
                BTClientConstants.CONTENT_PROVIDER.BT_DEVICE, CONTACTS);


        return uriMatcher;
    }

    public BTClientContentProvider() {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Implement this to handle requests to delete one or more rows.
        final SQLiteDatabase db = mDBHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);

        int deleteResult;

        switch (match){
            case DEVICE:
                String device_id = uri.getPathSegments().get(1);
                deleteResult = db.delete(BTClientDBContract.BTDeviceTable.TABLE_NAME,
                        BTClientDBContract.BTDeviceTable.COLUMN_DEVICE_BT_ADDRESS+"=?",
                        new String[]{device_id});
                break;
            case CONTACTS:
                String contact_id = uri.getPathSegments().get(1);
                deleteResult = db.delete(BTClientDBContract.FriendsTable.TABLE_NAME,
                        BTClientDBContract.FriendsTable.COLUMN_PRIMARY_NUMBER+"=?",
                        new String[]{contact_id});
                break;
            default:
                throw new UnsupportedOperationException("Not yet implemented");

        }
        if (deleteResult != 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return deleteResult;

    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // TODO: Implement this to handle requests to insert a new row.
        final SQLiteDatabase db = mDBHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match){
            case DEVICE:
                try{
                    long id = db.insert(BTClientDBContract.BTDeviceTable.TABLE_NAME,
                            null,
                            values);

                    if (id > 0){
                        returnUri = ContentUris.withAppendedId(BTClientDBContract.CONTENT_URI, id);
                    }
                    else{
                        throw new UnsupportedOperationException("Failed to add device "+uri);
                    }
                }
                catch (Exception ee){
                    throw new UnsupportedOperationException("Failed to add device "+uri);
                }
                break;
            case CONTACTS:
                try{
                    long id = db.insert(BTClientDBContract.FriendsTable.TABLE_NAME,
                            null,
                            values);

                    if (id > 0){
                        returnUri = ContentUris.withAppendedId(BTClientDBContract.CONTENT_URI, id);
                    }
                    else {
                        throw new UnsupportedOperationException("Failed to add contact(s) "+uri);
                    }
                }
                catch (Exception ee){
                    throw new UnsupportedOperationException("Failed to add contact "+uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Failed to perform insert operation "+uri);
        }

        return returnUri;
    }

    @Override
    public boolean onCreate() {
        // TODO: Implement this to initialize your content provider on startup.
        mDBHelper = new BTClientDBHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        // TODO: Implement this to handle query requests from clients.
        final SQLiteDatabase db = mDBHelper.getReadableDatabase();

        int match = sUriMatcher.match(uri);
        Cursor resultCursor;

        switch (match){
            case DEVICE:
                resultCursor = db.query(BTClientDBContract.BTDeviceTable.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null, sortOrder);
                break;
            case CONTACTS:
                resultCursor = db.query(BTClientDBContract.BTDeviceTable.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null, sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Not yet implemented");

        }

        return resultCursor;

    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
