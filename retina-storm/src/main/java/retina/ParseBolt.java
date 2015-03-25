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

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Map;
import java.util.Arrays;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * A bolt that parses the tweet into words
 */
public class ParseBolt extends BaseRichBolt
{
    // To output tuples from this bolt to the count bolt
    OutputCollector collector;
    //private String[] skipWords = {"http://", "https://", "(", "a", "an", "the", "for", "retweet", "RETWEET", "follow", "FOLLOW"};
    GsonBuilder builder;
    Gson gson;
    AppMetadata parseAppMetaData(String log) {
        String delims = ":";
        String[] tokens = log.split(delims);
        // for now we have fixed fields, and order of fields
        AppMetadata a = new AppMetadata(tokens[1], tokens[2]);
        return a;
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
        String rawlog = tuple.getString(0);
        String json = ParseString(rawlog);
        System.out.println("parser-bolt-Emitting->" + json);
        //System.out.println("parser bolt: "+ rawlog);

        collector.emit(new Values(json));
        //collector.emit(new Values(rawlog));
    }

    /*
    * <global timestamp assigned by spout> <event id by spout> <phone data json>
    * 2015-03-24T21:01:03Z 1 {"phonetimestamp":"03-12-2015","eventid":"1","eventtype":"M","phoneimei":"1234","appid":"app1","logs":"lollypop5.0.1 nexus5 m897 LRX22C\napp1 1.0\n"}
    * */
    static String ParseString(String log) {
        int index = log.indexOf("{");
        String[] tokens = log.substring(0, index).split(" ");
        String timestamp = tokens[0];
        String geventId = tokens[1];
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        PhoneData pd = gson.fromJson(log.substring(index), PhoneData.class);
        return parsePhoneData(pd, timestamp, geventId);
    }

    static String parsePhoneData(PhoneData pd, String timestamp, String geventid) {
        Hashtable<String, String> ht =  new Hashtable<String, String>();
        Hashtable<String, Integer> ht_click =  new Hashtable<String, Integer>();
        String opstr = "[{}]";
        Event e  =  new Event();
        e.appId = pd.appid;
        e.phoneId = pd.phoneimei;
        e.eventType = pd.eventtype;

        RetinaEvent re;

        if (pd.eventtype.equalsIgnoreCase("H")) {
            // ignore log
            re =  new RetinaEvent(e);
            re.timestamp = timestamp;
            re.eventid = geventid;
            opstr = re.toJSON();
        } else if (pd.eventtype.equalsIgnoreCase("M")) {
            String[] tokens = pd.logs.split("\n");
            // first line is phone meta
            // second line is app meta
            String[] phonemeta = tokens[0].split(" ");
            // string format : <phoneversion> <phonemodel> <phonebaseband> <phonebuild>
            PhoneMetaData pmdata =  new PhoneMetaData(pd.phoneimei,
                    phonemeta[0], // version
                    phonemeta[1], //model
                    phonemeta[2], // baseband
                    phonemeta[3]);//build
            String[] appmeta = tokens[1].split(" ");
            // <id>, <version>
            AppMetadata amdata = new AppMetadata(appmeta[0], appmeta[1]);
            re  = new RetinaEvent(pmdata, amdata, e);
            re.timestamp = timestamp;
            re.eventid = geventid;
            opstr = re.toJSON();
        } else if (pd.eventtype.equalsIgnoreCase("L")) {
            String[] tokens = pd.logs.split("\n");
            String temp;
            for (String token : tokens) {
                if (token.contains("Error")) {
                    // append the raw logs to the appropriate key-value
                    if (ht.get("Error") ==  null) {
                        ht.put("Error", token);
                    } else {
                        ht.put("Error", ht.get("Error") + "\n" + token);
                    }
                } else if ( token.contains("Warn")) {
                    if (ht.get("Warn") ==  null) {
                        ht.put("Warn", token);
                    } else {
                        ht.put("Warn", ht.get("Warn") + "\n" + token);
                    }
                } else if (token.contains("Click")) {
                    // log of the form "Click:<key>"
                    String[] line = token.split(":");
                    Integer n = ht_click.get(line[1]);
                    if (n == null) {
                        ht_click.put(line[1], 1);
                    } else {
                        ht_click.put(line[1], n + 1);
                    }
                }
            }

            Enumeration<String> htclickKey = ht_click.keys();
            String clickstr = "";
            while(htclickKey.hasMoreElements()) {
                String key = htclickKey.nextElement();
                String val = Integer.toString(ht_click.get(key));
                clickstr += key + ":" + val + " ";
            }

            String errorstr = (ht.get("Error") != null) ? ht.get("Error") : "";
            String warnstr = (ht.get("Warn") != null) ? ht.get("Warn") : "";
            String crashstr = (ht.get("Crash") != null) ? ht.get("Crash") : "";
            e.eventCrash = crashstr;
            e.eventError = errorstr;
            e.eventWarn = warnstr;
            e.eventClick = clickstr;
            re = new RetinaEvent(e);
            re.timestamp = timestamp;
            re.eventid = geventid;
            opstr = re.toJSON();
        }

        return opstr;
    }
    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer)
    {
        // tell storm the schema of the output tuple for this spout
        // tuple consists of a single column called 'tweet-word'
        declarer.declare(new Fields("event-json"));
    }
}