package jcpsnowproductions.mykotlinapp

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity;
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.content_activity_add_vehicle.*

import java.net.*

import kotlinx.android.synthetic.main.activity_add_vehicle.*
import kotlinx.android.synthetic.main.content_activity_add_vehicle.*
import kotlinx.android.synthetic.main.content_activity_add_vehicle.view.*
import kotlinx.android.synthetic.main.content_main.*
import org.json.JSONArray
import java.lang.NumberFormatException
import java.util.*
import kotlin.collections.ArrayList

import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class ActivityAddVehicle : AppCompatActivity() {

    val CONNECTON_TIMEOUT_MILLISECONDS = 60000
    private var fileDir = ""

    private var fileName = "data.dat"

    private var fullPath = ""


    private var yearInt = 1800
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_vehicle)
        setSupportActionBar(toolbar)

        fileDir = applicationContext.filesDir.toString()
        fullPath = fileDir + "/" + fileName
        var vhList = ArrayList<vehicle>()

        logd("Activity to add vehicle started")
        //decodeVin("5UXWX7C5*BA", 2011)

        editVehicleVin.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                logd(count.toString())
                if (count == 17)
                {
                    decodeVin(editVehicleVin.getText().toString(), 1800)
                }

            }
        })

        buttonAddSubmit.setOnClickListener {view ->
            //logd("Your first vehicle was added!")
            //Need to add validation code in and then push to new Intent (back to home screen probably).

            if (validateInput()) {
                //editVehicleYear.getText().toString().toInt()
                var vh = vehicle(
                    editVehicleName.getText().toString(),
                    editVehicleVin.getText().toString(),
                    yearInt,
                    editVehicleMake.getText().toString(),
                    editVehicleModel.getText().toString(),
                    editVehicleStyle.getText().toString(),
                    editVehicleColor.getText().toString(),
                    editVehicleEngine.getText().toString(),
                    editVehicleNotes.getText().toString()
                )
                //vhList.add(vh)
                //Temporary removal of the save functionality
                //SerializableManager.saveSerializable(applicationContext, vhList, fileName)
                Snackbar.make(view, "The name of the vehicle is ${vh.vName} and the year is: ${vh.vYear}", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()

                decodeVin(vh.vVin, vh.vYear)
            }
            else {
                Snackbar.make(view, "You must at least name your vehicle.", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
            }
        }




    /*fab.setOnClickListener { view ->
        Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
            .setAction("Action", null).show()
    }*/
    }

    fun updateFields(resultJson: JSONObject)
    {
        //logd(resultJson.toString())
        //var gsonResult: List<Results> = Gson().fromJson(resultJson.to, Array<Results>::class.java).toList()
        //val results = resultJson.getJSONObject("Results")
        //logd(results.toString())
        //var jo: JSONObject = JSONObject()
        //jo.put("Results", resultJson)
        var make: String = ""
        var model: String = ""
        var style: String = ""
        var year: Int = 0


        val results: JSONArray = resultJson.getJSONArray("Results")
       // val resultObj: String = results[0].getJ


        for (i in 0..(results.length() - 1)) {
            val item = results.getJSONObject(i)
            //Finish creating my key/value map

            val currVar: String = item.get("Variable").toString()

            if (currVar.equals("Make"))
            {
                make = item.get("Value").toString()
                editVehicleMake.setText(make)
            }
            else if (currVar.equals("Model"))
            {
                model = item.get("Value").toString()
                editVehicleModel.setText(model)
            }
            else if (currVar.equals("Trim"))
            {
                style = item.get("Value").toString()
                editVehicleStyle.setText(style)
            }
            else if (currVar.equals("Model Year"))
            {
                year = item.get("Value").toString().toInt()
                editVehicleYear.setText(year.toString())
            }
        }


    }

    fun decodeVin(vinNumber: String, yearNumber: Int) {

        //var urlString = "https://vpic.nhtsa.dot.gov/api/vehicles/decodevinvaluesextended/$vinNumber?format=json&modelyear=$yearNumber"
        var urlString: String
        if (yearNumber == 1800)
        {
            urlString = "https://vpic.nhtsa.dot.gov/api/vehicles/decodevin/$vinNumber?format=json"
        }
        else {
            urlString = "https://vpic.nhtsa.dot.gov/api/vehicles/decodevin/$vinNumber?format=json&modelyear=$yearNumber"
        }
        GetVehicleAsyncTask().execute(urlString)
        //var gsonResult: List<Results> = Gson().fromJson(result, Array<Results>::class.java).toList()
    }

    fun validateInput(): Boolean
    {
        if (editVehicleName.getText() != null && editVehicleName.getText().toString().trim().length > 0)
        {
            try {
                yearInt = editVehicleYear.getText().toString().toInt()
                if (yearInt > 1800 && yearInt <= (Calendar.getInstance().get(Calendar.YEAR) + 1))
                {
                    //Year between 1800 and current year + 1 was entered
                }
                else
                {
                    //Invalid year was entered, so we will use 1800
                    yearInt = 1800
                }
            }
            catch (nfe: NumberFormatException) {
                //If user didnt enter a valid year.
                yearInt = 1800
            }
        //If a valid vehicle name was entered,
        return true
        }
        //Vehicle Name is required, so return false if not provided.
        else {
            return false
        }
    }

    fun Activity.logd(message: String) {
        if (BuildConfig.DEBUG) Log.d(this::class.java.simpleName, message)
    }

    inner class GetVehicleAsyncTask : AsyncTask<String, String, String>() {
        override fun onPreExecute() {
            //super.onPreExecute()
            logd("onPreExecute")
        }

        override fun doInBackground(vararg urls: String?): String {
            var urlConnection: HttpURLConnection? = null

            try {
                val url = URL(urls[0])

                urlConnection = url.openConnection() as HttpURLConnection
                urlConnection.connectTimeout = CONNECTON_TIMEOUT_MILLISECONDS
                urlConnection.readTimeout = CONNECTON_TIMEOUT_MILLISECONDS

                var inString = streamToString(urlConnection.inputStream)
                publishProgress(inString)
            }
            catch (ex: Exception) {

            }
            finally {
                if (urlConnection != null) {
                    urlConnection.disconnect()
                }
            }
            return " "
        }

        override fun onProgressUpdate(vararg values: String?) {
            //super.onProgressUpdate(*values)
            //logd("onProgressUpdate values: ${values.size}")
            try {
                var json = JSONObject(values[0])
                /*val query = json.getJSONObject("query")
                val results = query.getJSONObject("results")
                */
                //This is where i need to update the other fields.
                updateFields(json)
            }
            catch (ex: Exception) {
                logd("Catch Exception (onProgressUpdate): ${ex.message.toString()}")
            }
        }

        override fun onPostExecute(result: String?) {
            //Done
        }
    }


    fun streamToString(inputStream: InputStream): String {

        val bufferReader = BufferedReader(InputStreamReader(inputStream))
        var line: String
        var result = ""

        try {
            do {
                line = bufferReader.readLine()
                if (line != null) {
                    result += line
                }
            } while (line != null)
            inputStream.close()
        } catch (ex: Exception) {

        }

        return result
    }
}
