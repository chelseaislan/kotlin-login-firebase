package id.kotlinapp.kotlin04.fragments

import android.app.Activity.RESULT_OK
import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.ktx.Firebase
import com.google.firebase.provider.FirebaseInitProvider
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import id.kotlinapp.kotlin04.R
import id.kotlinapp.kotlin04.Splash
import kotlinx.android.synthetic.main.fragment_profile.*
import java.io.ByteArrayOutputStream

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var imageUri: Uri
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    // Main function di sini
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        swipeRefresh.setOnRefreshListener {
//            refreshAction()                    // refresh your list contents somehow
//            swipeRefresh.isRefreshing = false   // reset the SwipeRefreshLayout (stop the loading spinner)
//        }

        // Initialize firebase
        auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        if (user != null) {
            // Check if the user already has photo or not
            when {
                user.photoUrl != null -> Picasso.get().load(user.photoUrl).into(imgProfile)
                else -> Picasso.get().load("https://bit.ly/3rvDO2w").into(imgProfile) // foto telur
            }

            // Initialize user data with edit text
            editFullName.setText(user.displayName)
            editPhone.setText(user.phoneNumber)
            editEmail.setText(user.email)

            // Verified email = blue, else = grey
            if (user.isEmailVerified) {
                imgVerified.visibility = View.VISIBLE
                txtVerified.visibility = View.VISIBLE
            } else {
                txtUnverified.visibility = View.VISIBLE
            }
            when {
                user.phoneNumber.isNullOrEmpty() -> editPhone.setText("")
                else -> editPhone.setText(user.phoneNumber)
            }
        }

        // No. 1 Button to Open Camera
        txtCamera.setOnClickListener {
            intentCamera()
        }

        // Button to save changes
        buttonSaveChanges.setOnClickListener {
            val image = when {
                ::imageUri.isInitialized -> imageUri // Kalau udah upload, pake gambar itu
                user?.photoUrl == null -> Uri.parse("https://bit.ly/3rvDO2w") // Kalo di awal belum upload, pake gambar telur
                else -> user.photoUrl // Kalo belum upload yg ke2 atau lebih, pake gambar telur
            }

            val name = editFullName.text.toString().trim()
            if (name.isEmpty()) {
                Toast.makeText(activity, "Please fill your name.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .setPhotoUri(image)
                .build().also {
                    user?.updateProfile(it)?.addOnCompleteListener {
                        if (it.isSuccessful) {
                            Log.d(TAG, "onViewCreated: Update profile for ${auth.currentUser?.uid} successful.")
                            Toast.makeText(activity, "Update profile successful.", Toast.LENGTH_SHORT).show()
                        } else {
                            Log.d(TAG, "onViewCreated: Update profile for ${auth.currentUser?.uid} failed.")
                            Log.d(TAG, "${it.exception?.message}")
                            Toast.makeText(activity, "${it.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
        }

        // Click to verify email. If already, blue check mark.
        txtUnverified.setOnClickListener {
            user?.sendEmailVerification()?.addOnCompleteListener {
                when {
                    it.isSuccessful -> Toast.makeText(activity, "Verification email has been sent.", Toast.LENGTH_SHORT).show()
                    else -> Toast.makeText(activity, "Error sending verification email: ${it.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Click edit email section
        editEmail.setOnClickListener() {
            val actionUpdEmail = ProfileFragmentDirections.actionUpdEmail()
            Navigation.findNavController(it).navigate(actionUpdEmail)
        }

        // Button to change password
        buttonChangePass.setOnClickListener {
            val actionUpdatePassword = ProfileFragmentDirections.actionUpdatePassword()
            Navigation.findNavController(it).navigate(actionUpdatePassword)
        }

        // Button to Log out
        buttonLogout.setOnClickListener {
            Log.d("MainActivity", "onCreate: Log out successful.")
            FirebaseAuth.getInstance().signOut()
            Toast.makeText(activity, "Logged out successfully.", Toast.LENGTH_SHORT).show()
            val i = Intent(activity, Splash::class.java)
            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(i)
        }
    }

    // no. 2 Open Camera
    private fun intentCamera() {
        val i = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        activity?.packageManager?.let { i.resolveActivity(it) }
        startActivityForResult(i, REQUEST_CAMERA)
    }

    // no. 3
    companion object {
        const val REQUEST_CAMERA = 100
    }

    // no. 4
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CAMERA && resultCode == RESULT_OK) {
            val imgBitmap = data?.extras?.get("data") as Bitmap
            uploadImage(imgBitmap)
        }
    }

    // no. 5 Upload image
    private fun uploadImage(imgBitmap: Bitmap) {
        val baos = ByteArrayOutputStream()
        val ref = FirebaseStorage.getInstance().reference.child("img/${FirebaseAuth.getInstance().currentUser?.uid}")
        imgBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val image = baos.toByteArray()

        ref.putBytes(image)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    ref.downloadUrl.addOnCompleteListener {
                        it.result?.let {
                            imageUri = it
                            imgProfile.setImageBitmap(imgBitmap)
                        }
                    }
                }
            }

    }
}