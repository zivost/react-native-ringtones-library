
package com.reactlibrary;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


class RingtoneUtils {

    private static final String LOG_TAG = "RingtoneUtils";

    public static boolean setRingtone(@NonNull Context context, @NonNull Uri ringtoneUri, int type) {
        Log.v(LOG_TAG, "Setting Ringtone to: " + ringtoneUri);
        if(ringtoneUri == null){
            Log.v(LOG_TAG, "Error setting ringtone");
            Toast.makeText(context, "Unable to set Ringtone", Toast.LENGTH_SHORT).show();
            return true;
        }

        if (!hasMarshmallow()) {
            Log.v(LOG_TAG, "On a Lollipop or below device, so go ahead and change device ringtone");
            setActualRingtone(context, ringtoneUri, type);
            return true;
        } else if (hasMarshmallow() && canEditSystemSettings(context)) {
            Log.v(LOG_TAG, "On a marshmallow or above device but app has the permission to edit system settings");
            setActualRingtone(context, ringtoneUri, type);
            return true;
        } else if (hasMarshmallow() && !canEditSystemSettings(context)) {
            Log.d(LOG_TAG, "On android Marshmallow and above but app does not have permission to" +
                    " edit system settings. Opening the manage write settings activity...");
            startManageWriteSettingsActivity(context);
            Toast.makeText(context, "Please allow app to edit settings so your ringtone/notification can be updated", Toast.LENGTH_LONG).show();
            return false;
        }

        return false;
    }

