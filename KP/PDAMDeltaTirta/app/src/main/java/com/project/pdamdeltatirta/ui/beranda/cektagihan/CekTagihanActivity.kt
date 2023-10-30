package com.project.pdamdeltatirta.ui.beranda.cektagihan

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.project.pdamdeltatirta.NotificationActivity
import com.project.pdamdeltatirta.R
import com.project.pdamdeltatirta.databinding.ActivityCekTagihanBinding

class CekTagihanActivity : AppCompatActivity() {

    private var _binding : ActivityCekTagihanBinding? = null
    private val binding get() = _binding!!
    private val uuid = FirebaseAuth.getInstance().currentUser!!.uid
    private var customerId = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityCekTagihanBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Glide.with(this)
            .load(R.drawable.ic_icon_app)
            .into(binding.imageView)
        Glide.with(this)
            .load(R.drawable.cek_tagihan)
            .into(binding.cekTagihan)
        checkIsAlreadySubmitPDAM()

        binding.apply {
            btnBack.setOnClickListener {
                finish()
            }
            imageView2.setOnClickListener {
                startActivity(Intent(this@CekTagihanActivity, NotificationActivity::class.java))
            }
        }

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
                        "1. Anda wajib melakukan pemasangan PDAM, pada menu Pasang Baru\n2. Pengajuan anda akan di tinjau terlebih dahulu\n3. Admin akan mengaktifasi pengajuan anda, dan PDAM Delta Tirta akan melakukan pemasangan PDAM\n\nSetelah itu anda dapat melakukan cek tagihan"
                    )
                } else {
                   cekTagihanFromFirebase()
                }
            }
    }

    private fun cekTagihanFromFirebase() {
        FirebaseFirestore
            .getInstance()
            .collection("check_invoice")
            .document("invoice-001")
            .get()
            .addOnSuccessListener {
                if (it.exists()) {
                    val jumlah = "" + it.data!!["jumlah_tagihan"]
                    binding.apply {
                        notificationCounter.text = "6"
                        etJumlahTagihan.setText(jumlah)
                        etNoPelanggan.setText(customerId)
                    }
                }
            }
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

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}