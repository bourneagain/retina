# RETINAA
RETINAA is a prototype framework that leverages the power of tools like Apache Storm, Apache Kafka DRUID to provide meaningful insights in the performance, stability and user interaction aspects of mobile apps. Seamless scalability of the framework is one of the most important feature. Some salient features of RETINAA are as follows:

* The core infrastructure consists of a group of ingestor servers which listen to RETINNA event messages from target devices; a STORM cluster to process the event jsons on the fly and perform additional processing like timestamping, preprocessingevent  jsons to DRUID compatible formats; Apache KAFKA, a distributed high performace messaging framework for passing messages between ingestors, STORM and DRUID; a web UI frontend for intuitive display of various app stats as a dashboard for developers.
* Generic encapsulation of events as objects so that RETINAA can be extended to be a EVENT Analytics framework for e.g click streams, crash events, sensor events.
* Bandwidth efficient messages which encapsulates only the minimal amount of information needed. Types of event - Registration Heartbeat and Logs event.
* Extremely fast aggregation queries on time series events like error counts. DRUID is almost 10x faster than MongDB. 
* The setup was tested on Google Compute Cloud using phone data simulation events in the order of millions of events, to test for scalability.

![alt tag](https://raw.githubusercontent.com/bourneagain/retina/master/report/block.png)
![alt tag](https://raw.githubusercontent.com/bourneagain/retina/master/report/flow2.png)
![alt tag](https://raw.githubusercontent.com/bourneagain/retina/master/report/stack.png)

![alt tag](https://raw.githubusercontent.com/bourneagain/retina/master/report/ui1.png)
![alt tag](https://raw.githubusercontent.com/bourneagain/retina/master/report/ui2.png)
![alt tag](https://raw.githubusercontent.com/bourneagain/retina/master/report/ui3.png)

This project has been awarded *"Best Entrepreneurial Project"* in Advanced Distributed Systems(cs525)  class, 2015 [https://courses.engr.illinois.edu/cs525/sp2015/index.html].
