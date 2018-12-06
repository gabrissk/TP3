package redes;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class Server {

    private int port;
    private String ixp_json;
    private String net_file;
    private String netixlan_file;
    private HashMap<Integer, HashSet<Integer>> ixp_nets;

    public Server(int port, String ixp, String net_file, String netixlan_file) throws IOException, ParseException {
        this.port = port;
        this.net_file = net_file;
        this.netixlan_file = netixlan_file;
        this.ixp_nets = new HashMap<>();

        JSONObject jsonObject = readJson(ixp);
        JSONArray array = (JSONArray) jsonObject.get("data");
        ArrayList<Integer> ixps = new ArrayList<>();
        for(Object obj: array) {
            JSONObject data = (JSONObject) obj;
            ixps.add(((Long)data.get("id")).intValue());
        }
        System.out.println(ixps);
        this.ixp_json = jsonObject.toJSONString();

    }

    public static void main(String args[]) throws IOException, ParseException {

        Server server = new Server(Integer.parseInt(args[0]), args[1], args[2], args[3]);

        ServerSocket serverSocket = new ServerSocket(server.port);
        while (true) {
            System.out.println("Waiting for client in port " + serverSocket.getLocalPort() + "...");
            Socket socket = serverSocket.accept();
            BufferedReader fromClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter toClient = new PrintWriter(socket.getOutputStream(), true);

            String line = fromClient.readLine();
            System.out.println("Server received: " + line);
            String[] tokens = line.split(" ");

            assert(tokens[0].equals("GET") && tokens[2].equals("HTTP/1.1"));

            if(tokens[1].equals("/api/ix"))
                toClient.println(server.ixp_json);

            else if(tokens[1].startsWith("/api/ixnets/")) {
                JSONObject j = readJson(server.netixlan_file);
                String response = getIxpNets(server, j, tokens[1]);
                toClient.println(response);
            }

            else if(tokens[1].startsWith("/api/netname/")) {
                JSONObject j = readJson(server.net_file);
                String response = getNetName(j, tokens[1]);
                toClient.println(response);
            }
        }
    }

    private static String getNetName(JSONObject j, String token) {
        String[] tokens = token.split("/");
        int id = Integer.parseInt(tokens[tokens.length-1]);
        JSONArray array = (JSONArray) j.get("data");

        String response = null;

        for(Object obj: array) {
            JSONObject data = (JSONObject) obj;
            if((((Long)data.get("id")).intValue()) == id ) {
                response = (String)data.get("name");
                break;
            }
        }
        return response;
    }

    private static String getIxpNets(Server server, JSONObject j, String token) {
        String[] tokens = token.split("/");
        int id = Integer.parseInt(tokens[tokens.length-1]);
        JSONArray array = (JSONArray) j.get("data");

        server.ixp_nets.put(id, new HashSet<>());

        for(Object obj: array) {
            JSONObject data = (JSONObject) obj;
            if((((Long)data.get("ix_id")).intValue()) == id ) {
                server.ixp_nets.get(id).add(((Long)data.get("net_id")).intValue());
            }
        }
        return "{\"data\":" + server.ixp_nets.get(id) + "}";
    }

    private static JSONObject readJson(String file) throws IOException, ParseException {
            JSONParser parser = new JSONParser();
            return (JSONObject) parser.parse(new FileReader(file));
        }

}
