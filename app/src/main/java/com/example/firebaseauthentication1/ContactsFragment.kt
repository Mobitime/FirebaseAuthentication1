package com.example.firebaseauthentication1

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ContactsFragment : Fragment() {
    private lateinit var nameEditText: EditText
    private lateinit var phoneEditText: EditText
    private lateinit var saveButton: Button
    private lateinit var backButton: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var contactAdapter: ContactAdapter
    private val contactsList = mutableListOf<Contact>()

    override fun onCreateView(
        inflater: LayoutInflater, 
        container: ViewGroup?, 
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_contacts, container, false)

        nameEditText = view.findViewById(R.id.editTextName)
        phoneEditText = view.findViewById(R.id.editTextPhone)
        saveButton = view.findViewById(R.id.buttonSave)
        backButton = view.findViewById(R.id.buttonBack)
        recyclerView = view.findViewById(R.id.recyclerViewContacts)

        setupRecyclerView()
        setupSaveButton()
        setupBackButton()
        loadContacts()

        return view
    }

    private fun setupRecyclerView() {
        contactAdapter = ContactAdapter(contactsList)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = contactAdapter
    }

    private fun setupSaveButton() {
        saveButton.setOnClickListener {
            val name = nameEditText.text.toString().trim()
            val phone = phoneEditText.text.toString().trim()

            if (name.isNotEmpty() && phone.isNotEmpty()) {
                saveContact(name, phone)
            } else {
                Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupBackButton() {
        backButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun saveContact(name: String, phone: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        userId?.let { 
            val database = FirebaseDatabase.getInstance()
            val contactsRef = database.getReference("users/$userId/contacts")
            
            val contactId = contactsRef.push().key
            contactId?.let { id ->
                val contact = Contact(name, phone)
                contactsRef.child(id).setValue(contact)
                    .addOnSuccessListener {
                        Toast.makeText(context, "Contact saved", Toast.LENGTH_SHORT).show()
                        clearInputFields()
                    }
                    .addOnFailureListener {
                        Toast.makeText(context, "Error saving contact", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }

    private fun loadContacts() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        userId?.let {
            val contactsRef = FirebaseDatabase.getInstance().getReference("users/$userId/contacts")
            contactsRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    contactsList.clear()
                    for (childSnapshot in snapshot.children) {
                        val contact = childSnapshot.getValue(Contact::class.java)
                        contact?.let { contactsList.add(it) }
                    }
                    contactAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, "Error loading contacts", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun clearInputFields() {
        nameEditText.text.clear()
        phoneEditText.text.clear()
    }
}
