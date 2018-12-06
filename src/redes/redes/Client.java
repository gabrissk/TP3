/*By: Gabriel Morais
  2018
 */

package redes;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.TreeSet;

public class Client {

    // Lista com informacoes dos ixp's
    private ArrayList<IXP> ixps;
    // Lista com informacoes das redes
    private ArrayList<Net> nets;
    // Ip para conexao
    private String ip;
    // Numero de porto para conexao
    private int port;
    private TreeSet<Integer> netSet;

    private Client(String arg) {
        this.ixps = new ArrayList<>();
        this.nets = new ArrayList<>();
        this.ip = arg.split(":")[0];
        this.port = Integer.parseInt(arg.split(":")[1]);
        this.netSet = new TreeSet<>();
    }

    public static void main(String args[]) throws IOException, ParseException {

        Client client = new Client(args[0]);

        // Envia requisicao /api/ix (primeiro endpoint)
        String line = sendGET(client.ip, client.port, 0, -1);

        // Monta a lista dos ixp's
        readIXPS(client, line);

        // Analise 0
        if(Integer.parseInt(args[1]) == 0) {
            setNets(client);
            analyzeNets(client);
        }

        // Analise 1
        if(Integer.parseInt(args[1]) == 1) {
            analyzeIXP(client);
        }
    }

    private static void setNets(Client client) {
        for(int i: client.netSet) {
            Net net = new Net(i);
            client.nets.add(net);
        }
    }


    // Analise redes para determinar ixp's de cada uma
    private static void analyzeNets(Client client) throws IOException {
        for(Net net: client.nets) {
            for(IXP ixp: client.ixps) {
                if(ixp.getNets().contains(String.valueOf(net.getId()))) {
                    net.getIxps().add(ixp.getId());
                }
            }
            // Define nome da rede
            getNetName(net, client.ip, client.port);
            net.setIxp_count(net.getIxps().size());
            // Imprime informacoes da rede
            printOutput0(net);
        }
    }

    // Envia requisicao /api/netname/{net_id} para descobrir nome da rede
    private static void getNetName(Net net, String ip, int port) throws IOException {
        net.setName(sendGET(ip ,port, 2, net.getId()));
    }

    // Analisa os ixp's para determinar as redes de cada um
    private static void analyzeIXP(Client client) {
        for(IXP ixp : client.ixps) {
            if(ixp.getNets().get(0).equals("")) ixp.setNet_count(0);
            else ixp.setNet_count(ixp.getNets().size());
            // Imprime informacoes do ixp
            printOutput1(ixp);
        }
    }

    // Lê o json com os objetos dos ixp's para montar lista de ixp's
    private static void readIXPS(Client client, String line) throws ParseException, IOException {
        JSONParser parser = new JSONParser();
        JSONObject jsonObject = (JSONObject) parser.parse(line);
        JSONArray array = (JSONArray) jsonObject.get("data");
        for(Object obj: array) {
            JSONObject data = (JSONObject) obj;
            IXP ixp = new IXP(((Long)data.get("id")).intValue(), (String) data.get("name"));
            String response = sendGET(client.ip, client.port, 1, ixp.getId());
            processResponse(ixp, response, client.netSet);
            client.ixps.add(ixp);
        }
    }

    // Processa a resposta para quebrar a string em uma lista de redes
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

    // Envia uma requisicao HTTP tipo GET
    private static String sendGET(String ip, int port, int endpoint, int id) throws IOException {

        Socket socket = new Socket(ip, Integer.parseInt(String.valueOf(port)));
        PrintWriter toServer = new PrintWriter(socket.getOutputStream(),true);
        BufferedReader fromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        // api/ix
        if(endpoint == 0) {
            toServer.println("GET /api/ix HTTP/1.1");
        }

        //  /api/ixnets/{ix_id}
        else if(endpoint == 1) {
            toServer.println("GET /api/ixnets/" +id+ " HTTP/1.1");
        }

        // /api/netname/{net_id}
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
