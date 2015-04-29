package retina;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.*;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;


/**
 * Created by saikat on 3/25/15.
 */
public class SimulatePhoneData implements Runnable {
    int counter;
    int timeinteval;
    GsonBuilder builder;
    Gson gson;
    String postUrl;// put in your url
    HttpPost post;

    StringEntity  postingString ;//new StringEntity();//convert your pojo to   json
    CloseableHttpClient httpClient = HttpClientBuilder.create().build();


//    Properties props;
//    Producer<String, String> producer;
//    ProducerConfig config;
//    String topic = "phone-data-test";
//    String key = "mykey";
//    String zk = "zk.connect";
//    String dest = "127.0.0.1:2181";

    SimulatePhoneData(int count, int timeinterval, String webServer) throws IOException {
        this.counter = count;
        this.timeinteval = timeinterval;
        builder = new GsonBuilder();
        postUrl = webServer;
        post = new HttpPost(postUrl);

        gson = builder.create();
//        props = new Properties();
//        props.put(zk, dest);
//        props.put("metadata.broker.list", "localhost:9092");
//        props.put("serializer.class", "kafka.serializer.StringEncoder");
//        props.put("request.required.acks", "1");
//        config = new ProducerConfig(props);
//        producer = new Producer<String, String>(config);


    }

    String generateTimeStamp() {
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

        // same phone same app but different versions
        PhoneData p_M = new PhoneData(generateTimeStamp(), "1", "M", "990000862471854", "app1",
                "lollypop5.0.1 nexus5 m123 LRX22C\napp1 1.0 angybirds\n");

//        PhoneData p_M2 = new PhoneData(generateTimeStamp(), "1", "M", "990000862471854", "app1",
//                "lollypop5.0.1 nexus5 m123 LRX22C\napp1 2.0 angrybirds\n");
//
//        PhoneData p_M3 = new PhoneData(generateTimeStamp(), "1", "M", "990000862471854", "app1",
//                "kitKat5.0.1 nexus5 m123 LRX22C\napp1 3.0 angrybirds\n");
//
//        PhoneData p_M3 = new PhoneData(generateTimeStamp(), "1", "M", "990000862471854", "app1",
//                "kitKat5.0.1 nexus5 m123 LRX22C\napp2 7.0 subwaysurfers\n");
//
//        PhoneData p_M4 = new PhoneData(generateTimeStamp(), "1", "M", "990000862471854", "app1",
//                "kitKat5.0.1 nexus5 m123 LRX22C\napp1 2.0 subwaysurfers\n");
//
//        // different phone same app
//
//        PhoneData p_M6 = new PhoneData(generateTimeStamp(), "1", "M", "351756051523999", "app1",
//                "lollypop5.0.1 nexus4 m123 LRX22C\napp1 1.0 appname\n");
//
//        PhoneData p_M7 = new PhoneData(generateTimeStamp(), "1", "M", "351756051523999", "app1",
//                "lollypop5.0.1 nexus4 m123 LRX22C\napp1 4.0 appname\n");
//
//
//        PhoneData p_M8 = new PhoneData(generateTimeStamp(), "1", "M", "990000862470001", "app1",
//                "lollypop5.0.1 nexus4 m123123 LRX22C\napp3 4.0 gmail\n");
//
//
//        PhoneData p_M9 = new PhoneData(generateTimeStamp(), "1", "M", "990000862470002", "app1",
//                "lollypop5.0.1 SamsungGalxay5 m123 LRX000C\napp1 4.0 angybirds\n");
//
//
//        PhoneData p_M10 = new PhoneData(generateTimeStamp(), "1", "M", "99000086247555", "app1",
//                "kitKat5.0.1 samsungGalaxy4 m245 LGALAXY2C\napp1 1.0 appname\n");
//
//        PhoneData p_M11 = new PhoneData(generateTimeStamp(), "1", "M", "99000086777777", "app1",
//                "lollypop5.0.1 MotoG m990 LMOTO99C\napp1 1.0 appname\n");


        //if event type is metadata: there will be two lines in the log field, phone metadata and app metadata
//        phonemetadata - <phoneversion> <phonemodel> <phonebaseband> <phonebuild>
  //      appmetadata - <appid> <appversion> <appname>

        PhoneData p_H = new PhoneData(generateTimeStamp(), "1", "H", "1234", "app1",
                "lollypop5.0.1 nexus5 m897 LRX22C\napp1 1.0\n");


        PhoneData p_L = new PhoneData(generateTimeStamp(), "1", "L", "1234", "app1",
                "Error:error print\nWarn: warn print\nClick:component1\n"
                        + "Crash:crash1\nError:error print1\n"
                        + "Crash:crash2\nError:error print2\n"
                        + "Crash:crash3\nError:error print3\n"
                        + "Click:component2\n");

        PhoneData p_L1 = new PhoneData(generateTimeStamp(), "2", "L", "1237", "app2",
                "Error:error print\nWarn: warn print\nClick:component1\n"
                        + "Crash:crash1\nError:error print12\n"
                        + "Crash:crash4\nError:error print10\n"
                        + "Click:component22\n");

        PhoneData p_L2 = new PhoneData(generateTimeStamp(), "3", "L", "2222", "app3",
                "Error:error print\nWarn: warn print\nClick:component1\n"
                        + "Crash:crash11\nError:error print1\n"
                        + "Crash:crash22\nError:error print2\n"
                        + "Crash:crash33\nError:error print3\n"
                        + "Click:component22\n");

        PhoneData p_L3 = new PhoneData(generateTimeStamp(), "4", "L", "1111", "app3",
                "Error:error print\nWarn: warn print\nClick:component1\n"
                        + "Crash:crash1\nError:error print1\n"
                        + "Crash:crash2\nError:error print2\n"
                        + "Crash:crash3\nError:error print3\n"
                        + "Click:component11\n");


        ArrayList<PhoneData> phoneDataArrayList = new ArrayList<PhoneData>(5);
        phoneDataArrayList.add(p_L);
        phoneDataArrayList.add(p_L2);
        phoneDataArrayList.add(p_L3);


        int i = 0;
//        i = counter;
        String json;
        Random _rand = new Random();
        while(i <= counter) {
            try {
                Thread.sleep(this.timeinteval);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (i == 0) {
                json = gson.toJson(p_M);
            } else {
                if (_rand.nextInt(2) % 2 == 0) {
                    json = gson.toJson(p_H);
                } else {
                    //json = gson.toJson(p_L);
                    json = gson.toJson(phoneDataArrayList.get(_rand.nextInt(3)));
                }
            }
            try {
                postingString = new StringEntity(json);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            };
            post.setEntity(postingString);
            post.setHeader("Content-type", "application/json");
            try {
                HttpResponse  response = httpClient.execute(post);
            } catch (IOException e) {
                e.printStackTrace();
            }

//            KeyedMessage<String, String> data = new KeyedMessage<String, String>(topic, key, json);
           // System.out.println("Simulator dumping json:" + json);
//            producer.send(data);
            i++;
        }
    }
    public static void main(String[] args) throws IOException {

        SimulatePhoneData sd = null;
        try {
            sd = new SimulatePhoneData(Integer.MAX_VALUE, 2000, args[0]);
        } catch (IOException e) {
            System.out.println(" ENTER THE URL WHERE THE INGESTOR IS RUNNING [ with PORT 1234 ] ");
            e.printStackTrace();
        }
//        SimulatePhoneData sd = new SimulatePhoneData(Integer.MAX_VALUE, 2000, "http://localhost:1234");
        (new Thread(sd)).start();

    }
}
