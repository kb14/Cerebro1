<?php
/**
 * Logging in user to a system
 * and getting his regid and other details. First check if user present in db using the getUserByName() function
 */
 
 if (isset($_POST["name"]) && isset($_POST["password"])) {
	$name = $_POST["name"];
	$password = $_POST["password"];
	$one = 1;
	include_once './db_functions.php';
    $db = new DB_Functions();
	// response Array
    $response = array("success" => 0, "error" => 0);
	
	// check for user
    $user = $db->getUserByName($name, $password);
	if ($user != false) {
		// user found
        // echo json with success = 1
        $response["success"] = 1;
		$response["user"]["name"] = $user["name"];
		$response["user"]["id"] = $user["id"];
		$response["user"]["latitude"] = $user["latitude"];
		$response["user"]["longitude"] = $user["longitude"];
		$response["user"]["regid"] = $user["gcm_regid"];
		mysql_query("UPDATE cerebro_users SET online=$one WHERE name = '$name'") or die(mysql_error());
		echo json_encode($response);
	}
	else{
		// user not found
		// echo json with error = 1
		$response["error"] = 1;
		$response["error_msg"] = "Username not found!";
		echo json_encode($response);
	}
	
	
 
 }

?>