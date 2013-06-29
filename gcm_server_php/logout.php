<?php

// Log outs a user with serverid from the server by setting online = 0

if (isset($_POST["serverid"])){
	
	$serverid = $_POST["serverid"];
	include_once './db_functions.php';
	$db = new DB_Functions();
	$zero = 0;
	// response Array
    $response = array("success" => 0, "error" => 0);
	$result = mysql_query("UPDATE cerebro_users SET online='$zero' WHERE id = '$serverid'") or die(mysql_error());
	if($result){
		$response["success"] = 1;
	}
	else{
		$response["error"] = 1;
	}

	echo json_encode($response);
}

?>