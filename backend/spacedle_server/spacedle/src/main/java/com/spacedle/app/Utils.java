package com.spacedle.app;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import org.apache.commons.io.FileUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.spacedle.model.Astro;
import com.spacedle.model.Rank;

import javax.imageio.ImageIO;

public class Utils {
    public static boolean cropImage(File img, String type) throws IOException, Exception {
        File dir = new File(type);
        if (!dir.isDirectory()) {
            dir.mkdirs();
        }
        for (File file : new ArrayList<>(Arrays.asList(dir.listFiles()))) {
            file.delete();
        }
        boolean status = false;
        FileInputStream fs = new FileInputStream(img);
        BufferedImage image = ImageIO.read(fs);
        int rows = 5;
        int columns = 5;
        BufferedImage imgs[] = new BufferedImage[25];
        int subimage_Width = image.getWidth() / columns;
        int subimage_Height = image.getHeight() / rows;
        int current_img = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                imgs[current_img] = new BufferedImage(subimage_Width, subimage_Height, image.getType());
                Graphics2D img_creator = imgs[current_img].createGraphics();
                int src_first_x = subimage_Width * j;
                int src_first_y = subimage_Height * i;
                int dst_corner_x = subimage_Width * j + subimage_Width;
                int dst_corner_y = subimage_Height * i + subimage_Height;
                img_creator.drawImage(image, 0, 0, subimage_Width, subimage_Height, src_first_x, src_first_y,
                        dst_corner_x, dst_corner_y, null);
                current_img++;
            }
        }
        for (int i = 0; i < 25; i++) {
            File outputFile = new File(type + "puzzle_" + (i + 1) + ".jpg");
            ImageIO.write(imgs[i], "jpg", outputFile);
        }
        System.out.println("Sub-imagens foram criadas com sucesso.");
        status = true;
        return status;
    }

    public static String getBase64(File img) {
        try {
            byte[] fileContent = FileUtils.readFileToByteArray(img);
            String encodedString = Base64.getEncoder().encodeToString(fileContent);
            return encodedString;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String stringToJson(Astro astro) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        // gson to json using object
        return gson.toJson(astro);
    }

    public static String stringToJson(File img) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        // gson to json using object
        return gson.toJson(getBase64(img));
    }

    public static String stringToJson(String names[]) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        // gson to json using object
        return gson.toJson(names);
    }

    public static String stringToJson(ArrayList<Rank> list) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        // gson to json using object
        return gson.toJson(list);
    }

    private static SimpleDateFormat sfd = new SimpleDateFormat("dd-MM-yyyy");

    public static String getParsed(java.util.Date d) throws Exception {
        return sfd.format(d);
    }
}
