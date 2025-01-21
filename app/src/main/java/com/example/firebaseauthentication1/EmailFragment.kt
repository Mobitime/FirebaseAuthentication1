package com.example.firebaseauthentication1

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.firebaseauthentication1.databinding.FragmentEmailBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase

class EmailFragment : Fragment() {
    private var _binding: FragmentEmailBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private lateinit var emailAdapter: EmailAdapter
    private val emailsList = mutableListOf<Email>()

    override fun onCreateView(
        inflater: LayoutInflater, 
        container: ViewGroup?, 
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEmailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = Firebase.auth

        setupToolbar()
        setupRecyclerView()
        setupAddEmailButton()
        loadEmails()
    }

    private fun setupRecyclerView() {
        emailAdapter = EmailAdapter(emailsList) { email ->
            deleteEmail(email)
        }
        binding.rvEmails.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = emailAdapter
        }
    }

    private fun setupAddEmailButton() {
        binding.fabAddEmail.setOnClickListener {
            val sender = binding.etSender.text.toString().trim()
            val subject = binding.etSubject.text.toString().trim()
            val preview = binding.etPreview.text.toString().trim()

            if (sender.isNotEmpty() && subject.isNotEmpty() && preview.isNotEmpty()) {
                addEmail(sender, subject, preview)
            } else {
                Toast.makeText(context, "Заполните все поля", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupToolbar() {
        binding.toolbar.apply {
            title = "Входящие (${auth.currentUser?.email})"
            inflateMenu(R.menu.email_menu)
            
            setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.action_exit -> {
                        auth.signOut()
                        findNavController().navigate(R.id.action_emailFragment_to_loginFragment)
                        true
                    }
                    else -> false
                }
            }
        }
    }

    private fun loadEmails() {
        val userId = auth.currentUser?.uid
        userId?.let {
            val emailsRef = FirebaseDatabase.getInstance().getReference("users/$userId/emails")
            emailsRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    emailsList.clear()
                    for (childSnapshot in snapshot.children) {
                        val email = childSnapshot.getValue(Email::class.java)?.copy(
                            id = childSnapshot.key ?: ""
                        )
                        email?.let { emailsList.add(it) }
                    }
                    emailAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, "Ошибка загрузки писем", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun addEmail(sender: String, subject: String, preview: String) {
        val userId = auth.currentUser?.uid
        userId?.let {
            val database = FirebaseDatabase.getInstance()
            val emailsRef = database.getReference("users/$userId/emails")
            
            val emailId = emailsRef.push().key
            emailId?.let { id ->
                val email = Email(id, sender, subject, preview)
                emailsRef.child(id).setValue(email)
                    .addOnSuccessListener {
                        Toast.makeText(context, "Письмо сохранено", Toast.LENGTH_SHORT).show()
                        clearInputFields()
                    }
                    .addOnFailureListener {
                        Toast.makeText(context, "Ошибка сохранения", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }

    private fun deleteEmail(email: Email) {
        val userId = auth.currentUser?.uid
        userId?.let {
            val emailRef = FirebaseDatabase.getInstance()
                .getReference("users/$userId/emails/${email.id}")
            
            emailRef.removeValue()
                .addOnSuccessListener {
                    Toast.makeText(context, "Письмо удалено", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Ошибка удаления", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun clearInputFields() {
        binding.etSender.text?.clear()
        binding.etSubject.text?.clear()
        binding.etPreview.text?.clear()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}