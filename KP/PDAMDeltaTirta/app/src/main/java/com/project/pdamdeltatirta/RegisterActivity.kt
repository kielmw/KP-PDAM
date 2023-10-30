package com.project.pdamdeltatirta

import android.os.Bundle
import android.text.InputType
import android.text.SpannableString
import android.text.method.PasswordTransformationMethod
import android.text.style.UnderlineSpan
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.project.pdamdeltatirta.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    private var _binding : ActivityRegisterBinding? = null
    private val binding get() = _binding!!
    private var isShowPassword = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            btnRegister.text = underlineText(btnRegister.text.toString())
            btnLogin.text = underlineText(btnLogin.text.toString())

            btnClose.setOnClickListener {
                finish()
            }

            btnLogin.setOnClickListener {
                finish()
            }

            btnEye.setOnClickListener {
                if (!isShowPassword) {
                    isShowPassword = true
                    etPassword.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                    etPassword.transformationMethod = null // Set null to remove the transformation method
                    btnEye.backgroundTintList = ContextCompat.getColorStateList(this@RegisterActivity, R.color.primary)
                } else {
                    isShowPassword = false
                    etPassword.inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD
                    etPassword.transformationMethod = PasswordTransformationMethod.getInstance() // Set to PasswordTransformationMethod
                    btnEye.backgroundTintList = ContextCompat.getColorStateList(this@RegisterActivity, R.color.grey)
                }
            }

            btnRegister.setOnClickListener {
                val name = etNamel.text.toString().trim()
                val email = etEmail.text.toString().trim()
                val phone = etPhone.text.toString().trim()
                val password = etPassword.text.toString().trim()
                val isValidEmail = isValidEmail(email)

                if (name.isEmpty()) {
                    showToast("Nama Lengkap tidak boleh kosong!")
                } else if (email.isEmpty()) {
                    showToast("Email tidak boleh kosong!")
                } else if (!isValidEmail) {
                    showToast("Format email harus sesuai, contoh: abc@gmail.com")
                } else if (phone.isEmpty()) {
                    showToast("Nomor Telepon tidak boleh kosong!")
                } else if (password.isEmpty() || password.length < 6) {
                    showToast("Password minimum 6 karakter!")
                } else {
                    progressBar.visibility = View.VISIBLE

                    FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                val uid = FirebaseAuth.getInstance().currentUser!!.uid
                                val data = mapOf(
                                    "uid" to uid,
                                    "customer_id" to "Belum Melakukan Pemasangan PDAM",
                                    "name" to name,
                                    "email" to email,
                                    "phone" to phone,
                                    "password" to password,
                                    "image_profile" to "",
                                    "age" to "",
                                    "role" to "user",
                                )

                                val dataMeter = mapOf(
                                    "user_id" to uid,
                                    "user_name" to name,
                                    "meter_bulan_ini" to "1034",
                                    "meter_bulan_lalu" to "1010",
                                    "pemakaian_bulan_ini" to "24 M3",
                                )

                                val historyPembayaran = mapOf(
                                    "user_id" to uid,
                                    "use_name" to name,
                                    "bulan_pembayaran" to "September",
                                    "jumlah" to "Rp. 105.000,00" ,
                                    "pesan" to "Silahkan lakukan pembayaran untuk bulan September !",
                                    "status" to "Belum Dibayar",
                                )

                                FirebaseFirestore.getInstance()
                                    .collection("transaction")
                                    .document(uid)
                                    .collection("bulan")
                                    .document()
                                    .set(historyPembayaran);

                                FirebaseFirestore
                                    .getInstance()
                                    .collection("check_meter")
                                    .document(uid)
                                    .set(dataMeter)

                                FirebaseFirestore
                                    .getInstance()
                                    .collection("users")
                                    .document(uid)
                                    .set(data)
                                    .addOnCompleteListener { result ->
                                        binding.progressBar.visibility = View.GONE
                                        if(result.isSuccessful) {
                                            showSuccessDialog()
                                        } else {
                                            showFailureDialog()
                                        }
                                    }
                            } else {
                                binding.progressBar.visibility = View.GONE
                                showFailureDialog()
                            }
                        }
                }
            }
        }

    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun underlineText(text: String): CharSequence {
        val spannableString = SpannableString(text)

        // Apply the UnderlineSpan to the desired portion of the text
        val underlineSpan = UnderlineSpan()
        spannableString.setSpan(underlineSpan, 0, text.length, 0)
        return spannableString
    }

    private fun isValidEmail(email: String): Boolean {
        val emailPattern = Patterns.EMAIL_ADDRESS // Android's built-in email pattern
        return emailPattern.matcher(email).matches()
    }

    /// munculkan dialog ketika gagal registrasi
    private fun showFailureDialog() {
        AlertDialog.Builder(this)
            .setTitle("Gagal Mendaftar")
            .setMessage("Harap periksa koneksi Anda terlebih dahulu, dan pengguna tidak dapat mendaftar dengan akun yang sama dengan yang mendaftar sebelumnya")
            .setIcon(R.drawable.baseline_clear_24)
            .setCancelable(false)
            .setPositiveButton("OKE") { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
            .show()
    }

    /// munculkan dialog ketika sukses registrasi
    private fun showSuccessDialog() {
        AlertDialog.Builder(this)
            .setTitle("Sukses Mendaftar")
            .setMessage("Silakan masuk dengan akun Anda!")
            .setIcon(R.drawable.baseline_check_circle_outline_24)
            .setCancelable(false)
            .setPositiveButton("OKE") { dialogInterface, _ ->
                dialogInterface.dismiss()
                binding.apply {
                    binding.etEmail.setText("")
                    binding.etNamel.setText("")
                    binding.etPassword.setText("")
                    binding.etPhone.setText("")
                }
            }
            .show()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}