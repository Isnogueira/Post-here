package com.domain.posthere

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity(), RecycleViewItemListener {

    private lateinit var auth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog

    private val postDao = PostDao()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()

        progressDialog = ProgressDialog( this)
        progressDialog.setTitle("Please wait")
        progressDialog.setMessage("logging Out")
        progressDialog.setCanceledOnTouchOutside(false)

        supportActionBar!!.hide()

        this.atualizarLista()

        this.postar()

        this.firebaseSignout()
   }

    private fun atualizarLista() {

        postDao.listar().addOnSuccessListener { listaDeDocumentos ->

            val users = ArrayList<Post>()
            for(documento in listaDeDocumentos) {
                val user = documento.toObject(Post::class.java)
                users.add(user)
            }
            //--------------------------------------------------------------------------------------
            val lstContatos = this.findViewById<RecyclerView>(R.id.lstPosts)
            lstContatos.layoutManager = LinearLayoutManager(this)
            val adapter = ListaPostAdapter()
            adapter.listaPosts = users
            adapter.setRecyclerViewItemListener(this)
            lstContatos.adapter = adapter
            //--------------------------------------------------------------------------------------

        }?.addOnFailureListener { exception ->

            Toast.makeText(this, exception.message, Toast.LENGTH_LONG).show()
        }
    }

    private fun postar(){
        val btnPostar = this.findViewById<Button>(R.id.btnPostar)
        btnPostar.setOnClickListener {

            val txtContent = this.findViewById<EditText>(R.id.txtPost)
            val date = Date().toString()

            val post = Post(
                null,
                "Ingrid",
                date,
                txtContent.text.toString()
            )
            postDao.salvar(post)?.addOnSuccessListener {
                txtContent.text = null
                this.atualizarLista()
            }?.addOnFailureListener { exception ->

                Toast.makeText(this, exception.message, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun firebaseSignout(){
        val btnSair = this.findViewById<Button>(R.id.btnSair)
        btnSair.setOnClickListener{
            auth.signOut()
            progressDialog.show()
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    override fun recycleViewItemClicked(view: View, id: String) {
        TODO("Not yet implemented")
    }
}
