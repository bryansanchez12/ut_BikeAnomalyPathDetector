<?php

function GetAnomaliesFromDB(){
    $conn = new mysqli('localhost', 'id17557197_bryansanchez', '@E%nSCF|R24bkoQy', 'id17557197_ss_challenge1');
    $response = array();


    $stmt = $conn->prepare("SELECT * FROM Anomalies");
    $stmt->execute();
    $result = $stmt->get_result();
    $outp = $result->fetch_all(MYSQLI_ASSOC);
    $conn->close();
    echo json_encode($outp);
}

GetAnomaliesFromDB();
?>