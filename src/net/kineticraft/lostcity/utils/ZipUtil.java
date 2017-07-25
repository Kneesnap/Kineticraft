package net.kineticraft.lostcity.utils;

import lombok.SneakyThrows;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.*;

/**
 * Utilities for data compression.
 * Created by Kneesnap on 7/24/2017.
 */
public class ZipUtil {

    /**
     * Decompress a string using GZip.
     * @param data
     * @return decompressed
     */
    @SneakyThrows
    public static String decompress(String data) {
        ByteArrayInputStream bais = new ByteArrayInputStream(data.getBytes());
        BufferedReader in = new BufferedReader(new InputStreamReader(new GZIPInputStream(bais), "UTF-8"));
        StringWriter sw = new StringWriter();
        for (int i = 0; i < bais.available(); i++)
            sw.write(in.read());
        return sw.toString();
    }

    /**
     * Compress data using GZip.
     * @param data
     * @return compressed
     */
    @SneakyThrows
    public static String compress(String data) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        GZIPOutputStream gzip = new GZIPOutputStream(out);
        gzip.write(data.getBytes("UTF-8"));
        gzip.close();
        return out.toString("UTF-8");
    }

    /**
     * Unzip a zip file to a given directory.
     * @param zipFile
     * @param destDirectory
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    @SneakyThrows
    public static void unzip(File zipFile, String destDirectory) {
        File destDir = new File(destDirectory);
        if (!destDir.exists())
            destDir.mkdir();

        ZipInputStream zipIn = new ZipInputStream(new FileInputStream(zipFile));
        ZipEntry entry;
        while ((entry = zipIn.getNextEntry()) != null) {
            String filePath = destDirectory + File.separator + entry.getName();
            if (entry.isDirectory()) {
                new File(filePath).mkdir(); // Create the directory.
            } else {
                extractFile(zipIn, filePath); // Extract the fie.
            }
            zipIn.closeEntry();
        }
        zipIn.close();
    }

    /**
     * Extracts a zip entry (file entry)
     * @param zipIn
     * @param filePath
     * @throws IOException
     */
    private static void extractFile(ZipInputStream zipIn, String filePath) throws IOException {
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));
        byte[] bytesIn = new byte[4096];
        int read;
        while ((read = zipIn.read(bytesIn)) != -1)
            bos.write(bytesIn, 0, read);
        bos.close();
    }

    /**
     * Compress a file or directory.
     * @param file
     * @param outputName
     */
    @SneakyThrows
    public static void zip(File file, String outputName) {
        if (file.isDirectory()) {
            zipDirectory(file, outputName);
            return;
        }

        //create ZipOutputStream to write to the zip file
        FileOutputStream fos = new FileOutputStream(outputName);
        ZipOutputStream zos = new ZipOutputStream(fos);
        //add a new Zip Entry to the ZipOutputStream
        ZipEntry ze = new ZipEntry(file.getName());
        zos.putNextEntry(ze);
        //read the file and write to ZipOutputStream
        FileInputStream fis = new FileInputStream(file);
        byte[] buffer = new byte[1024];
        int len;
        while ((len = fis.read(buffer)) > 0)
            zos.write(buffer, 0, len);

        //Close the zip entry to write to zip file
        zos.closeEntry();
    }

    /**
     * Compress the contents of a directory to a zip file.
     * @param dir
     * @param zipDirName
     */
    @SneakyThrows
    private static void zipDirectory(File dir, String zipDirName) {
        List<String> files = new ArrayList<>();
        populateFilesList(dir, files);

        FileOutputStream fos = new FileOutputStream(zipDirName);
        ZipOutputStream zos = new ZipOutputStream(fos);
        for(String filePath : files){ // Zip files one by one.
            //for ZipEntry we need to keep only relative file path, so we used substring on absolute path
            ZipEntry ze = new ZipEntry(filePath.substring(dir.getAbsolutePath().length()+1, filePath.length()));
            zos.putNextEntry(ze);
            //read the file and write to ZipOutputStream
            FileInputStream fis = new FileInputStream(filePath);
            byte[] buffer = new byte[1024];
            int len;
            while ((len = fis.read(buffer)) > 0)
                zos.write(buffer, 0, len);
            zos.closeEntry();
        }
    }

    /**
     * This method populates all the files in a directory to a List
     * @param dir
     * @throws IOException
     */
    @SuppressWarnings("ConstantConditions")
    private static void populateFilesList(File dir, List<String> paths) throws IOException {
        for(File file : dir.listFiles()){
            if(file.isFile()){
                paths.add(file.getAbsolutePath());
            } else {
                populateFilesList(file, paths);
            }
        }
    }
}
