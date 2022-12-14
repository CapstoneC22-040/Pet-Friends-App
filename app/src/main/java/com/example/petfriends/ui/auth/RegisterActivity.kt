package com.example.petfriends.ui.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.example.petfriends.R
import com.example.petfriends.data.local.model.UserModel
import com.example.petfriends.databinding.ActivityRegisterBinding
import com.example.petfriends.ui.MainActivity
import com.example.petfriends.ui.add_data.add_pet.MainAddPetActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import java.util.regex.Matcher
import java.util.regex.Pattern

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var mAuth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        mAuth = Firebase.auth

        showLoading(false)

        setupRegister()
        binding.tvLoginRegister.setOnClickListener {
            startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
        }
    }

    private fun setupRegister() {
        binding.apply {
                btnRegister.setOnClickListener {
                    val photo = tvUidRegister.text.toString()
                    val name = edNameRegister.text.toString()
                    val email = edEmailRegister.text.toString()
                    val password = edPasswordRegister1.text.toString()
                    val password2 = edPasswordRegister2.text.toString()

                    when{
                        name.isEmpty() -> {
                            edNameRegister.error = getString(R.string.enter_name)
                            edNameRegister.requestFocus()
                        }
                        email.isEmpty() -> {
                            edEmailRegister.error = getString(R.string.enter_email)
                            edEmailRegister.requestFocus()
                        }
                        password.isEmpty() -> {
                            errEmail.visibility = View.VISIBLE
                            edPasswordRegister1.error = getString(R.string.enter_password)
                            edPasswordRegister1.requestFocus()
                        }
                        password.length < 6 -> {
                            edPasswordRegister1.error = getString(R.string.length_password)
                            edPasswordRegister1.requestFocus()
                        }
                        !isEmailValid(email) -> {
                            errEmail.visibility = View.VISIBLE
                            edEmailRegister.requestFocus()
                        } (password != password2) -> {
                            AlertDialog.Builder(this@RegisterActivity).apply {
                                setTitle("Password")
                                setMessage("Password is not match")
                                setPositiveButton("Back"){ dialog, _ ->
                                    dialog.dismiss()
                                }
                                create()
                                show()
                        }
                        }else -> {
                                showLoading(true)
                                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this@RegisterActivity){
                                    val mUser = Firebase.auth.currentUser
                                    val uId = mAuth.currentUser?.uid.toString()
                                    val user = UserModel(
                                        uId,
                                        photo,
                                        name,
                                        email,
                                        password
                                    )
                                    if(it.isSuccessful) {
                                        showLoading(false)
                                        val profileUpdates = userProfileChangeRequest{
                                            displayName = user.name
                                        }
                                        mUser!!.updateProfile(profileUpdates).addOnCompleteListener(this@RegisterActivity){ task ->
                                            if (task.isSuccessful){
                                                Log.d(TAG, "User profile updated.")
                                                actionDatabase(user)
                                            }else{
                                                Log.d(TAG, "User profile error:", it.exception)
                                            }
                                        }
                                        Log.d(TAG, getString(R.string.success_register))
                                        AlertDialog.Builder(this@RegisterActivity).apply {
                                            setTitle(getString(R.string.success))
                                            setMessage(getString(R.string.success_register))
                                            setPositiveButton(getString(R.string.cont)){ _, _ ->
//                                                val intent = Intent(this@RegisterActivity, MainActivity::class.java)
//                                                startActivity(intent)
//                                                finish()
                                                updateUi(mAuth.currentUser)
                                            }
                                            create()
                                            show()
                                        }
                                    }else {
                                        showLoading(false)
                                        Log.d(TAG, getString(R.string.failed_register), it.exception)
                                        AlertDialog.Builder(this@RegisterActivity).apply {
                                            setTitle(getString(R.string.failed))
                                            setMessage(getString(R.string.email_is_used))
                                            setPositiveButton(getString(R.string.cont)){ _, _ ->
                                                show().dismiss()
                                            }
                                            create()
                                            show()
                                        }
                                        binding.edEmailRegister.error = getString(R.string.email_is_used)
                                        binding.edEmailRegister.setText("")
                                    }
                                }
                            }
                    }
                }
        }
    }

    private fun updateUi(currentUser: FirebaseUser?) {
        database = FirebaseDatabase.getInstance().getReference("Pets")
        val user = mAuth.currentUser
        if (currentUser != null){
            database.child(user?.uid.toString()).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val petCreated = snapshot.child("petCreated")
                    if (petCreated.exists()) {
                        Log.d(TAG, "Success ${petCreated}")
                        val mainactivity = Intent(this@RegisterActivity, MainActivity::class.java)
                        mainactivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(mainactivity)
                    }else {
                        val addpetactivity = Intent(this@RegisterActivity, MainAddPetActivity::class.java)
                        addpetactivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(addpetactivity)
                    }
                    finish()
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d(TAG, error.message)
                }

            })
        }

    }

    private fun actionDatabase(user: UserModel) {
        database = FirebaseDatabase.getInstance().getReference("Users")
        database.child(user.uId).setValue(user)
            .addOnCompleteListener(this@RegisterActivity){ task ->
                showLoading(true)
                if (task.isSuccessful) {
                    showLoading(false)
                    Log.w(TAG, "success")
                }else {
                    showLoading(false)
                    Log.w(TAG, "failure", task.exception)
                }
            }

    }

    private fun isEmailValid(email: String): Boolean {
        val expression = "^[\\w.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$"
        val pattern: Pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE)
        val matcher: Matcher = pattern.matcher(email)
        return matcher.matches()
    }

    private fun showLoading(isLoading: Boolean){
        binding.pbRegister.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    companion object{
        private const val TAG = "RegisterActivity"
    }
}