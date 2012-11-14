<?php

if (isset($_POST["name"])){
	
	$name = $_POST["name"];
	$one = 1;
	include_once './db_functions.php';
    $db = new DB_Functions();
	// response Array
    $response = array("success" => 0, "error" => 0);
	
	$result = mysql_query("SELECT * FROM cerebro_users WHERE online = $one AND name = $name") or die(mysql_error());
	
	// check for empty result
	if (mysql_num_rows($result) > 0) {
		// success
		$response["success"] = 1;
		$result = mysql_fetch_array($result);
		
		$response["latitude"] = $result['latitude'];
		$response["longitude"] = $result['longitude'];
		
		echo json_encode($response);
	}
	else{
		// no users found
		$response["error"] = 1;
		$response["message"] = "User not found";

		// echo no users JSON
		echo json_encode($response);
	}
}

?>