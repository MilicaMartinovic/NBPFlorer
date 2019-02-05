package com.example.milica.nbp_florer;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Milica on 27-Apr-18.
 */

public class FileResolver {

    File directory, file;
    Uri file_uri;
    Bitmap bitmap;
    String encoded_string;
    String mCurrentPhotoPath;
    Session session;


    public FileResolver() { }

    public FileResolver(Session s) {
        this.session = s;
    }

    public String getEncoded_string() {
        return encoded_string;
    }

    public void setEncoded_string(String encoded_string) {
        this.encoded_string = encoded_string;
    }

    public File getDirectory() {
        return directory;
    }

    public void setDirectory(File directory) {
        this.directory = directory;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public Uri getFile_uri() {
        return file_uri;
    }

    public void setFile_uri(Uri file_uri) {
        this.file_uri = file_uri;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public void setmCurrentPhotoPath(String path)
    {
        this.mCurrentPhotoPath = path;
    }

    public String getmCurrentPhotoPath() {

        return mCurrentPhotoPath;
    }

    public File createImageFile(Context context) throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName;
        if(context.getClass() == MyAccountActivity.class) {
            imageFileName = session.prefs.getString("user", "");
        }
        else
            imageFileName = "JPEG_" + timeStamp + "_";

        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    public void getFileUri(Context context, String imgName) {

        ContextWrapper cw = new ContextWrapper(context);
        directory = cw.getDir("FlorerExplorer", Context.MODE_PRIVATE);

        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = null;
        try {
             image = File.createTempFile(
                    imgName,  /* prefix */
                    ".jpg",         /* suffix */
                    storageDir      /* directory */
            );
        } catch (IOException e) {
            e.printStackTrace();
        }

       // file = new File(directory, imgName);
        mCurrentPhotoPath = image.getAbsolutePath();
       // file_uri = Uri.fromFile(file);
    }

    public String saveToInternalStorage(Context context, Bitmap bitmapImage){
        ContextWrapper cw = new ContextWrapper(context);
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath=new File(directory,"profile.jpg");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {

            try {

                fos.close();
            }
            catch (IOException e) {

                e.printStackTrace();
            }
        }

        return directory.getAbsolutePath();
    }


}