    private static void setActualRingtone(@NonNull Context context, @NonNull Uri ringtoneUri, int type) {

        RingtoneManager.setActualDefaultRingtoneUri(context, type, ringtoneUri);
        String message="";
        if(type == RingtoneManager.TYPE_RINGTONE) {
            message = "Ringtone Set Successfully";
        } else if(type == RingtoneManager.TYPE_NOTIFICATION) {
            message = "Notification tone Set Successfully";
        }
        Log.d("Rn-Native", String.valueOf(RingtoneManager.getActualDefaultRingtoneUri(context, type)));
        Log.d("Rn-Native", String.valueOf(ringtoneUri));
        Log.d("Rn-Native", String.valueOf(type));
        if ((RingtoneManager.getActualDefaultRingtoneUri(context, type)).equals(ringtoneUri)) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private static void startManageWriteSettingsActivity(@NonNull Context context) {
        Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
        // Passing in the app package here allows the settings app to open the exact app
        intent.setData(Uri.parse("package:" + context.getApplicationContext().getPackageName()));
        // Optional. If you pass in a service context without setting this flag, you will get an exception
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    private static boolean hasMarshmallow() {
        // returns true if the device is Android Marshmallow or above, false otherwise
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private static boolean canEditSystemSettings(@NonNull Context context) {
        // returns true if the app can edit system settings, false otherwise
        return Settings.System.canWrite(context.getApplicationContext());
    }

}

public class RingtonesLibraryModule extends ReactContextBaseJavaModule {

    private final ReactApplicationContext reactContext;
    private static final String TYPE_ALARM_KEY = "TYPE_ALARM";
    private static final String TYPE_ALL_KEY = "TYPE_ALL";
    private static final String TYPE_NOTIFICATION_KEY = "TYPE_NOTIFICATION";
    private static final String TYPE_RINGTONE_KEY = "TYPE_RINGTONE";

    final static class SettingsKeys {
        public static final String URI = "uri";
        public static final String TITLE = "title";
        public static final String ARTIST = "artist";
        public static final String SIZE = "size";
        public static final String MIME_TYPE = "mimeType";
        public static final String DURATION = "duration";
        public static final String RINGTONE_TYPE = "ringtoneType";
    }

    public RingtonesLibraryModule(final ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
    }

    @Override
    public String getName() {
        return "RingtonesLibrary";
    }

    @ReactMethod
    public void getRingtones(final Callback successCallback) {
        Log.d("RN-Native","getRingtones");
        getRingsByType(RingtoneManager.TYPE_ALL, successCallback);
    }

    @ReactMethod
    public void getRingsByType(final int ringtoneType, final Callback successCallback) {
        Log.d("RN-Native","getRingsByType - 2");
        final RingtoneManager manager = new RingtoneManager(this.reactContext);
        manager.setType(ringtoneType);
        final Cursor cursor = manager.getCursor();

        final WritableArray result = Arguments.createArray();
        int key = 0;
        while (cursor.moveToNext()) {
            final WritableMap data = Arguments.createMap();
            final String notificationTitle = cursor.getString(RingtoneManager.TITLE_COLUMN_INDEX);
            final Uri notificationUri = Uri.parse(cursor.getString(RingtoneManager.URI_COLUMN_INDEX) + "/"
                    + cursor.getString(RingtoneManager.ID_COLUMN_INDEX));
            final WritableMap song = getPathFromUri(this.reactContext, notificationUri);

            // File ringtone = new File(uriStr);
            MediaExtractor mex = new MediaExtractor();
            try {
                mex.setDataSource(song.getString("path"));// the adresss location of the sound on sdcard.
            } catch (IOException e) {
                e.printStackTrace();
            }
            
            MediaFormat mf = mex.getTrackFormat(0);
            
           // Integer bitRate = mf.getInteger(MediaFormat.KEY_BIT_RATE);
            Integer sampleRate = mf.getInteger(MediaFormat.KEY_SAMPLE_RATE);

            data.putInt("KEY", key);
            data.putInt("TYPE", ringtoneType);
            data.putString("TITLE", notificationTitle);
            data.putString("URI", song.getString("path"));
            data.putString("NOTIFICATION_URI", notificationUri.toString());
            // data.put("SIZE", settings.getInt(SettingsKeys.SIZE));
            // data.put("MIME_TYPE", settings.getString(SettingsKeys.MIME_TYPE));
            // data.put("ARTIST", settings.getString(SettingsKeys.ARTIST));
            // data.put("DURATION", settings.getInt(SettingsKeys.DURATION));
            data.putString("NOTIFICATION", song.getString("path"));
            data.putString("ARTIST", song.getString("artist"));
            data.putString("ALBUM", song.getString("album"));
            data.putInt("SAMPLERATE", sampleRate );
            data.putInt("BITRATE", 0);
            result.pushMap(data);
            key = key + 1;
        }
        successCallback.invoke(result);
    }

    @SuppressLint("NewApi")
    public WritableMap getPathFromUri (final Context context, Uri uri)  {
        Log.d("RN-Native","getPathFromUri" + uri);
        final boolean needToCheckUri = Build.VERSION.SDK_INT >= 19;
        String selection = null;
        String[] selectionArgs = null;
        // Uri is different in versions after KITKAT (Android 4.4), we need to
        // deal with different Uris.
        String type="audio";
        Log.d("RN-Native",String.valueOf(isDownloadsDocument(uri)));
        Log.d("RN-Native - idd",String.valueOf(isDownloadsDocument(uri)));
        Log.d("RN-Native - dcgd",DocumentsContract.getDocumentId(uri));
        if (needToCheckUri && DocumentsContract.isDocumentUri(context.getApplicationContext(), uri)) {
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
               // return Environment.getExternalStorageDirectory() + "/" + split[1];
            } else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                uri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
            } else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                type = split[0];
        
                if ("image".equals(type)) {
                    uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                selection = "_id=?";
                selectionArgs = new String[]{ split[1] };
            }
        }
        //val toast = Toast.makeText(this.reactContext, uri.getScheme());
       // toast.show();
/*
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            
            final String[] projection = { MediaStore.Images.Media.DATA };
            Cursor cursor = null;
            try {
                cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
                final int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                if (cursor.moveToFirst()) {
                   // return cursor.getString(column_index);
                }
            } catch (final Exception e) {
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {


            if("audio".equals(type)){*/
                final String[] projection = {MediaStore.Audio.Media.DISPLAY_NAME,MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.ALBUM,MediaStore.Audio.Media.ALBUM_ID };
                Cursor cursor = null;
                final WritableMap song = Arguments.createMap();
                try {
                    cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
                    final int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
                    if (cursor.moveToFirst()) {
                        do {
                        final String songName = cursor.getString(cursor.
                        getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
                        final String artist = cursor.getString(cursor.
                        getColumnIndex(MediaStore.Audio.Media.ARTIST));
                         final String path = cursor.getString(cursor
                        .getColumnIndex(MediaStore.Audio.Media.DATA));
                         final String albumName = cursor.getString(cursor
                        .getColumnIndex(MediaStore.Audio.Media.ALBUM));
                         final int albumId = cursor.getInt(cursor
                        .getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
    
                        song.putString("title", songName);
                        song.putString("path", path);
                        song.putString("artist", artist);
                        song.putString("album", albumName);
                        //song.put("albumId",albumId);
                        
                    } while (cursor.moveToNext());
                    return song;
                  }
                 } catch (final Exception e) {
                }
           // }
            
           // return uri.getPath();
       // }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(final Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(final Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(final Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @ReactMethod
    public void createRingtone(final ReadableMap settings) {
       final String uriStr = settings.getString(SettingsKeys.URI);
       final File ringtone = new File(uriStr);
       final ContentValues values = new ContentValues();
       values.put(MediaStore.MediaColumns.DATA, ringtone.getAbsolutePath());
       values.put(MediaStore.MediaColumns.TITLE, settings.getString(SettingsKeys.TITLE));
       values.put(MediaStore.MediaColumns.SIZE, settings.getInt(SettingsKeys.SIZE));
       values.put(MediaStore.MediaColumns.MIME_TYPE, settings.getString(SettingsKeys.MIME_TYPE));
       values.put(MediaStore.Audio.Media.ARTIST, settings.getString(SettingsKeys.ARTIST));
       values.put(MediaStore.Audio.Media.DURATION, settings.getInt(SettingsKeys.DURATION));
       values.put(MediaStore.Audio.Media.IS_RINGTONE, true);
       values.put(MediaStore.Audio.Media.IS_NOTIFICATION, false);
       values.put(MediaStore.Audio.Media.IS_ALARM, false);
       values.put(MediaStore.Audio.Media.IS_MUSIC, false);
       if (ringtone.exists() && getCurrentActivity() != null) {
           Uri uri = MediaStore.Audio.Media.getContentUriForPath(ringtone.getAbsolutePath());
           Cursor cursor = getCurrentActivity().getContentResolver().query(uri, null, MediaStore.MediaColumns.DATA + "=?", new String[]{ringtone.getAbsolutePath()}, null);
           if (cursor != null && cursor.moveToFirst() && cursor.getCount() > 0) {
               String id = cursor.getString(cursor.getColumnIndex("_id"));
               getCurrentActivity().getContentResolver().update(uri, values, MediaStore.MediaColumns.DATA + "=?", new String[]{ringtone.getAbsolutePath()});
               try {
                   Uri finalURI = ContentUris.withAppendedId(uri, Long.valueOf(id));
                   RingtoneUtils.setRingtone(getCurrentActivity(), finalURI, RingtoneManager.TYPE_RINGTONE);
               }catch (Exception t) {
                   Log.e("SPNativeError","Set ringtone err - " + t.getMessage());
                   Toast.makeText(getCurrentActivity(), "Unable to set Ringtone", Toast.LENGTH_SHORT).show();
               }finally {
                   if(cursor != null) {
                       cursor.close();
                   }
               }
           }else{
               Log.e("SPNativeError","Cursor error");
           }
       }else{
           Log.e("SPNativeError","Ringtone or Context Missing");
       }
    }

    @ReactMethod
    public void setRingtone(final String uri) {
        Log.d("RN Native", "setRingtone uri "+uri);
    }

    @ReactMethod
    public void pickRingtone() {

    }

    @Override
    public Map<String, Object> getConstants() {
        final Map<String, Object> constants = new HashMap<>();
        constants.put(TYPE_ALARM_KEY, RingtoneManager.TYPE_ALARM);
        constants.put(TYPE_ALL_KEY, RingtoneManager.TYPE_ALL);
        constants.put(TYPE_NOTIFICATION_KEY, RingtoneManager.TYPE_NOTIFICATION);
        constants.put(TYPE_RINGTONE_KEY, RingtoneManager.TYPE_RINGTONE);
        return constants;
    }

    /**
     * Returns true when the given ringtone type matches the ringtone to compare.
     * Will default to true if the given ringtone type is RingtoneManager.TYPE_ALL.
     * 
     * @param ringtoneType          ringtone type given
     * @param ringtoneTypeToCompare ringtone type to compare to
     * @return true if the type matches or is TYPE_ALL
     */
    private boolean isRingtoneType(final int ringtoneType, final int ringtoneTypeToCompare) {
        return ringtoneTypeToCompare == ringtoneType || RingtoneManager.TYPE_ALL == ringtoneType;
    }


}
