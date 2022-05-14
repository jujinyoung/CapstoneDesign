<?php 
    $con = mysqli_connect("localhost", "dietplan", "diet001", "dietplan");
    mysqli_query($con,'SET NAMES utf8');

	$id = $_POST["_id"];
    $picture0 = $_POST["picture0"];
    $picture1 = $_POST["picture1"];
    $picture2 = $_POST["picture2"];
    $picture3 = $_POST["picture3"];
    $tot_kcal = $_POST["tot_kcal"];
    $fname0 = $_POST["food0"];
    $fname1 = $_POST["food1"];
    $fname2 = $_POST["food2"];
    $fname3 = $_POST["food3"];
    
    $statement = mysqli_prepare($con, "INSERT INTO foodimg(id, picture0, picture1, picture2, picture3, tot_kcal, fname0, fname1, fname2, fname3)VALUES ('$id','$picture0','$picture1','$picture2','$picture3','$tot_kcal','$fname0','$fname1','$fname2','$fname3')");

    mysqli_stmt_execute($statement);
 
    mysqli_commit($con);
    $response = array();
    $response["success"] = true;
 
    echo json_encode($response);
?>
