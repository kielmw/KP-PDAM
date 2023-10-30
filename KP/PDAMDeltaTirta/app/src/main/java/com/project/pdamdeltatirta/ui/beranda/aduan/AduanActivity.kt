package com.project.pdamdeltatirta.ui.beranda.aduan

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.project.pdamdeltatirta.NotificationActivity
import com.project.pdamdeltatirta.R
import com.project.pdamdeltatirta.databinding.ActivityAduanBinding

class AduanActivity : AppCompatActivity() {

    private var _binding : ActivityAduanBinding? = null
    private val binding get() = _binding!!
    private val uuid = FirebaseAuth.getInstance().currentUser!!.uid
    private var customerId = ""
    private var tipeAduan = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityAduanBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Glide.with(this)
            .load(R.drawable.ic_icon_app)
            .into(binding.imageView)
        Glide.with(this)
            .load(R.drawable.aduan)
            .into(binding.aduan)

        checkIsAlreadySubmitPDAM()

        binding.apply {
            btnBack.setOnClickListener {
                finish()
            }
            btnSend.setOnClickListener {
                validateInput()
            }
            etTipeAduan.setOnClickListener {
                showOptionMenu()
            }
            imageView2.setOnClickListener {
                startActivity(Intent(this@AduanActivity, NotificationActivity::class.java))
            }
        }
    }

    private fun validateInput() {
        val aduan = binding.etAduan.text.toString().trim()
        if (tipeAduan.isEmpty()) {
            showToast("Tipe aduan tidak boleh kosong!")
        } else if (aduan.isEmpty()) {
            showToast("Deskripsi aduan tidak boleh kosong!")
        } else {
            binding.progressBar.visibility = View.VISIBLE

            val idAduan = System.currentTimeMillis().toString()
            val data = mapOf(
                "id_aduan" to idAduan,
                "no_pelanggan" to customerId,
                "tipe_aduan" to tipeAduan,
                "deskripsi_aduan" to aduan
            )

            FirebaseFirestore
                .getInstance()
                .collection("aduan")
                .document(idAduan)
                .set(data)
                .addOnCompleteListener {
                    binding.progressBar.visibility = View.GONE
                    if (it.isSuccessful) {
                        showSuccessDialog()
                    } else {
                        showFailureDialog()
                    }
                }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun checkIsAlreadySubmitPDAM() {
        FirebaseFirestore
            .getInstance()
            .collection("users")
            .document(uuid)
            .get()
            .addOnSuccessListener {
                customerId = it.data!!["customer_id"].toString()

                if (!customerId.contains("PDAM-")) {
                    showWarningDialog(
                        "Prosedur Cek Tagihan",
                        "1. Anda wajib melakukan pemasangan PDAM, pada menu Pasang Baru\n2. Pengajuan anda akan di tinjau terlebih dahulu\n3. Admin akan mengaktifasi pengajuan anda, dan PDAM Delta Tirta akan melakukan pemasangan PDAM\n\nSetelah itu anda dapat mengirimkan aduan jika ada pelayanan yang perlu kami tingkatkan."
                    )
                    binding.btnSend.isEnabled = false
                } else {
                    binding.apply {
                        notificationCounter.text = "6"
                        etNoPelanggan.setText(customerId)
                    }
                }
            }
    }

    private fun showOptionMenu() {
        val camera: TextView
        val gallery: TextView
        val title: TextView
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.popup_option)
        title = dialog.findViewById(R.id.title)
        camera = dialog.findViewById(R.id.camera)
        gallery = dialog.findViewById(R.id.gallery)

        title.text = "Pilih Tipe Aduan"
        camera.text = "Harga"
        gallery.text = "Kualitas Pelayanan"



        camera.setOnClickListener {
            tipeAduan = "Harga"
            binding.etTipeAduan.text = tipeAduan
            dialog.dismiss()
        }
        gallery.setOnClickListener {
            tipeAduan = "Kualitas Pelayanan"
            binding.etTipeAduan.text = tipeAduan
            dialog.dismiss()
        }
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
    }

    private fun showWarningDialog(title: String, message: String) {
        AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setIcon(R.drawable.baseline_warning_24)
            .setCancelable(false)
            .setPositiveButton("OKE") { dialogInterface, _ ->
                dialogInterface.dismiss()
                finish()
            }
            .show()
    }

    /// munculkan dialog ketika gagal registrasi
    private fun showFailureDialog() {
        AlertDialog.Builder(this)
            .setTitle("Gagal Membuat Aduan")
            .setMessage("Harap periksa koneksi Anda terlebih dahulu")
            .setIcon(R.drawable.baseline_clear_24)
            .setCancelable(false)
            .setPositiveButton("OKE") { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
            .show()
    }

    private fun showSuccessDialog() {
        AlertDialog.Builder(this)
            .setTitle("Sukses Membuat Aduan")
            .setMessage("Terima kasih telah memberikan feedback kepada kami, kami akan meninjau aduan anda lebih lanjut.")
            .setIcon(R.drawable.baseline_check_circle_outline_24)
            .setCancelable(false)
            .setPositiveButton("OKE") { dialogInterface, _ ->
                dialogInterface.dismiss()
                binding.apply {
                    tipeAduan = ""
                    etAduan.setText("")
                }
            }
            .show()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}