/*By: Gabriel Morais
  2018
 */


package redes;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;

public class Server {

    // Numero do porto para conexao
    private int port;
    // json com os ids dos ixp's
    private String ixp_json;
    // Arquivos .json
    private String net_file;
    private String netixlan_file;
    // Lista com as redes de cada ixp
    private HashMap<Integer, HashSet<Integer>> ixp_nets;

    public Server(int port, String net_file, String ixp, String netixlan_file) throws IOException, ParseException {
        this.port = port;
        this.net_file = net_file;
        this.netixlan_file = netixlan_file;
        this.ixp_nets = new HashMap<>();

        // Processa o json e descobre os ids dos ixp's
        JSONObject jsonObject = readJson(ixp);
        this.ixp_json = jsonObject.toJSONString();

    }

    public static void main(String args[]) throws IOException, ParseException {

        Server server = new Server(Integer.parseInt(args[0]), args[1], args[2], args[3]);
        ServerSocket serverSocket = new ServerSocket(server.port, 50, InetAddress.getByName("0.0.0.0"));
        while (true) {
            Socket socket = serverSocket.accept();
            BufferedReader fromClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter toClient = new PrintWriter(socket.getOutputStream(), true);

            String line = fromClient.readLine();
            String[] tokens = line.split(" ");

            // Valida o cabeçalho HTTP
            assert(tokens[0].equals("GET") && tokens[2].equals("HTTP/1.1"));

            // Endpoint /api/ix -> responde com todos ipx's
            if(tokens[1].equals("/api/ix"))
                toClient.println(server.ixp_json);

            // Endpoint /api/ixnets/{ix_id} -> responde com as redes do ixp identificado por ix_id
            else if(tokens[1].startsWith("/api/ixnets/")) {
                JSONObject j = readJson(server.netixlan_file);
                String response = getIxpNets(server, j, tokens[1]);
                toClient.println(response);
            }

            // Endpoint /api/netname/{net_id} -> responde com o nome da rede identificada por net_id
            else if(tokens[1].startsWith("/api/netname/")) {
                JSONObject j = readJson(server.net_file);
                String response = getNetName(j, tokens[1]);
                toClient.println(response);
            }
        }
    }

    // Retorna o nome da rede identificada por um id
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

    // Define uma lista de redes para cada ixp
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

    // Lê um arquivo .json
    private static JSONObject readJson(String file) throws IOException, ParseException {
            JSONParser parser = new JSONParser();
            return (JSONObject) parser.parse(new FileReader(file));
        }

}
