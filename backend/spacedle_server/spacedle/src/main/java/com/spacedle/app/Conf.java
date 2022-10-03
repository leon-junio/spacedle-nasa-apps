package com.spacedle.app;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class Conf {
    String nomedoJar = "spacedle-1.0";
    String caminhoDoSeuJar = System.getProperty("user.dir") + System.getProperty("file.separator") + nomedoJar + ".jar";

    public ArrayList<File> getFile(String path) {
        try {
            ArrayList<String> ts = new ArrayList<>();
            ZipFile zipFile = new ZipFile(caminhoDoSeuJar);
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry zp = (ZipEntry) entries.nextElement();
                if (!zp.isDirectory() && zp.getName().contains(path)) {
                    System.out.println(zp.getName());

                    ts.add(zp.getName());
                }
            }
            ArrayList<File> list = new ArrayList<>();
            for (String name : ts) {
                list.add(isToFl(this.getClass().getClassLoader().getResourceAsStream("/" + name),
                        new File(name.replace(path, ""))));
            }
            zipFile.close();
            return list;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static File isToFl(InputStream inputStream, File file)
            throws IOException {

        // append = false
        try (FileOutputStream outputStream = new FileOutputStream(file, false)) {
            int read;
            byte[] bytes = new byte[8192];
            while ((read = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }
        }
        return file;
    }
}
