<?php
    $con = mysqli_connect("localhost", "dietplan", "diet001", "dietplan");
    mysqli_query($con, 'SET NAMES utf8');
 
    $userID = $_POST["userID"];
    $userPW = $_POST["userPass"];
    
    $statement = mysqli_prepare($con, "SELECT userID, userPW FROM user WHERE userID = ? AND userPW = ?");
    mysqli_stmt_bind_param($statement, "ss", $userID, $userPW);
    mysqli_stmt_execute($statement);
 
    mysqli_stmt_bind_result($statement, $userID, $userPW);
 
    $response = array();
    $response["success"] = false;
 
    while(mysqli_stmt_fetch($statement)) {
        $response["success"] = true;
        $response["userID"] = $userID;
        $response["userPass"] = $userPW;       
    }
 
    echo json_encode($response);
 
?>
