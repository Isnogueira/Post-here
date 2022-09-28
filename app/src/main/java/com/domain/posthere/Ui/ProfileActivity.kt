package com.domain.posthere.Ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.*
import com.domain.posthere.R

class ProfileActivity : AppCompatActivity() {

    val CAMERA_PERMISSION_CODE = 100
    val CAMERA_REQUEST = 1888

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        supportActionBar!!.hide()

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


}