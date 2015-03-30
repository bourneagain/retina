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

import java.util.*;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import scala.Array;
import scala.Int;

/**
 * A bolt that parses the tweet into words
 */
public class ParseBolt extends BaseRichBolt
{
    OutputCollector collector;
    GsonBuilder builder;
    Gson gson;
    HashMap<String, ArrayList<String>> appDetailsMap = new HashMap<String, ArrayList<String>>();
    HashMap<String,ArrayList<String>> phoneDetailsMap = new HashMap<String, ArrayList<String>>();


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
        if (json.equals("[{}]") ) {
            System.out.printf(" NULL JSON OUT OF ORDER MESSAGE READ");
        } else {
            System.out.println("Emitting ParseBolt:" + json);
            collector.emit(new Values(json));
        }

        //collector.emit(new Values(rawlog));
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

    public  String parsePhoneData(PhoneData pd, String timestamp, String geventid) {
        System.out.println("parsephonebolt debug");
        Hashtable<String, String> ht = new Hashtable<String, String>();
        Hashtable<String, Integer> ht_click = new Hashtable<String, Integer>();
        Integer globalCrashCount = 0;
        Integer globalErrorCount = 0;
        Integer globalWarnCount = 0;


//        this.phoneVersion = version;
//        this.phoneBaseband = baseband;
//        this.phoneBuild = build;
//        this.phoneModel =  model;


        String opstr = "[{}]";
        Event e = new Event();
        e.appId = pd.appid;
        e.phoneId = pd.phoneimei;
        e.eventType = pd.eventtype;

        RetinaEvent re;

        if (pd.eventtype.equalsIgnoreCase("H")) {
            //
//            PhoneData p_H = new PhoneData(generateTimeStamp(), "1", "H", "1234", "app1",
//                    "lollypop5.0.1 nexus5 m897 LRX22C\napp1 1.0\n");

            ArrayList<String> appDetails = new ArrayList<String>();
            ArrayList<String> phoneDetails = new ArrayList<String>();

            if (phoneDetailsMap.containsKey(pd.phoneimei) && appDetailsMap.containsKey(pd.appid)) {
                phoneDetails = returnMapDetails(phoneDetailsMap, pd.phoneimei);
                appDetails = returnMapDetails(appDetailsMap, pd.appid);

                AppMetadata am = new AppMetadata(pd.appid, appDetails.get(0), appDetails.get(1));
                PhoneMetaData pm = new PhoneMetaData(pd.phoneimei, phoneDetails.get(0), phoneDetails.get(1), phoneDetails.get(2), phoneDetails.get(3));
                appDetails = null;
                phoneDetails = null;

                // ignore log
                // if H means M must have come before and the app and phone map details are set

                re = new RetinaEvent(pm, am, e);

//            re = new RetinaEvent(p,a, e);
                re.timestamp = timestamp;
                re.eventid = geventid;
                opstr = re.toJSON();
            } else {
                opstr = null;
            }
        } else if (pd.eventtype.equalsIgnoreCase("M")) {
            String[] tokens = pd.logs.split("\n");

            //        PhoneData p_M = new PhoneData(generateTimeStamp(), "1", "M", "1234", "app1",
            // log :[0] "lollypop5.0.1 nexus5 m897 LRX22C"
            // log :[1] " appid 1.0 appname");

            //if event type is metadata: there will be two lines in the log field, phone metadata and app metadata
//        phonemetadata - <phoneversion> <phonemodel> <phonebaseband> <phonebuild>
            //      appmetadata - <appid> <appversion> <appname>

            // first line is phone meta
            // second line is app meta
            String[] phonemeta = tokens[0].split(" ");
            // string format : <phoneversion> <phonemodel> <phonebaseband> <phonebuild>
            PhoneMetaData pmdata = new PhoneMetaData(pd.phoneimei,
                    phonemeta[0], // version
                    phonemeta[1], //model
                    phonemeta[2], // baseband
                    phonemeta[3]);//build
            String[] appmeta = tokens[1].split(" ");
            // <id>, <version>
            //save this phone information in has ? or push to mysql or some db : and redis for look up

            ArrayList<String> tempPhoneData = new ArrayList<String>(4);
            tempPhoneData.add(phonemeta[0]);
            tempPhoneData.add(phonemeta[1]);
            tempPhoneData.add(phonemeta[2]);
            tempPhoneData.add(phonemeta[3]);
            phoneDetailsMap.put(pd.phoneimei, tempPhoneData);
            tempPhoneData = null; // for gc

            AppMetadata amdata = new AppMetadata(appmeta[0], appmeta[1], appmeta[2]);
            re = new RetinaEvent(pmdata, amdata, e);
            re.timestamp = timestamp;
            re.eventid = geventid;
            // set the appid parameters here so it can be used when message of L or H comes next
            ArrayList<String> tempAppData = new ArrayList<String>(2);
            tempAppData.add(appmeta[1]);
            tempAppData.add(appmeta[2]);
            appDetailsMap.put(appmeta[0], tempAppData);
            tempAppData = null;

            AppMetadata am = new AppMetadata(pd.appid, tempAppData.get(0), tempAppData.get(1));
            PhoneMetaData pm = new PhoneMetaData(pd.phoneimei, tempPhoneData.get(0), tempPhoneData.get(1), tempPhoneData.get(2), tempPhoneData.get(3));
            tempPhoneData = null;
            tempAppData = null;

            // ignore log
            // if H means M must have come before and the app and phone map details are set

            re = new RetinaEvent(pm, am, e);

//            re = new RetinaEvent(p,a, e);
            re.timestamp = timestamp;
            re.eventid = geventid;
            opstr = re.toJSON();

        } else if (pd.eventtype.equalsIgnoreCase("L")) {
            if ( phoneDetailsMap.containsKey(pd.phoneimei) && appDetailsMap.containsKey(pd.appid) ) {
            String[] tokens = pd.logs.split("\n");
            String temp;
            for (String token : tokens) {


                if (token.contains("Crash")) {
                    globalCrashCount++;
                    if (ht.get("Crash") == null) {
                        ht.put("Crash", token);
                    } else {
                        ht.put("Crash", ht.get("Crash") + "\n" + token);
                    }
                } else if (token.contains("Error")) {
                    globalErrorCount++;
                    // append the raw logs to the appropriate key-value
                    if (ht.get("Error") == null) {
                        ht.put("Error", token);
                    } else {
                        ht.put("Error", ht.get("Error") + "\n" + token);
                    }
                } else if (token.contains("Warn")) {
                    globalWarnCount++;
                    if (ht.get("Warn") == null) {
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
                } else {
                    // take the first one which contains phoneimei and app id; get datails from appDetailsMap and phoneDetailsMap
//                    PhoneData(generateTimeStamp(), "1", "L", "1234", "app1",
//                            "Error:error print\nWarn: warn print\nClick:component1\n"
//                                    + "Crash:crash1\nError:error print1\n"
//                                    + "Crash:crash2\nError:error print2\n"
//                                    + "Crash:crash3\nError:error print3\n"
//                                    + "Click:component2\n");
//
                    //first line with timestamp
//                    PhoneData(String time, String eid, String etype, String imei, String appid, String logs) {
//                        this.phonetimestamp = time;
//                        this.eventid = eid;
//                        this.eventtype = etype;
//                        this.phoneimei = imei;
//                        this.appid = appid;
//                        this.logs = logs;
//                    }


                    ArrayList<String> phoneDetails = returnMapDetails(phoneDetailsMap, pd.phoneimei);
                    ArrayList<String> appDetails = returnMapDetails(appDetailsMap, pd.appid);

//                            new ArrayList<String>();
//                    for ( String el : phoneDetailsMap.get(Integer.parseInt(pd.phoneimei))) {
//                        phoneDetails.add(el);
//                    }
//
//                    ArrayList<String> appDetails = new ArrayList<String>();
//                    for ( String el : appDetailsMap.get(Integer.parseInt(pd.appid))) {
//                        appDetails.add(el);
//                    }
                    AppMetadata am = new AppMetadata(pd.appid, appDetails.get(0), appDetails.get(1));
                    PhoneMetaData pm = new PhoneMetaData(pd.phoneimei, phoneDetails.get(0), phoneDetails.get(1), phoneDetails.get(2), phoneDetails.get(3));

                }
            }

            Enumeration<String> htclickKey = ht_click.keys();
            String clickstr = "";
            while (htclickKey.hasMoreElements()) {
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

            ArrayList<String> phoneDetails = returnMapDetails(phoneDetailsMap, pd.phoneimei);
            ArrayList<String> appDetails = returnMapDetails(appDetailsMap, pd.appid);


            AppMetadata am = new AppMetadata(pd.appid, appDetails.get(0), appDetails.get(1));
            PhoneMetaData pm = new PhoneMetaData(pd.phoneimei, phoneDetails.get(0), phoneDetails.get(1), phoneDetails.get(2), phoneDetails.get(3));

//            re = new RetinaEvent(e);
            //re = new RetinaEvent(PhoneMetaData p, AppMetadata a);
            re = new RetinaEvent(pm, am, e);

            re.timestamp = timestamp;
            re.eventid = geventid;
            re.crashcount = globalCrashCount;
            re.errorcount = globalErrorCount;
            re.crashcount = globalWarnCount;
            opstr = re.toJSON();
            System.out.println("DONE WITH FORMING JSON INSIDE PARSE BOLOT ****************************************************");
            System.out.println(opstr);
        }
        }   else {
            opstr = null;
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