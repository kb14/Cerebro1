<?php
 
// response json
$json = array();
 
/**
 * Registering a user device
 * Store reg id in users table
 */
if (isset($_POST["name"]) && isset($_POST["password"]) && isset($_POST["regId"])) {
    $name = $_POST["name"];
    $password = $_POST["password"];
    $gcm_regid = $_POST["regId"]; // GCM Registration ID
	
	// response Array
    $response = array("success" => 0, "error" => 0);
    // Store user details in db
    include_once './db_functions.php';
    include_once './GCM.php';
 
    $db = new DB_Functions();
    $gcm = new GCM();
	
	if ($db->isUserExisted($name)) {
            // user is already existed - error response
            $response["error"] = 2;
            $response["error_msg"] = "Username already exists";
            echo json_encode($response);
        }
	else{
		$one = 1;
		$res = $db->storeUser($name, $password, $gcm_regid);
	 
		$registatoin_ids = array($gcm_regid);
		$message = array("product" => "shirt");
	 
		//$result = $gcm->send_notification($registatoin_ids, $message);
		
		
		// check for user
		$user = $db->getUserByName($name);
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
		}
		else{
			// user not found
			// echo json with error = 1
			$response["error"] = 1;
			$response["error_msg"] = "Username not found!";
		}
		echo json_encode($response);
	}
} else {
    // user details missing
}
?>