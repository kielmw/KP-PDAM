package com.project.pdamdeltatirta

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.project.pdamdeltatirta.databinding.ActivityNotificationBinding
import com.project.pdamdeltatirta.ui.history.HistoryModel

class NotificationActivity : AppCompatActivity() {

    private var _binding: ActivityNotificationBinding? = null
    private val binding get() = _binding!!
    private val notificationList = ArrayList<HistoryModel>()
    private val uuid = FirebaseAuth.getInstance().currentUser!!.uid
    private var customerId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityNotificationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        checkIsAlreadySubmitPDAM()

        binding.btnBack.setOnClickListener {
            finish()
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

                if (customerId == "Belum Melakukan Pemasangan PDAM") {
                    binding.noData.visibility = View.VISIBLE
                } else {
                    binding.notificationWrapper.visibility = View.VISIBLE
                    getNotificationFromFirebase()
                }
            }
    }

    private fun getNotificationFromFirebase() {
        FirebaseFirestore
            .getInstance()
            .collection("transaction")
            .get()
            .addOnSuccessListener { documents ->
                documents.forEach {
                    val pesan = "" + it.getString("pesan")
                    notificationList.add(
                        HistoryModel(
                            pesan = pesan
                        )
                    )
                }
                setNotificationToView()
            }
    }

    private fun setNotificationToView() {
        notificationList.forEachIndexed { index, data ->
            binding.apply {
                when (index) {
                    0 -> {
                        septemberNotification.text = data.pesan
                    }
                    1 -> {
                        augustNotification.text = data.pesan
                    }
                    2 -> {
                        julyNotification.text = data.pesan
                    }
                    3 -> {
                        juneNotification.text = data.pesan
                    }
                    4 -> {
                        mayNotification.text = data.pesan
                    }
                    5 -> {
                        aprilNotification.text = data.pesan
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}