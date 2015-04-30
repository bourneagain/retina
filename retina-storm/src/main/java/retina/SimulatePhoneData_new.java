package retina;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import scala.Int;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;


/**
 * Created by saikat on 3/25/15.
 */
public class SimulatePhoneData_new implements Runnable {
    static volatile int counter;
    int timeinteval;
    static String AppName = "app";
    static String AppID = "appid";
    static int numApps = 5;
    static String IMEI = "990000000000000";
    static String buildNum = "m.x.y.z";
    static String KernelVersion = "LRX22C";
    static String[] AppVersion = {"0.1", "0.2", "0.3", "0.4"};
    static String[] PhoneModel = {"Nexus4", "Nexus5", "SamsungGalaxy", "MotoX"};
    static String[] OSVersion = {"Lollypop", "Kitkat", "IceCreamSandwich"};
    static int numUsers = 100;

    GsonBuilder builder;
    Gson gson;
    String postUrl;// put in your url
    HttpPost post;

    StringEntity  postingString ;//new StringEntity();//convert your pojo to   json
    CloseableHttpClient httpClient = HttpClientBuilder.create().build();

    // totest
    SimulatePhoneData_new(int count, int timeinterval) throws IOException {
        this.counter = count;
        this.timeinteval = timeinterval;
        builder = new GsonBuilder();
        //postUrl = webServer;
        //post = new HttpPost(postUrl);
        gson = builder.create();
    }

    SimulatePhoneData_new(int count, int timeinterval, String webServer) throws IOException {
        this.counter = count;
        this.timeinteval = timeinterval;
        builder = new GsonBuilder();
        postUrl = webServer;
        post = new HttpPost(postUrl);
        gson = builder.create();
    }

    static String generateTimeStamp() {
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd:HH:mm:ss").format(new Date());
        String result = timeStamp.substring(0, 10) + "T" + timeStamp.substring(11) + 'Z';
        return result;
    }

