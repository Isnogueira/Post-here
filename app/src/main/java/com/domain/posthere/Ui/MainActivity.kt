package com.domain.posthere.Ui

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.domain.posthere.R
import com.domain.posthere.adapters.ListaPostAdapter
import com.domain.posthere.dao.PostDao
import com.domain.posthere.model.Post
import com.google.firebase.auth.FirebaseAuth
import java.io.*
import java.util.*

class MainActivity : AppCompatActivity(), RecycleViewItemListener {

    private lateinit var auth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog

    private companion object{
        private const val STORAGE_PERMISSION_CODE = 100
    }

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

        val email = intent.getStringExtra("email").toString()
        val lblEmail = this.findViewById<TextView>(R.id.lblEmail)
        lblEmail.text = email

        this.atualizarLista()

        this.postar(email)

        this.acessarPostsApi()

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

    private fun postar(email: String){
        val btnPostar = this.findViewById<Button>(R.id.btnPostar)
        btnPostar.setOnClickListener {

            val txtContent = this.findViewById<EditText>(R.id.txtPost)
            val date = Date().toString()

            val post = Post(
                null,
                email,
                date,
                txtContent.text.toString()
            )

            salvarArquivo(post)

            postDao.salvar(post)?.addOnSuccessListener {
                txtContent.text = null
                this.atualizarLista()
            }?.addOnFailureListener { exception ->

                Toast.makeText(this, exception.message, Toast.LENGTH_LONG).show()
            }
        }
    }

    @SuppressLint("SdCardPath")
    private fun salvarArquivo(post: Post) {
        val date = Date().toString()
        try {
            val fos: FileOutputStream =
                this.openFileOutput("dadosPost($date).txt", MODE_PRIVATE)
            val text = "User: ${post.user}\n" +
                    "Date: ${post.date}\n" +
                    "Content: ${post.content}\n" +
                    "-------------------------------------------------\n"

            val path = "/data/data/com.domain.posthere/files/dadosPost($date).txt"
            try {
                FileWriter(path, true).use {
                    it.write(text)
                }
            }catch (e: IOException) {
                e.printStackTrace()
            }

            Toast.makeText(this, "Dados salvos com sucesso em arquivo", Toast.LENGTH_SHORT).show()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: NumberFormatException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
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
        postDao.deletar(id).addOnSuccessListener {
            this.atualizarLista()
        }.addOnFailureListener { exception ->

            Toast.makeText(this, exception.message, Toast.LENGTH_LONG).show()
        }

    }

    fun acessarPostsApi(){
        val btnPostsAPI = this.findViewById<Button>(R.id.btnPostsApi)
        btnPostsAPI.setOnClickListener{
            startActivity(Intent(this, PostApiActivity::class.java))
        }
    }



}
