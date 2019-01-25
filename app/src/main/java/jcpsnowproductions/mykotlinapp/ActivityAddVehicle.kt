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
import android.view.View
import android.view.WindowManager
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
import java.io.File
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class ActivityAddVehicle : AppCompatActivity() {

    val CONNECTON_TIMEOUT_MILLISECONDS = 60000
    private var fileDir = ""

    private var fileName = "data.dat"

    private var fullPath = ""

    var vhList: ArrayList<vehicle> = ArrayList<vehicle>()


    private var yearInt = 1800
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_vehicle)
        setSupportActionBar(toolbar)

        //Make Progress bar invisible
        progressVINAPI.visibility = View.GONE

        fileDir = applicationContext.filesDir.toString()
        fullPath = fileDir + "/" + fileName

        //vhList = ArrayList<vehicle>()
        var file = File(fullPath)
        if (file.exists()) {
            logd("File Exists")
            vhList = SerializableManager.readSerializable<ArrayList<vehicle>>(applicationContext, fileName)

        }

        else
        {
            logd("File doesn't exist")
            //vhList = ArrayList<vehicle>()
        }


        //decodeVin("5UXWX7C5*BA", 2011)

        /*
        When the vin text field is changed, check how many characters are there.
        Once it reaches 17, then try to decode the VIN via (API) call
         */
        editVehicleVin.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                var currentCount: Int = editVehicleVin.text.toString().length
                logd(currentCount.toString())
                if (currentCount == 17)
                {
                    decodeVin(editVehicleVin.getText().toString(), 1800)
                }

            }
        })

        /*Submit the addition of a new vehicle.
            1. Get current list of saved vehicles
            2. Create a new vehicle object
            3. Add new vehicle object to list
            4. Serializes the vehicle list and save to user's device (in future should also save to cloud)
            5. Switch to main activity where a new button (to that vehicle) should be added
        */
        buttonAddSubmit.setOnClickListener {view ->
            //Need to add validation code in and then push to new Intent (back to home screen probably).

            if (validateInput()) {
                //editVehicleYear.getText().toString().toInt()
                var vh = vehicle(
                    createVehicleID(),
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
                vhList.add(vh)

                SerializableManager.saveSerializable(applicationContext, vhList, fileName)
                Snackbar.make(view, "The name of the vehicle is ${vh.vName} and the year is: ${vh.vYear}", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()

                //Return to main activity
                val intent = MainActivity.newIntent(this, vh)
                startActivity(intent)

            }
            else {
                Snackbar.make(view, "You must at least name your vehicle.", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
            }
        }

    }

    //creates a random vehicle ID and returns it if the current list of vehicles doesn't include it otherwise tries again.
    fun createVehicleID(): Int
    {

        var rand = Random()
        var randNum = rand.nextInt(9999)
        var exists: Boolean = false

        for (i in 0..(vhList.count() - 1)) {
            if (randNum == vhList[i].vID)
            {
                exists = true
                break
            }
        }
        if (exists)
            return createVehicleID()
        else
            return randNum

    }

    //At the end of the onProgressUpdate of GetVehicleAsyncTask, this function is called to update the Make, Model, Style, Year, and possibly vehicle name based on the JSON results of the VIN call
    fun updateFields(resultJson: JSONObject)
    {
        var make: String = ""
        var model: String = ""
        var style: String = ""
        var year: Int = 0

        val results: JSONArray = resultJson.getJSONArray("Results")

        //Loop through each JSONObject {} within the Results JSONArray
        //If the variable is something we are interested in, pull it out and update that field.
        for (i in 0..(results.length() - 1)) {
            val item = results.getJSONObject(i)

            val currVar: String = item.get("Variable").toString()

            if (currVar.equals("Make"))
            {
                make = item.get("Value").toString()
                if (make.equals("null"))
                    make = ""
                editVehicleMake.setText(make)
            }
            else if (currVar.equals("Model"))
            {
                model = item.get("Value").toString()
                if (model.equals("null"))
                    model = ""
                editVehicleModel.setText(model)
            }
            else if (currVar.equals("Trim"))
            {
                style = item.get("Value").toString()
                if (style.equals("null"))
                    style = ""
                editVehicleStyle.setText(style)
            }
            else if (currVar.equals("Model Year"))
            {
                year = item.get("Value").toString().toInt()
                if (year.equals("null"))
                    year = 0
                editVehicleYear.setText(year.toString())
            }
        }

        logd(editVehicleName.text.toString())
        //Update Name field if currently blank:
        if (editVehicleName.text == null ||editVehicleName.text.toString().equals(""))
        {
            editVehicleName.setText("${if (year == 0) "" else year.toString()} $make $model")
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
        if (BuildConfig.DEBUG) Log.d(this::class.java.simpleName, "Logd: $message")
    }

    fun runBackgroundTask()
    {
        progressVINAPI.visibility = View.VISIBLE
        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)

        Snackbar.make(findViewById(android.R.id.content), "Getting Vehicle Information from VIN. This may take up to 30 seconds", Snackbar.LENGTH_LONG)
            .setAction("Action", null).show()
    }

    fun completeBackgroundTask()
    {
        progressVINAPI.visibility = View.GONE
        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
    }

    inner class GetVehicleAsyncTask : AsyncTask<String, String, String>() {
        override fun onPreExecute() {
            //super.onPreExecute()
            logd("onPreExecute")
            runBackgroundTask()
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
            completeBackgroundTask()
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
