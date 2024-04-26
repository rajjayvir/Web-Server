import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.StringTokenizer;

public class WebServer {
    public static void main(String[] args) throws IOException {
        // Set the port number
        int port = 8080;

        // Establish the listen socket
        ServerSocket welcomeSocket = new ServerSocket( port );
        System.out.println( "Server started on port " + port );

        // Process HTTP service requests in an infinite loop
        while (true) {
            // Listen for a TCP connection request
            Socket connectionSocket = welcomeSocket.accept();
            // Establish the connection
            System.out.println( "Connection established from " + connectionSocket.getInetAddress() );

            // Get a reference to the socket's input and output streams
            BufferedReader inFromClient = new BufferedReader( new InputStreamReader( connectionSocket.getInputStream() ) );
            DataOutputStream outToClient = new DataOutputStream( connectionSocket.getOutputStream() );

            // Read the HTTP request message
            String requestMessageLine = inFromClient.readLine();
            // Print the request message for debugging purposes
            System.out.println( "Request: " + requestMessageLine );

            // Extract the filename from the request line
            StringTokenizer tokens = new StringTokenizer( requestMessageLine );
            tokens.nextToken(); // Skip the HTTP method (GET)
            String fileName = tokens.nextToken(); // Get the filename

            // Prepend a "." so that file request is within the current directory
            fileName = "." + fileName;

            // Open the requested file
            FileInputStream fileInputStream = null;
            boolean fileExists = true;
            try {
                fileInputStream = new FileInputStream( fileName );
            } catch ( FileNotFoundException e ) {
                fileExists = false;
            }

            // Construct the response message
            String statusLine;
            String contentTypeLine;
            String entityBody;
            if (fileExists) {
                statusLine = "HTTP/1.1 200 OK";
                contentTypeLine = "Content-type: " + contentType( fileName ) + "\r\n";
                entityBody = "";
            } else {
                statusLine = "HTTP/1.1 404 Not Found";
                contentTypeLine = "Content-type: text/html\r\n";
                entityBody = "<html><head></head><body>404 Not Found</body></html>";
            }

            // Send the status line and content type line into the socket
            outToClient.writeBytes( statusLine + "\r\n" );
            outToClient.writeBytes( contentTypeLine + "\r\n" );

            // Send the content of the requested file to the client
            if (fileExists) {
                sendBytes( fileInputStream, outToClient );
                fileInputStream.close();
            } else {
                outToClient.writeBytes( entityBody );
            }

            // Close streams and socket
            outToClient.close();
            inFromClient.close();
            connectionSocket.close();
        }
    }

    private static void sendBytes(FileInputStream fileInputStream, OutputStream outToClient) throws IOException {
        byte[] buffer = new byte[1024];
        int bytes;
        while ((bytes = fileInputStream.read( buffer )) != -1) {
            outToClient.write( buffer, 0, bytes );
        }
    }

    private static String contentType(String fileName) {
        if (fileName.endsWith( ".htm" ) || fileName.endsWith( ".html" )) {
            return "text/html";
        }
        if (fileName.endsWith( ".gif" )) {
            return "image/gif";
        }
        if (fileName.endsWith( ".jpeg" ) || fileName.endsWith( ".jpg" )) {
            return "image/jpeg";
        }
        if (fileName.endsWith( ".png" )) {
            return "image/png";
        }
        return "application/octet-stream";
    }
}
