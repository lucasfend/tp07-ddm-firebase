package com.example.appfirebaseauth.view

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.appfirebaseauth.R
import com.example.appfirebaseauth.databinding.ActivityLoginBinding
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth

    private val signInLauncher = registerForActivityResult(
        FirebaseAuthUIActivityResultContract(),
    ) { res ->
        this.onSignInResult(res)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        binding.btnEntrar.setOnClickListener {
            realizarLogin()
        }

        binding.btnIrParaCadastro.setOnClickListener {
            startActivity(Intent(this, CadastroActivity::class.java))
        }

        binding.btnFirebaseUI.setOnClickListener {
            createSignInIntent()
        }
    }

    override fun onStart() {
        super.onStart()
        if (auth.currentUser != null) {
            irParaHome()
        }
    }

    private fun createSignInIntent() {
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder()
                .setAllowNewAccounts(true)
                .setRequireName(true)
                .build(),
        )

        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .setLogo(R.drawable.ic_lock)
            .setTheme(R.style.Theme_AppFirebaseAuth)
            .build()

        signInLauncher.launch(signInIntent)
    }

    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        if (result.resultCode == RESULT_OK) {
            val user = FirebaseAuth.getInstance().currentUser
            Toast.makeText(this, "Bem-vindo, ${user?.displayName ?: user?.email}", Toast.LENGTH_SHORT).show()
            irParaHome()
        } else {
            Toast.makeText(this, "Falha na autenticação", Toast.LENGTH_SHORT).show()
        }
    }

    private fun realizarLogin() {
        val email = binding.editEmailLogin.text.toString().trim()
        val senha = binding.editSenhaLogin.text.toString().trim()

        binding.tilEmailLogin.error = null
        binding.tilSenhaLogin.error = null

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.tilEmailLogin.error = "Informe um e-mail válido"
            return
        }

        if (senha.length < 6) {
            binding.tilSenhaLogin.error = "Mínimo de 6 caracteres"
            return
        }

        binding.progressLogin.visibility = View.VISIBLE

        auth.signInWithEmailAndPassword(email, senha)
            .addOnCompleteListener(this) { task ->
                binding.progressLogin.visibility = View.GONE
                if (task.isSuccessful) {
                    irParaHome()
                } else {
                    Toast.makeText(this, "E-mail ou senha inválidos", Toast.LENGTH_SHORT).show()
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
