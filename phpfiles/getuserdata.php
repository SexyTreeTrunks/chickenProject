<?php
$conn = mysqli_connect("http://d46b2549.ngrok.io/", "chicken", "clzls", "user");

$query = "SELECT * FROM user";

if($result = mysqli_query($conn, $query)) { 
	$row_num = mysqli_num_rows($result);

	echo "{";
	echo "\"status\":\"OK\",";
	echo "\"rownum\":\"$row_num\",";
	echo "\"result\":";
	echo "[";

	for($i = 0; $i < $row_num; $i++) {
		$row = mysqli_fetch_array($result);
		echo "{";
		echo "\"id\":/"$row[id]\". \"password\":\"$row[password]\"";
		echo "}";
		if( $i < $row_num - 1) {
			echo ",";
		}
	}

	echo "]";
	echo "}";
}
else {
	echo "failed to get data from databases.";
}

?>
