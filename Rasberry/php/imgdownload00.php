<?php
    $con = mysqli_connect("localhost", "dietplan", "diet001", "dietplan");
    mysqli_query($con,'SET NAMES utf8');

  	$startdate = [];
	$query = "SELECT NO, picture1, fname1 FROM foodimg";
	$result = mysqli_query($con, $query);

	$response = array();

	while ($row = mysqli_fetch_array($result)) {
		array_push($response,
            array('NO' => $row['NO'],
                'picture1' => $row['picture1'],
                'food1' => $row['fname1']
                )
                );
		}
	echo json_encode($response);
?>
