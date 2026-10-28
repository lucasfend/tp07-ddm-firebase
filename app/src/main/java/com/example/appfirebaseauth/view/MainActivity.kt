package com.example.appfirebaseauth.view

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.appfirebaseauth.databinding.ActivityMainBinding
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        carregarDadosUsuario()

        binding.btnSair.setOnClickListener {
            signOut()
        }

        binding.btnExcluirConta.setOnClickListener {
            confirmarExclusaoConta()
        }
    }

    override fun onStart() {
        super.onStart()
        if (auth.currentUser == null) {
            voltarParaLogin()
        }
    }

    private fun carregarDadosUsuario() {
        val user = auth.currentUser ?: run {
            voltarParaLogin()
            return
        }

        val nome = user.displayName?.takeIf { it.isNotBlank() } ?: "Usuário"
        val email = user.email ?: "-"

        binding.txtBemVindo.text = "Olá, $nome!"
        binding.txtNomeUsuario.text = "Nome: $nome"
        binding.txtEmailUsuario.text = "E-mail: $email"
        binding.txtUidUsuario.text = "UID: ${user.uid}"
    }

    private fun signOut() {
        AuthUI.getInstance()
            .signOut(this)
            .addOnCompleteListener {
                auth.signOut()
                voltarParaLogin()
            }
    }

    private fun confirmarExclusaoConta() {
        AlertDialog.Builder(this)
            .setTitle("Excluir conta")
            .setMessage("Deseja excluir sua conta?")
            .setPositiveButton("Excluir") { _, _ ->
                AuthUI.getInstance()
                    .delete(this)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Conta excluída", Toast.LENGTH_SHORT).show()
                            voltarParaLogin()
                        } else {
                            Toast.makeText(this, "Erro ao excluir conta", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun voltarParaLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
