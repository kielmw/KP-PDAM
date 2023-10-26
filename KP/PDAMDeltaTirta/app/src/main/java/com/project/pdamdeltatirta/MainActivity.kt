package com.project.pdamdeltatirta

import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.text.Spannable
import android.text.SpannableString
import android.text.method.PasswordTransformationMethod
import android.text.style.ForegroundColorSpan
import android.text.style.UnderlineSpan
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.project.pdamdeltatirta.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private var _binding : ActivityMainBinding? = null
    private val binding get() = _binding!!
    private var isShowPassword = false
    private var isSaveAccount = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        checkAutoLogin()
        coloredText()

        binding.apply {
            val sharedPreferences = getSharedPreferences("remember_me", Context.MODE_PRIVATE)
            isSaveAccount = sharedPreferences.getBoolean("isSaveAccount", false)
            if (isSaveAccount) {
                val emailFromSharedPreference = sharedPreferences.getString("email", "")
                val passwordFromSharedPreference = sharedPreferences.getString("password", "")
                etEmail.setText(emailFromSharedPreference)
                etPassword.setText(passwordFromSharedPreference)
                btnCheck.backgroundTintList = ContextCompat.getColorStateList(this@MainActivity, R.color.primary)
            }


            btnForgotPassword.text = underlineText(btnForgotPassword.text.toString())
            btnRegister.text = underlineText(btnRegister.text.toString())

            btnEye.setOnClickListener {
                if (!isShowPassword) {
                    isShowPassword = true
                    etPassword.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                    etPassword.transformationMethod = null // Set null to remove the transformation method
                    btnEye.backgroundTintList = ContextCompat.getColorStateList(this@MainActivity, R.color.primary)
                } else {
                    isShowPassword = false
                    etPassword.inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD
                    etPassword.transformationMethod = PasswordTransformationMethod.getInstance() // Set to PasswordTransformationMethod
                    btnEye.backgroundTintList = ContextCompat.getColorStateList(this@MainActivity, R.color.grey)
                }
            }

            btnCheck.setOnClickListener {
                val editor = sharedPreferences.edit()
                if(!isSaveAccount) {
                    isSaveAccount = true
                    val email = etEmail.text.toString().trim()
                    val password = etPassword.text.toString().trim()

                    editor.putString("email", email)
                    editor.putString("password", password)
                    editor.putBoolean("isSaveAccount", isSaveAccount)
                    editor.apply()
                    btnCheck.backgroundTintList = ContextCompat.getColorStateList(this@MainActivity, R.color.primary)
                } else {
                    isSaveAccount = false
                    editor.putBoolean("isSaveAccount", false)
                    editor.apply()
                    btnCheck.backgroundTintList = ContextCompat.getColorStateList(this@MainActivity, R.color.grey)
                }
            }

            btnRegister.setOnClickListener {
                startActivity(Intent(this@MainActivity, RegisterActivity::class.java))
            }


            btnLogin.setOnClickListener {
                loginFunction()
            }

        }
    }

    private fun coloredText() {
        binding.apply {
            val text = binding.linearLayout3.text.toString().trim()
            val spannableText = SpannableString(text)

            // Define the words/phrases you want to change color
            val termsOfServiceStart = text.indexOf("Terms of Service")
            val termsOfServiceEnd = termsOfServiceStart + "Terms of Service".length
            val privacyPolicyStart = text.indexOf("Privacy Policy")
            val privacyPolicyEnd = privacyPolicyStart + "Privacy Policy".length

            // Set the color for the specific words/phrases to #0F80FD
            spannableText.setSpan(ForegroundColorSpan(Color.parseColor("#0F80FD")), termsOfServiceStart, termsOfServiceEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            spannableText.setSpan(ForegroundColorSpan(Color.parseColor("#0F80FD")), privacyPolicyStart, privacyPolicyEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

            // Set the modified text to your TextView
            linearLayout3.text = spannableText
        }
    }

    private fun checkAutoLogin() {
        /// CEK TERLEBIH DAHULU APAKAH USER SUDAH PERNAH LOGIN SEBELUMNYA
        if(FirebaseAuth.getInstance().currentUser != null) {
            /// JIKA SEBELUMNYA PERNAH LOGIN, MAKA AUTO LOGIN, LANGSUNG KE HOMEPAGE
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun loginFunction() {
        binding.apply {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (email.isEmpty()) {
                showToast("Email tidak boleh kosong!")
            } else if (!isValidEmail(email)) {
                showToast("Format email harus sesuai, contoh: abc@gmail.com")
            } else if (password.isEmpty() || password.length < 6) {
                showToast("Password minimum 6 karakter!")
            } else {
                progressBar.visibility = View.VISIBLE

                FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener {
                        progressBar.visibility = View.GONE
                        if (it.isSuccessful) {
                            startActivity(Intent(this@MainActivity, HomeActivity::class.java))
                            finish()
                        } else {
                            showFailureLoginDialog()
                        }
                    }
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun isValidEmail(email: String): Boolean {
        val emailPattern = Patterns.EMAIL_ADDRESS // Android's built-in email pattern
        return emailPattern.matcher(email).matches()
    }

    private fun underlineText(text: String): CharSequence {
        val spannableString = SpannableString(text)

        // Apply the UnderlineSpan to the desired portion of the text
        val underlineSpan = UnderlineSpan()
        spannableString.setSpan(underlineSpan, 0, text.length, 0)
        return spannableString
    }

    /// kalau gagal maka muncul dialog ini
    private fun showFailureLoginDialog() {
        AlertDialog.Builder(this)
            .setTitle("Gagal Login")
            .setMessage("Harap periksa email dan password anda, dan pastikan koneksi internet anda stabil!")
            .setIcon(R.drawable.baseline_clear_24)
            .setCancelable(false)
            .setPositiveButton("OKE") { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
            .show()
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}