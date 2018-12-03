import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class Client {

    private ArrayList<Long> ixps;

    public Client() {
        this.ixps = new ArrayList<>();
    }

    public static void main(String args[]) throws IOException, ParseException {

        Client client = new Client();

        Socket socket = new Socket("0.0.0.0", Integer.parseInt(args[0]));
        System.out.println("Just connected to " + socket.getRemoteSocketAddress());
        PrintWriter toServer = new PrintWriter(socket.getOutputStream(),true);
        BufferedReader fromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        toServer.println("GET /api/ix HTTP/1.1");
        String line = fromServer.readLine();
        System.out.println("Client received: " + line + " from Server");

        readIXPS(client, line);

        System.out.println(client.ixps);

        //toServer.flush();

        socket = new Socket("0.0.0.0", Integer.parseInt(args[0]));
        toServer = new PrintWriter(socket.getOutputStream(),true);
        fromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        toServer.println("GET /api/ixnets/1 HTTP/1.1");
        //toServer.flush();
        line = fromServer.readLine();
        System.out.println("Client received: " + line + " from Server");

        toServer.close();
        fromServer.close();
        socket.close();
    }

    private static void readIXPS(Client client, String line) throws ParseException {
        JSONParser parser = new JSONParser();
        JSONObject jsonObject = (JSONObject) parser.parse(line);
        JSONArray array = (JSONArray) jsonObject.get("data");
        for(Object obj: array) {
            JSONObject data = (JSONObject) obj;
            client.ixps.add((Long) data.get("id"));
        }
    }
}
