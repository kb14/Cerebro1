<?php
 
// response json
$json = array();
 
/**
 * Sending a chat message
 * Store reg id in users table
 */
if (isset($_POST["regId"]) && isset($_POST["message"]) && isset($_POST["sid"])) {
    // response Array
    $response = array("success" => 0, "error" => 0);
	
	$gcm_regid = $_POST["regId"]; // Send to: GCM Registration ID
    $message = $_POST["message"]; // The message
	$sid = $_POST["sid"]; //The user who sent it
	
	include_once './db_functions.php';
    include_once './GCM.php';
 
    $db = new DB_Functions();
    $gcm = new GCM();
 
    $registatoin_ids = array($gcm_regid);
	$message = array("price" => $message, "sid" => $sid);
   
	//TODO : If online == 1 only then send message
    $result = $gcm->send_notification($registatoin_ids, $message);
	
	if($result != false){
		$response["success"] = 1;
	}
	else{
		$response["error"] = 1;
	}
    echo json_encode($response);
} else {
    // user details missing
}
?>