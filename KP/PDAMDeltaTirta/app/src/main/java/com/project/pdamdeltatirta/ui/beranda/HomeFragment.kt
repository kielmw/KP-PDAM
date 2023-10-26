package com.project.pdamdeltatirta.ui.beranda

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.project.pdamdeltatirta.NotificationActivity
import com.project.pdamdeltatirta.R
import com.project.pdamdeltatirta.databinding.FragmentHomeBinding
import com.project.pdamdeltatirta.ui.beranda.aduan.AduanActivity
import com.project.pdamdeltatirta.ui.beranda.cekmeter.CekMeterActivity
import com.project.pdamdeltatirta.ui.beranda.cektagihan.CekTagihanActivity
import com.project.pdamdeltatirta.ui.beranda.pasangbaru.PasangBaruActivity
import java.util.*

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val uuid = FirebaseAuth.getInstance().currentUser!!.uid
    private var customerId = ""


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
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

                if (customerId != "Belum Melakukan Pemasangan PDAM") {
                    binding.notificationCounter.text = "6"
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.apply {
            Glide.with(requireActivity())
                .load(R.drawable.ic_icon_app)
                .into(imageView)
            Glide.with(requireActivity())
                .load(R.drawable.banner1)
                .into(banner1)
            Glide.with(requireActivity())
                .load(R.drawable.banner2)
                .into(banner2)
            Glide.with(requireActivity())
                .load(R.drawable.banner3)
                .into(banner3)
            Glide.with(requireActivity())
                .load(R.drawable.category1)
                .into(cat1)
            Glide.with(requireActivity())
                .load(R.drawable.category2)
                .into(cat2)
            Glide.with(requireActivity())
                .load(R.drawable.category3)
                .into(cat3)
            Glide.with(requireActivity())
                .load(R.drawable.category4)
                .into(cat4)
            Glide.with(requireActivity())
                .load(R.drawable.location)
                .into(location)


            btnGoToMaps.setOnClickListener {
                val latitude = -7.450325464947562
                val longitude = 112.71295946205967

                val uri = Uri.parse("geo:$latitude,$longitude")
                val mapIntent = Intent(Intent.ACTION_VIEW, uri)
                mapIntent.setPackage("com.google.android.apps.maps") // Open in Google Maps app

                if (mapIntent.resolveActivity(requireActivity().packageManager) != null) {
                    startActivity(mapIntent)
                } else {
                    // If Google Maps app is not available, open in a web browser
                    val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://maps.google.com/?q=$latitude,$longitude"))
                    startActivity(webIntent)
                }
            }

            imageView2.setOnClickListener {
                startActivity(Intent(activity, NotificationActivity::class.java))
            }

            btnPasangBaru.setOnClickListener {
                startActivity(Intent(activity, PasangBaruActivity::class.java))
            }

            btnAduan.setOnClickListener {
                startActivity(Intent(activity, AduanActivity::class.java))
            }

            btnCekTagihan.setOnClickListener {
                startActivity(Intent(activity, CekTagihanActivity::class.java))
            }

            btnCekMeter.setOnClickListener {
                startActivity(Intent(activity, CekMeterActivity::class.java))
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}