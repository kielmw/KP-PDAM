package com.project.pdamdeltatirta

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
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

                if (!customerId.contains("PDAM-")) {
                    binding.noData.visibility = View.VISIBLE
                } else {
                    getNotificationFromFirebase()
                }
            }
    }

    private fun getNotificationFromFirebase() {
        val uid = FirebaseAuth.getInstance().currentUser!!.uid
        FirebaseFirestore
            .getInstance()
            .collection("transaction")
            .document(uid)
            .collection("bulan")
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
                        binding.notificationWrapper.visibility = View.VISIBLE
                        septemberNotification.text = data.pesan
                    }
                    1 -> {
                        binding.notificationWrapper1.visibility = View.VISIBLE
                        augustNotification.text = data.pesan
                    }
                    2 -> {
                        binding.notificationWrapper2.visibility = View.VISIBLE
                        julyNotification.text = data.pesan
                    }
                    3 -> {
                        binding.notificationWrapper3.visibility = View.VISIBLE
                        juneNotification.text = data.pesan
                    }
                    4 -> {
                        binding.notificationWrapper4.visibility = View.VISIBLE
                        mayNotification.text = data.pesan
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