<?php header('Access-Control-Allow-Origin: *'); ?>
<html>
<head>
<link rel="stylesheet" href="http://maxcdn.bootstrapcdn.com/bootstrap/3.2.0/css/bootstrap.min.css">
<link rel="stylesheet" href="css/iThing.css" type="text/css" />
<link rel="stylesheet" href="css/main.css" type="text/css" />
<link rel="stylesheet" href="css/layouts/blog.css">
<meta name="viewport" content="width=device-width, initial-scale=1"> <!-- for maintaining width as per device width -->
<style>
tr.spaceUnder > td
{
  padding-bottom: 1em;
}
</style>
</head>
<title>RETINAA : Real Time App Analytics</title>
<body>


<div class="text-center">
<nav class="navbar navbar-default navbar-fixed-top">

  <!--<div class="col-md-12" id='logo'><img src='img/logo.png'></img></div>-->
  <div class="container">
        <h1>RETINAA: A Real Time App Analytics Framework </h1>
  </div>
</nav>
</div>

        <div class="col-md-4" id='leftPane'>
		<h1>Filter Metrics</h1>
            <table style="width:100%">
              <tr class='leftPaneTableTr'>
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
              <tr class='leftPaneTableTr'>
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
              <tr class='leftPaneTableTr'>
               <td>
                 Intervals        
               </td>
               <td>
                    <div id='slider'></slider>    
               </td>
              </tr>

              <tr class='leftPaneTableTr'>
               <td>
                Thresholds 
               </td>
               <td><div class='input-group'><input type="text" id='threshold' class="form-control" placeholder="Query Limit" aria-describedby="basic-addon1"></div>
               </td>
              </tr>

              <tr class='leftPaneTableTr'>
               <td>
                Aggregation    FieldNames
               </td>
               <td>
                    <select id='aggField' class="aggDim">
                     <option>errorcount</option>
                     <option>phoneimei</option>
                     <option>appid</option>
               </td>
              </tr>
               <tr class='leftPaneTableTr'>
                  <td>Filter Dimensions</td>
                  <td>
                    <div class='filterDim'>
                        <div id='clonefilter1'>
                            <select id='filField1'>
                             <option>eventtype</option>
                             <option>phoneimei </option>
                             <option>appid </option>
                            </select>
                             <div class='input-group'>
                                <input type="text" id='filFieldInput1' aria-describedby="basic-addon1">
                            </div>
                        </div>
                    </div>
                     <input type="button" id="btnAdd" value="add another filter dimension" />
                  </td>
              </tr>
              </table>
            <div class="btn-group" role="group" aria-label="...">
              <button type="button" id='query'  class="btn btn-default">ViewStats</button>
              <button type="button" id='drillQuery'  class="btn btn-default">ViewDrillDown</button>
              <button type="button" id='viewlive'  class="btn btn-default">viewLiveData</button>
            </div>

		</div>

	</div> <!-- end of leftPane -->

    <div class="col-md-8" id='rightPane'>
		<h1><View Data</h1>
		<div id='container'></div>
        <table id="example" class="display" cellspacing="0" width="100%">
                <thead>
                    <tr>
                        <th>app</th>
                        <th>errorcount</th>
                    </tr>
                </thead>
       			 <tfoot>
                    <tr>
                        <th>app</th>
                        <th>errorcount</th>
                    </tr>
                </tfoot>
         		<tbody>
         		</tbody>
        </table>
<pre id="tsv" style="display:none">
</pre>

<div id='drilldowndata'>
<div>
	</div> <!-- end of right -->
</div>
<footer>
<?php include_once('footer.php'); ?>
</footer>


<br>
<br>
<!--
Sample Input : 
<input id='input' size='100'></input>
<br>
-->
 

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

<script src="http://code.jquery.com/jquery-1.10.2.min.js"></script>
<script src="http://code.jquery.com/ui/1.10.3/jquery-ui.min.js"></script>

<script src="js/jQRangeSlider-min.js"></script>
<script src="js/jQDateRangeSlider-min.js"></script>

<!-- <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.1/jquery.min.js"></script> -->
<script src="http://maxcdn.bootstrapcdn.com/bootstrap/3.2.0/js/bootstrap.min.js"></script>
<!-- DataTables CSS -->
<link rel="stylesheet" type="text/css" href="//cdn.datatables.net/1.10.5/css/jquery.dataTables.css">
<!-- DataTables -->
<script type="text/javascript" charset="utf8" src="//cdn.datatables.net/1.10.5/js/jquery.dataTables.js"></script>
<link rel="stylesheet" href="http://yui.yahooapis.com/pure/0.6.0/pure-min.css">
<link rel="stylesheet" href="http://yui.yahooapis.com/pure/0.6.0/grids-responsive-min.css">
                        <script src="http://code.highcharts.com/highcharts.js"></script>
