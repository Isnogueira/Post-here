package com.domain.posthere

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog
    private var email = ""
    private var senha = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        supportActionBar!!.hide()

        progressDialog = ProgressDialog( this)
        progressDialog.setTitle("Please wait")
        progressDialog.setMessage("Creating account In...")
        progressDialog.setCanceledOnTouchOutside(false)



                auth = FirebaseAuth.getInstance()

        val txtEmail = this.findViewById<EditText>(R.id.txtEmailCadastro)
        val txtSenha = this.findViewById<EditText>(R.id.txtSenhaCadastro)

        this.enviar(txtEmail, txtSenha)
    }

    private fun enviar(txtEmail: EditText,txtSenha: EditText){
        val btnEnviar = this.findViewById<Button>(R.id.btnEnviar)
        btnEnviar.setOnClickListener{
            this.trataDados(txtEmail, txtSenha)
        }
    }

    private fun trataDados(txtEmail: EditText, txtSenha: EditText){
        email = txtEmail.text.toString().trim()
        senha = txtSenha.text.toString().trim()

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            txtEmail.error = "Email inválido"
        } else if (TextUtils.isEmpty(senha)) {
            txtSenha.error = "Digite sua senha primeiro"
        } else if(senha.length < 6) {
            txtSenha.error = "A senha deve conter mais de 6 digitos"
        }else{
            this.firebaseSignup(email, senha)
        }
    }

    private fun firebaseSignup(email: String,senha: String) {

        progressDialog.show()

        auth.createUserWithEmailAndPassword(email, senha)
            .addOnSuccessListener {
                progressDialog.dismiss()

                val emailAuth = auth.currentUser!!.email
                Toast.makeText(this, "Usuário criado como $emailAuth", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()

            }.addOnFailureListener{e ->
                progressDialog.dismiss()
                Toast.makeText(this, "Não épossível cadastrar! ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}