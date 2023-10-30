package com.project.pdamdeltatirta.ui.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.project.pdamdeltatirta.databinding.FragmentHistoryBinding



class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val historyList = ArrayList<HistoryModel>()
    private val uuid = FirebaseAuth.getInstance().currentUser!!.uid
    private var customerId = ""


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        checkIsAlreadySubmitPDAM()
        return binding.root
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
                    getTransactionFromFirebase()
                }
            }
    }

    private fun getTransactionFromFirebase() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid != null) {
            FirebaseFirestore
                .getInstance()
                .collection("transaction")
                .document(uid)
                .collection("bulan")
                .whereNotEqualTo("jumlah", null)
                .get()
                .addOnSuccessListener { documents ->
                    documents.forEach {
                            val bulan = it.getString("bulan_pembayaran") ?: ""
                            val jumlah = it.getString("jumlah") ?: ""
                            val status = it.getString("status") ?: ""

                            historyList.add(
                                HistoryModel(
                                    bulan = bulan,
                                    jumlah = jumlah,
                                    status = status
                                )
                            )
                        }
                        //binding.historyWrapper.visibility = View.VISIBLE
                        setHistoryToView()
                }
                .addOnFailureListener { exception ->
                    // Handle any errors that occur during the query
                }
        }
    }







    private fun setHistoryToView() {
        historyList.forEachIndexed { index, data ->
            binding.apply {
                when (index) {
                    0 -> {
                        binding.historyWrapper.visibility = View.VISIBLE
                        textView8.text = data.bulan
                        textView9.text = data.jumlah
                        status8.text = data.status
                    }
                    1 -> {
                        binding.historyWrapper1.visibility = View.VISIBLE
                        textView88888.text = data.bulan
                        textView99999.text = data.jumlah
                        status88.text = data.status
                    }
                    2 -> {
                        binding.historyWrapper2.visibility = View.VISIBLE
                        textView888.text = data.bulan
                        textView999.text = data.jumlah
                        status999.text = data.status
                    }
                    3 -> {
                        binding.historyWrapper3.visibility = View.VISIBLE
                        textView888888.text = data.bulan
                        textView999999.text = data.jumlah
                        status888.text = data.status
                    }
                    4 -> {
                        binding.historyWrapper4.visibility = View.VISIBLE
                        textView8888888.text = data.bulan
                        textView9999999.text = data.jumlah
                        status9999999.text = data.status
                    }
                    5 -> {
                        binding.historyWrapper5.visibility = View.VISIBLE
                        textView88888888.text = data.bulan
                        textView99999999.text = data.jumlah
                        status88888888.text = data.status
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}