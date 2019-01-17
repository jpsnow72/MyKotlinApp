package jcpsnowproductions.mykotlinapp

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity;
import android.util.Log
import android.widget.EditText

import kotlinx.android.synthetic.main.activity_add_vehicle.*
import kotlinx.android.synthetic.main.content_activity_add_vehicle.*
import kotlinx.android.synthetic.main.content_main.*
import java.lang.NumberFormatException
import java.util.*
import kotlin.collections.ArrayList

class ActivityAddVehicle : AppCompatActivity() {
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
                vhList.add(vh)

                SerializableManager.saveSerializable(applicationContext, vhList, fileName)
                Snackbar.make(view, "The name of the vehicle is ${vh.vName} and the year is: ${vh.vYear}", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
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

    /*companion object {
        fun newIntent(context: Context): Intent {
            val intent = Intent(context: Context)
            return intent
        }
    }*/

}
