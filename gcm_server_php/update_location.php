<?php

//Updates latitude/longitude of the user corresponding to the sent serverid. Pretty intuitive code

if (isset($_POST["serverid"]) && isset($_POST["latitude"]) && isset($_POST["longitude"])){

	$serverid = $_POST["serverid"];
	$latitude = $_POST["latitude"];
	$longitude = $_POST["longitude"];
	include_once './db_functions.php';
	$db = new DB_Functions();
	// response Array
	$response = array("success" => 0, "error" => 0);

	$result = mysql_query("UPDATE cerebro_users SET latitude='$latitude', longitude='$longitude' WHERE id = '$serverid'") or die(mysql_error());
	if($result){
		$response["success"] = 1;
	}
	else{
		$response["error"] = 1;
	}

	echo json_encode($response);
	
}
?>