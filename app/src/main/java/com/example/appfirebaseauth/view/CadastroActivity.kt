package com.example.appfirebaseauth.view

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.appfirebaseauth.databinding.ActivityCadastroBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest

class CadastroActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCadastroBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCadastroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        binding.btnCadastrar.setOnClickListener {
            cadastrarUsuario()
        }

        binding.btnVoltarLogin.setOnClickListener {
            finish()
        }
    }

    private fun cadastrarUsuario() {
        val nome = binding.editNomeCadastro.text.toString().trim()
        val email = binding.editEmailCadastro.text.toString().trim()
        val senha = binding.editSenhaCadastro.text.toString().trim()
        val confirmarSenha = binding.editConfirmarSenhaCadastro.text.toString().trim()

        binding.tilNomeCadastro.error = null
        binding.tilEmailCadastro.error = null
        binding.tilSenhaCadastro.error = null
        binding.tilConfirmarSenhaCadastro.error = null

        if (nome.isEmpty()) {
            binding.tilNomeCadastro.error = "Informe seu nome"
            return
        }

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.tilEmailCadastro.error = "Informe um e-mail válido"
            return
        }

        if (senha.length < 6) {
            binding.tilSenhaCadastro.error = "Mínimo de 6 caracteres"
            return
        }

        if (senha != confirmarSenha) {
            binding.tilConfirmarSenhaCadastro.error = "As senhas não coincidem"
            return
        }

        binding.progressCadastro.visibility = View.VISIBLE

        auth.createUserWithEmailAndPassword(email, senha)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName(nome)
                        .build()

                    auth.currentUser?.updateProfile(profileUpdates)?.addOnCompleteListener {
                        binding.progressCadastro.visibility = View.GONE
                        Toast.makeText(this, "Usuário cadastrado com sucesso", Toast.LENGTH_SHORT).show()
                        irParaHome()
                    }
                } else {
                    binding.progressCadastro.visibility = View.GONE
                    Toast.makeText(this, "Erro ao cadastrar usuário", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun irParaHome() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
