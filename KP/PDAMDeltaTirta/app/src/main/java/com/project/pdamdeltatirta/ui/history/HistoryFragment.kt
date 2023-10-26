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

                if (customerId == "Belum Melakukan Pemasangan PDAM") {
                    binding.noData.visibility = View.VISIBLE
                } else {
                    binding.historyWrapper.visibility = View.VISIBLE
                    getTransactionFromFirebase()
                }
            }
    }

    private fun getTransactionFromFirebase() {
        FirebaseFirestore
            .getInstance()
            .collection("transaction")
            .get()
            .addOnSuccessListener { documents ->
                documents.forEach {
                    val bulan = "" + it.getString("bulan")
                    val jumlah = "" + it.getString("jumlah")
                    val status = "" + it.getString("status")
                    historyList.add(
                        HistoryModel(
                            bulan = bulan,
                            jumlah = jumlah,
                            status = status
                        )
                    )
                }
                setHistoryToView()
            }
    }

    private fun setHistoryToView() {
        historyList.forEachIndexed { index, data ->
            binding.apply {
                when (index) {
                    0 -> {
                        textView8.text = data.bulan
                        textView9.text = data.jumlah
                        status8.text = data.status
                    }
                    1 -> {
                        textView88888.text = data.bulan
                        textView99999.text = data.jumlah
                        status88.text = data.status
                    }
                    2 -> {
                        textView888.text = data.bulan
                        textView999.text = data.jumlah
                        status999.text = data.status
                    }
                    3 -> {
                        textView888888.text = data.bulan
                        textView999999.text = data.jumlah
                        status888.text = data.status
                    }
                    4 -> {
                        textView8888888.text = data.bulan
                        textView9999999.text = data.jumlah
                        status9999999.text = data.status
                    }
                    5 -> {
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