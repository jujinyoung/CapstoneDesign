<?php
    $con = mysqli_connect("localhost", "dietplan", "diet001", "dietplan");
    mysqli_query($con,'SET NAMES utf8');

  	$NO = "'".$_POST['NO']."'";
	$query = "SELECT * FROM foodimg WHERE NO = ".$NO;
	$result = mysqli_query($con, $query);

	$response["success"] = false;

	while ($row = mysqli_fetch_array($result)) {
		$response["success"] = true;
		$response["_id"] = $row["id"];
        $response["picture0"] = $row["picture0"];
        $response["picture1"] = $row["picture1"];
        $response["picture2"] = $row["picture2"];
        $response["picture3"] = $row["picture3"];
        $response["tot_kcal"] = $row["tot_kcal"];
        $response["food0"] = $row["fname0"];
        $response["food1"] = $row["fname1"];
        $response["food2"] = $row["fname2"];
        $response["food3"] = $row["fname3"];
		}
	
	echo json_encode($response);

?>
