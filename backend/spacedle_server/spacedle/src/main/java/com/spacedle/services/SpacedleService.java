package com.spacedle.services;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import com.spacedle.app.App;
import com.spacedle.app.Conf;
import com.spacedle.app.Utils;
import com.spacedle.model.Astro;
import com.spacedle.model.Rank;

import spark.Request;
import spark.Response;

public class SpacedleService {
    private static HashMap<Integer, int[]> hashZada = new HashMap<>();
    private static ArrayList<File> resp = new ArrayList<>();
    public static ArrayList<Rank> rankSolar = new ArrayList<>();
    public static ArrayList<Rank> rankGalatico = new ArrayList<>();
    public static ArrayList<Rank> rankUniverso = new ArrayList<>();

    public static void startRank() {
        rankGalatico.clear();
        rankSolar.clear();
        rankUniverso.clear();
    }

    public static void startHash() {
        int[] FT = { 1 };
        int[] SC = { 2, 7, 6 };
        int[] TH = { 3, 8, 11, 12, 13, 14 };
        int[] FO = { 16, 17, 18, 19, 10, 9, 4 };
        int[] FI = { 5, 10, 15, 20, 25, 24, 23, 22, 21 };
        hashZada.put(0, FT);
        hashZada.put(1, SC);
        hashZada.put(2, TH);
        hashZada.put(3, FO);
        hashZada.put(4, FI);
    }

    public static Object sendNames(Request request, Response response) {
        Conf c = new Conf();
        try {
            int type = Integer.parseInt(request.params(":type"));
            ArrayList<File> files = new ArrayList<>(
                    Arrays.asList(new File("target/extras/resources/" + App.getType(type) + "/desc").listFiles()));
            if (files.isEmpty()) {
                throw new Exception("Lista esta vazia para esse parametro");
            }
            String[] names = new String[files.size()];
            int i = 0;
            BufferedReader buff;
            for (File fl : files) {
                buff = new BufferedReader(new InputStreamReader(new FileInputStream(fl), "UTF-8"));
                names[i] = buff.readLine();
                buff.close();
                i++;
            }
            response.header("Content-Type", "Json; charset=utf-8;");
            response.status(200);
            return Utils.stringToJson(names);
        } catch (Exception e) {
            e.printStackTrace();
            response.status(203);
            return null;
        }
    }

    public static Object sendPicture(Request request, Response response) {
        try {
            Conf c = new Conf();
            int type = Integer.parseInt(request.params(":type"));
            String nome = request.params(":nome");
            ArrayList<File> files = new ArrayList<>(
                    Arrays.asList(new File("target/extras/resources/" + App.getType(type) + "/pics").listFiles()));
            File imagem = null;
            for (File fl : files) {
                if (fl.getName().substring(0, fl.getName().indexOf('.')).equals(nome)) {
                    imagem = fl;
                    break;
                }
            }
            if (imagem == null || !imagem.isFile()) {
                throw new Exception("File not found infelizmente");
            }
            response.header("Content-Type", "Json; charset=utf-8;");
            response.status(200);
            return Utils.stringToJson(imagem);
        } catch (Exception e) {
            e.printStackTrace();
            response.status(203);
            return null;
        }
    }

    public static Object sendData(Request request, Response response) {
        try {
            int idlePicture = Integer.parseInt(request.params(":idle"));
            int type = Integer.parseInt(request.params(":type"));
            resp.clear();
            for (Integer i : hashZada.get(idlePicture)) {
                resp.add(new File("idle/" + App.getType(type) + "/puzzle_" + i + ".jpg"));
            }
            BufferedReader buff = new BufferedReader(new InputStreamReader(
                    new FileInputStream(new File("idle/" + App.getType(type) + "/desc.txt")), "UTF-8"));
            Astro astro = new Astro();
            astro.setNome(buff.readLine());
            String read = "", desc = "";
            do {
                read = buff.readLine();
                if (read != null) {
                    desc += read;
                }
            } while (read != null);
            astro.setDescricao(desc);
            astro.setImgLen(resp.size());
            for (File fl : resp) {
                astro.setImgByFl(resp.indexOf(fl), Integer.parseInt(
                        fl.getName().substring(0, fl.getName().indexOf(".")).replace("puzzle_", "")), fl);
            }
            buff.close();
            response.header("Content-Type", "Json; charset=utf-8;");
            response.status(200);
            return Utils.stringToJson(astro);
        } catch (Exception e) {
            e.printStackTrace();
            response.status(203);
            return null;
        }
    }

    public static Object sendDataContinue(Request request, Response response) {
        try {
            int last = Integer.parseInt(request.params(":last"));
            int type = Integer.parseInt(request.params(":type"));
            resp.clear();
            for (int idle = last; idle >= 0; idle--) {
                for (Integer i : hashZada.get(idle)) {
                    resp.add(new File("idle/" + App.getType(type) + "/puzzle_" + i + ".jpg"));
                }
            }
            BufferedReader buff = new BufferedReader(new InputStreamReader(
                    new FileInputStream(new File("idle/" + App.getType(type) + "/desc.txt")), "UTF-8"));
            Astro astro = new Astro();
            astro.setNome(buff.readLine());
            String read = "", desc = "";
            do {
                read = buff.readLine();
                if (read != null) {
                    desc += read;
                }
            } while (read != null);
            astro.setDescricao(desc);
            astro.setImgLen(resp.size());
            for (File fl : resp) {
                astro.setImgByFl(resp.indexOf(fl), Integer.parseInt(
                        fl.getName().substring(0, fl.getName().indexOf(".")).replace("puzzle_", "")), fl);
            }
            buff.close();
            response.header("Content-Type", "Json; charset=utf-8;");
            response.status(200);
            return Utils.stringToJson(astro);
        } catch (Exception e) {
            e.printStackTrace();
            response.status(203);
            return null;
        }
    }

    public static Object getRankUser(Request request, Response response) {
        try {
            int type = Integer.parseInt(request.params(":type"));
            String json = "";
            if (type == 0) {
                json = Utils.stringToJson(rankSolar);
            } else if (type == 1) {
                json = Utils.stringToJson(rankGalatico);
            } else if (type == 2) {
                json = Utils.stringToJson(rankUniverso);
            }
            response.header("Content-Type", "Json; charset=utf-8;");
            response.status(200);
            return json;
        } catch (Exception e) {
            e.printStackTrace();
            response.status(203);
            return null;
        }
    }

    public static Object addRankUser(Request request, Response response) {
        try {
            int type = Integer.parseInt(request.params(":type"));
            String nome = java.net.URLDecoder.decode(request.params(":nome"), StandardCharsets.UTF_8.name());
            Rank ra = new Rank();
            ra.setNome(nome);
            if (type == 0) {
                ra.setLugar(rankSolar.size() + 1);
                rankSolar.add(ra);
            } else if (type == 1) {
                ra.setLugar(rankGalatico.size() + 1);
                rankGalatico.add(ra);
            } else if (type == 2) {
                ra.setLugar(rankUniverso.size() + 1);
                rankUniverso.add(ra);
            }
            response.header("Content-Type", "Json; charset=utf-8;");
            response.status(200);
            return response.status();
        } catch (Exception e) {
            e.printStackTrace();
            response.status(203);
            return null;
        }
    }
}
