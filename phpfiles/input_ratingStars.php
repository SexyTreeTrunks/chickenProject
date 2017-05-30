
<?php
//header("Content-type: text/html; charset=utf-8");

$host = 'localhost';
$user = 'root';
$password = 'clzls';
$dbname = 'chicken';

$conn = mysqli_connect($host, $user, $password, $dbname);

mysqli_set_charset($conn, "utf8");
  
// 연결 오류 발생 시 스크립트 종료
if (mysqli_connect_errno()) {
    die('Connect Error: '.mysqli_connect_error());
}

mysqli_query($link, "set session character_set_connection=utf8;");
mysqli_query($link, "set session character_set_results=utf8;");
mysqli_query($link, "set session character_set_client=utf8;");

$type = $_POST['type'];
$restaurantName = $_POST['restaurantName'];
$ratingStars = $_POST['ratingStars'];

$sql = "SELECT * FROM $type";
$result = mysqli_query($conn, $sql);
$total_record = mysqli_num_rows($result);

$tableName_ratingStars = $type."_ratingStars";
$tableName_name = $type."_name";

for($i=0; $i <$total_record; $i++) {
	mysqli_data_seek($result, $i);
	$row = mysqli_fetch_array($result);
	
	//불일치 하는경우 1을 반환
	if( !strcmp($row[$tableName_name], $restaurantName)) {
		echo "$row[$tableName_ratingStars]";
		$sql = "UPDATE $type SET $tableName_ratingStars = $ratingStars WHERE $tableName_name = '$restaurantName'"; 	
		mysqli_query($conn, $sql);
	}
}


// 접속 종료
mysqli_close($conn);
?>
