<?php
    $con = mysqli_connect("localhost", "dietplan", "diet001", "dietplan");
    mysqli_query($con, 'SET NAMES utf8');
 
    $userID = $_POST["userID"];
    
    $statement = mysqli_prepare($con, "SELECT userID FROM user WHERE userID = ?");
    mysqli_stmt_bind_param($statement, "s", $userID,);
    mysqli_stmt_execute($statement);
 
    mysqli_stmt_bind_result($statement, $userID);
 
    $response = array();
    $response["success"] = false;
 
    while(mysqli_stmt_fetch($statement)) {
        $response["success"] = true;
        $response["userID"] = $userID;      
    }
 
    echo json_encode($response);
 
?>
