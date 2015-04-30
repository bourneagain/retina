<?php

$json=$_POST['json'];

$cmd = "curl -X POST 'http://104.154.46.154:8082/druid/v2/'  -H 'content-type: application/json' -d'".$json."'";
#echo $cmd;
$op = shell_exec($cmd);
$a=json_decode($op);
foreach($a as $key=>$val){
    $count = 1;
    foreach($val as $k=>$v){
        if ( $count < 3 ) {
            $count++;
            continue;
        }
        echo $v->appid." ".$v->appversion."\t".$v->errorcount."%";
    }
        echo "\n";
}

?>
