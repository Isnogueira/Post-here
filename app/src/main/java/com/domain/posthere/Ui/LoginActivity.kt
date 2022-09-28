package com.domain.posthere.Ui

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.domain.posthere.R
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog
    private var email = ""
    private var senha = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        progressDialog = ProgressDialog( this)
        progressDialog.setTitle("Please wait")
        progressDialog.setMessage("Logging in...")
        progressDialog.setCanceledOnTouchOutside(false)


        val txtEmail = this.findViewById<EditText>(R.id.txtEmail)
        val txtSenha = this.findViewById<EditText>(R.id.txtSenha)

        supportActionBar!!.hide()

        auth = FirebaseAuth.getInstance()

        val currentUser = auth.currentUser
        if(currentUser != null){
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        this.entrar(txtEmail, txtSenha)
        this.registrar()
    }

    private fun entrar(txtEmail: EditText,txtSenha: EditText){
        val btnEntrar = this.findViewById<Button>(R.id.btnEntrar)
        btnEntrar.setOnClickListener{
            this.trataDados(txtEmail, txtSenha)
        }
    }

    private fun registrar(){
        val btncadastrar = this.findViewById<Button>(R.id.btnTelaCadastro)
        btncadastrar.setOnClickListener{
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun trataDados(txtEmail: EditText, txtSenha: EditText){
        email = txtEmail.text.toString().trim()
        senha = txtSenha.text.toString().trim()

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            txtEmail.error = "Email inválido"
        } else if (TextUtils.isEmpty(senha)){
            txtSenha.error = "Digite sua senha primeiro"

        }else{
            this.firebaseLogin(email, senha)
        }
    }

    private fun firebaseLogin(email: String, senha: String) {

        progressDialog.show()

        auth.signInWithEmailAndPassword(email,senha)
            .addOnSuccessListener {
                progressDialog.dismiss()

                val emailAuth = auth.currentUser!!.email
                Toast.makeText(this, "Logado como $emailAuth", Toast.LENGTH_SHORT).show()
                val mainIntent = Intent(this, MainActivity::class.java)
                val profileIntent = Intent(this, ProfileActivity::class.java)
                mainIntent.putExtra("email", emailAuth)
                profileIntent.putExtra("email", emailAuth)
                startActivity(mainIntent)

            }.addOnFailureListener { e ->
                progressDialog.dismiss()

                Toast.makeText(this, "Falha no login ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}