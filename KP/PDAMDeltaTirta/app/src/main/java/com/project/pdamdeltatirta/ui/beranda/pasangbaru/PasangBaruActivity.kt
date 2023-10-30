package com.project.pdamdeltatirta.ui.beranda.pasangbaru

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.project.pdamdeltatirta.NotificationActivity
import com.project.pdamdeltatirta.R
import com.project.pdamdeltatirta.databinding.ActivityPasangBaruBinding
import java.lang.Math.abs
import java.text.SimpleDateFormat
import java.util.*

class PasangBaruActivity : AppCompatActivity() {

    private var _binding: ActivityPasangBaruBinding? = null
    private val binding get() = _binding!!
    private var gender = ""
    private var dateOfBirth = ""
    private var age = 0
    private var houseStatus = ""
    private val calendar = Calendar.getInstance()
    private val uuid = FirebaseAuth.getInstance().currentUser!!.uid
    private var isAlreadySubmitted = false
    private var customerId = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityPasangBaruBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Glide.with(this)
            .load(R.drawable.ic_icon_app)
            .into(binding.imageView)
        Glide.with(this)
            .load(R.drawable.pasang_baru)
            .into(binding.pasangBaru)

        checkIsAlreadySubmitPDAM()

        binding.apply {
            btnBack.setOnClickListener {
                finish()
            }

            btnSend.setOnClickListener {
                submitInstallationPDAMValidation()
            }

            etJenisKelamin.setOnClickListener {
                Log.e("adasada", "adsasad")
                showOptionMenu("gender")
            }

            etStatusRumah.setOnClickListener {
                showOptionMenu("houseStatus")
            }

            etTanggalLahir.setOnClickListener {
                showDatePickerDialog()
            }

            imageView2.setOnClickListener {
                startActivity(Intent(this@PasangBaruActivity, NotificationActivity::class.java))
            }
        }
    }

    private fun checkIsAlreadySubmitPDAM() {
        FirebaseFirestore
            .getInstance()
            .collection("installation")
            .whereEqualTo("id_pelanggan", uuid)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    isAlreadySubmitted = true
                    customerId = document.getString("no_pelanggan").toString()
                    val namaPelanggan = document.getString("nama_pelanggan")
                    gender = document.getString("jenis_kelamin").toString()
                    val tempatLahir = document.getString("tempat_lahir")
                    dateOfBirth = document.getString("tanggal_lahir").toString()
                    val blok = document.getString("blok")
                    val nomorRumah = document.getString("nomor_rumah")
                    val rt = document.getString("rt")
                    val rw = document.getString("rw")
                    val kecamatan = document.getString("kecamatan")
                    val kelurahan = document.getString("kelurahan")
                    val perumahan = document.getString("perumahan")
                    val noTelp = document.getString("no_telp")
                    val noHp = document.getString("no_hp")
                    houseStatus = document.getString("status_rumah").toString()
                    val ktp = document.getString("ktp")
                    val kk = document.getString("kk")
                    val catatan = document.getString("catatan")
                    val totalPenghuni = document.getString("total_penghuni")
                    val sumberAirLain = document.getString("sumber_air_lain")

                    binding.apply {
                        textView.text = "Pengajuan PDAM Anda"
                        btnSend.text = "Update Pengajuan"
                        etNamaPelanggan.setText(namaPelanggan)
                        etTempatLahir.setText(tempatLahir)
                        etBlok.setText(blok)
                        etJenisKelamin.setText(gender)
                        etTanggalLahir.setText(dateOfBirth)
                        etNomor.setText(nomorRumah)
                        etRT.setText(rt)
                        etRW.setText(rw)
                        etKecamatan.setText(kecamatan)
                        etKelurahan.setText(kelurahan)
                        etPerumahan.setText(perumahan)
                        etNoTelp.setText(noTelp)
                        etNoHP.setText(noHp)
                        etKTP.setText(ktp)
                        etKK.setText(kk)
                        etCatatan.setText(catatan)
                        etJumlahPenghuni.setText(totalPenghuni)
                        etSumberAirLain.setText(sumberAirLain)
                    }

                    if (customerId.contains("PDAM-")) {
                        binding.notificationCounter.text = "6"
                        showSuccessDialog(
                            "Pemberitahuan Pasang Baru",
                            "Anda sebelumnya sudah mengajukan pemasangan baru melalui apliaksi PDAM Delta Tirta Sidoarjo, dan sudah berhasil di aktivasi.\n\nKlik oke untuk melihat / memperbarui data pengajuan PDAM anda"
                        )
                    } else {
                        showSuccessDialog(
                            "Pemberitahuan Pasang Baru",
                            "Pengajuan pemasangan PDAM anda sendang dalam proses peninjauan oleh PDAM Delta Tirta Sidoarjo.\n\nKlik oke untuk melihat / memperbarui data pengajuan PDAM anda"
                        )
                    }
                }
            }
    }

    private fun submitInstallationPDAMValidation() {
        binding.apply {
            val name = etNamaPelanggan.text.toString().trim()
            val address = etTempatLahir.text.toString().trim()
            val block = etBlok.text.toString().trim()
            val houseNumber = etNomor.text.toString().trim()
            val rt = etRT.text.toString().trim()
            val rw = etRW.text.toString().trim()
            val subDistrict = etKecamatan.text.toString().trim()
            val ward = etKelurahan.text.toString().trim()
            val housingArea = etPerumahan.text.toString().trim()
            val callNumber = etNoTelp.text.toString().trim()
            val phoneNumber = etNoHP.text.toString().trim()
            val ktp = etKTP.text.toString().trim()
            val kk = etKK.text.toString().trim()
            val notes = etCatatan.text.toString().trim()
            val totalResidence = etJumlahPenghuni.text.toString().trim()
            val waterSource = etSumberAirLain.text.toString().trim()

            if (name.isEmpty()) {
                showToast("Nama Pelanggan tidak boleh kosong!")
            } else if (gender.isEmpty()) {
                showToast("Jenis Kelamin tidak boleh kosong!")
            } else if (address.isEmpty()) {
                showToast("Tempat Lahir tidak boleh kosong!")
            } else if (dateOfBirth.isEmpty()) {
                showToast("Tanggal Lahir tidak boleh kosong!")
            } else if (block.isEmpty()) {
                showToast("Blok tidak boleh kosong!")
            } else if (houseNumber.isEmpty()) {
                showToast("Nomor Rumah tidak boleh kosong!")
            } else if (rt.isEmpty()) {
                showToast("RT tidak boleh kosong!")
            } else if (rw.isEmpty()) {
                showToast("RW tidak boleh kosong!")
            } else if (subDistrict.isEmpty()) {
                showToast("Kecamatan tidak boleh kosong!")
            } else if (ward.isEmpty()) {
                showToast("Kelurahan tidak boleh kosong!")
            } else if (housingArea.isEmpty()) {
                showToast("Perumahan tidak boleh kosong!")
            } else if (callNumber.isEmpty()) {
                showToast("Nomor Telpon tidak boleh kosong!")
            } else if (phoneNumber.isEmpty()) {
                showToast("Nomor Handohone tidak boleh kosong!")
            } else if (houseStatus.isEmpty()) {
                showToast("Status Rumah tidak boleh kosong!")
            } else if (ktp.isEmpty()) {
                showToast("KTP tidak boleh kosong!")
            } else if (kk.isEmpty()) {
                showToast("KK tidak boleh kosong!")
            } else if (notes.isEmpty()) {
                showToast("Catatan tidak boleh kosong!")
            } else if (totalResidence.isEmpty()) {
                showToast("Jumlah Penghuni tidak boleh kosong!")
            } else if (waterSource.isEmpty()) {
                showToast("Sumber Air Lain tidak boleh kosong!")
            } else {

                progressBar.visibility = View.VISIBLE

                if (!isAlreadySubmitted) {
                    val customerId = "PB-${System.currentTimeMillis()}"

                    val data = mapOf(
                        "id_pelanggan" to uuid,
                        "no_pelanggan" to customerId,
                        "nama_pelanggan" to name,
                        "jenis_kelamin" to gender,
                        "tempat_lahir" to address,
                        "tanggal_lahir" to dateOfBirth,
                        "blok" to block,
                        "nomor_rumah" to houseNumber,
                        "rt" to rt,
                        "rw" to rw,
                        "kecamatan" to subDistrict,
                        "kelurahan" to ward,
                        "perumahan" to housingArea,
                        "no_telp" to callNumber,
                        "no_hp" to phoneNumber,
                        "status_rumah" to houseStatus,
                        "ktp" to ktp,
                        "kk" to kk,
                        "catatan" to notes,
                        "total_penghuni" to totalResidence,
                        "sumber_air_lain" to waterSource,
                        "status_pdam" to "active"
                    )

                    val dataUser = mapOf(
                        "customer_id" to customerId,
                        "age" to age.toString()
                    )

                    FirebaseFirestore
                        .getInstance()
                        .collection("users")
                        .document(uuid)
                        .update(dataUser)

                    FirebaseFirestore
                        .getInstance()
                        .collection("installation")
                        .document(customerId)
                        .set(data)
                        .addOnCompleteListener { result ->
                            binding.progressBar.visibility = View.GONE
                            if (result.isSuccessful) {
                                showSuccessDialog(
                                    "Sukses Mengajukan Instalasi PDAM",
                                    "Admin PDAM Delta Tirta Sidoarjo akan menghubungi anda, selanjutnya tim instalasi PDAM akan melakukan survey ke lokasi anda\n\nTerima kasih."
                                )
                            } else {
                                showFailureDialog()
                            }
                        }


                } else {
                    val data = mapOf(
                        "nama_pelanggan" to name,
                        "jenis_kelamin" to gender,
                        "tempat_lahir" to address,
                        "tanggal_lahir" to dateOfBirth,
                        "blok" to block,
                        "nomor_rumah" to houseNumber,
                        "rt" to rt,
                        "rw" to rw,
                        "kecamatan" to subDistrict,
                        "kelurahan" to ward,
                        "perumahan" to housingArea,
                        "no_telp" to callNumber,
                        "no_hp" to phoneNumber,
                        "status_rumah" to houseStatus,
                        "ktp" to ktp,
                        "kk" to kk,
                        "catatan" to notes,
                        "total_penghuni" to totalResidence,
                        "sumber_air_lain" to waterSource,
                    )

                    val dataUser = mapOf(
                        "age" to age.toString()
                    )

                    FirebaseFirestore
                        .getInstance()
                        .collection("users")
                        .document(uuid)
                        .update(dataUser)

                    FirebaseFirestore
                        .getInstance()
                        .collection("installation")
                        .document(customerId)
                        .update(data)
                        .addOnCompleteListener { result ->
                            binding.progressBar.visibility = View.GONE
                            if (result.isSuccessful) {
                                showSuccessDialog(
                                    "Sukses Memperbarui Data",
                                    "Admin PDAM Delta Tirta Sidoarjo akan mencatat pembaruan pada sistem.\n\nTerima kasih."
                                )
                            } else {
                                showFailureDialog()
                            }
                        }
                }
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showOptionMenu(action: String) {
        val camera: TextView
        val gallery: TextView
        val title: TextView
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.popup_option)
        title = dialog.findViewById(R.id.title)
        camera = dialog.findViewById(R.id.camera)
        gallery = dialog.findViewById(R.id.gallery)

        if (action == "gender") {
            title.text = "Pilih Jenis Kelamin"
            camera.text = "Pria"
            gallery.text = "Wanita"
        } else {
            title.text = "Pilih Status Rumah"
            camera.text = "Permanen / Kepemilikan"
            gallery.text = "Sewa / Kontrak"
        }


        camera.setOnClickListener {
            if (action == "gender") {
                gender = "Pria"
                binding.etJenisKelamin.text = gender
            } else {
                houseStatus = "Permanen / Kepemilikan"
                binding.etStatusRumah.text = houseStatus
            }
            dialog.dismiss()
        }
        gallery.setOnClickListener {
            if (action == "gender") {
                gender = "Wanita"
                binding.etJenisKelamin.text = gender
            } else {
                houseStatus = "Sewa / Kontrak"
                binding.etStatusRumah.text = houseStatus
            }
            dialog.dismiss()
        }
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
    }

    private fun showDatePickerDialog() {
        val datePickerListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            // Update the calendar with the selected date
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            // Format the selected date
            val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
            val formattedDate = dateFormat.format(calendar.time)

            // Display the formatted date
            dateOfBirth = formattedDate
            age = calculateAge(calendar.time)
            binding.etTanggalLahir.text = dateOfBirth
        }

        val datePickerDialog = DatePickerDialog(
            this,
            datePickerListener,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        // Show the date picker dialog
        datePickerDialog.show()
    }

    private fun calculateAge(selectedDate: Date): Int {
        val currentDate = Calendar.getInstance().time
        val diff = currentDate.time - selectedDate.time
        val age = kotlin.math.abs(diff / (365 * 24 * 60 * 60 * 1000L))
        return age.toInt()
    }

    /// munculkan dialog ketika gagal
    private fun showFailureDialog() {
        AlertDialog.Builder(this)
            .setTitle("Gagal Mengajukan Instalasi PDAM")
            .setMessage("Harap periksa koneksi Anda terlebih dahulu, dan pengguna tidak dapat mendaftar dengan akun yang sama dengan yang mendaftar sebelumnya")
            .setIcon(R.drawable.baseline_clear_24)
            .setCancelable(false)
            .setPositiveButton("OKE") { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
            .show()
    }

    /// munculkan dialog ketika sukses
    private fun showSuccessDialog(title: String, message: String) {
        AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setIcon(R.drawable.baseline_check_circle_outline_24)
            .setCancelable(false)
            .setPositiveButton("OKE") { dialogInterface, _ ->
                dialogInterface.dismiss()
                if (title != "Pemberitahuan Pasang Baru") {
                    finish()
                }
            }
            .show()
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}