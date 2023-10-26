package com.project.pdamdeltatirta.ui.account

import android.app.Activity.RESULT_OK
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.project.pdamdeltatirta.MainActivity
import com.project.pdamdeltatirta.R
import com.project.pdamdeltatirta.databinding.FragmentAccountBinding

class AccountFragment : Fragment() {

    private var _binding: FragmentAccountBinding? = null
    private val binding get() = _binding!!
    private var mProgressDialog: ProgressDialog? = null
    private val REQUEST_CODE = 1001
    private var image = ""
    private var uid = FirebaseAuth.getInstance().currentUser!!.uid


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAccountBinding.inflate(inflater, container, false)
        mProgressDialog = ProgressDialog(activity)
        getUserFromFirebase()
        return binding.root
    }

    private fun getUserFromFirebase() {
        FirebaseFirestore
            .getInstance()
            .collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener { data ->
                if (data.exists()) {
                    val image = "" + data.data!!["image_profile"]
                    val name = "" + data.data!!["name"]
                    val customerId = "" + data.data!!["customer_id"]
                    val email = "" + data.data!!["email"]
                    var age = "" + data.data!!["age"]
                    if (age.isEmpty()) age = " - "

                    binding.apply {
                        Glide.with(requireActivity())
                            .load(image)
                            .error(R.drawable.ic_placeholder_dp)
                            .into(ivImageProfile)

                        etName.setText(name)
                        etNoPelanggan.setText(customerId)
                        etEmail.setText(email)
                        etAge.setText(age)
                    }
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.apply {
            btnLogout.setOnClickListener {
                AlertDialog.Builder(requireContext())
                    .setTitle("Konfirmasi Logout")
                    .setMessage("Apakah anda  yakin ingin logout ?")
                    .setIcon(R.drawable.baseline_warning_24)
                    .setPositiveButton("YA") { dialogInterface, _ ->
                        // sign out dari firebase autentikasi
                        FirebaseAuth.getInstance().signOut()
                        dialogInterface.dismiss()

                        val intent = Intent(requireContext(), MainActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                        activity?.finish()

                    }
                    .setNegativeButton("TIDAK", null)
                    .show()
            }

            btnEdit.setOnClickListener {
                showOptionMenu()
            }
        }
    }

    /// menampilkan pilihan help atau aboutr apps
    private fun showOptionMenu() {
        val camera: TextView
        val gallery: TextView
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.popup_option)
        camera = dialog.findViewById(R.id.camera)
        gallery = dialog.findViewById(R.id.gallery)
        camera.setOnClickListener {
            showProgressBar()
            ImagePicker.with(this)
                .cameraOnly()
                .compress(1024)
                .start(REQUEST_CODE)
            dialog.dismiss()
        }
        gallery.setOnClickListener {
            showProgressBar()
            ImagePicker.with(this)
                .galleryOnly()
                .compress(1024)
                .start(REQUEST_CODE)
            dialog.dismiss()
        }
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
    }

    private fun showProgressBar() {
        mProgressDialog?.setMessage("Sedang memuat profil...")
        mProgressDialog?.setCanceledOnTouchOutside(false)
        mProgressDialog?.show()
    }

    /// ini adalah program untuk menambahkan gambar kedalalam halaman ini
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        mProgressDialog?.dismiss()
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                REQUEST_CODE -> {
                    uploadImage(data?.data)
                    Glide.with(this)
                        .load(data?.data)
                        .into(binding.ivImageProfile)
                }
            }
        }
    }

    /// fungsi untuk mengupload foto kedalam cloud storage
    private fun uploadImage(data: Uri?) {
        showProgressBar()
        val mStorageRef = FirebaseStorage.getInstance().reference
        val imageFileName = "user/image_" + System.currentTimeMillis() + ".png"
        mStorageRef.child(imageFileName).putFile(data!!)
            .addOnSuccessListener {
                mStorageRef.child(imageFileName).downloadUrl
                    .addOnSuccessListener { uri: Uri ->
                        image = uri.toString()

                        val dataImage = mapOf(
                            "image_profile" to image
                        )
                        FirebaseFirestore
                            .getInstance()
                            .collection("users")
                            .document(uid)
                            .update(dataImage)
                            .addOnCompleteListener {
                                mProgressDialog?.dismiss()
                            }
                    }
                    .addOnFailureListener { e: Exception ->
                        mProgressDialog?.dismiss()
                        e.printStackTrace()
                        showToast("Gagal upload foto profil")
                        Log.e("imageDp: ", e.toString())
                    }
            }
            .addOnFailureListener { e: Exception ->
                e.printStackTrace()
                mProgressDialog?.dismiss()
                showToast( "Gagal upload foto profil")
                Log.e("imageDp: ", e.toString())
            }
    }

    private fun showToast(message: String) {
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}