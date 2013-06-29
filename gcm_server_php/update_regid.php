<?php

// Updates gcm registration id of the user with the given serverid. Read about GCM to know more about how it works
// and what its registration id is etc.

if (isset($_POST["serverid"]) && isset($_POST["regId"])){
	
	$serverid = $_POST["serverid"];
	$regId = $_POST["regId"];
	include_once './db_functions.php';
	$db = new DB_Functions();
	// response Array
    $response = array("success" => 0, "error" => 0);
	
	$result = mysql_query("UPDATE cerebro_users SET gcm_regid='$regId' WHERE id = '$serverid'") or die(mysql_error());
	if($result){
		$response["success"] = 1;
	}
	else{
		$response["error"] = 1;
	}

	echo json_encode($response);

}

?>