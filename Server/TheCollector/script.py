import MySQLdb
import numpy as np
import requests
import json


URL_DB = "http://maps.googleapis.com/maps/api/geocode/json"
anomalies = []


def getAnomalies():
    URL = "https://thecollector12.000webhostapp.com/GetAnomaliesFromDB.php"
    r = requests.get(url=URL)

    # extracting data in json format
    data = r.json()

    temp = {'anomalies': []}
    for x in data:
        y = {"anomalyID": str(x['AnomalyID']),
             "userID": str(x['UserID']),
             "latitude1": str(x['Latitude']),
             "longitude1": str(x['Longitude']),
             "type1": str(x['type'])}

        temp['anomalies'].append(y)

    json.dumps(temp, sort_keys=True, indent=4)
    return temp
