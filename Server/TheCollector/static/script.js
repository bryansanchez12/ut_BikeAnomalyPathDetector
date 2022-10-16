/*
    Script Sheet
 */

var anomalyCounter = 1;
var anomalies = [];
var latitudes = [];
var longitudes = [];
var anomalyTypes = [];
var tableRows = [];
var map;
var urlDB = "https://thecollector12.000webhostapp.com";



// Initialize and add the map
function initMap() {
      // The location of Uluru
      const center = { lat: 52.241800, lng: 6.853297 };
      // The map, centered at Uluru
      map = new google.maps.Map(document.getElementById("map"), {
        zoom: 14.5,
        center: center,
      });
    getAnomalies();
}

function addAnomalyMarker(latitude, longitude){
      const anomaly = { lat: latitude, lng: longitude };
      // The marker, positioned at Uluru
      const marker = new google.maps.Marker({
        position: anomaly,
        map: map,
      });


}

function getAnomalies(){

    var server = window.location.href;
    var	http = new XMLHttpRequest();
    var txt = "", x, row;
    var table = document.getElementById("tAnomalies");

    http.onreadystatechange = function() {
        if (this.readyState == 4 && this.status == 200) {

            anomalies = JSON.parse(this.responseText);

            for (x in anomalies['anomalies']) {

                // Get data from the DataBase
                //anomalies.push(anomalies['anomalies'][x].anomalyID.toString());
                // anomalies.push(anomalies['anomalies'][x].userID.toString());
                const lat = anomalies['anomalies'][x].latitude1.toString();
                const lon = anomalies['anomalies'][x].longitude1.toString();
                const type = anomalies['anomalies'][x].type1.toString();
                latitudes.push(lat);
                longitudes.push(lon);
                anomalyTypes.push(type);

                // Add markers to map
                addAnomalyMarker(parseFloat(lat), parseFloat(lon));

                // Create a new row inside the table
                row = "<tr>\n" +
                    "      <th scope=\"row\">" + anomalyCounter + "</th>\n" +
                    "      <td>" + type + "</td>\n" +
                    "      <td> <button id=\"abtn" + anomalyCounter + "\" " +
                                "onclick=\"displayMarker(" + lat + ", " + lon + ")\" " +
                                " \" type=\"button\" class=\"btn btn-secondary\">Locate</button> </td>\n" +
                    "   </tr>";

                tableRows.push(row);

                // increase the anomaly counter
                anomalyCounter += 1;
            }
            addAnomaliesToTable();
        }
    };
    http.open("GET", server + "getAnomalies", true);
    http.send();
}

function addAnomaliesToTable(){
    var table = document.getElementById("tAnomalies");
    var text = ""
    console.log(tableRows)
    var count = 0;

    for (x in tableRows){
        text += tableRows[count];
        count += 1;
    }
    
    table.innerHTML = text
    console.log(text)
}

function displayMarker(latitude, longitude){
    const anomaly = { lat: latitude, lng: longitude };
    map.setCenter(anomaly);
    map.setZoom(18);
}

