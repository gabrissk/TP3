import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class Client {




    public static void main(String args[]) throws IOException {

        Socket socket = new Socket("0.0.0.0", Integer.parseInt(args[0]));
        System.out.println("Just connected to " + socket.getRemoteSocketAddress());
        PrintWriter toServer = new PrintWriter(socket.getOutputStream(),true);
        BufferedReader fromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        toServer.println("GET /api/ix HTTP/1.1");
        String line = fromServer.readLine();
        System.out.println("Client received: " + line + " from Server");
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
}
