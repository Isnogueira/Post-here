package com.domain.posthere

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.security.crypto.EncryptedFile
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.google.firebase.auth.FirebaseAuth
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity(), RecycleViewItemListener {

    private lateinit var auth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog

    val masterkey = MasterKey.Builder(this)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()
    val file = File(this.filesDir, "teste.dir")
    val encryptedFile = getEncriptedFile(masterkey,file)

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

            val posts = ArrayList<Post>()
            for(documento in listaDeDocumentos) {
                val postConvertido = documento.toObject(Post::class.java)
                posts.add(postConvertido)
            }

            //--------------------------------------------------------------------------------------
            val lstContatos = this.findViewById<RecyclerView>(R.id.lstPosts)
            lstContatos.layoutManager = LinearLayoutManager(this)
            val adapter = ListaPostAdapter()
            adapter.listaPosts = posts
            adapter.setRecyclerViewItemListener(this)
            lstContatos.adapter = adapter
            //--------------------------------------------------------------------------------------

        }.addOnFailureListener { exception ->

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

            val sharedPreferences = EncryptedSharedPreferences.create(
                "posts.shp",
                post.toString(),
                applicationContext,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
            val editor = sharedPreferences.edit()
            editor.putString("post", post.toString())
            editor.apply()

            postDao.salvar(post)?.addOnSuccessListener {
                txtContent.text = null
                this.atualizarLista()
            }?.addOnFailureListener { exception ->

                Toast.makeText(this, exception.message, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun getEncriptedFile(masterkey: MasterKey, file:File): EncryptedFile {
        val encryptedFile = EncryptedFile.Builder(
            this,
            file,
            masterkey,
            EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
        ).build()

        return encryptedFile
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
