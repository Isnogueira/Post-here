package com.domain.posthere.Ui

import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import com.domain.posthere.R
import com.domain.posthere.interfaces.Endpoint
import com.domain.posthere.model.PostsAPI
import com.domain.posthere.utils.NetworkUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PostApiActivity : AppCompatActivity() {

    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_postsapi)

        supportActionBar!!.hide()

        progressDialog = ProgressDialog( this)
        progressDialog.setTitle("Please wait")
        progressDialog.setMessage("loading...")
        progressDialog.setCanceledOnTouchOutside(true)

        progressDialog.show()
        this.getData()
    }

    private fun getData() {
        val retrofitClient = NetworkUtils
            .getRetrofitInstance("https://jsonplaceholder.typicode.com")

        val endpoint = retrofitClient.create(Endpoint::class.java)
        val callback = endpoint.getPostsApi()

        callback.enqueue(object : Callback<List<PostsAPI>> {

            override fun onFailure(call: Call<List<PostsAPI>>, t: Throwable) {
                Toast.makeText(baseContext, t.message, Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<List<PostsAPI>>, response: Response<List<PostsAPI>>) {

                response.body()?.forEach {
                    val txtTexto = findViewById<TextView>(R.id.txtTexto)
                    txtTexto.text = txtTexto.text.toString().plus(it.body)
                    Log.i("ENT", "API acessada")
                }
            }
        })

    }
}
