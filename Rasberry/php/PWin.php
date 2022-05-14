<?php
 
    $con = mysqli_connect("localhost", "dietplan", "diet001", "dietplan");
    mysqli_query($con,'SET NAMES utf8');
    
    $userID = $_POST["userID"];
    $userPW = $_POST["userPass"];
    
    $statement = mysqli_prepare($con, "UPDATE user SET userPW = ? WHERE userID = ?");
    mysqli_stmt_bind_param($statement, "ss", $userPW, $userID);
    mysqli_stmt_execute($statement);
 
    mysqli_commit($con);
 
    $response = array();
    $response["success"] = true;
 
    echo json_encode($response);
 
 
 
?>
