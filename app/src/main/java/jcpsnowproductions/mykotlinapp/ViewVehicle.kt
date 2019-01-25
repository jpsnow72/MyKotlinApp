package jcpsnowproductions.mykotlinapp

import android.os.Bundle
import android.support.v7.app.AppCompatActivity;

import kotlinx.android.synthetic.main.activity_view_vehicle.*
import kotlinx.android.synthetic.main.content_view_vehicle.*

class ViewVehicle : AppCompatActivity() {

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

    }

}
