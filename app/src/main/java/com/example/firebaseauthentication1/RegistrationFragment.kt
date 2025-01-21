package com.example.firebaseauthentication1

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.example.firebaseauthentication1.databinding.FragmentRegistrationBinding

class RegistrationFragment : Fragment() {
    private lateinit var binding: FragmentRegistrationBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegistrationBinding.inflate(inflater, container, false)
        auth = FirebaseAuth.getInstance()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        AnimationUtils.animateLoginScreen(
            binding.logo,
            binding.tilEmail,
            binding.tilPassword,
            binding.tilConfirmPassword,
            binding.btnRegister,
            binding.tvLoginLink
        )


        binding.btnRegister.setOnClickListener {

            AnimationUtils.createButtonPressAnimation(it)

            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            val confirmPassword = binding.etConfirmPassword.text.toString().trim()

            if (validateInput(email, password, confirmPassword)) {
                registerUser(email, password)
            }
        }


        binding.tvLoginLink.setOnClickListener {

            AnimationUtils.createFragmentTransitionAnimation(it)
            findNavController().navigate(R.id.action_registrationFragment_to_loginFragment)
        }
    }


    private fun validateInput(email: String, password: String, confirmPassword: String): Boolean {
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
            confirmPassword.isEmpty() -> {
                binding.etConfirmPassword.error = "Подтвердите пароль"
                binding.tilConfirmPassword.startAnimation(AnimationUtils.createShakeAnimation(requireContext()))
                false
            }
            password != confirmPassword -> {
                binding.etConfirmPassword.error = "Пароли не совпадают"
                binding.tilConfirmPassword.startAnimation(AnimationUtils.createShakeAnimation(requireContext()))
                false
            }
            else -> true
        }
    }


    private fun isValidEmail(email: String): Boolean {
        val emailRegex = Regex("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")
        return emailRegex.matches(email)
    }


    private fun registerUser(email: String, password: String) {

        binding.progressBar.visibility = View.VISIBLE
        binding.btnRegister.isEnabled = false

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->

                binding.progressBar.visibility = View.GONE
                binding.btnRegister.isEnabled = true

                if (task.isSuccessful) {

                    Toast.makeText(
                        requireContext(),
                        "Регистрация успешна",
                        Toast.LENGTH_SHORT
                    ).show()


                    AnimationUtils.createFragmentTransitionAnimation(binding.root)
                    findNavController().navigate(R.id.action_registrationFragment_to_loginFragment)
                } else {

                    val errorMessage = when (task.exception) {
                        is com.google.firebase.auth.FirebaseAuthUserCollisionException ->
                            "Электронная почта уже используется"
                        is com.google.firebase.auth.FirebaseAuthWeakPasswordException ->
                            "Слабый пароль"
                        else ->
                            "Ошибка регистрации: ${task.exception?.message}"
                    }

                    Toast.makeText(
                        requireContext(),
                        errorMessage,
                        Toast.LENGTH_SHORT
                    ).show()


                    binding.btnRegister.startAnimation(AnimationUtils.createShakeAnimation(requireContext()))
                }
            }
    }
}