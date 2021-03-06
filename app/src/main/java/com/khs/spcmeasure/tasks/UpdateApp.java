package com.khs.spcmeasure.tasks;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Mark on 5/16/2016.
 * handles application update
 * see: http://stackoverflow.com/questions/4967669/android-install-apk-programmatically
 */
public class UpdateApp extends AsyncTask<String,Void,Void> {
    private static final String TAG = "UpdateApp";

    private static final String APK_FILE = "spcMeasure.apk";

    private Context mContext;
    private OnUpdateAppListener mListener;

    // constructor
    public UpdateApp(Context context, OnUpdateAppListener listener) {
        mContext = context;
        mListener = listener;
    }

    @Override
    protected void onPreExecute() {
        mListener.onUpdateAppStarted();
    }

    @Override
    protected Void doInBackground(String... arg0) {
        try {
            URL url = new URL(arg0[0]);
            HttpURLConnection c = (HttpURLConnection) url.openConnection();
            c.setRequestMethod("GET");
            c.setDoOutput(true);
            c.connect();

            // old hard coded path
            // String PATH = "/mnt/sdcard/Download/";
            // File file = new File(PATH);

            // get the apk full pathname
            File outputFile = getApkFullPathname();

            // delete apk file if it exists
            deleteFullPathname(outputFile);

            // copy apk from url connection to device
            FileOutputStream fos = new FileOutputStream(outputFile);

            InputStream is = c.getInputStream();

            byte[] buffer = new byte[1024];
            int len1 = 0;
            while ((len1 = is.read(buffer)) != -1) {
                fos.write(buffer, 0, len1);
            }
            fos.close();
            is.close();

            // install apk on the device
            Intent intent = new Intent(Intent.ACTION_VIEW);
            // intent.setDataAndType(Uri.fromFile(new File("/mnt/sdcard/Download/update.apk")), "application/vnd.android.package-archive");
            intent.setDataAndType(Uri.fromFile(outputFile), "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // without this flag android returned a intent error!
            mContext.startActivity(intent);

        } catch (Exception e) {
            Log.e("UpdateAPP", "Update error! " + e.getMessage());
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        mListener.onUpdateAppFinished();
    }

    // communication interface
    public interface OnUpdateAppListener {
        public void onUpdateAppStarted();

        // TODO: Update argument type and name
        public void onUpdateAppFinished();
    }

    // get apk full pathname
    public static File getApkFullPathname() {
        File file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        file.mkdirs();
        return new File(file, APK_FILE);
    }

    // delete file if it exists
    public static void deleteFullPathname(File delFile) {
        if(delFile.exists()){
            delFile.delete();
        }
    }

}
