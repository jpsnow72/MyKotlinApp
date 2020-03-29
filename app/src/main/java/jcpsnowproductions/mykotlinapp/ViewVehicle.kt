package jcpsnowproductions.mykotlinapp

import android.app.Activity
import android.content.Intent.getIntent
import android.os.AsyncTask
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity;
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import android.support.v4.app.Fragment

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


    val manager = supportFragmentManager

    protected lateinit var myVehicle: vehicle
    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_recalls -> {

                toolbar.title = "Recalls"
                val recallsFragment = RecallsFragment.newInstance(1, myVehicle)
                openFragment(recallsFragment)

                /*val transaction = manager.beginTransaction()
                val currFragment = RecallsFragment()
                transaction.replace(R.id.view_vehicle_layout, currFragment)
                transaction.addToBackStack(null)
                transaction.commit()*/
                return@OnNavigationItemSelectedListener  true

            }
            R.id.navigation_maintenance -> {
                val toast = Toast.makeText(getApplicationContext(), "Maintenance!", Toast.LENGTH_SHORT) as Toast
                toast.show()
            }
            R.id.navigation_edit -> {
                val toast = Toast.makeText(getApplicationContext(), "Edit!", Toast.LENGTH_SHORT) as Toast
                toast.show()
            }
        }
        false

    }

    private fun openFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }
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
        myVehicle = v
        /*buttonEdit.setOnClickListener {view ->

        }*/

        //Re-enable getRecallInfo or put it on a different screen.
        //

        val bottomNav: BottomNavigationView = findViewById(R.id.bottomNavigation)

        bottomNav.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)


    }


    fun Activity.logd(message: String) {
        if (BuildConfig.DEBUG) Log.d(this::class.java.simpleName, "Logd: $message")
    }




}