<script src="http://code.highcharts.com/modules/data.js"></script>
<script src="http://code.highcharts.com/modules/drilldown.js"></script>

<script>

var chart; // global
var period = 30;


//
//var bounds = $("#slider").dateRangeSlider("option", "bounds");
//$(document).ready(function(){

var min = new Date(2010, 0, 1),
    max = new Date();

    $("#slider").dateRangeSlider({
      bounds: {min: min, max: max},
      defaultValues: {
        min: new Date(2012, 0, 1),
        max: max 
    }
});


function formQuery(queryString){
  var resultJson = "";

}

$("#slider").bind("valuesChanged", function(e, data){
  min_date = data.values.min;
  eax_date = data.values.max;
});

function formQuery(){
// include the query map details depending on what live view is required to view
}

function requestData() {
    var queryMap =  {};
    queryMap = getValues();
    queryMap['queryType']="timeseries";
    //queryMap['granularity']="all";
    var temp={};
    queryMap["aggregations"] = [];
    temp["type"]="longSum";
    temp["fieldName"]="errorcount";
    temp["name"]="errorcount";
    queryMap["aggregations"].push(temp);
//	var maxd= new Date();
    //var maxd= new Date();
    //var mind = new Date(); 
    var d1 = new Date();
    var maxd  = new Date( d1.getUTCFullYear(), d1.getUTCMonth(), d1.getUTCDate(), d1.getUTCHours(), d1.getUTCMinutes(), d1.getUTCSeconds() );
    var mind = new Date( d1.getUTCFullYear(), d1.getUTCMonth(), d1.getUTCDate(), d1.getUTCHours(), d1.getUTCMinutes(), d1.getUTCSeconds() );

    mind.setSeconds(maxd.getSeconds() - period  );
    var maxd_year = maxd.getFullYear();
    var maxd_month = (maxd.getMonth()+1)%12;
    var maxd_date = maxd.getDate();
    var maxd_min= maxd.getMinutes();
    var maxd_hour= maxd.getHours();
    var maxd_second = maxd.getSeconds();
    var mind_year = mind.getFullYear();
    var mind_month = (mind.getMonth()+1)%12;
    var mind_date = mind.getDate();
    var mind_min= mind.getMinutes();
    var mind_hour= mind.getHours();
    var mind_second = mind.getSeconds();
    var maxd_str = maxd_year+'-'+maxd_month+'-'+maxd_date+'T'+maxd_hour+':'+maxd_min+':'+maxd_second;
    var mind_str = mind_year+'-'+mind_month+'-'+mind_date+'T'+mind_hour+':'+mind_min+':'+mind_second;
    var intervals = mind_str+'/'+maxd_str;
    queryMap["intervals"] = [];
    queryMap["intervals"].push(intervals);
    console.log(queryMap);
    $.ajax({
        type : "post",
        url: 'live-server-data.php', 
        data: {
            "json" : JSON.stringify(queryMap)
        },
        success: function(point) {
            // console.log("DATA"+data);
            // data = data.slice(1, - 1);
            // console.log("stringdata "+data);
            // ajaxJson = JSON.parse(data);    
            // var errorCount =  ajaxJson.result.errorcount;
            // console.log(errorcount);
            // var timestamp = (new Date).getTime();
            // var point = [];
            // point.push(timestamp); 
            // point.push(errorCount); 
             //console.log("BEFORE"+point)
             //var res = point.split("|");
             //point = res[1];
             console.log("FINAL"+point);
             var series = chart.series[0],
                shift = series.data.length > 20; // shift if the series is longer than 20
    
            chart.series[0].addPoint(eval(point), true, shift);
            
            setTimeout(requestData,1000);    
        },
        cache: false
    });
}

