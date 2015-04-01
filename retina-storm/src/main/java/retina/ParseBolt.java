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
import java.util.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;



/**
 * A bolt that parses the tweet into words
 */
public class ParseBolt extends BaseRichBolt
{
    OutputCollector collector;
    GsonBuilder builder;
    Gson gson;
    HashMap<String, ArrayList<String>> appDetailsMap = new HashMap<String, ArrayList<String>>();
    HashMap<String, ArrayList<String>> phoneDetailsMap = new HashMap<String, ArrayList<String>>();

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
        System.out.println("parser bolt: "+ rawlog);
        String json = ParseString(rawlog);
        System.out.println("Emitting ParseBolt:" + json);
        if (json == null || json.equals("[{}]") ) {
            System.out.printf(" NULL JSON OUT OF ORDER MESSAGE READ");
        } else {
            System.out.println("Emitting ParseBolt:" + json);
            collector.emit(new Values(json));
        }
    }


    public ArrayList<String> returnMapDetails(HashMap<String, ArrayList<String> > mapType, String key){
        ArrayList<String> result = new ArrayList<String>();
        for ( String el : mapType.get(key)) {
            result.add(el);
        }
        return result;

    }
    /*
    * <global timestamp assigned by spout> <event id by spout> <phone data json>
    * 2015-03-24T21:01:03Z 1 {"phonetimestamp":"03-12-2015","eventid":"1","eventtype":"M","phoneimei":"1234","appid":"app1","logs":"lollypop5.0.1 nexus5 m897 LRX22C\napp1 1.0\n"}
    * */
    public String ParseString(String log) {
        int index = log.indexOf("{");
        String[] tokens = log.substring(0, index).split(" ");
        String timestamp = tokens[0];
        String geventId = tokens[1];
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        PhoneData pd = gson.fromJson(log.substring(index), PhoneData.class);
        return parsePhoneData(pd, timestamp, geventId);

    }

    public String parsePhoneData(PhoneData pd, String timestamp, String geventid) {
        Hashtable<String, String> ht =  new Hashtable<String, String>();
        Hashtable<String, Integer> ht_click =  new Hashtable<String, Integer>();
        Integer globalCrashCount = 0;
        Integer globalErrorCount = 0;
        Integer globalWarnCount = 0;

        String opstr = "[{}]";
        Event e  =  new Event();
        e.appId = pd.appid;
        e.phoneId = pd.phoneimei;
        e.eventType = pd.eventtype;

        RetinaEvent re;

        if (pd.eventtype.equalsIgnoreCase("H")) {
            ArrayList<String> appDetails = new ArrayList<String>();
            ArrayList<String> phoneDetails = new ArrayList<String>();

            if (phoneDetailsMap.containsKey(pd.phoneimei) && appDetailsMap.containsKey(pd.appid)) {
                phoneDetails = returnMapDetails(phoneDetailsMap, pd.phoneimei);
                appDetails = returnMapDetails(appDetailsMap, pd.appid);

                AppMetadata am = new AppMetadata(pd.appid, appDetails.get(0), appDetails.get(1));
                PhoneMetaData pm = new PhoneMetaData(pd.phoneimei, phoneDetails.get(0), phoneDetails.get(1), phoneDetails.get(2), phoneDetails.get(3));

                re = new RetinaEvent(pm, am, e);
                re.timestamp = timestamp;
                re.eventid = geventid;
                opstr = re.toJSON();
            } else {
                opstr = null;
            }
        } else if (pd.eventtype.equalsIgnoreCase("M")) {
            String[] tokens = pd.logs.split("\n");
            String[] phonemeta = tokens[0].split(" ");

            ArrayList<String> tempPhoneData = new ArrayList<String>(4);
            tempPhoneData.add(phonemeta[0]);
            tempPhoneData.add(phonemeta[1]);
            tempPhoneData.add(phonemeta[2]);
            tempPhoneData.add(phonemeta[3]);
            phoneDetailsMap.put(pd.phoneimei, tempPhoneData);
            PhoneMetaData pmdata =  new PhoneMetaData(pd.phoneimei,
                    phonemeta[0], // version
                    phonemeta[1], //model
                    phonemeta[2], // baseband
                    phonemeta[3]);//build


            String[] appmeta = tokens[1].split(" ");
            ArrayList<String> tempAppData = new ArrayList<String>(2);
            tempAppData.add(appmeta[1]);
            tempAppData.add(appmeta[2]);
            appDetailsMap.put(appmeta[0], tempAppData);
            AppMetadata amdata = new AppMetadata(appmeta[0], appmeta[1], appmeta[2]);


            re  = new RetinaEvent(pmdata, amdata, e);
            re.timestamp = timestamp;
            re.eventid = geventid;
            opstr = re.toJSON();

        } else if (pd.eventtype.equalsIgnoreCase("L")) {
            AppMetadata am;
            PhoneMetaData pm;
            //{"phonetimestamp":"2015-03-30T21:07:03Z","eventid":"3","eventtype":"L","phoneimei":"2222","appid":"app3","logs":"Error:error print\nWarn: warn print\nClick:component1\nCrash:crash11\nError:error print1\nCrash:crash22\nError:error print2\nCrash:crash33\nError:error print3\nClick:component22\n"}

            if ( phoneDetailsMap.containsKey(pd.phoneimei) && appDetailsMap.containsKey(pd.appid) ) {
                ArrayList<String> phoneDetails = returnMapDetails(phoneDetailsMap, pd.phoneimei);
                ArrayList<String> appDetails = returnMapDetails(appDetailsMap, pd.appid);
                am = new AppMetadata(pd.appid, appDetails.get(0), appDetails.get(1));
                pm = new PhoneMetaData(pd.phoneimei, phoneDetails.get(0), phoneDetails.get(1), phoneDetails.get(2), phoneDetails.get(3));
            } else {
                System.out.println("WARN: NO APP AND PHONE MAP FOUND! ");
                am = new AppMetadata(pd.appid, "", "");
                pm = new PhoneMetaData(pd.phoneimei, "","","","");
            }

            String[] tokens = pd.logs.split("\n");
            String temp;
            for (String token : tokens) {
                if (token.contains("Crash")) {
                    globalCrashCount++;
                    if (ht.get("Crash") ==  null) {
                        ht.put("Crash", token);
                    } else {
                        ht.put("Crash", ht.get("Crash") + "\n" + token);
                    }
                }else if (token.contains("Error")) {
                    globalErrorCount++;
                    // append the raw logs to the appropriate key-value
                    if (ht.get("Error") ==  null) {
                        ht.put("Error", token);
                    } else {
                        ht.put("Error", ht.get("Error") + "\n" + token);
                    }
                } else if ( token.contains("Warn")) {
                    globalWarnCount++;
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
            re = new RetinaEvent(pm, am, e);
            re.timestamp = timestamp;
            re.eventid = geventid;
            re.crashcount = globalCrashCount;
            re.errorcount = globalErrorCount;
            re.crashcount = globalWarnCount;
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