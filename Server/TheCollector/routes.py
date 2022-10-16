from flask import render_template, request, redirect, url_for

from TheCollector import app
from TheCollector.script import getAnomalies


@app.route('/login', methods=['GET', 'POST'])
def login():
    if request.method == 'POST':
        if request.form['inputEmail'] == "admin@admin.com" and request.form['inputPassword'] == "password":
            return redirect(url_for('home'))
        else:
            return redirect(url_for('login'))
    else:
        return render_template('login.html')


@app.route('/')
def home():
    return render_template('home.html')


@app.route('/getAnomalies', methods=["GET"])
def getAnomaliesData():
    return getAnomalies()

