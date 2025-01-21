package com.example.firebaseauthentication1

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.firebaseauthentication1.databinding.FragmentEmailBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class EmailFragment : Fragment() {
    private var _binding: FragmentEmailBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private lateinit var emailAdapter: EmailAdapter

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


        val emails = generateTestEmails()

        emailAdapter = EmailAdapter(emails)
        binding.rvEmails.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = emailAdapter
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


    private fun generateTestEmails(): List<Email> = listOf(
        Email(
            sender = "Google",
            subject = "Безопасность аккаунта",
            preview = "Мы обнаружили новый вход в ваш аккаунт"
        ),
        Email(
            sender = "GitHub",
            subject = "Новый коммит в репозитории",
            preview = "Пользователь UserName добавил изменения в проект"
        ),
        Email(
            sender = "Работа",
            subject = "Приглашение на интервью",
            preview = "Здравствуйте, мы хотели бы пригласить вас на собеседование"
        ),
        Email(
            sender = "Банк",
            subject = "Выписка по счету",
            preview = "Ваш баланс и последние транзакции"
        ),
        Email(
            sender = "Почта России",
            subject = "Посылка в отделении",
            preview = "Ваша посылка ожидает вас на почте"
        )
    )

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}