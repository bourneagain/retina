/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package retina;

import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;
import backtype.storm.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Random;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class EventSpout extends BaseRichSpout {
    SpoutOutputCollector _collector;
    Random _rand;
    int eventId;
    int debugcount = 0;

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
  public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
    _collector = collector;
    _rand = new Random();
  }

  @Override
  public void nextTuple() {
      Utils.sleep(10000);
      PhoneData p_M = new PhoneData(generateTimeStamp(), "1", "M", "1234", "app1",
              "lollypop5.0.1 nexus5 m897 LRX22C\nappid 1.0 appname\n");

      PhoneData p_H = new PhoneData(generateTimeStamp(), "1", "H", "1234", "app1",
              "lollypop5.0.1 nexus5 m897 LRX22C\napp1 1.0\n");

      PhoneData p_L = new PhoneData(generateTimeStamp(), "1", "L", "1234", "app1",
              "Error:error print\nWarn: warn print\nClick:component1\n"
                      + "Click:component1\nError:error print\n"
                      + "Click:component2\n");
      String currTime = generateTimeStamp();
      /* this json will be received from the phones */

      GsonBuilder builder = new GsonBuilder();
      Gson gson = builder.create();
      String json = "[]";
      if (debugcount == 0) {
          json = gson.toJson(p_M);
          debugcount++;
      } else {

          if (_rand.nextInt(2) % 2 == 0) {
              json = gson.toJson(p_H);
          } else {
              json = gson.toJson(p_L);
          }
      }
//      String[] sentences = new String[]{ "the cow jumped over the moon", "an apple a day keeps the doctor away",
//        "four score and seven years ago", "snow white and the seven dwarfs", "i am at two with nature" };
//      String sentence = sentences[_rand.nextInt(sentences.length)];

      //String op = currTime + " " + generateEventID() + " " + json;
      System.out.println("Emitting EventSpout:" + json);
      //System.out.println("Emitting sentence:" + sentence);
      //_collector.emit(new Values(sentence));
      _collector.emit(new Values(json));
  }

  @Override
  public void ack(Object id) {
  }

  @Override
  public void fail(Object id) {
  }


    @Override
        public void declareOutputFields(OutputFieldsDeclarer declarer)
        {
            // tell storm the schema of the output tuple for this spout
            // tuple consists of a single column called 'tweet-word'
            declarer.declare(new Fields("event"));
        }

}