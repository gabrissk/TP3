package redes;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.Socket;
import java.util.*;

public class Client {

    private ArrayList<IXP> ixps;
    private ArrayList<Net> nets;
    private int port;
    private TreeSet<Integer> netSet;

    private Client(int port) {
        this.ixps = new ArrayList<>();
        this.nets = new ArrayList<>();
        this.port = port;
        this.netSet = new TreeSet<>();
    }

    public static void main(String args[]) throws IOException, ParseException {

        Client client = new Client(Integer.parseInt(args[0]));

        String line = sendGET(client.port, 0, -1);
        readIXPS(client, line);

        if(Integer.parseInt(args[1]) == 0) {
            setNets(client);
            analyzeNets(client);
        }

        if(Integer.parseInt(args[1]) == 1) {
            analyzeIXP(client);
        }
        System.out.println(client.netSet+ " size: " + client.netSet.size());

    }

    private static void setNets(Client client) {
        for(int i: client.netSet) {
            Net net = new Net(i);
            client.nets.add(net);
        }
    }

    private static void analyzeNets(Client client) throws IOException {
        int c=0; int s=0;
        for(Net net: client.nets) {
            for(IXP ixp: client.ixps) {
                if(ixp.getNets().contains(String.valueOf(net.getId()))) {
                    net.getIxps().add(ixp.getId());
                }
            }
            getNetName(net, client.port);
            net.setIxp_count(net.getIxps().size());
            printOutput0(net);
        }
    }

    private static void getNetName(Net net, int port) throws IOException {
        net.setName(sendGET(port, 2, net.getId()));
    }

    private static void analyzeIXP(Client client) {
        int c =0; int s = 0;
        for(IXP ixp : client.ixps) {
            if(ixp.getNets().get(0).equals("")) ixp.setNet_count(0);
            else ixp.setNet_count(ixp.getNets().size());
            printOutput1(ixp);
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
            processResponse(ixp, response, client.netSet);
            client.ixps.add(ixp);
        }
    }

    private static void processResponse(IXP ixp, String response, TreeSet<Integer> set) {
        StringBuilder nets = new StringBuilder(response);
        nets = nets.delete(0, 9).delete(nets.length()-2, nets.length());
        ixp.setNets(Arrays.asList(nets.toString().split(", ")));
        addNet(ixp, set);

    }

    private static void addNet(IXP ixp, TreeSet<Integer> set) {
        if(!ixp.getNets().get(0).equals(""))
            ixp.getNets().forEach(p -> set.add(Integer.valueOf(p)));
    }


    private static String sendGET(int port, int endpoint, int id) throws IOException { // 0: api/ix | 1: api/ixnets{id} | 2: api/netname{id}

        Socket socket = new Socket("0.0.0.0", Integer.parseInt(String.valueOf(port)));
        PrintWriter toServer = new PrintWriter(socket.getOutputStream(),true);
        BufferedReader fromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        if(endpoint == 0) {
            toServer.println("GET /api/ix HTTP/1.1");
        }

        else if(endpoint == 1) {
            toServer.println("GET /api/ixnets/" +id+ " HTTP/1.1");
        }

        else if(endpoint == 2) {
            toServer.println("GET /api/netname/" +id+ " HTTP/1.1");
        }

        return fromServer.readLine();
    }

    private static void printOutput0(Net net) {
        System.out.println(net.getId()+"\t"+net.getName()+"\t"+net.getIxp_count());
    }

    private static void printOutput1(IXP ixp) {
        System.out.println(ixp.getId()+"\t"+ixp.getName()+"\t"+ixp.getNet_count());
    }

}
