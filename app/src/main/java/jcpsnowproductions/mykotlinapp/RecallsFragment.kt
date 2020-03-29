package jcpsnowproductions.mykotlinapp

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import jcpsnowproductions.mykotlinapp.dummy.DummyContent
import jcpsnowproductions.mykotlinapp.dummy.DummyContent.DummyItem
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.content_view_vehicle.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

/**
 * A fragment representing a list of Items.
 * Activities containing this fragment MUST implement the
 * [RecallsFragment.OnListFragmentInteractionListener] interface.
 */
class RecallsFragment : Fragment() {

    val CONNECTON_TIMEOUT_MILLISECONDS = 60000
    var NHTSACampaignNumber: ArrayList<String> = ArrayList()
    var ReportReceivedDate: ArrayList<String> = ArrayList()
    var Component: ArrayList<String> = ArrayList()
    var Summary: ArrayList<String> = ArrayList()
    var Conequence: ArrayList<String> = ArrayList()
    var Remedy: ArrayList<String> = ArrayList()
    var Notes: ArrayList<String> = ArrayList()


    protected lateinit var v: vehicle
    // TODO: Customize parameters
    private var columnCount = 1

    private var listener: OnListFragmentInteractionListener? = null

    fun updateRecallsRecyler() {

        logd("Initiating RecylcerView")
        /*val view = inflater.inflate(R.layout.fragment_recalls_list, container, false)
        var rAdapter: MyRecallsRecyclerViewAdapter = MyRecallsRecyclerViewAdapter(Component, listener)
        recyclerViewer.adapter = rAdapter
        recyclerViewer.layoutManager = LinearLayoutManager(context)
        */
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //val view = inflater.inflate(R.layout.fragment_recalls_list, container, false)

        /*val testList: ArrayList<String> = ArrayList()
        testList.add("Testing 1")
        testList.add("Testing 2")

        */
        getRecallInfo(v)
        logd(Component.count().toString())
        // Set the adapter
        /*if (view is RecyclerView) {
            with(view) {
                layoutManager = when {
                    columnCount <= 1 -> LinearLayoutManager(context)
                    else -> GridLayoutManager(context, columnCount)
                }

                adapter = MyRecallsRecyclerViewAdapter(testList, listener)


            }
        }
        */

        // 2/24/19 - Start here! - the above normally creates a dummy list. Below uses the API to call the recall list. It currently attempts to load one of the text fields with that info, but I really need to put it into the list from above.


        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        /*if (context is OnListFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnListFragmentInteractionListener")
        }*/
    }
    override fun onDetach() {
        super.onDetach()
        listener = null
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson
     * [Communicating with Other Fragments](http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onListFragmentInteraction(item: String?)
    }



    companion object {

        // TODO: Customize parameter argument names
        const val ARG_COLUMN_COUNT = "column-count"

        // TODO: Customize parameter initialization
        @JvmStatic
        fun newInstance(columnCount: Int, vehicleObj: vehicle) =
            RecallsFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)
                }
                v = vehicleObj
            }
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

    fun logd(message: String) {
        if (BuildConfig.DEBUG) Log.d(this::class.java.simpleName, "Logd: $message")
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

    fun createRecallsArrays(resultJson: JSONObject)
    {
        /*var NHTSACampaignNumber: ArrayList<String> = ArrayList()
        var ReportReceivedDate: ArrayList<String> = ArrayList()
        var Component: ArrayList<String> = ArrayList()
        var Summary: ArrayList<String> = ArrayList()
        var Conequence: ArrayList<String> = ArrayList()
        var Remedy: ArrayList<String> = ArrayList()
        var Notes: ArrayList<String> = ArrayList()
    */

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
        logd("logd - updating recylcer viewer: " + Component.size)
        updateRecallsRecyler()
        //MyRecallsRecyclerViewAdapter(Component, listener)
        //3/2/2019 - Need to update the current adapter, but I am not sure how to send it here.

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
                createRecallsArrays(json)
            }
            catch (ex: Exception) {
                logd("Catch Exception (onProgressUpdate): ${ex.message.toString()}")
            }
        }

        override fun onPostExecute(result: String?) {
            completeBackgroundTask()
        }
    }
}
