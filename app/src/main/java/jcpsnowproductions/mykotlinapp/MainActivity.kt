package jcpsnowproductions.mykotlinapp

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity;
import android.util.Log
import android.view.Menu
import android.view.MenuItem

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import java.io.File
import android.R.attr.button
//import com.sun.org.apache.xerces.internal.util.DOMUtil.getParent
import android.view.ViewGroup



class MainActivity : AppCompatActivity() {


    private var fileDir = ""

    private var fileName = "data.dat"

    private var fullPath = ""



    override fun onCreate(savedInstanceState: Bundle?) {

        fileDir = applicationContext.filesDir.toString()
        fullPath = fileDir + "/" + fileName
        //SerializableManager.removeSerializable(applicationContext, fileName)  //DELETE on first load (just for testing purposes)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        var file = File(fullPath)
        if (file.exists())
        {
            logd("File Exists")

            //Remove the main vehicle button.
            /*val layout = buttonAddFirstVehicle.getParent() as ViewGroup
            layout?.removeView(buttonAddFirstVehicle)
            */

            var vhList = SerializableManager.readSerializable<ArrayList<vehicle>>(applicationContext, fileName)
            vhList.forEach {
                logd(it.vName + "Got here!")
            }

        }
        else
        {
            logd("File Doesn't exist.")
        }


        buttonAddFirstVehicle.setOnClickListener {view ->
            logd("Add First Vehicle button was clicked!")
            //Creating a new intent allows us to basically open another Activity. In this case, we are going to open the Add Vehicle activity.
            val intent = Intent(this, ActivityAddVehicle::class.java)
            startActivity(intent)
        }

        buttonAddVehicle.setOnClickListener{view ->

        }

        buttonDelete.setOnClickListener{view ->
            SerializableManager.removeSerializable(applicationContext, fileName)
        }




        /*fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()


        }*/
    }
    fun Activity.logd(message: String) {
        if (BuildConfig.DEBUG) Log.d(this::class.java.simpleName, message)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}
