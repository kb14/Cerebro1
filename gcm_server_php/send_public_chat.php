<?php

if (isset($_POST["name"]) && isset($_POST["message"]) && isset($_POST["sid"]) && isset($_POST["latitude"]) && isset($_POST["longitude"])) {
	
	include_once './db_functions.php';
    include_once './GCM.php';
	$db = new DB_Functions();
    $gcm = new GCM();
	
	// response Array
    $response = array("success" => 0, "error" => 0);
	
	$name = $_POST["name"]; // Name of one who sent the message
    $message = $_POST["message"]; // The message
	$sid = $_POST["sid"]; //The user who sent it server id
	$latitude = $_POST["latitude"];
	$longitude = $_POST["longitude"];
	$distance = 500;
	$test = "haha";
	
	$one = 1;
	$negone = -1;
	$message = array("price" => $message, "sid" => $negone, "name" => $name);
	$gcm_regid = "nothini";
	
	$result = mysql_query("SELECT * FROM cerebro_users WHERE online = $one") or die(mysql_error());
	
	// check for empty result
	if (mysql_num_rows($result) > 0) {
		
		$registration_ids = array();
	
		while ($row = mysql_fetch_array($result)) {
			$lat = $row["latitude"];
			$lon = $row["longitude"];
			$distance = $db->getDistanceBetweenPointsNew(floatval($latitude), floatval($longitude), floatval($lat), floatval($lon));
			if($distance < 2 && $sid != $row["id"]){
				//$test = "tumul";
				$gcm_regid = $row["gcm_regid"];
				array_push($registration_ids,$gcm_regid);
			}
		}
		
		//TODO : If online == 1 only then send message
		$result = $gcm->send_notification($registration_ids, $message);
		// success
		$response["success"] = 1;
		$response["test"] = $registration_ids;
		// echoing JSON response
		echo json_encode($response);
	}
	else{
		// no users found
		$response["error"] = 1;
		$response["message"] = "No users found";

		// echo no users JSON
		echo json_encode($response);
	}
	
}

?>