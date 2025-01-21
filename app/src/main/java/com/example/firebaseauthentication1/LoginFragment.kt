package com.example.firebaseauthentication1

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.example.firebaseauthentication1.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        auth = FirebaseAuth.getInstance()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        setupToolbar()


        AnimationUtils.animateLoginScreen(
            binding.logo,
            binding.tilEmail,
            binding.tilPassword,
            binding.btnLogin,
            binding.tvRegisterLink
        )

        binding.btnLogin.setOnClickListener {

            AnimationUtils.createButtonPressAnimation(it)

            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (validateInput(email, password)) {
                loginUser(email, password)
            }
        }

        binding.tvRegisterLink.setOnClickListener {

            AnimationUtils.createFragmentTransitionAnimation(it)
            findNavController().navigate(R.id.action_loginFragment_to_registrationFragment)
        }
    }


    private fun validateInput(email: String, password: String): Boolean {
        return when {
            email.isEmpty() -> {
                binding.etEmail.error = "Электронная почта не может быть пустой"
                binding.tilEmail.startAnimation(AnimationUtils.createShakeAnimation(requireContext()))
                false
            }
            !isValidEmail(email) -> {
                binding.etEmail.error = "Неверный формат электронной почты"
                binding.tilEmail.startAnimation(AnimationUtils.createShakeAnimation(requireContext()))
                false
            }
            password.isEmpty() -> {
                binding.etPassword.error = "Пароль не может быть пустым"
                binding.tilPassword.startAnimation(AnimationUtils.createShakeAnimation(requireContext()))
                false
            }
            password.length < 6 -> {
                binding.etPassword.error = "Пароль должен содержать не менее 6 символов"
                binding.tilPassword.startAnimation(AnimationUtils.createShakeAnimation(requireContext()))
                false
            }
            else -> true
        }
    }


    private fun isValidEmail(email: String): Boolean {
        val emailRegex = Regex("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")
        return emailRegex.matches(email)
    }


    private fun loginUser(email: String, password: String) {

        binding.btnLogin.isEnabled = false

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->

                binding.btnLogin.isEnabled = true

                if (task.isSuccessful) {

                    Toast.makeText(
                        requireContext(),
                        "Вход выполнен",
                        Toast.LENGTH_SHORT
                    ).show()


                    AnimationUtils.createFragmentTransitionAnimation(binding.root)
                    findNavController().navigate(R.id.action_loginFragment_to_emailFragment)
                } else {

                    val errorMessage = when (task.exception) {
                        is com.google.firebase.auth.FirebaseAuthInvalidCredentialsException ->
                            "Неверный email или пароль"
                        is com.google.firebase.auth.FirebaseAuthInvalidUserException ->
                            "Пользователь не найден"
                        else ->
                            "Ошибка входа: ${task.exception?.message}"
                    }

                    Toast.makeText(
                        requireContext(),
                        errorMessage,
                        Toast.LENGTH_SHORT
                    ).show()


                    binding.btnLogin.startAnimation(AnimationUtils.createShakeAnimation(requireContext()))
                }
            }
    }


    private fun setupToolbar() {
        binding.toolbar.inflateMenu(R.menu.login_menu)
        binding.toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_exit -> {

                    exitApplication()
                    true
                }
                else -> false
            }
        }
    }


    private fun exitApplication() {

        requireActivity().window.decorView.animate()
            .alpha(0f)
            .setDuration(300)
            .withEndAction {

                requireActivity().finish()

                android.os.Process.killProcess(android.os.Process.myPid())
            }
            .start()
    }
}