    public void run() {

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Phone data simulator thread started!");
        int i = 0;
        String json;
        Random _rand = new Random();
        while(i <= counter) {
            try {
                Thread.sleep(this.timeinteval);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            ArrayList<PhoneData> pd;
            if (i == 0) {
                pd = generateAppMetaPhoneData(i);
            } else {
                if (_rand.nextInt(2) % 2 == 0) {
                    pd = generateHBLogs(i);
                } else {
                    pd = generateErrorPerApp(i);
                }
            }
            // can randomly pick indices from pd also if kafka is overloaded
            for (PhoneData p : pd) {
                json = gson.toJson(p);
                postString(json);
            }
            i++;
        }
    }

    void postString(String json) {
        if (post == null) return;
        try {
            postingString = new StringEntity(json);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        post.setEntity(postingString);
        post.setHeader("Content-type", "application/json");
        try {
            HttpResponse response = httpClient.execute(post);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static ArrayList<PhoneData> generateAppMetaPhoneData(int iter) {
        ArrayList<PhoneData> pd = new ArrayList<PhoneData>();
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();

        PrintWriter writer = null;
        try {
            writer = new PrintWriter("json-meta-"  + Integer.toString(iter) + ".txt", "UTF-8");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        long num = 0;
        for (int i = 0; i<numApps; i++) {//app name
            for (int j = 0; j < AppVersion.length; j++) {
                for (int k = 0; k < PhoneModel.length; k++) {
                    for (int l = 0; l < OSVersion.length; l++) {
                        for (int m = 0; m < numUsers; m++) {
                            int id_num = j * numApps + i;
                            pd.add(new PhoneData(generateTimeStamp(), "1", "M", IMEI + Integer.toString(m), AppID + Integer.toString(id_num),
                                    OSVersion[l] + " " + PhoneModel[k] + " " + buildNum + " " + KernelVersion + "\n"+
                                            AppID + Integer.toString(id_num) + " " + AppVersion[j] + " " + AppName + Integer.toString(i) + "\n"));
                            System.out.println("meta logs write to file entry num:" + num);
                            num++;
                            writer.println(gson.toJson(pd.get(pd.size() - 1)));
//                            try {
//                                Thread.sleep(1000);
//                            } catch (InterruptedException e) {
//                                e.printStackTrace();
//                            }
                        }
                    }
                }
            }
        }

        writer.close();
        return pd;
    }

    static ArrayList<PhoneData> generateHBLogs(int iter) {
        ArrayList<PhoneData> pd = new ArrayList<PhoneData>();
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        PrintWriter writer = null;
        try {
            writer = new PrintWriter("json-HB-" + Integer.toString(iter) + ".txt", "UTF-8");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        long num = 0;
        for (int i = 0; i<numApps; i++) {//app name
            for (int j = 0; j < AppVersion.length; j++) {
                for (int k = 0; k < PhoneModel.length; k++) {
                    for (int l = 0; l < OSVersion.length; l++) {
                        for (int m = 0; m < numUsers; m++) {
                            int id_num = j * numApps + i;
                            pd.add(new PhoneData(generateTimeStamp(), "1", "M", IMEI + Integer.toString(m), AppID + Integer.toString(id_num),
                                    OSVersion[l] + " " + PhoneModel[k] + " " + buildNum + " " + KernelVersion + "\n"+
                                            AppID + Integer.toString(id_num) + " " + AppVersion[j] + " " + AppName + "\n"));
                            System.out.println("hb logs write to file entry num:" + num);
                            num++;
                            writer.println(gson.toJson(pd.get(pd.size() - 1)));
//                            try {
//                                Thread.sleep(1000);
//                            } catch (InterruptedException e) {
//                                e.printStackTrace();
//                            }
                        }
                    }
                }
            }
        }
        writer.close();
        return pd;
    }

    static ArrayList<PhoneData> generateErrorPerApp(int iter) {
        ArrayList<PhoneData> pd = new ArrayList<PhoneData>();
        PrintWriter writer = null;
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        try {
            writer = new PrintWriter("json-error-" + Integer.toString(iter) + ".txt", "UTF-8");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String[] components = {"component1", "component2", "component3", "component4"};
        String[] errorlogs = {
                "Error:error print\nWarn: warn print\nClick:component1\n"
                        + "Crash:crash1\nError:error print1\n"
                        + "Crash:crash2\nError:error print2\n"
                        + "Crash:crash3\nError:error print3\n"
                        + "Click:component2\n",
                "Error:error print\nWarn: warn print\nClick:component1\n"
                        + "Crash:crash1\nError:error print1\n"
                        + "Crash:crash2\nError:error print2\n"
                        + "Crash:crash3\nError:error print3\n"
                        + "Click:component11\n",
                "Error:error print\nWarn: warn print\nClick:component1\n"
                        + "Crash:crash11\nError:error print1\n"
                        + "Crash:crash22\nError:error print2\n"
                        + "Crash:crash33\nError:error print3\n"
                        + "Click:component22\n",
                "Error:error print\nWarn: warn print\nClick:component1\n"
                        + "Crash:crash1\nError:error print12\n"
                        + "Crash:crash4\nError:error print10\n"
                        + "Click:component22\n"
        };

        long num = 0;
        Random _rand = new Random();
        for (int num_iterations = 0; num_iterations < 5; num_iterations++) {
            for (int i = 0; i < numApps; i++) {//app name
                for (int j = 0; j < AppVersion.length; j++) {
                    for (int m = 0; m < numUsers; m++) {
                        int id_num = j * numApps + i;
                        pd.add(new PhoneData(generateTimeStamp(), "1", "L", IMEI + Integer.toString(m), AppID + Integer.toString(id_num),
                                errorlogs[_rand.nextInt(errorlogs.length)]));
                        System.out.println("error logs write to file entry num:" + num);
                        num++;
                        writer.println(gson.toJson(pd.get(pd.size() - 1)));
                    }
                }
            }
        }
        writer.close();
        return pd;
    }

    public static void main(String[] args) throws IOException {

        SimulatePhoneData_new sd = null;

        try {
//            sd = new SimulatePhoneData_new(Integer.MAX_VALUE, 2000, args[0]);
            sd = new SimulatePhoneData_new(10 /*Integer.MAX_VALUE*/, 2000);
        } catch (IOException e) {
            System.out.println(" ENTER THE URL WHERE THE INGESTOR IS RUNNING [ with PORT 1234 ] ");
            e.printStackTrace();
        }

//        SimulatePhoneData sd = new SimulatePhoneData(Integer.MAX_VALUE, 2000, "http://localhost:1234");
        (new Thread(sd)).start();
//        generateAppMetaPhoneData();
//        generateHBLogs();
//        generateErrorPerApp();
    }
}
