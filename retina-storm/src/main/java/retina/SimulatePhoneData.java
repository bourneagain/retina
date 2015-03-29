package retina;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;
import java.util.Random;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

/**
 * Created by saikat on 3/25/15.
 */
public class SimulatePhoneData implements Runnable {
    int counter;
    int timeinteval;
    GsonBuilder builder;
    Gson gson;
    Properties props;
    Producer<String, String> producer;
    ProducerConfig config;
    String topic = "phone-data-test";
    String key = "mykey";
    String zk = "zk.connect";
    String dest = "127.0.0.1:2181";

    SimulatePhoneData(int count, int timeinterval) {
        this.counter = count;
        this.timeinteval = timeinterval;
        builder = new GsonBuilder();

        gson = builder.create();
        props = new Properties();
        props.put(zk, dest);
        props.put("metadata.broker.list", "localhost:9092");
        props.put("serializer.class", "kafka.serializer.StringEncoder");
        props.put("request.required.acks", "1");
        config = new ProducerConfig(props);
        producer = new Producer<String, String>(config);
    }

    String generateTimeStamp() {
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd:HH:mm:ss").format(new Date());
        String result = timeStamp.substring(0, 10) + "T" + timeStamp.substring(11) + 'Z';
        return result;
    }

    public void run() {
        System.out.println("Phone data simulator thread started!");
        PhoneData p_M = new PhoneData(generateTimeStamp(), "1", "M", "1234", "app1",
                "lollypop5.0.1 nexus5 m897 LRX22C\nappid 1.0 appname\n");

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
            KeyedMessage<String, String> data = new KeyedMessage<String, String>(topic, key, json);
            System.out.println("Simulator dumping json:" + json);
            producer.send(data);
            i++;
        }
    }
}
