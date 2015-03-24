package mygson;

/**
 * Created by saikat on 3/24/15.
 */
import java.text.SimpleDateFormat;
import java.util.*;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;
import kafka.javaapi.message.ByteBufferMessageSet;
import kafka.message.Message;
import kafka.producer.SyncProducerConfig;

public class KafkaProducer {
    
    static String generateTimeStamp() {
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd:HH:mm:ss").format(new Date());
        String result = timeStamp.substring(0, 10) + "T" + timeStamp.substring(11) + 'Z';
        return result;
    }
    
    static Integer eventID = 0;
    
    static String getEventID() {
        eventID++;
        return Integer.toString(eventID);
    }
    public static void main(String[] args) {
        Properties props = new Properties();
        String zk = "zk.connect";
        String dest = "127.0.0.1:2181";
        props.put(zk, dest);
        //props.put("metadata.broker.list", "broker1:9092,broker2:9092 ");
        props.put("metadata.broker.list", "localhost:9092");
        props.put("serializer.class", "kafka.serializer.StringEncoder");
        props.put("request.required.acks", "1");
        ProducerConfig config = new ProducerConfig(props);
        Producer<String, String> producer = new Producer<String, String>(config);
        
        PhoneMetadata pm = new PhoneMetadata("1234", "Nexus5", "M8974", "LRX22C");
        AppMetadata a = new AppMetadata("app1234", "1.0");
        Event e =  new Event("Click", "type1:3 type2:4");
        RetinaEvent re = new RetinaEvent(pm, a, e);
        re.timestamp = generateTimeStamp();
        re.eventid = getEventID();
        Random rnd = new Random();
        String ip = "192.168.2." + rnd.nextInt(255);
        //String msg = re.toJSON();
        //System.out.println("produce to kafka queue:" + msg);
        //KeyedMessage<String, String> data = new KeyedMessage<String, String>("test", ip, msg);
        //producer.send(data);
        
        ArrayList<KeyedMessage<String, String>> data_array = new ArrayList<KeyedMessage<String, String>>();
        
        for (int i = 0; i < 10; i++) {
            re.timestamp = generateTimeStamp();
            re.eventid = getEventID();
            String msg1 = re.toJSON();
            System.out.println("produce to kafka queue:" + msg1);
            data_array.add(new KeyedMessage<String, String>("test", ip, msg1));
        }
        producer.send(data_array);
    }
}
