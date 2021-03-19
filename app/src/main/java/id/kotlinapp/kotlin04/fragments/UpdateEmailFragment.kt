package id.kotlinapp.kotlin04.fragments

import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import id.kotlinapp.kotlin04.MainActivity
import id.kotlinapp.kotlin04.R
import kotlinx.android.synthetic.main.fragment_update_email.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [UpdateEmailFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class UpdateEmailFragment : Fragment() {
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
        return inflater.inflate(R.layout.fragment_update_email, container, false)
    }

    // Main function
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()
        val user = auth.currentUser

        // Set visibility
        layoutConfirmPass.visibility = View.VISIBLE
        layoutUpdateEmail.visibility = View.GONE

        buttonConfirmPass.setOnClickListener {
            val password = editConfirmPass.text.toString().trim()
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
                        layoutConfirmPass.visibility = View.GONE
                        layoutUpdateEmail.visibility = View.VISIBLE
                    } else if (it.exception is FirebaseAuthInvalidCredentialsException) {
                        Toast.makeText(activity, "Password is invalid.", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(activity, "${it.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            // Updating Email
            buttonUpdateEmail.setOnClickListener {view ->
                val email = editEmail1.text.toString().trim()
                val email2 = editEmail2.text.toString().trim()
                if (email.isEmpty() || email2.isEmpty()) {
                    Toast.makeText(activity, "Please enter and confirm email.", Toast.LENGTH_SHORT)
                        .show()
                    return@setOnClickListener
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    Toast.makeText(activity, "Please enter a valid email.", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                if (email != email2) {
                    Toast.makeText(activity, "Emails do not match.", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                user.let {
                    user.updateEmail(email).addOnCompleteListener {
                        if (it.isSuccessful) {
                            Toast.makeText(activity, "Email updated successfully.", Toast.LENGTH_SHORT).show()
                            val actionEmailUpdated = UpdateEmailFragmentDirections.actionEmailUpdated()
                            Navigation.findNavController(view).navigate(actionEmailUpdated)
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
         * @return A new instance of fragment UpdateEmailFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            UpdateEmailFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}