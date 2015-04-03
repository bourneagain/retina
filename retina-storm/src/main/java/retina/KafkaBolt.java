package retina;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.testing.TestWordSpout;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;
import backtype.storm.utils.Utils;

import java.util.Map;
import java.util.Arrays;

import java.text.SimpleDateFormat;
import java.util.*;

//import com.sun.tools.doclets.formats.html.SourceToHTMLConverter;
import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;
import kafka.javaapi.message.ByteBufferMessageSet;
import kafka.message.Message;
import kafka.producer.SyncProducerConfig;
/**
 * A bolt that parses the tweet into words
 */
public class KafkaBolt extends BaseRichBolt
{
    // To output tuples from this bolt to the count bolt
    OutputCollector collector;
    // replace this with topic name that druid is expecting
    String topic = "test";
    String key = "mykey";
    String zk = "zk.connect";
    String dest = "127.0.0.1:2181";
    Properties props;
    Producer<String, String> producer;
    ProducerConfig config;
    //private String[] skipWords = {"http://", "https://", "(", "a", "an", "the", "for", "retweet", "RETWEET", "follow", "FOLLOW"};
    @Override
    public void prepare(
            Map                     map,
            TopologyContext         topologyContext,
            OutputCollector         outputCollector)
    {
        // save the output collector for emitting tuples
        collector = outputCollector;
        props = new Properties();
        props.put(zk, dest);
        props.put("metadata.broker.list", "localhost:9092");
        props.put("serializer.class", "kafka.serializer.StringEncoder");
        props.put("request.required.acks", "1");
        config = new ProducerConfig(props);
        producer = new Producer<String, String>(config);
    }

    public void produceJSONtoKafkaQ(String json) {
        KeyedMessage<String, String> data = new KeyedMessage<String, String>(topic, key, json);
        System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n*****************  Dumping json to druid over kafka :" + topic +" : *****" + json);
        producer.send(data);
    }

    @Override
    public void execute(Tuple tuple) {
        String json = tuple.getString(0);
        System.out.println("kafkabolt received:" + json);
        produceJSONtoKafkaQ(json);
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        // this bolt doesn't need to emit
    }
    public static void main(String[] args){

    }
}