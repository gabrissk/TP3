import java.io.*;
import java.net.Socket;

public class Client {




    public static void main(String args[]) throws IOException {

        Socket socket = new Socket("0.0.0.0", Integer.parseInt(args[0]));

        BufferedWriter out = new BufferedWriter(
                new OutputStreamWriter(socket.getOutputStream(), "UTF8"));
        BufferedReader in = new BufferedReader(
                new InputStreamReader(socket.getInputStream()));

        out.write("blabla");
        out.flush();

    }
}
