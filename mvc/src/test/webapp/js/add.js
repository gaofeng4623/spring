
function addRowToTable() {
	var tbl = document.getElementById("att");
	var lastRow = tbl.rows.length;
	var iteration = lastRow;
	var row = tbl.insertRow(lastRow);
		
	var cellzxmc = row.insertCell(0);
	var zxmcel = document.createElement("input");
	zxmcel.type = "file";
	zxmcel.name = "attach";
	cellzxmc.appendChild(zxmcel);
}

