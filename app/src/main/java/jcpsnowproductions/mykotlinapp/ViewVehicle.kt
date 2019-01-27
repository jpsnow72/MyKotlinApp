package jcpsnowproductions.mykotlinapp

import android.app.Activity
import android.content.Intent.getIntent
import android.os.AsyncTask
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity;
import android.util.Log
import android.view.View
import android.view.WindowManager

import kotlinx.android.synthetic.main.activity_view_vehicle.*
import kotlinx.android.synthetic.main.content_view_vehicle.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class ViewVehicle : AppCompatActivity() {
    val CONNECTON_TIMEOUT_MILLISECONDS = 60000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_vehicle)
        setSupportActionBar(toolbar)
        val v: vehicle = getIntent().getSerializableExtra("vehicle") as vehicle
        setTitle(v.vName)
        textVehicleInfo.setText("""Vin: ${v.vVin}
Year: ${v.vYear}
Make: ${v.vMake}
Model: ${v.vModel}
Style/Trim: ${v.vStyle}
Color: ${v.vColor}
Engine/Transmission: ${v.vEngine}""")
        textNotes.setText(v.vNotes)

        buttonEdit.setOnClickListener {view ->

        }
        getRecallInfo(v)
    }

    fun getRecallInfo(v: vehicle)
    {
        //If we have some valid values for year, make, and model then try to call the API
        if (v!=null && v.vYear !=null && v.vYear != 1800 && v.vYear != 0 && v.vMake.length > 0 && v.vModel.length > 0)
        {
            var urlString = "https://one.nhtsa.gov/webapi/api/Recalls/vehicle/modelyear/${v.vYear}/make/${v.vMake}/model/${v.vModel.substringBefore(' ')}?format=json"
            logd(urlString)
            GetVehicleAsyncTask().execute(urlString)

        }
    }
    fun Activity.logd(message: String) {
        if (BuildConfig.DEBUG) Log.d(this::class.java.simpleName, "Logd: $message")
    }

    fun updateFields(resultJson: JSONObject)
    {
        var NHTSACampaignNumber: ArrayList<String> = ArrayList()
        var ReportReceivedDate: ArrayList<String> = ArrayList()
        var Component: ArrayList<String> = ArrayList()
        var Summary: ArrayList<String> = ArrayList()
        var Conequence: ArrayList<String> = ArrayList()
        var Remedy: ArrayList<String> = ArrayList()
        var Notes: ArrayList<String> = ArrayList()

        var recallList: String = ""

        val results: JSONArray = resultJson.getJSONArray("Results")
        logd(results.length().toString() + " Results: " + results.toString())

        //Loop through each JSONObject {} within the Results JSONArray
        //If the variable is something we are interested in, pull it out and update that field.
        for (i in 0..(results.length() - 1)) {
            val item = results.getJSONObject(i)

            NHTSACampaignNumber.add(item.get("NHTSACampaignNumber").toString())
            ReportReceivedDate.add(item.get("ReportReceivedDate").toString())
            Component.add(item.get("Component").toString())
            Summary.add(item.get("Summary").toString())
            Conequence.add(item.get("Conequence").toString())
            Remedy.add(item.get("Remedy").toString())
            Notes.add(item.get("Notes").toString())

            recallList += "${Component[i]} \n\n"


        }

        textRecalls.setText(recallList)

      /*  logd(editVehicleName.text.toString())
        //Update Name field if currently blank:
        if (editVehicleName.text == null ||editVehicleName.text.toString().equals(""))
        {
            editVehicleName.setText("${if (year == 0) "" else year.toString()} $make $model")
        }
        */


    }
    fun runBackgroundTask()
    {
        /*progressVINAPI.visibility = View.VISIBLE
        window.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)

        Snackbar.make(findViewById(android.R.id.content), "Getting Vehicle Information from VIN. This may take up to 30 seconds", Snackbar.LENGTH_LONG)
            .setAction("Action", null).show()
            */
    }

    fun completeBackgroundTask()
    {
        /*
        progressVINAPI.visibility = View.GONE
        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        */
    }

    inner class GetVehicleAsyncTask : AsyncTask<String, String, String>() {
        override fun onPreExecute() {
            //super.onPreExecute()
            //logd("onPreExecute")
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
