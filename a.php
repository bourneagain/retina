<?php header('Access-Control-Allow-Origin: *'); ?>
<head>
<link rel="stylesheet" href="http://maxcdn.bootstrapcdn.com/bootstrap/3.2.0/css/bootstrap.min.css">
<link href="css/sidebar.css" rel="stylesheet">
</head>
<style>
body { 
padding-top: 200px;
padding-left: 70px;
 }
</style>


<body>

<nav class="navbar navbar-default navbar-fixed-top">
  <div class="container">
        <h1>RETINA </h1>
  </div>
</nav>

<div class="row">
  <div class="col-md-4">
    <table style="width:100%">
      <tr>
        <td>Query Type</td>
        <td>
      		<div class="radiosButtons">
      			<label>
      			  <input type="radio" name='queryType' id="topn" value="topN">
      			  TopN 
      			</label>
      			<label>
      			  <input type="radio" name='queryType' id="timeseries" value="timeSeries">
      		    TimeSeries
      			</label>
            <label>
              <input type="radio" name='queryType' id="timeboundary" value="timeBoundary">
              TimeBoundary
            </label>
      		</div>
      </td>
      </tr>
      <tr>
      <td>Granularity</td>
      <td>
        <!-- 
        all, none, minute, fifteen_minute, thirty_minute, hour and day
      -->
        <div class="radiosButtons">
            <label>
              <input type="radio" name='granularity' id="gall" value="all">
              All 
            </label>
            <label>
              <input type="radio" name='granularity' id="gminute" value="minute">
              Minute
            </label>
            <label>
              <input type="radio" name='granularity' id="ghour" value="hour">
              Hour
            </label>
          </div>
      </td>
      </tr>
       <tr>
          <td>Dimensions</td>
          <td>
            <select class="dimension">
             <option>warn</option>
             <option>error</option>
             <option>click</option>
            </select>
          </td>
      </tr>
      </table>
  </div>
  <div class="col-md-8">
		Result : <input id='results' size='200'></input>
	</div>
</div>

<br>
<br>
<br>
<br>
<br>
<br>
<br>

<div class="btn-group" role="group" aria-label="...">
  <button type="button" id='query'  class="btn btn-default">Query</button>
</div>

Sample Input : 
<input id='input' size='100'></input>
<br>

</div>{
 

<!-- 
   "queryType": "topN",
  "dataSource": "wikipedia", 
  "granularity": "all", 
  "dimension": "page",
  "metric": "edit_count",
  "threshold" : 10,
  "aggregations": [
    {"type": "longSum", "fieldName": "count", "name": "edit_count"}
  ], 
  "filter": { "type": "selector", "dimension": "country", "value": "United States" }, 
  "intervals": ["2012-10-01T00:00/2020-01-01T00"]
}
  -->

<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.1/jquery.min.js"></script>
<script src="http://maxcdn.bootstrapcdn.com/bootstrap/3.2.0/js/bootstrap.min.js"></script>
<script>

function formQuery(queryString){
  var resultJson = "";

}
$('#query').click(function(){
        var json = $("#input").val();   
        var topic = "wikipedia";
        var radios = $(":radio:checked");
        //var checkboxes = $(":checkboxes:checked");
        var queryMap = {}
        queryMap["topic"] = topic;
        for (i = 0; i < radios.length; i++) { 
			       queryMap[radios[i].name] = radios[i].value  
        }


        //console.log("json is" + json);
        console.log(queryMap);

        $.ajax({
                type : "post",
                //url: "http://146.148.85.88:8082/druid/v2/",
                url: "b.php",
                data: {
                //      "queryType":"timeBoundary",
                //      "dataSource":"wikipedia"
                        "json" :  json
                },
                //dataType : 'json',
                success : function(data){
                        $('#results').val(data);
//      console.log(data);
                }
        })
});
</script>

</body>
