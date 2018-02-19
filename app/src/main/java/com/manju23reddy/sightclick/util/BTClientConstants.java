package com.manju23reddy.sightclick.util;

import android.net.Uri;

/**
 * Created by MReddy3 on 2/2/2018.
 */

public final class BTClientConstants {

    private BTClientConstants(){

    }

    public static class TO_Device{

    };

    public static class CONTENT_PROVIDER{
        public final static String AUTHORITY = "com.manju23reddy.sightclick";
        public final static String BT_DEVICE = "BT_DEVICE";
        public final static String FRIENDS = "FRIENDS";
        public final static Uri BASE_CONTENT_URI = Uri.parse("content://"+AUTHORITY);
    }

}