$('#viewlive').click(function(){
        
        /**
         * Request data from the server, add it to the graph and set a timeout to request again
         */

   chart = new Highcharts.Chart({
                    chart: {
                        renderTo: 'container',
                        defaultSeriesType: 'spline',
                        events: {
                            load: requestData
                        }
                    },
                    title: {
                        text: 'Real Time Analytics data'
                    },
                    xAxis: {
                        type: 'datetime',
                        tickPixelInterval: 150,
                        maxZoom: 20 * 1000
                    },
                    yAxis: {
                        minPadding: 0.2,
                        maxPadding: 0.2,
                        title: {
                            text: 'Data',
                            margin: 80
                        }
                    },
                    series: [{
                        name: 'Real Time Statistics',
                        data: []
                    }]
    });        
});
$('#drillQuery').click(function(){
	var queryMap = {};
	queryMap = getValues();
	queryMap["queryType"] = "groupBy";
	queryMap["granularity"] = "day";
        queryMap["dimensions"]=["appid","appversion","appname"];
	delete queryMap["metric"];
	delete queryMap["threshold"];
        console.log(queryMap);
	
    $.ajax({
        type : "post",
        url: 'c.php', 
        data: {
            "json" : JSON.stringify(queryMap)
        },
        success: function(point) {
		
             console.log("DATA"+point);
            document.getElementById('tsv').innerHTML = point;
            // data = data.slice(1, - 1);
            // console.log("stringdata "+data);
            // ajaxJson = JSON.parse(data);    
            // var errorCount =  ajaxJson.result.errorcount;
            // console.log(errorcount);
            // var timestamp = (new Date).getTime();
            // var point = [];
            // point.push(timestamp); 
            // point.push(errorCount); 
             //console.log("BEFORE"+point)
             //var res = point.split("|");
             //point = res[1];
        //     console.log("FINAL"+point);
        //     var series = chart.series[0],
        //        shift = series.data.length > 20; // shift if the series is longer than 20
    
        //    chart.series[0].addPoint(eval(point), true, shift);
        //    
        //    setTimeout(requestData,1000);    

(function($){ // encapsulate jQuery
	$(function () {
    Highcharts.data({
        csv: document.getElementById('tsv').innerHTML,
        itemDelimiter: '\t',
        parsed: function (columns) {

            var brands = {},
                brandsData = [],
                versions = {},
                drilldownSeries = [];

           // Parse percentage strings
           columns[1] = $.map(columns[1], function (value) {
               if (value.indexOf('%') === value.length - 1) {
                   value = parseFloat(value);
               }
               return value;
           });

            $.each(columns[0], function (i, name) {
                var brand,
                    version;

                if (i >= 0) {

                    // Remove special edition notes
                    name = name.split(' -')[0];

                    // Split into brand and version
                    version = name.match(/([0-9]+[\.0-9x]*)/);
                    if (version) {
                        version = version[0];
                    }
                    brand = name.replace(version, '');

                    // Create the main data
                    if (!brands[brand]) {
                        brands[brand] = columns[1][i];
                    } else {
                        brands[brand] += columns[1][i];
                    }

                    // Create the version data
                    if (version !== null) {
                        if (!versions[brand]) {
                            versions[brand] = [];
                        }
                        versions[brand].push(['v' + version, columns[1][i]]);
                    }
                }

            });

            $.each(brands, function (name, y) {
                brandsData.push({
                    name: name,
                    y: y,
                    drilldown: versions[name] ? name : null
                });
            });
            $.each(versions, function (key, value) {
                drilldownSeries.push({
                    name: key,
                    id: key,
                    data: value
                });
            });

            // Create the chart
            $('#container').highcharts({
                chart: {
                    type: 'column'
                },
                title: {
                    text: 'All Deployed Applications stats'
                },
                subtitle: {
                    text: 'Click the columns to view app versions'
                },
                xAxis: {
                    type: 'category'
                },
                yAxis: {
                    title: {
                        text: 'Total error count stats'
                    }
                },
                legend: {
                    enabled: false
                },
                plotOptions: {
                    series: {
                        borderWidth: 0,
                        dataLabels: {
                            enabled: true,
                            format: '{point.y}'
                        }
                    }
                },

                tooltip: {
                    headerFormat: '<span style="font-size:11px">{series.name}</span><br>',
                    pointFormat: '<span style="color:{point.color}">{point.name}</span>: <b>{point.y}%</b> of total<br/>'
                },

                series: [{
                    name: 'errorCount',
                    colorByPoint: true,
                    data: brandsData
                }],
                drilldown: {
                    series: drilldownSeries
                }
            });
        }
    });
});


})(jQuery);


        },
        cache: false
    });



});

