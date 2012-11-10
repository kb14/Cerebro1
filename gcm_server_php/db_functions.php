<?php
 
class DB_Functions {
 
    private $db;
 
    //put your code here
    // constructor
    function __construct() {
        include_once './db_connect.php';
        // connecting to database
        $this->db = new DB_Connect();
        $this->db->connect();
    }
 
    // destructor
    function __destruct() {
 
    }
	/**
     * Check user is existed or not
     */
    public function isUserExisted($name) {
        $result = mysql_query("SELECT name from cerebro_users WHERE name = '$name'");
        $no_of_rows = mysql_num_rows($result);
        if ($no_of_rows > 0) {
            // user existed 
            return true;
        } else {
            // user not existed
            return false;
        }
    }
 
    /**
     * Storing new user
     * returns user details
     */
    public function storeUser($name, $password, $gcm_regid) {
		$hash = $this->hashSSHA($password);
		$encrypted_password = $hash["encrypted"]; // encrypted password
        $salt = $hash["salt"]; // salt
        // insert user into database
        $result = mysql_query("INSERT INTO cerebro_users(name, encrypted_password, salt, gcm_regid, created_at) VALUES('$name', '$encrypted_password', '$salt', '$gcm_regid', NOW())");
        // check for successful store
        if ($result) {
            // get user details
            $id = mysql_insert_id(); // last inserted id
            $result = mysql_query("SELECT * FROM gcm_users WHERE id = $id") or die(mysql_error());
            // return user details
            if (mysql_num_rows($result) > 0) {
                return mysql_fetch_array($result);
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
	
	/**
     * Getting a user by name
     */
	 
	 public function getUserByName($name, $password){
		$result = mysql_query("SELECT * FROM cerebro_users WHERE name = '$name'") or die(mysql_error());
        // check for result 
        $no_of_rows = mysql_num_rows($result);
		if ($no_of_rows > 0) {
            $result = mysql_fetch_array($result);
			$salt = $result['salt'];
            $encrypted_password = $result['encrypted_password'];
			$hash = $this->checkhashSSHA($salt, $password);
			// check for password equality
            if ($encrypted_password == $hash) {
                // user authentication details are correct
                return $result;
            }
            
        } else {
            // user not found
            return false;
        }
	 }
 
    /**
     * Getting all users
     */
    public function getAllUsers() {
        $result = mysql_query("select * FROM gcm_users");
        return $result;
    }
	/**
     * Encrypting password
     * @param password
     * returns salt and encrypted password
     */
    public function hashSSHA($password) {

        $salt = sha1(rand());
        $salt = substr($salt, 0, 10);
        $encrypted = base64_encode(sha1($password . $salt, true) . $salt);
        $hash = array("salt" => $salt, "encrypted" => $encrypted);
        return $hash;
    }

    /**
     * Decrypting password
     * @param salt, password
     * returns hash string
     */
    public function checkhashSSHA($salt, $password) {

        $hash = base64_encode(sha1($password . $salt, true) . $salt);

        return $hash;
    }
 
}
 
?>