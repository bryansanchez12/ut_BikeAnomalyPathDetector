<?php

function GetFromDB(){
    $conn = new mysqli('localhost', 'id17557197_bryansanchez', '(L\7uY]8ov5SLrzn', 'id17557197_ss_challenge1');
    $response = array();


    $stmt = $conn->prepare("SELECT * FROM Accelerometer_Data");
    $stmt->execute();
    $result = $stmt->get_result();
    $outp = $result->fetch_all(MYSQLI_ASSOC);
    $conn->close();
    echo json_encode($outp);
}

GetFromDB();
?>