function getValues(){
        var topic = "test";
	var queryMap = {}
        queryMap["dataSource"] = topic;
        var radios = $(":radio:checked");
	//console.log("RADIOS " + radios);
        //var checkboxes = $(":checkboxes:checked");
        for (i = 0; i < radios.length; i++) { 
                   queryMap[radios[i].name] = radios[i].value  ;
        }
        var sliderDates = $("#slider").dateRangeSlider("values");
        var maxd = sliderDates.max;
        var mind = sliderDates.min;
        var maxd_year = maxd.getFullYear()+1;
        var maxd_month = maxd.getMonth()+1;
        var maxd_date = maxd.getDate();
        var maxd_str = maxd_year+'-'+maxd_month+'-'+maxd_date;
        var mind_year = mind.getFullYear();
        var mind_month = mind.getMonth()+1;
        var mind_date = mind.getDate();
        var mind_str = mind_year+'-'+mind_month+'-'+mind_date;


//"intervals": [ "2000-01-01/2020-01-01" ], 
        intervals = mind_str+'/'+maxd_str;
//    $("#slider").bind("valuesChanged", function(e, data){
//      console.log("Values just changed. min: " + data.values.min + " max: " + data.values.max);
//    });

        //console.log("json is" + json);
        queryMap["intervals"] = [];
        queryMap["intervals"].push(intervals);
        var threshold = $("#threshold").val();   
        // debug
        queryMap["threshold"] = threshold;
        //queryMap["threshold"] = "10";

        var aggregations = $( "#aggField option:selected" ).text();
        aggregations = aggregations.trim();    
        queryMap["aggregations"]=[];
        var temp={};
        temp["type"]="longSum";
        temp["fieldName"]=aggregations;
        temp["name"]="errorcount";
        queryMap["aggregations"].push(temp);
        queryMap["dimension"]="appid";
        queryMap["metric"]="errorcount";


        var filterSelected = $( "#filField1 option:selected" ).text();
         //    var filterSelectedValue = $( "#filFieldInput1").val();
        // debug
        // var filterSelectedValue = "United States"; 
        var filterSelectedValue = "L"; 
        filterSelected = filterSelected.trim();
        filterSelectedValue = filterSelectedValue.trim();
        queryMap["filter"]={};
        queryMap["filter"]["type"]="selector";
       // queryMap["filter"]["dimension"]=filterSelected;
        queryMap["filter"]["dimension"]="eventtype";
        queryMap["filter"]["value"]=filterSelectedValue;
	return queryMap;
}

$('#query').click(function(){
        $('#example').show();
        var table = $('#example').DataTable();    
         table.destroy();
        var json = $("#input").val();   
	var queryMap = {};
	queryMap = getValues();
        console.log(queryMap);
        $.ajax({ 
                type : "post",
                //url: "http://146.148.85.88:8082/druid/v2/",
                url: "b.php",
                data: {
                //      "queryType":"timeBoundary",
                //      "dataSource":"wikipedia"
                "json" : JSON.stringify(queryMap)
                },
                //dataType : 'json',
                success : function(data){
                    data = data.slice(1, - 1);
                    console.log("stringdata "+data);
                    ajaxJson = JSON.parse(data);    
                    console.log("TYPE OF OBJECT success"+ajaxJson+"printed json");
                    console.log("ajaxJsonResult" + ajaxJson.result.length);
                    datatablesData =  ajaxJson.result;
                    var data2=[];
                    for (i=0; i< datatablesData.length ;i++) {
                        var temp = [];
                        temp.push(datatablesData[i].appid);
                        temp.push(datatablesData[i].errorcount);
                        data2.push(temp);
                    }
                    console.log(data2);
                    $('#example').dataTable({
                        "aaData": data2,
                        "aoColumns": [
                            { "sTitle": "appid" },
                            { "sTitle": "errorcount" }
                        ]
                    }); // datatables end
                } // success end    
          }); // ajax end
}); 
// end of click function





$(document).ready(function() {
    $('#btnAdd').hide();
    $('#example').hide();
    // comment to have additional filter addition
     $('#btnAdd').click(function() {
            var num = $('.filterDim').length;
            var newNum  = new Number(num + 1)
            var newEl = $('#clonefilter'+num).clone().attr('id','clonefilter'+newNum);
            newEl.find('#filField'+num).attr('id','filField'+newNum)
            newEl.find('.input-group').find('#filFieldInput'+num).attr('id','filFieldInput'+newNum)
            $('.filterDim').append(newEl);
            //$('.filterDim').after(newEl);
    });
});
</script>
</body>
</html>


<!--

{
  "queryType": "topN",
  "dataSource": "test",
  "granularity": "all",
  "dimension": "appid",
  "metric": "errorcount",
  "threshold" : 10,
  "aggregations": [
    {"type": "longSum", "fieldName": "errorcount", "name": "errorcount"}
  ],
  "filter": { "type": "selector", "dimension": "eventtype", "value": "L" },
  "intervals": ["2012-10-01T00:00/2020-01-01T00"]
}

[ {
  "timestamp" : "2015-03-29T20:23:33.000Z",
  "result" : [ {
    "errorcount" : 1060,
    "appid" : "app3"
  }, {
    "errorcount" : 584,
    "appid" : "app1"
  } ]
} ]
-->

