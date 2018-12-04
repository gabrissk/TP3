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
    private HashMap<Long, HashSet<Long>> ixp_nets;

    public Server(int port, String ixp, String net_file, String netixlan_file) throws IOException, ParseException {
        this.port = port;
        this.net_file = net_file;
        this.netixlan_file = netixlan_file;
        this.ixp_nets = new HashMap<>();

        JSONObject jsonObject = readJson(ixp);
        JSONArray array = (JSONArray) jsonObject.get("data");
        ArrayList<Long> ixps = new ArrayList<>();
        for(Object obj: array) {
            JSONObject data = (JSONObject) obj;
            ixps.add((Long) data.get("id"));
        }
        System.out.println(ixps);
        this.ixp_json = jsonObject.toJSONString();

    }

    public static void main(String args[]) throws IOException, ParseException {

        Server server = new Server(Integer.parseInt(args[0]), args[1], args[2], args[3]);

        ServerSocket serverSocket = new ServerSocket(server.port);
        //serverSocket.setSoTimeout(10000);
        while (true) {
            System.out.println("Waiting for client in port " + serverSocket.getLocalPort() + "...");

            Socket socket = serverSocket.accept();
            System.out.println("Just connected to client");

            BufferedReader fromClient =
                    new BufferedReader(
                            new InputStreamReader(socket.getInputStream()));
            PrintWriter toClient =
                    new PrintWriter(socket.getOutputStream(), true);
            String line = fromClient.readLine();
            System.out.println("Server received: " + line);
            String[] tokens = line.split(" ");
            assert(tokens[0].equals("GET") && tokens[2].equals("HTTP/1.1"));
            if(tokens[1].equals("/api/ix"))
                toClient.println(server.ixp_json);
            else if(tokens[1].startsWith("/api/ixnets/")) {
                JSONObject j = readJson(server.netixlan_file);
                String response = getIxpNets(server, j, tokens[1]);
                System.out.println(response);
                toClient.println(response);


            }
            //toClient.flush();

        }

    }

    private static String getIxpNets(Server server, JSONObject j, String token) {
        String[] tokens = token.split("/");
        Long id = Long.parseLong(tokens[tokens.length-1]);
        //System.out.println("id:"+id);
        JSONArray array = (JSONArray) j.get("data");
        System.out.println(array);
        StringBuilder response = new StringBuilder("{\"data\":[");

        server.ixp_nets.put(id, new HashSet<>());

        //ArrayList<Long> regs = new ArrayList<>();
        for(Object obj: array) {
            JSONObject data = (JSONObject) obj;
            //System.out.println("ix: " + data.get("ix_id")+ " net: "+data.get("net_id"));
            if(((Long) data.get("ix_id")).equals(id)) {
                server.ixp_nets.get(id).add((Long) data.get("net_id"));
                response.append(",").append(data.get("net_id"));
            }
        }
        response.append("]}");
        System.out.println(server.ixp_nets.get(id));
        /*if(response.length() > 9)
            response.delete(response.length()-1, response.length());*/



        return "{\"data\":" + server.ixp_nets.get(id) + "}";
    }


    private static JSONObject readJson(String file) throws IOException, ParseException {
            JSONParser parser = new JSONParser();
            return (JSONObject) parser.parse(new FileReader(file));
        }

}
