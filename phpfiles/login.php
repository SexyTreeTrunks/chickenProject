<?php

//데이터베이스 접근하기 위한 부분
$db_host = "localhost";
$db_user = "root";
$db_passwd = "clzls";
$db_name = "chicken";
$conn = mysqli_connect($dbhost, $db_user, $db_passwd, $db_name);
if(mysqli_connect_errno($conn)) {
	echo "데이터베이스 연결 실패: ".mysqli_connect_error();
}

//아이디와 비밀번호를 POST방식으로 받음
$id = $_POST['id'];
$password = $_POST['password'];

//입력받은 아이디가 존재하는지 체크
$sql = "SELECT userId FROM user WHERE userId = '$id'";
$result = mysqli_query($conn, $sql);
$total_record = mysqli_num_rows($result);

for($i=0; $i<1; $i++) {
	mysqli_data_seek($result, $i);
	$row = mysqli_fetch_array($result);
	
	if( !strcmp($row[userId], $id)){
		$sql = "SELECT password FROM user WHERE userId = '$id' AND password = '$password'";
		$result = mysqli_query($conn, $sql);
		$total_record = mysqli_num_rows($result);
		
		if($total_record){
			echo "SUCCESS";
		}
	}
}

//접속 종료
mysqli_close($conn);

?>

