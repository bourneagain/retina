package server;

/**
 * Created by sam on 4/25/15.
 */



import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLEncoder;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.TimeZone;

public class IngestorServer {

    public static final String HTTP_OK = "200 OK", HTTP_REDIRECT = "301 Moved Permanently",
            HTTP_FORBIDDEN = "403 Forbidden", HTTP_NOTFOUND = "404 Not Found",
            HTTP_BADREQUEST = "400 Bad Request", HTTP_INTERNALERROR = "500 Internal Server Error",
            HTTP_NOTIMPLEMENTED = "501 Not Implemented";

    public static final String MIME_PLAINTEXT = "text/plain", MIME_HTML = "text/html",
            MIME_DEFAULT_BINARY = "application/octet-stream";

    public IngestorServer(int port) throws IOException {
        myTcpPort = port;
        System.out.println("RETINA INJESTER STARTED .... ... .. ");
        final ServerSocket ss = new ServerSocket(myTcpPort);
        Thread t = new Thread(new Runnable() {
            public void run() {
                try {
                    while (true)
                        new HTTPSession(ss.accept());
                } catch (IOException ioe) {
                }
            }
        });
        t.setDaemon(true);
        t.start();
    }

    public class Response {
        public String status;
        public String mimeType;
        public InputStream data;
        public Properties header = new Properties();

        public Response() {
            this.status = HTTP_OK;
        }
        public Response(String status, String mimeType, InputStream data) {
            this.status = status;
            this.mimeType = mimeType;
            this.data = data;
        }

        public Response(String status, String mimeType, String txt) {
            this.status = status;
            this.mimeType = mimeType;
            this.data = new ByteArrayInputStream(txt.getBytes());
        }
        public void addHeader(String name, String value) {
            header.put(name, value);
        }


    }

    public static void main(String[] args) {

        int port = 1234;
        IngestorServer ris = null;
        try {
            ris = new IngestorServer(port);
        } catch (IOException ioe) {
            System.err.println("Couldn't start server:\n" + ioe);
            System.exit(-1);
        }

        try {
            System.in.read();
        } catch (Throwable t) {
        }
        ;
    }


    private class HTTPSession implements Runnable {
        public HTTPSession(Socket s) {
            mySocket = s;
            Thread t = new Thread(this);
            t.setDaemon(true);
            t.start();
        }

        public void run() {
            try {
                InputStream is = mySocket.getInputStream();
                if (is == null)
                    return;
                BufferedReader in = new BufferedReader(new InputStreamReader(is));

                // Read the request line
                StringTokenizer st = new StringTokenizer(in.readLine());
                if (!st.hasMoreTokens())
                    sendError(HTTP_BADREQUEST, "BAD REQUEST: Syntax error. Usage: GET /example/file.html");

                String method = st.nextToken();

                if (!st.hasMoreTokens())
                    sendError(HTTP_BADREQUEST, "BAD REQUEST: Missing URI. Usage: GET /example/file.html");

                String uri = decodePercent(st.nextToken());

                // Decode parameters from the URI
                Properties parms = new Properties();
                int qmi = uri.indexOf('?');
                if (qmi >= 0) {
                    decodeParms(uri.substring(qmi + 1), parms);
                    uri = decodePercent(uri.substring(0, qmi));
                }

                // If there's another token, it's protocol version,
                // followed by HTTP headers. Ignore version but parse headers.
                // NOTE: this now forces header names uppercase since they are
                // case insensitive and vary by client.
                Properties header = new Properties();
                if (st.hasMoreTokens()) {
                    String line = in.readLine();
                    while (line.trim().length() > 0) {
                        int p = line.indexOf(':');
                        header.put(line.substring(0, p).trim().toLowerCase(), line.substring(p + 1).trim());
                        line = in.readLine();
                    }
                }

                // If the method is POST, there may be parameters
                // in data section, too, read it:
                if (method.equalsIgnoreCase("POST")) {
                    long size = 0x7FFFFFFFFFFFFFFFl;
                    String contentLength = header.getProperty("content-length");
                    if (contentLength != null) {
                        try {
                            size = Integer.parseInt(contentLength);
                        } catch (NumberFormatException ex) {
                        }
                    }
                    String postLine = "";
                    char buf[] = new char[512];
                    int read = in.read(buf);
                    while (read >= 0 && size > 0 && !postLine.endsWith("\r\n")) {
//                        System.out.println("READ"+buf);
                        size -= read;
                        postLine += String.valueOf(buf, 0, read);
                        if (size > 0)
                            read = in.read(buf);
                    }
                    postLine = postLine.trim();
                    System.out.println(postLine);
//                    decodeParms(postLine, parms);
                    printJSONObject(postLine);
                }


//                Response r = serve(uri, method, header, parms);
                Response r = new Response(uri, method, "JSON DONE");
//                if (r == null)
//                    sendError(HTTP_INTERNALERROR, "SERVER INTERNAL ERROR: Serve() returned a null response.");
//                else
                    sendResponse(r.status, r.mimeType, r.header, r.data);
                in.close();
            } catch (IOException ioe) {
                try {
                    sendError(HTTP_INTERNALERROR, "SERVER INTERNAL ERROR: IOException: " + ioe.getMessage());
                } catch (Throwable t) {
                }
            } catch (InterruptedException ie) {
                // Thrown by sendError, ignore and exit the thread.
            }
        }

