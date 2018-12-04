import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

public class Client {

    private ArrayList<IXP> ixps;
    private int port;

    private Client(int port) {
        this.ixps = new ArrayList<>();
        this.port = port;
    }

    public static void main(String args[]) throws IOException, ParseException {

        Client client = new Client(Integer.parseInt(args[0]));

        if(Integer.parseInt(args[1]) == 1) {
            String line = sendGET(client.port, 0, -1);
            //System.out.println("Client received: " + line + " from Server");
            readIXPS(client, line);

            analyzeIXP(client);

        }

        /*toServer.close();
        fromServer.close();
        socket.close();*/
    }

    private static void analyzeIXP(Client client) {
        int c =0; int s = 0;
        for(IXP ixp : client.ixps) {
            if(ixp.getNets().get(0).equals("")) ixp.setNet_count(0);
            else ixp.setNet_count(ixp.getNets().size());
            //System.out.println(ixp.getId()+ ": "+ixp.getName()+" "+ ixp.getNets()+ " "+ixp.getNet_count());
            printOutput(ixp);
            c++;
            s+= ixp.getNet_count();
        }
        System.out.println("count: "+c+ " sum: "+s);
    }

    private static void readIXPS(Client client, String line) throws ParseException, IOException {
        JSONParser parser = new JSONParser();
        JSONObject jsonObject = (JSONObject) parser.parse(line);
        JSONArray array = (JSONArray) jsonObject.get("data");
        for(Object obj: array) {
            JSONObject data = (JSONObject) obj;
            IXP ixp = new IXP(((Long)data.get("id")).intValue(), (String) data.get("name"));
            String response = sendGET(client.port, 1, ixp.getId());
            processResponse(ixp, response);
            client.ixps.add(ixp);
        }
    }

    private static void processResponse(IXP ixp, String response) {
        StringBuilder nets = new StringBuilder(response);
        nets = nets.delete(0, 9).delete(nets.length()-2, nets.length());
        ixp.setNets(Arrays.asList(nets.toString().split(",")));
    }


    private static String sendGET(int port, int endpoint, int id) throws IOException { // 0: api/ix | 1: api/ixnets{id} | 2: api/netname{id}

        Socket socket = new Socket("0.0.0.0", Integer.parseInt(String.valueOf(port)));
        //System.out.println("Just connected to server");
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

    private static void printOutput(IXP ixp) {
        System.out.println(ixp.getId()+"\t"+ixp.getName()+"\t"+ixp.getNet_count());
    }


}
