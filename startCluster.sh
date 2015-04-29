#BOOSTSTRAP CODE FOR DEMO
#CHANGE PATH 
echo "START ZOOKEEPER"
~/zookeeper-3.4.6/bin/zkServer.sh start &> /dev/null 
echo "START kafka"
~/kafka_2.8.0-0.8.1.1/bin/kafka-server-start.sh ~/kafka_2.8.0-0.8.1.1/config/server.properties &> /tmp/kafkaServer.log &
sleep 2
# DELETE TOPIC FIRST JUST TO BE SURE
~/kafka_2.8.0-0.8.1.1/bin/kafka-run-class.sh kafka.admin.DeleteTopicCommand --zookeeper localhost:2181 --topic test 
~/kafka_2.8.0-0.8.1.1/bin/kafka-run-class.sh kafka.admin.DeleteTopicCommand --zookeeper localhost:2181 --topic phone-data-test
echo "TOPICS DELETED"

echo "create 'test' topic in kafka"
~/kafka_2.8.0-0.8.1.1/bin/kafka-topics.sh --create --zookeeper localhost:2181   --replication-factor 1 --partitions 1 --topic test  
echo ""
echo "create 'phone-data-test' topic in kafka"
~/kafka_2.8.0-0.8.1.1/bin/kafka-topics.sh --create --zookeeper localhost:2181   --replication-factor 1 --partitions 1 --topic phone-data-test
sleep 1
exit
#echo ""
#echo ""
#echo "START REALTIME"
cd /home/sam/druid-0.7.1-rc1/
java -Xmx512m -Duser.timezone=UTC -Dfile.encoding=UTF-8              -Ddruid.realtime.specFile=/home/sam/retina/retina.spec       -classpath "config/_common:config/realtime:lib/*"               io.druid.cli.Main server realtime 2>&1 > /tmp/realtime.log &
sleep 1
echo ""

echo "START BROKER"
java -Xmx256m -Duser.timezone=UTC -Dfile.encoding=UTF-8 -classpath config/_common:config/broker:lib/* io.druid.cli.Main server broker 2>&1 > /tmp/broker.log &
sleep 1
echo ""
echo "START HISTORICAL"
java -Xmx256m -Duser.timezone=UTC -Dfile.encoding=UTF-8 -classpath config/_common:config/historical:lib/* io.druid.cli.Main server historical 2>&1 > /tmp/historical.log &
sleep 1
echo ""
echo "START COORDINATOR"
java -Xmx256m -Duser.timezone=UTC -Dfile.encoding=UTF-8 -classpath config/_common:config/coordinator:lib/* io.druid.cli.Main server coordinator 2>&1 > /tmp/coordinator.log &
echo ""
echo "START RETINA APPLICATION"
cd ~/retina/retina-storm/
java -jar  /home/sam/retina/retina-storm/target/retina-storm-jar-with-dependencies.jar

