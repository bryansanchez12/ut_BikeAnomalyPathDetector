<?php

function submitAccToDB() {
    $conn = new mysqli('localhost', 'id17557197_bryansanchez', '@E%nSCF|R24bkoQy', 'id17557197_ss_challenge1');
    $response = array();
    // Data From the mobile phone
    $userID = $_POST["userID"];

    // Data From the GPS sensor
    $gpsLatitude  = $_POST["gpsLatitude"];
    $gpsLongitude = $_POST["gpsLongitude"];


    $aType  = $_POST["aType"];
    $accMagnitude1  = $_POST["accMagnitude"];

    //$db = new DB_Connect();

    // Query -> Table: Accelerometer_Data
    $query = "INSERT INTO Accelerometer_Data(UserID,Magnitude) VALUES('$userID','$accMagnitude1')";
    $result = $conn -> query($query) or die(mysqli_error($conn));
    if ($result) {
        $response["error"] = false;
        $response["message"] = "Measures from the accelerometer sensor was added successfully!";
    } else {
        $response["error"] = true;
        $response["message"] = "Failed to add data from the accelerometer sensor!";
    }

    // echo json response
    echo json_encode($response);

    // Query -> Table: GPS_Data
    $query = "INSERT INTO GPS_Data(UserID,Latitude,Longitude) VALUES('$userID','$gpsLatitude','$gpsLongitude')";
    $result = $conn->query($query) or die(mysqli_error($conn));
    if ($result) {
        $response["error"] = false;
        $response["message"] = "Measures from the GPS sensor was added successfully!";
    } else {
        $response["error"] = true;
        $response["message"] = "Failed to add data from the GPS sensor!";
    }

    // echo json response
    echo json_encode($response);

    // Query -> Table: Anomalies
    $query = "INSERT INTO Anomalies(UserID,Latitude,Longitude,type) VALUES('$userID','$gpsLatitude','$gpsLongitude', '$aType')";
    $result = $conn->query($query) or die(mysqli_error($conn));
    if ($result) {
        $response["error"] = false;
        $response["message"] = "Anomalies measures were added successfully!";
    } else {
        $response["error"] = true;
        $response["message"] = "Failed to add data from the anomaly!";
    }

    // echo json response
    echo json_encode($response);

    $conn->close();
}
submitAccToDB();
?>