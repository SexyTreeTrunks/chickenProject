
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

$restaurantName = $_POST['restaurantName'];
$userId = $_POST['userId'];
$ratingStars = $_POST['ratingStars'];
$contents = $_POST['contents'];

$sql = "INSERT INTO review (restaurantName, userId, ratingStars, contents) VALUES ('$restaurantName', '$userId', '$ratingStars', '$contents')";
$result = mysqli_query($conn, $sql);

if($result) {
	echo 'success';
}
else {
	echo 'failure';
	echo $restaurantName;
	echo $userId;
	echo $ratingStars;
	echo $contents;
}


// 접속 종료
mysqli_close($conn);
?>
