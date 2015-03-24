<?php
#echo "json IS " . $_POST['json'] ;
$json=$_POST['json'];
#$cmd = "curl -X POST 'http://localhost:8084/druid/v2/'  -H 'content-type: application/json' -d'{\"queryType\":\"timeBoundary\",\"dataSource\":\"wikipedia\"}'";
#$cmd = "curl -X POST 'http://localhost:8084/druid/v2/'  -H 'content-type: application/json' -d'".addcslashes($json, '"')."'";
$cmd = "curl -X POST 'http://localhost:8084/druid/v2/'  -H 'content-type: application/json' -d'{".$json."}'";
#echo $cmd;
$a = shell_exec($cmd);
echo $a;
?>
