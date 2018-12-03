import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {

    private int port;
    private String ixpJson;

    public Server(int port, String ixp, String net, String netixlan) throws IOException, ParseException {
        this.port = port;
        JSONParser parser = new JSONParser();
        JSONObject jsonObject = (JSONObject) parser.parse(new FileReader(ixp));
        JSONArray array = (JSONArray) jsonObject.get("data");
        ArrayList<Long> ixps = new ArrayList<>();
        for(Object obj: array) {
            JSONObject data = (JSONObject) obj;
            ixps.add((Long) data.get("id"));
        }
        System.out.println(ixps);
        this.ixpJson = jsonObject.toJSONString();
        /*System.out.println(jsonObject.toJSONString());
        System.out.println(array.toJSONString());*/

    }

    public static void main(String args[]) throws IOException, ParseException {

        Server server = new Server(Integer.parseInt(args[0]), args[1], args[2], args[3]);

        ServerSocket serverSocket = new ServerSocket(server.port);
        //serverSocket.setSoTimeout(10000);
        while (true) {
            System.out.println("Waiting for client on port " + serverSocket.getLocalPort() + "...");

            Socket socket = serverSocket.accept();
            System.out.println("Just connected to " + socket.getRemoteSocketAddress());

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
                toClient.println(server.ixpJson);
            else
                toClient.println(tokens[1] + " NOT IMPLEMENTED YET");
            //toClient.flush();

        }
    }
}
