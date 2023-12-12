package edu.farmingdale.bcs421_bims

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import edu.farmingdale.bcs421_bims.databinding.FragmentItemBinding
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

        binding.toolBar.leftIcon.setOnClickListener {
            requireActivity().onBackPressed()
        }
        binding.toolBar.RightIcon.setOnClickListener {
            //showPopupMenu()
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
//        //Firebase sign out
//        auth.signOut()
//
//        //Google sign out
//        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//            .requestIdToken(getString(R.string.default_web_client_id))
//            .requestEmail()
//            .build()
//        val googleSignInClient = GoogleSignIn.getClient(this, gso)
//        googleSignInClient.signOut().addOnCompleteListener(this) {
//            //Update UI after sign out
//            val intent = Intent(this, LoginActivity::class.java)
//            startActivity(intent)
//            //Close HomeActivity
//            finish()
//        }
    }
}