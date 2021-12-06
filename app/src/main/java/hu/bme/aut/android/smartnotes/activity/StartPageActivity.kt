package hu.bme.aut.android.smartnotes.activity

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import hu.bme.aut.android.smartnotes.R
import hu.bme.aut.android.smartnotes.databinding.ActivityStartPageBinding
import hu.bme.aut.android.smartnotes.list.NotesActivity

class StartPageActivity : BaseActivity() {
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var binding: ActivityStartPageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStartPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        binding.btnRegister.setOnClickListener { registerClick() }
        binding.btnLogin.setOnClickListener { loginClick() }

        initActionBar(false)
    }

    private fun validateForm() =
        binding.etEmail.validateNonEmpty() && binding.etPassword.validateNonEmpty()

    private fun EditText.validateNonEmpty(): Boolean {
        if (text.isEmpty()) {
            error = context.getString(R.string.required)
            return false
        }
        return true
    }

    private fun registerClick() {
        if (!validateForm()) {
            return
        }

        showProgressDialog()

        firebaseAuth
            .createUserWithEmailAndPassword(
                binding.etEmail.text.toString(),
                binding.etPassword.text.toString()
            )
            .addOnSuccessListener { result ->
                val firebaseUser = result.user
                val profileChangeRequest = UserProfileChangeRequest.Builder()
                    .setDisplayName(firebaseUser?.email?.substringBefore('@'))
                    .build()
                firebaseUser?.updateProfile(profileChangeRequest)

                snack(binding.root, getString(R.string.registrationmessage))

                signIn(
                    binding.etEmail.text.toString(),
                    binding.etPassword.text.toString()
                )
            }
            .addOnFailureListener { exception ->
                hideProgressDialog()
                exception.message?.let { snack(binding.root, it) }
            }
    }

    private fun loginClick() {
        if (!validateForm()) {
            return
        }
        signIn(
            binding.etEmail.text.toString(),
            binding.etPassword.text.toString()
        )
    }

    private fun signIn(email: String, password: String) {
        showProgressDialog()
        firebaseAuth
            .signInWithEmailAndPassword(
                email,
                password
            )
            .addOnSuccessListener {
                hideProgressDialog()

                startActivity(Intent(this, NotesActivity::class.java))
                finish()
            }
            .addOnFailureListener { exception ->
                hideProgressDialog()

                snack(binding.root, exception.localizedMessage ?: "")
            }
    }
}