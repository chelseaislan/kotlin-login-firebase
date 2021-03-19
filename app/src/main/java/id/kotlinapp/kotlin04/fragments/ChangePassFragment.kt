package id.kotlinapp.kotlin04.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import id.kotlinapp.kotlin04.R
import kotlinx.android.synthetic.main.fragment_change_pass.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ChangePassFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ChangePassFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    // Initialize firebase
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
        return inflater.inflate(R.layout.fragment_change_pass, container, false)
    }

    // Main function
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()
        val user = auth.currentUser

        // Set visibility
        layoutConfirmOldPass.visibility = View.VISIBLE
        layoutNewPass.visibility = View.GONE

        // Confirm old password
        buttonOldPass.setOnClickListener {
            val password = editOldPass.text.toString().trim()
            // Validation
            if (password.isEmpty()) {
                Toast.makeText(activity, "Enter your password to continue.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Check credentials
            user?.let {
                val userCredential = EmailAuthProvider.getCredential(it.email!!, password)
                it.reauthenticate(userCredential).addOnCompleteListener {
                    if (it.isSuccessful) {
                        layoutConfirmOldPass.visibility = View.GONE
                        layoutNewPass.visibility = View.VISIBLE
                    } else if (it.exception is FirebaseAuthInvalidCredentialsException) {
                        Toast.makeText(activity, "Password is invalid.", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(activity, "${it.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            // Updating Password
            buttonUpdatePass.setOnClickListener {view ->
                val newPassword = editNewPass.text.toString().trim()
                val newPassword2 = editConfirmNewPass.text.toString().trim()
                if (newPassword.isEmpty() || newPassword2.isEmpty()) {
                    Toast.makeText(activity, "Please enter and confirm new password.", Toast.LENGTH_SHORT)
                        .show()
                    return@setOnClickListener
                }
                if (newPassword.length < 6) {
                    Toast.makeText(activity, "New password must be more than 6 characters.", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                if (newPassword != newPassword2) {
                    Toast.makeText(activity, "New passwords do not match.", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                user.let {
                    user.updatePassword(newPassword).addOnCompleteListener {
                        if (it.isSuccessful) {
                            Toast.makeText(activity, "Password updated successfully.", Toast.LENGTH_SHORT).show()
                            val actionPasswordUpdated = ChangePassFragmentDirections.actionPasswordUpdated()
                            Navigation.findNavController(view).navigate(actionPasswordUpdated)
                        } else {
                            Toast.makeText(activity, "${it.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ChangePassFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ChangePassFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}