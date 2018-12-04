import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Client {

    private ArrayList<IXP> ixps;
    private int port;

    public Client(int port) {
        this.ixps = new ArrayList<>();
        this.port = port;
    }

    public static void main(String args[]) throws IOException, ParseException {

        Client client = new Client(Integer.parseInt(args[0]));

        String line = sendGET(client.port, 0, null);
        System.out.println("Client received: " + line + " from Server");
        readIXPS(client, line);

        /*toServer.close();
        fromServer.close();
        socket.close();*/
    }

    private static void readIXPS(Client client, String line) throws ParseException, IOException {
        JSONParser parser = new JSONParser();
        JSONObject jsonObject = (JSONObject) parser.parse(line);
        JSONArray array = (JSONArray) jsonObject.get("data");
        for(Object obj: array) {
            JSONObject data = (JSONObject) obj;
            IXP ixp = new IXP((Long)data.get("id"), (String) data.get("name"));
            client.ixps.add(ixp);
            String response = sendGET(client.port, 1, ixp.getId());
            List<String> n = processResponse(client, response);
            System.out.println(ixp.getId()+ ": "+n+" size: "+ n.size());
        }
    }

    private static List<String> processResponse(Client client, String response) {
        StringBuilder nets = new StringBuilder(response);
        nets = nets.delete(0, 9).delete(nets.length()-2, nets.length());
        List<String> n = Arrays.asList(nets.toString().split(","));
        return n;
    }


    private static String sendGET(int port, int endpoint, Long id) throws IOException { // 0: api/ix | 1: api/ixnets{id} | 2: api/netname{id}

        Socket socket = new Socket("0.0.0.0", Integer.parseInt(String.valueOf(port)));
        System.out.println("Just connected to server");
        PrintWriter toServer = new PrintWriter(socket.getOutputStream(),true);
        BufferedReader fromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        if(endpoint == 0) {
            toServer.println("GET /api/ix HTTP/1.1");
        }

        else if(endpoint == 1) {
            toServer.println("GET /api/ixnets/" +id+ " HTTP/1.1");
        }

        return fromServer.readLine();
    }


}
