package edu.farmingdale.bcs421_bims

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import edu.farmingdale.bcs421_bims.databinding.FragmentSettingBinding

class SettingFragment : Fragment() {
    private lateinit var auth: FirebaseAuth
    private var _binding: FragmentSettingBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSettingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        binding.toolBar.ToolBarText.text = "Settings"
        binding.toolBar.leftIcon.visibility = View.GONE
        binding.toolBar.RightIcon.visibility = View.GONE

        binding.bnChangeUserInfo.setOnClickListener {
            //TODO Change user info
        }

        binding.bnChangePassword.setOnClickListener {
            showChangePasswordDialog()
        }

        binding.bnLogout.setOnClickListener {
           signOut()
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun signOut() {
        //Firebase sign out
        auth.signOut()

        //Google sign out
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        val googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)
        googleSignInClient.signOut().addOnCompleteListener(requireActivity()) {
            //Update UI after sign out
            val intent = Intent(requireActivity(), LoginActivity::class.java)
            startActivity(intent)
            //Close Fragments
            requireActivity().finish()
        }
    }

    private fun showChangePasswordDialog() {
        val editTextNewPassword = EditText(context).apply {
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            hint = "Enter new password"
        }
        AlertDialog.Builder(requireContext()) // Use requireContext() to get non-null context
            .setTitle("Change Password")
            .setView(editTextNewPassword)
            .setPositiveButton("Submit") { dialog, which ->
                val newPassword = editTextNewPassword.text.toString()
                if (newPassword.isNotEmpty()) {
                    changePassword(newPassword)
                } else {
                    Toast.makeText(requireContext(), "Password cannot be empty", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun changePassword(newPassword: String) {
        val user = auth.currentUser
        user?.updatePassword(newPassword)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(requireContext(), "Password updated successfully", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Failed to update password: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
}