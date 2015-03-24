<?php header('Access-Control-Allow-Origin: *'); ?>
<head>
<script src="http://ajax.googleapis.com/ajax/libs/jquery/1.11.2/jquery.min.js"></script>
</head>
<?php
echo "hi";
?>
<input type='button' id='query' name='query'>
</input>
<input id='input'></input>
<input id='results'></input>
<script>
$('#query').click(function(){
   	var json = $("#input").val();	
	console.log("json is" + json);
	$.ajax({
		type : "post",
		//url: "http://146.148.85.88:8082/druid/v2/",
		url: "b.php",
		data: {
		//	"queryType":"timeBoundary",
		//	"dataSource":"wikipedia"
			"json" :  json
		},
		//dataType : 'json',
		success : function(data){
//			$('#results').val(data);
	console.log(data);
		}
	})
});
</script>
