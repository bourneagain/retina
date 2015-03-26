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

import java.text.SimpleDateFormat;
import java.util.*;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * A bolt that parses the tweet into words
 */
public class TimestampBolt extends BaseRichBolt
{
    OutputCollector collector;
    GsonBuilder builder;
    Gson gson;

    Integer eventId = 0;
    static String generateTimeStamp() {
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd:HH:mm:ss").format(new Date());
        String result = timeStamp.substring(0, 10) + "T" + timeStamp.substring(11) + 'Z';
        return result;
    }

    String generateEventID() {
        eventId++;
        return Integer.toString(eventId);
    }

    @Override
    public void prepare(
            Map                     map,
            TopologyContext         topologyContext,
            OutputCollector         outputCollector)
    {
        // save the output collector for emitting tuples
        collector = outputCollector;
        builder = new GsonBuilder();
        gson = builder.create();
    }


    @Override
    public void execute(Tuple tuple)
    {
        String phonejson = tuple.getString(0);
        //System.out.println("parser bolt: "+ phonejson);
        //String json = ParseString(rawlog);
        String currTime = generateTimeStamp();
        String op = currTime + " " + generateEventID() + " " + phonejson;
        System.out.println("Emitting TimestampBolt: "+ phonejson);
        collector.emit(new Values(op));
    }

    /*
    * <global timestamp assigned by spout> <event id by spout> <phone data json>
    * 2015-03-24T21:01:03Z 1 {"phonetimestamp":"03-12-2015","eventid":"1","eventtype":"M","phoneimei":"1234","appid":"app1","logs":"lollypop5.0.1 nexus5 m897 LRX22C\napp1 1.0\n"}
    * */

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer)
    {
        // tell storm the schema of the output tuple for this spout
        // tuple consists of a single column called 'tweet-word'
        declarer.declare(new Fields("event-timestamp-json"));
    }
}