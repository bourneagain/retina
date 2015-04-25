package server;
/*
 * HTTPPOSTServer.java
 * Author: S.Prasanna
 * @version 1.00 
*/

import java.io.*;
import java.net.*;
import java.util.*;

public class HTTPPOSTServer extends Thread {

    static final String HTML_START =
            "<html>" +
                    "<title>HTTP POST Server in java</title>" +
                    "<body>";

    static final String HTML_END =
            "</body>" +
                    "</html>";

    Socket connectedClient = null;
    BufferedReader inFromClient = null;
    DataOutputStream outToClient = null;


    public HTTPPOSTServer(Socket client) {
        connectedClient = client;
    }

    public void run() {

        String currentLine = null, postBoundary = null, contentength = null, filename = null, contentLength = null;
        PrintWriter fout = null;

        try {

            System.out.println( "The Client "+
                    connectedClient.getInetAddress() + ":" + connectedClient.getPort() + " is connected");

            inFromClient = new BufferedReader(new InputStreamReader (connectedClient.getInputStream()));
            outToClient = new DataOutputStream(connectedClient.getOutputStream());

            currentLine = inFromClient.readLine();
            String headerLine = currentLine;
            StringTokenizer tokenizer = new StringTokenizer(headerLine);
            String httpMethod = tokenizer.nextToken();
            String httpQueryString = tokenizer.nextToken();

            System.out.println(currentLine);

            if (httpMethod.equals("GET")) {
                System.out.println("GET request");
                if (httpQueryString.equals("/")) {
                    // The default home page
                    String responseString = HTTPPOSTServer.HTML_START +
                            "<form action=\"http://127.0.0.1:5000\" enctype=\"multipart/form-data\"" +
                            "method=\"post\">" +
                            "Enter the name of the File <input name=\"file\" type=\"file\"><br>" +
                            "<input value=\"Upload\" type=\"submit\"></form>" +
                            "Upload only text files." +
                            HTTPPOSTServer.HTML_END;
                    sendResponse(200, responseString , false);
                } else {
                    sendResponse(404, "<b>The Requested resource not found ...." +
                            "Usage: http://127.0.0.1:5000</b>", false);
                }
            }
            else { //POST request
                System.out.println("POST request");
                while (true){
                    currentLine = inFromClient.readLine();
                    System.out.println(currentLine);
                }
//                while((currentLine = inFromClient.readLine()) != null) {
////                    currentLine = inFromClient.readLine();
//                    System.out.println(currentLine);
//                    if (currentLine.indexOf("Content-Type:application/json") != -1) {
//                        while((currentLine = inFromClient.readLine()) != null) {
//                            System.out.println("START READING JSON"+ currentLine);
//                        }
//                    }
//                }

            } // else

        } catch (Exception e) {
            try {
                sendResponse(200, "DONE ", false);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        }
    }

    public void sendResponse (int statusCode, String responseString, boolean isFile) throws Exception {

        String statusLine = null;
        String serverdetails = "Server: Java HTTPServer";
        String contentLengthLine = null;
        String fileName = null;
        String contentTypeLine = "Content-Type: text/html" + "\r\n";
        FileInputStream fin = null;

        if (statusCode == 200)
            statusLine = "HTTP/1.1 200 OK" + "\r\n";
        else
            statusLine = "HTTP/1.1 404 Not Found" + "\r\n";

        if (isFile) {
            fileName = responseString;
            fin = new FileInputStream(fileName);
            contentLengthLine = "Content-Length: " + Integer.toString(fin.available()) + "\r\n";
            if (!fileName.endsWith(".htm") && !fileName.endsWith(".html"))
                contentTypeLine = "Content-Type: \r\n";
        }
        else {
            responseString = HTTPPOSTServer.HTML_START + responseString + HTTPPOSTServer.HTML_END;
            contentLengthLine = "Content-Length: " + responseString.length() + "\r\n";
        }

        outToClient.writeBytes(statusLine);
        outToClient.writeBytes(serverdetails);
        outToClient.writeBytes(contentTypeLine);
        outToClient.writeBytes(contentLengthLine);
        outToClient.writeBytes("Connection: close\r\n");
        outToClient.writeBytes("\r\n");

        if (isFile) sendFile(fin, outToClient);
        else outToClient.writeBytes(responseString);

        outToClient.close();
    }

    public void sendFile (FileInputStream fin, DataOutputStream out) throws Exception {
        byte[] buffer = new byte[1024] ;
        int bytesRead;

        while ((bytesRead = fin.read(buffer)) != -1 ) {
            out.write(buffer, 0, bytesRead);
        }
        fin.close();
    }

    public static void main (String args[]) throws Exception {

        ServerSocket Server = new ServerSocket (5000, 10, InetAddress.getByName("127.0.0.1"));
        System.out.println ("HTTP Server Waiting for client on port 5000");

        while(true) {
            System.out.println("WAITING");
            Socket connected = Server.accept();
            (new HTTPPOSTServer(connected)).start();
        }
    }
}
