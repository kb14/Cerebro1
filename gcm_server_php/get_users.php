<?php


if(isset($_GET["latitude"]) && isset($_GET["longitude"])){
	include_once './db_functions.php';
	$db = new DB_Functions();
	$distance = 0;
	$one = 1;
	$latitude = $_GET["latitude"];
	$longitude = $_GET["longitude"];
	$result = mysql_query("SELECT * FROM cerebro_users WHERE online = $one") or die(mysql_error());

	// check for empty result
	if (mysql_num_rows($result) > 0) {
		// Response Array
		$response["users"] = array();
		
		while ($row = mysql_fetch_array($result)) {
		
			$lat = $row["latitude"];
			$lon = $row["longitude"];
			
			$distance = $db->getDistanceBetweenPointsNew(floatval($latitude), floatval($longitude), floatval($lat), floatval($lon));
			if($distance < 2){
				$user = array();
				
				$user["id"] = $row["id"];
				$user["gcm_regid"] = $row["gcm_regid"];
				$user["name"] = $row["name"];
				$user["latitude"] = $lat;
				$user["longitude"] = $lon;
				
				//push single user into final response array
				array_push($response["users"], $user);
			}
		}

		// success
		$response["success"] = 1;
		$response["distance"] = $distance;
		// echoing JSON response
		echo json_encode($response);
	}
	else{
		// no users found
		$response["success"] = 0;
		$response["message"] = "No users found";

		// echo no users JSON
		echo json_encode($response);
	}
}

?>