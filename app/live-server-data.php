<?php 
// Set the JSON header
header("Content-type: text/json");

// The x value is the current JavaScript time, which is the Unix time multiplied by 1000.
$x = time() * 1000;
// The y value is a random number
#$y = rand(0, 100);

// Create a PHP array and echo it as JSON
#$ret = array($x, $y);
#echo $ret;
#echo json_encode($ret);


$json=$_POST['json'];
#$json = '{"queryType":"timeseries","dataSource":"test","granularity":"all","aggregations":[{"type":"longSum","fieldName":"errorcount","name":"errorcount"}],"intervals":["2015-4-3T13:58:10/2015-4-3T13:58:40"]}';
##$cmd = "curl -X POST 'http://localhost:8084/druid/v2/'  -H 'content-type: application/json' -d'{\"queryType\":\"timeBoundary\",\"dataSource\":\"wikipedia\"}'";
##$cmd = "curl -X POST 'http://localhost:8084/druid/v2/'  -H 'content-type: application/json' -d'".addcslashes($json, '"')."'";
$cmd = "curl -X POST 'http://104.154.46.154:8082/druid/v2/'  -H 'content-type: application/json' -d'".$json."'";
$tem = shell_exec($cmd);
#echo $cmd."|";
$tem  = json_encode($tem);
$message = json_decode($tem);
$t = json_decode($message,true); 
$y=($t[0]['result']['errorcount']);
$ret = array($x, $y);
echo json_encode($ret);
?>
