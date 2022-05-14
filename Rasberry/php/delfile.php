<?php
 
    $con = mysqli_connect("localhost", "dietplan", "diet001", "dietplan");
    mysqli_query($con,'SET NAMES utf8');
 
    $NO = $_POST["NO"];
 
    $statement = mysqli_prepare($con, "DELETE FROM foodimg WHERE NO = ?");
    mysqli_stmt_bind_param($statement, "s", $NO);
    mysqli_stmt_execute($statement);
 
    mysqli_commit($con);
 
    $response = array();
    $response["success"] = true;
 
    echo json_encode($response);
 
 
 
?>
