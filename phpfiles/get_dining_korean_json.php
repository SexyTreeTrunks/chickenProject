
<?php
header("Content-type: text/html; charset=utf-8");

$host = 'localhost';
$user = 'root';
$password = 'clzls';
$dbname = 'chicken';

$link = mysqli_connect($host, $user, $password, $dbname);
  
// 연결 오류 발생 시 스크립트 종료
if (mysqli_connect_errno()) {
    die('Connect Error: '.mysqli_connect_error());
}

mysqli_query($link, "set session character_set_connection=utf8;");
mysqli_query($link, "set session character_set_results=utf8;");
mysqli_query($link, "set session character_set_client=utf8;");

$sql = "SELECT * FROM dining_korean";
$result = mysqli_query($link, $sql);
$total_record = mysqli_num_rows($result);

echo "{\"result\":[";

for($i = 0; $i < $total_record; $i++) {
	mysqli_data_seek($result, $i);
	$row = mysqli_fetch_array($result);
	echo "{\"id\":\"$row[dining_korean_id]\",\"name\":\"$row[dining_korean_name]\",\"longitude\":\"$row[dining_korean_longitude]\",\"latitude\":\"$row[dining_korean_latitude]\",\"ratingStars\":\"$row[dining_korean_ratingStars]\"}";

	if($i < $total_record-1) {
		echo ",";
	}
}

echo "]}";


// 접속 종료
mysqli_close($link);
?>
