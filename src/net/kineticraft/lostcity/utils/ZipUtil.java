package net.kineticraft.lostcity.utils;

import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;

import java.io.*;
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
     * Compress a directory to a zip file.
     * Adapted From: https://stackoverflow.com/questions/23318383/compress-directory-into-a-zipfile-with-commons-io
     * @param source
     * @param outputFile
     */
    @SneakyThrows
    public static void zip(File source, String outputFile) {
        ZipOutputStream zipFile = new ZipOutputStream(new FileOutputStream(outputFile));
        compressDirectory(source, source, zipFile);
        IOUtils.closeQuietly(zipFile);
    }

    @SneakyThrows
    private static void compressDirectory(File origin, File source, ZipOutputStream out) {
        for (File file : source.listFiles()) {
            String name = file.getPath().substring(origin.getPath().length() + 1);
            if (file.isDirectory()) {
                out.putNextEntry(new ZipEntry(name + "/"));
                compressDirectory(origin, file, out);
            } else {
                ZipEntry entry = new ZipEntry(name);
                out.putNextEntry(entry);
                FileInputStream in = new FileInputStream(file);
                IOUtils.copy(in, out);
                IOUtils.closeQuietly(in);
            }
        }
    }
}
