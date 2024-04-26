import java.io.*;
import java.net.*;

public class JavaClient {
    public static void main(String[] args) {
        // Check if all required command line arguments are provided
        if (args.length != 3) {
            System.err.println("Usage: java SimpleHTTPClient <server_address> <port> <path>");
            return;
        }

        String serverAddress = args[0];
        int port = Integer.parseInt(args[1]);
        String path = args[2];

        try {
            // Create a TCP socket to connect to the server
            Socket socket = new Socket(serverAddress, port);

            // Create output stream to send data to server
            OutputStream outToServer = socket.getOutputStream();
            PrintWriter requestWriter = new PrintWriter(outToServer);

            // Send HTTP GET request
            requestWriter.println("GET " + path + " HTTP/1.1");
            requestWriter.println("Host: " + serverAddress);
            requestWriter.println();
            requestWriter.flush();

            // Create input stream to read data from server
            InputStream inFromServer = socket.getInputStream();
            BufferedReader responseReader = new BufferedReader(new InputStreamReader(inFromServer));

            // Read and display the server response
            String line;
            while ((line = responseReader.readLine()) != null) {
                System.out.println(line);
            }

            // Close the streams and socket
            responseReader.close();
            requestWriter.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
