echo "START ZOOKEEPER"
/usr/lib/zookeeper-3.4.6/bin/zkServer.sh start &> /dev/null 
echo "START kafka"
/usr/lib/kafka_2.9.2-0.8.1.1/bin/kafka-server-start.sh /usr/lib/kafka_2.9.2-0.8.1.1/config/server.properties &> /tmp/kafkaServer.log &
sleep 2
echo "create 'test' topic in kafka"
/usr/lib/kafka_2.9.2-0.8.1.1/bin/kafka-topics.sh --create --zookeeper localhost:2181   --replication-factor 1 --partitions 1 --topic test  
echo ""
echo "create 'phone-data-test' topic in kafka"
/usr/lib/kafka_2.9.2-0.8.1.1/bin/kafka-topics.sh --create --zookeeper localhost:2181   --replication-factor 1 --partitions 1 --topic phone-data-test
sleep 1
echo ""
echo ""
echo "START REALTIME"
cd /usr/lib/druid-0.7.0/ 
java -Xmx512m -Duser.timezone=UTC -Dfile.encoding=UTF-8              -Ddruid.realtime.specFile=/mnt/hgfs/saikat/Documents/UIUC/spring_2015/cs525/project/retina/retina.spec       -classpath "config/_common:config/realtime:lib/*"               io.druid.cli.Main server realtime 2>&1 > /tmp/realtime.log &
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
cd /mnt/hgfs/saikat/Documents/UIUC/spring_2015/cs525/project/retina/retina-storm
java -jar  target/retina-storm-jar-with-dependencies.jar

