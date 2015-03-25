<?php header('Access-Control-Allow-Origin: *'); ?>
<head>
<link rel="stylesheet" href="http://maxcdn.bootstrapcdn.com/bootstrap/3.2.0/css/bootstrap.min.css">
<link rel="stylesheet" href="css/iThing.css" type="text/css" />
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
	   <td>
		 Intervals		
	   </td>
	   <td>
			<div id='slider'></slider>	
	   </td>
	  </tr>

	  <tr>
	   <td>
		Thresholds 
	   </td>
	   <td><div class='input-group'><input type="text" id='threshold' class="form-control" placeholder="Query Limit" aria-describedby="basic-addon1"></div>
	   </td>
	  </tr>

	  <tr>
	   <td>
		Aggregation	FieldNames
	   </td>
	   <td>
            <select id='aggField' class="aggDim">
             <option>count</option>
             <option>phoneimei</option>
             <option>appid</option>
	   </td>
	  </tr>
       <tr>
          <td>Filter Dimensions</td>
          <td>
			<div class='filterDim'>
				<div id='clonefilter1'>
					<select id='filField1'>
					 <option>country </option>
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
  </div>
  <div class="col-md-8">
		Result : <input id='results' size='50'></input>

<table id="example" class="display" cellspacing="0" width="100%">
        <thead>
            <tr>
                <th>Name</th>
                <th>Position</th>
                <th>Office</th>
                <th>Age</th>
                <th>Start date</th>
                <th>Salary</th>
            </tr>
        </thead>
<tfoot>
            <tr>
                <th>Name</th>
                <th>Position</th>
                <th>Office</th>
                <th>Age</th>
                <th>Start date</th>
                <th>Salary</th>
            </tr>
        </tfoot>
<tbody>
            <tr>
                <td>Tiger Nixon</td>
                <td>System Architect</td>
                <td>Edinburgh</td>
                <td>61</td>
                <td>2011/04/25</td>
                <td>$320,800</td>
            </tr>
            <tr>
                <td>Garrett Winters</td>
                <td>Accountant</td>
                <td>Tokyo</td>
                <td>63</td>
                <td>2011/07/25</td>
                <td>$170,750</td>
            </tr>
            <tr>
                <td>Ashton Cox</td>
                <td>Junior Technical Author</td>
                <td>San Francisco</td>
                <td>66</td>
                <td>2009/01/12</td>
                <td>$86,000</td>
            </tr>
 </tbody>
    </table>

	</div>
</div>

<br>
<br>
<div class="btn-group" role="group" aria-label="...">
  <button type="button" id='query'  class="btn btn-default">Query</button>
</div>

Sample Input : 
<input id='input' size='100'></input>
<br>
</div>
 

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

<script src="jQRangeSlider-min.js"></script>
<script src="jQDateRangeSlider-min.js"></script>

<!-- <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.1/jquery.min.js"></script> -->
<script src="http://maxcdn.bootstrapcdn.com/bootstrap/3.2.0/js/bootstrap.min.js"></script>
<!-- DataTables CSS -->
<link rel="stylesheet" type="text/css" href="//cdn.datatables.net/1.10.5/css/jquery.dataTables.css">
<!-- DataTables -->
<script type="text/javascript" charset="utf8" src="//cdn.datatables.net/1.10.5/js/jquery.dataTables.js"></script>

<script>
$(document).ready(function() {

 $('#btnAdd').hide()
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


$('#query').click(function(){
		
        var json = $("#input").val();   
        var topic = "wikipedia";
        var radios = $(":radio:checked");
        //var checkboxes = $(":checkboxes:checked");
        queryMap = {}
        queryMap["dataSource"] = topic;
        for (i = 0; i < radios.length; i++) { 
			       queryMap[radios[i].name] = radios[i].value  
        }
		var sliderDates = $("#slider").dateRangeSlider("values");
		var maxd = sliderDates.max;
		var mind = sliderDates.min;
		var maxd_year = maxd.getFullYear();
		var maxd_month = maxd.getMonth()+1;
		var maxd_date = maxd.getDate();
		var maxd_str = maxd_year+'-'+maxd_month+'-'+maxd_date;
		var mind_year = mind.getFullYear();
		var mind_month = mind.getMonth()+1;
		var mind_date = mind.getDate();
		var mind_str = mind_year+'-'+mind_month+'-'+mind_date;


//"intervals": [ "2000-01-01/2020-01-01" ], 
		intervals = mind_str+'/'+maxd_str;
//	$("#slider").bind("valuesChanged", function(e, data){
//  	console.log("Values just changed. min: " + data.values.min + " max: " + data.values.max);
//	});

        //console.log("json is" + json);
		queryMap["intervals"] = [];
		queryMap["intervals"].push(intervals);
        var threshold = $("#threshold").val();   
		queryMap["threshold"] = threshold;

		var aggregations = $( "#aggField option:selected" ).text();
		aggregations = aggregations.trim();	
		queryMap["aggregations"]=[]
		var temp={};
		temp["type"]="longSum";
		temp["fieldName"]=aggregations;
		temp["name"]="edit_count";
		queryMap["aggregations"].push(temp);
		queryMap["dimension"]="page";
		queryMap["metric"]="edit_count";


		var filterSelected = $( "#filField1 option:selected" ).text();
		var filterSelectedValue = $( "#filFieldInput1").val();
		filterSelected = filterSelected.trim();
		filterSelectedValue = filterSelectedValue.trim();
		queryMap["filter"]={};
		queryMap["filter"]["type"]="selector";
		queryMap["filter"]["dimension"]=filterSelected;
		queryMap["filter"]["value"]=filterSelectedValue;

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
                        $('#results').val(data);
//      console.log(data);
                }
        })
}); 
// end of click function

</script>

</body>


<!--
{
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

