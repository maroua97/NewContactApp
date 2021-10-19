package com.example.myihmapplication;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class StorageUtils {

    public static File createFile(File storageDir, String folderName) throws IOException {
        // Create a file name
        String fileName = createUniqueFileName();

        // Use an existing directory or create it if necessary
        File picturesDir = new File(storageDir, folderName);
        if (!(picturesDir.exists())) {
            picturesDir.mkdir();
        }

        // Create the name of the file with suffix .jpg
        File pathToFile = null;
        try {
            pathToFile = File.createTempFile(
                    fileName,
                    ".jpg",
                    picturesDir
            );
        } catch (IOException e) {
            e.printStackTrace();
        }

        return pathToFile;
    }

    private static String createUniqueFileName() throws IOException {
        // Create a unique file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fileName = "JPEG_" + timeStamp + "_";

        return fileName;
    }

    public static File createFileTxt(File storageDir, String folderName) throws IOException {
        // Create a file name
        String fileName = createUniqueFileNameTxt();

        // Use an existing directory or create it if necessary
        File picturesDir = new File(storageDir, folderName);
        if (!(picturesDir.exists())) {
            picturesDir.mkdir();
        }

        // Create the name of the file with suffix .txt
        File pathToFile = null;
        try {
            pathToFile = File.createTempFile(
                    fileName,
                    ".txt",
                    picturesDir
            );
        } catch (IOException e) {
            e.printStackTrace();
        }

        return pathToFile;
    }

    private static String createUniqueFileNameTxt() throws IOException {
        // Create a unique file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fileName = "Comment_JPEG_" + timeStamp + "_";

        return fileName;
    }
}
