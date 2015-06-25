#!/usr/bin/node

var fs = require("fs"),
	filename = process.argv[2],
	milestone = process.argv[3];


console.log("Leyendo "+filename);
/**
 * Sirve para pasar issues de github a formato rest
 * las milestones se bajan de este link: 
 *     https://api.github.com/repos/NickCis/7552-taller-prog-2-2015-C1/milestones?state=all
 * las issues relacionadas con un milestone:
 *     https://api.github.com/repos/NickCis/7552-taller-prog-2-2015-C1/issues?milestone=<milestone number>&state=all
	*/

fs.readFile(filename, "utf8", function(err, data){
	if(err)
		return console.log("Error leyendo archivo");

	var json = JSON.parse(data);
		for(var e, i=0; e = json[i]; i++){
			var localdate = new Date(e.closed_at),
				str = " * `"+e.title+" <"+e.url+">`_ Realizada por: "+(e.asignee||e.user).login;
			if(e.state == "closed")
				str += " ["+localdate+"]";

			console.log(str);
	}
});
