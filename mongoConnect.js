// query mongodb and calculate error count
var MongoClient = require('mongodb').MongoClient,
   Db = require('mongodb').Db;

var errorCountSum;
MongoClient.connect("mongodb://localhost:27017/retina", function(err, db) {
  if(err) { return console.dir(err); }
  var collection = db.collection('test');
  var start_time = (new Date).getTime();
  collection.find().toArray(function(err, docs) {
      errorCountSum = 0;
      for(i=0;i<docs.length;i++){
         var temp;   
         temp = docs[i];
		errorCountSum+=temp.errorcount;
      }
  var end_time = (new Date).getTime();
  var elp = end_time-start_time;
  console.log(elp+":"+errorCountSum);
  }); 
});