        private String decodePercent(String str) throws InterruptedException {
            try {
                StringBuffer sb = new StringBuffer();
                for (int i = 0; i < str.length(); i++) {
                    char c = str.charAt(i);
                    switch (c) {
                        case '+':
                            sb.append(' ');
                            break;
                        case '%':
                            sb.append((char) Integer.parseInt(str.substring(i + 1, i + 3), 16));
                            i += 2;
                            break;
                        default:
                            sb.append(c);
                            break;
                    }
                }
                return new String(sb.toString().getBytes());
            } catch (Exception e) {
                sendError(HTTP_BADREQUEST, "BAD REQUEST: Bad percent-encoding.");
                return null;
            }
        }


        // added by *sam*
        private void printJSONObject(String params){
            Gson gson ;
            GsonBuilder builder;
            builder = new GsonBuilder();
            gson = builder.create();
            String json;
            json = gson.toJson(params);
//            Gson gson = new Gson();
            JsonElement element = gson.fromJson (params, JsonElement.class);
            JsonObject jsonObj = element.getAsJsonObject();
        }
        private void decodeParms(String parms, Properties p) throws InterruptedException {
            if (parms == null)
                return;

            StringTokenizer st = new StringTokenizer(parms, "&");
            while (st.hasMoreTokens()) {
                String e = st.nextToken();
                int sep = e.indexOf('=');
                if (sep >= 0)
                    p.put(decodePercent(e.substring(0, sep)).trim(), decodePercent(e.substring(sep + 1)));
            }
        }

        /**
         * Returns an error message as a HTTP response and throws
         * InterruptedException to stop furhter request processing.
         */
        private void sendError(String status, String msg) throws InterruptedException {
            sendResponse(status, MIME_PLAINTEXT, null, new ByteArrayInputStream(msg.getBytes()));
            throw new InterruptedException();
        }

        /**
         * Sends given response to the socket.
         */
        private void sendResponse(String status, String mime, Properties header, InputStream data) {
            try {
                if (status == null)
                    throw new Error("sendResponse(): Status can't be null.");

                OutputStream out = mySocket.getOutputStream();
                PrintWriter pw = new PrintWriter(out);
                pw.print("HTTP/1.0 " + status + " \r\n");

                if (mime != null)
                    pw.print("Content-Type: " + mime + "\r\n");

                if (header == null || header.getProperty("Date") == null)
                    pw.print("Date: " + gmtFrmt.format(new Date()) + "\r\n");

                if (header != null) {
                    Enumeration e = header.keys();
                    while (e.hasMoreElements()) {
                        String key = (String) e.nextElement();
                        String value = header.getProperty(key);
                        pw.print(key + ": " + value + "\r\n");
                    }
                }

                pw.print("\r\n");
                pw.flush();

                if (data != null) {
                    byte[] buff = new byte[2048];
                    while (true) {
                        int read = data.read(buff, 0, 2048);
                        if (read <= 0)
                            break;
                        out.write(buff, 0, read);
                    }
                }
                out.flush();
                out.close();
                if (data != null)
                    data.close();
            } catch (IOException ioe) {
                // Couldn't write? No can do.
                try {
                    mySocket.close();
                } catch (Throwable t) {
                }
            }
        }

        private Socket mySocket;
    };
    private String encodeUri(String uri) {
        String newUri = "";
        StringTokenizer st = new StringTokenizer(uri, "/ ", true);
        while (st.hasMoreTokens()) {
            String tok = st.nextToken();
            if (tok.equals("/"))
                newUri += "/";
            else if (tok.equals(" "))
                newUri += "%20";
            else {
                newUri += URLEncoder.encode(tok);
                // For Java 1.4 you'll want to use this instead:
                // try { newUri += URLEncoder.encode( tok, "UTF-8" ); } catch (
                // UnsupportedEncodingException uee )
            }
        }
        return newUri;
    }

    private int myTcpPort;

    private static Hashtable theMimeTypes = new Hashtable();
    static {
        StringTokenizer st = new StringTokenizer("htm    text/html " + "html   text/html "
                + "txt    text/plain " + "asc    text/plain " + "gif    image/gif " + "jpg    image/jpeg "
                + "jpeg   image/jpeg " + "png    image/png " + "mp3    audio/mpeg "
                + "m3u    audio/mpeg-url " + "pdf    application/pdf " + "doc    application/msword "
                + "ogg    application/x-ogg " + "zip    application/octet-stream "
                + "exe    application/octet-stream " + "class    application/octet-stream ");
        while (st.hasMoreTokens())
            theMimeTypes.put(st.nextToken(), st.nextToken());
    }

    private static java.text.SimpleDateFormat gmtFrmt;
    static {
        gmtFrmt = new java.text.SimpleDateFormat("E, d MMM yyyy HH:mm:ss 'GMT'", Locale.US);
        gmtFrmt.setTimeZone(TimeZone.getTimeZone("GMT"));
    }

}



