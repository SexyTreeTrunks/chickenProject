<?
	$connect=mysql_connect("http://d46b2549.ngrok.io/", "chicken", "clzls") or die("SQL server에 연결할 수 없습니다.");

	mysql_query("SET NAMES UTF8");
	mysql_select_db("appdqtq", $connect);

	session_start();

	$sql = "SELECT * FROM user";

	$result = mysql_query($sql, $connect);
	$total_record = mysql_num_rows($result);

	echo "{\"status\":\"OK\",\"num_results\":\"$total_record\",\"results\":[";

	for($i=0; $i < $total_record; $i++) {
		mysql_data_seek($result, $i);
		$row = mysql_fetch_array($result);
		echo "{\"id\":$row[id],\"password\":\"$row[password]\"}";

		if($i < $total_record - 1) {
			echo ",";
		}
	}

	echo "]}";

?>
