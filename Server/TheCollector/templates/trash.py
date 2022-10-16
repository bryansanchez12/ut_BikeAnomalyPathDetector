import MySQLdb
import numpy as np
import requests
import json


anomalies = []
accMeasures = np.array([], dtype=int)
gpsCoordinates = np.array([], dtype=int)


def initDataFromDB():
    connection = MySQLdb.connect(
        host='localhost',
        port=3306,
        user='id17557197_bryansanchez',
        passwd='@E%nSCF|R24bkoQy'
        # RMVDOIqfHRD]1na     (L\\7uY]8ov5SLrzn  @E%nSCF|R24bkoQy   Nc6Yu8fMFh-n   kxIVuMS7RxYibcwz
        #db='id17557197_smart_spaces'  # id17557197_ss_challenge1   id17557197_smart_spaces
    )

    # collector = mysql.connector.connect(
    #    host='localhost',
    #    user='id17557197_bryansanchez',
    #    password='(L\\7uY]8ov5SLrzn',
    #    database='id17557197_ss_challenge1'
    # )

    dbCursor = connection.cursor()
    # dbCursor.execute("USE id17557197_ss_challenge1")
    dbCursor.execute("SELECT * FROM Anomalies")

    result = dbCursor.fetchall()

    for x in result:
        print(x)
        anomalies.append(x)

    connection.close()
    return result


def getAccMeasures():
    x = 2
    measures = []
    for x in measures:
        accMeasures.append(x)


def getGPSCoordinates():
    y = 3
