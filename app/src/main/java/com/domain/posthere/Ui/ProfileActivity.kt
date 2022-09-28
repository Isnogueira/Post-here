package com.domain.posthere.Ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.*
import com.domain.posthere.R

class ProfileActivity : AppCompatActivity(), LocationListener {

    val CAMERA_PERMISSION_CODE = 100
    val CAMERA_REQUEST = 1888

    //Código de permissão para localizaçao não precisa
    val COARSE_REQUEST = 12345
    //Código de permissão para localizaçao precisa
    val FINE_REQUEST = 67890

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        supportActionBar!!.hide()

        this.getLocationByNetwork()

        val btnCamera = this.findViewById<ImageButton>(R.id.btnCamera)
        btnCamera.setOnClickListener {
            if(this.checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
                this.requestPermissions(arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_CODE)
            }else{
                abreCamera()
            }
        }

        val btnProximo = findViewById<Button>(R.id.btnProximo)
        btnProximo.setOnClickListener{
            val mainIntent = Intent(this, MainActivity::class.java)
            startActivity(mainIntent)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == CAMERA_PERMISSION_CODE){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                abreCamera()

            } else{

                Toast.makeText(this, "Acesso a camera negado", Toast.LENGTH_LONG).show()
            }
        }
        if(requestCode == COARSE_REQUEST && grantResults[0] == PackageManager.PERMISSION_GRANTED){

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == CAMERA_REQUEST && resultCode == RESULT_OK){
            val foto = data?.extras!!["data"] as Bitmap
            val imgPerfil = this.findViewById<ImageView>(R.id.imgPerfil)
            imgPerfil.setImageBitmap(foto)
        }
    }

    private fun abreCamera(){

        val cameraIntent = Intent(Intent(MediaStore.ACTION_IMAGE_CAPTURE))
        this.startActivityForResult(cameraIntent, CAMERA_REQUEST)
    }


    private fun getLocation(provider: String, permission: String) : Location? {
        // o retorno da localização
        var location: Location? = null
        // chave para acessar o Hardware através das bibliotecas nativas
        val locationManager = this.getSystemService(LOCATION_SERVICE) as LocationManager
        val isServiceEnabled = locationManager.isProviderEnabled(provider)
        if (isServiceEnabled) {
            Log.i("DR4", "Indo pela Rede")
            if (checkSelfPermission(permission) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                locationManager.requestLocationUpdates(
                    provider,
                    2000L,
                    0F,
                    this
                )
                location = locationManager.getLastKnownLocation(provider)
            } else {
                requestPermissions(arrayOf(permission), COARSE_REQUEST)
            }
        }
        return location
    }

    private fun getLocationByNetwork(){
        val location: Location? = this.getLocation(LocationManager.NETWORK_PROVIDER, Manifest.permission.ACCESS_COARSE_LOCATION)
        if (location != null) {
            val lblLatitude = this.findViewById<TextView>(R.id.lblLatitude)
            lblLatitude.text = location.latitude.toString()
            val lblLongitude = this.findViewById<TextView>(R.id.lblLongitude)
            lblLongitude.text = location.longitude.toString()
        }
    }

    override fun onLocationChanged(location: Location) {
        val lblLatitude = this.findViewById<TextView>(R.id.lblLatitude)
        lblLatitude.text = location.latitude.toString()
        val lblLongitude = this.findViewById<TextView>(R.id.lblLongitude)
        lblLongitude.text = location.longitude.toString()
    }


}