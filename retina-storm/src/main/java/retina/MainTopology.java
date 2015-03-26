package retina;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.spout.SchemeAsMultiScheme;
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
import storm.kafka.*;

class MainTopology
{
    static String TOPIC_NAME = "phone-data-test";
    static SpoutConfig kafkaConfig;
    static void setKafkaSpout() {
        BrokerHosts brokerHosts = new ZkHosts("localhost:2181");
        kafkaConfig = new SpoutConfig(brokerHosts, TOPIC_NAME, "", "storm");
        //kafkaConfig.
        //kafkaConfig.forceStartOffsetTime(readFromMode  /* either earliest or current offset */);
        kafkaConfig.scheme = new SchemeAsMultiScheme(new StringScheme());
    }
    public static void main(String[] args) throws Exception
    {
        // create the topology
        TopologyBuilder builder = new TopologyBuilder();
        boolean kafkaSpout  = true;

        String sourceSpout = "event-spout";
        SimulatePhoneData sd = new SimulatePhoneData(20, 1000);

        // attach the tweet spout to the topology - parallelism of 1
        if (kafkaSpout) {
            sourceSpout = "kafka-spout";
            setKafkaSpout();
            builder.setSpout(sourceSpout, new KafkaSpout(kafkaConfig), 1);

        } else {
            sourceSpout = "event-spout";
            builder.setSpout(sourceSpout, new EventSpout(), 1);
        }

        //builder.setSpout(sourceSpout, new EventSpout(), 1);
        builder.setBolt("timestamp-bolt", new TimestampBolt(), 1).shuffleGrouping(sourceSpout);
        builder.setBolt("parse-event-bolt", new ParseBolt(), 1).shuffleGrouping("timestamp-bolt");
        builder.setBolt("kafka-producer-bolt", new KafkaBolt(), 1).shuffleGrouping("parse-event-bolt");

        // create the default config object
        Config conf = new Config();

        // set the config in debugging mode
        conf.setDebug(true);

        if (args != null && args.length > 0) {

            // run it in a live cluster

            // set the number of workers for running all spout and bolt tasks
            conf.setNumWorkers(3);

            // create the topology and submit with config
            StormSubmitter.submitTopology(args[0], conf, builder.createTopology());
            (new Thread(sd)).start();

        } else {

            // run it in a simulated local cluster

            // set the number of threads to run - similar to setting number of workers in live cluster
            conf.setMaxTaskParallelism(3);

            // create the local cluster instance
            LocalCluster cluster = new LocalCluster();

            // submit the topology to the local cluster
            cluster.submitTopology("retina-storm", conf, builder.createTopology());
            (new Thread(sd)).start();
            // let the topology run for 300 seconds. note topologies never terminate!
            Utils.sleep(300000);

            // now kill the topology
            cluster.killTopology("retina-storm");

            // we are done, so shutdown the local cluster
            cluster.shutdown();
        }
    }
}