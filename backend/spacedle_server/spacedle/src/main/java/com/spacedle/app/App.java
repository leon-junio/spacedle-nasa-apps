package com.spacedle.app;

import static spark.Spark.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;
import spark.Filter;
import spark.Request;
import spark.Response;
import com.spacedle.services.*;
import spark.Spark;

/*
 * @NASE SPACE APPS
 */
public class App {
    public static final int porta = 8080;
    private static boolean running = false;
    public static final int SOLAR = 0;
    public static final int GALATICO = 1;
    public static final int UNIVERSAL = 2;
    private static final HashMap<String, String> corsHeaders = new HashMap<String, String>();
    static {
        corsHeaders.put("Access-Control-Allow-Methods", "GET,PUT,POST,DELETE,OPTIONS");
        corsHeaders.put("Access-Control-Allow-Origin", "*");
        corsHeaders.put("Access-Control-Allow-Headers",
                "Content-Type,Authorization,X-Requested-With,Content-Length,Accept,Origin,");
        corsHeaders.put("Access-Control-Allow-Credentials", "true");
    }

    public final static void apply() {
        Filter filter = new Filter() {
            @Override
            public void handle(Request request, Response response) throws Exception {
                corsHeaders.forEach((key, value) -> {
                    response.header(key, value);
                });
            }
        };
        Spark.after(filter);
    }

    public static void main(String[] args) {
        try {
            ProcessBuilder process = new ProcessBuilder();
            System.out.println("Starting API SPACEDLE");
            SpacedleService.startHash();
            SpacedleService.startRank();
            running = true;
            int port = 0;
            if (process.environment().get("PORT") != null) {
                port = Integer.parseInt(process.environment().get("PORT"));
            } else {
                port = 6789;
            }
            port(port);
            staticFiles.location("/public");
            App.apply();
            System.out.println(porta + " aberta para conexões");
            dayService();
            get("/api/spacedle/:idle/:type", (request, response) -> SpacedleService.sendData(request, response));
            get("/api/names/:type", (request, response) -> SpacedleService.sendNames(request, response));
            get("/api/pics/:type/:nome", (request, response) -> SpacedleService.sendPicture(request, response));
            get("/api/continue/:last/:type",(request, response) -> SpacedleService.sendDataContinue(request, response));
            get("/api/global/rank/:type", (request, response) -> SpacedleService.getRankUser(request, response));
            post("/api/rank/:type/:nome", (request, response) -> SpacedleService.addRankUser(request, response));
        } catch (Exception e) {
            System.err.println("Falha inesperada internamente:\n" + e.getMessage());
            e.printStackTrace();
        }
    }

    // Serviço diário que disponibiliza as fotos e os dados de cada
    public static void dayService() {
        new Thread() {
            @Override
            public void run() {
                try {
                    boolean today = false;
                    String ultimoDia = Utils.getParsed(new Date()), diaAtual = Utils.getParsed(new Date());
                    setFilesDir("universal");
                    taskFotos(UNIVERSAL);
                    setFilesDir("solar");
                    taskFotos(SOLAR);
                    setFilesDir("galatico");
                    taskFotos(GALATICO);
                    while (running) {
                        sleep(60000);
                        diaAtual = Utils.getParsed(new Date());
                        if (today && !ultimoDia.equals(diaAtual)) {
                            today = false;
                        }
                        if (!today && !diaAtual.equals(ultimoDia)) {
                            SpacedleService.startRank();
                            System.out.println("JOIN renew the DAY");
                            today = true;
                            ultimoDia = diaAtual;
                            setFilesDir("universal");
                            taskFotos(UNIVERSAL);
                            setFilesDir("solar");
                            taskFotos(SOLAR);
                            setFilesDir("galatico");
                            taskFotos(GALATICO);
                        }
                    }
                } catch (IOException io) {
                    System.err.println("Erro de IO internamente\n" + io.getMessage());
                } catch (InterruptedException ie) {
                    System.err.println("Erro de Interrupted exception internamente\n" + ie.getMessage());
                } catch (Exception ie) {
                    System.err.println("Erro louco\n" + ie.getMessage());
                    ie.printStackTrace();
                }
            }
        }.start();
    }

    public static ArrayList<File> files;
    public static ArrayList<File> infos;

    public static boolean setFilesDir(String dir) {
        try {
            files = new ArrayList<>(Arrays.asList(new File("target/extras/resources/" + dir + "/pics/").listFiles()));
            infos = new ArrayList<>(Arrays.asList(new File("target/extras/resources/" + dir + "/desc/").listFiles()));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean taskFotos(int op) throws IOException {
        boolean status = false;
        try {
            try {
                if (files.isEmpty()) {
                    files = new ArrayList<>(Arrays.asList(new File("target/extras/resources/" + getType(op)).listFiles()));
                }
            } catch (NullPointerException nu) {
                System.out.println("Pasta vazia! Invalidando slicer!");
            }
            Random gen = new Random();
            int numRand = gen.nextInt(!files.isEmpty() ? files.size() - 1 : 0);
            File imagem = files.get(numRand);
            if (!imagem.isFile()) {
                throw new IOException("Imagem não localizada para a função diária");
            }
            files.remove(imagem);
            if (Utils.cropImage(imagem, "idle/" + getType(op) + "/")) {
                System.out.println("A imagem foi dividida com sucesso!");
            } else {
                throw new Exception("A imagem não foi dividida em partes internamente!");
            }
            BufferedReader buff = new BufferedReader(new FileReader(infos.get(numRand)));
            BufferedWriter buffwtt = new BufferedWriter(new FileWriter(new File("idle/" + getType(op) + "/desc.txt")));
            buffwtt.write(buff.readLine() + "\n");
            String read = "", desc = "";
            do {
                read = buff.readLine();
                if (read != null) {
                    desc += read;
                }
            } while (read != null);
            buffwtt.write(desc);
            buff.close();
            buffwtt.flush();
            buffwtt.close();
            status = true;
        } catch (Exception e) {
            System.err.println("Falha interna da task do sistema");
            e.printStackTrace();
            status = false;
        }
        return status;
    }

    public static String getType(int op) {
        switch (op) {
            case 0:
                return "solar";
            case 1:
                return "galatico";
            case 2:
                return "universal";
            default:
                return "Erro";
        }
    }

}
