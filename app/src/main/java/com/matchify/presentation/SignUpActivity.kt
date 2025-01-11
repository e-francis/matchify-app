package com.matchify.presentation

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.matchify.R
import com.matchify.databinding.ActivitySignUpBinding
import com.matchify.presentation.auth.signup.SignUpViewModel
import com.matchify.presentation.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class SignUpActivity : BaseActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private val viewModel: SignUpViewModel by viewModels()
    private val calendar = Calendar.getInstance()

    // Add this class-level property for the gender adapter
    private lateinit var genderAdapter: ArrayAdapter<String>

    companion object {
        const val USER_DATA_PREFS = "user_data"
        const val KEY_FIRST_NAME = "first_name"
        const val KEY_LAST_NAME = "last_name"
        const val KEY_EMAIL = "email"
        const val KEY_GENDER = "gender"
        const val KEY_DOB = "dob"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupGenderDropdown()
        setupDateOfBirthPicker()
        setupClickListeners()
        setupInputValidations()
        restoreUserData()
    }

    private fun setupGenderDropdown() {
        val genders = arrayOf("Select Gender", "Male", "Female", "Other")

        // Assign the genderAdapter to the spinner inside genderDropdown
        genderAdapter = object : ArrayAdapter<String>(
            this,
            android.R.layout.simple_spinner_item,
            genders
        ) {
            // Custom view implementations for both the closed and open dropdown states
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getView(position, convertView, parent) as TextView
                view.apply {
                    text = getItem(position)
                    textSize = 16f
                    setTextColor(
                        if (position == 0) ContextCompat.getColor(context, R.color.text_color_hint)
                        else ContextCompat.getColor(context, R.color.text_color)
                    )
                }
                return view
            }

            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getDropDownView(position, convertView, parent) as TextView
                view.apply {
                    text = getItem(position)
                    textSize = 16f
                    setTextColor(
                        if (position == 0) ContextCompat.getColor(context, R.color.text_color_hint)
                        else ContextCompat.getColor(context, R.color.text_color)
                    )
                    setPadding(16, 16, 16, 16)
                }
                return view
            }
        }

        // Set the adapter to the spinner inside genderDropdown
        binding.spinnerGender.adapter = genderAdapter

        // Set the item selection listener
        binding.spinnerGender.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                hasUserInteracted = true
                validateGender()
                validateInputs()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
            }
        }
    }

    private fun setupDateOfBirthPicker() {
        val defaultYear = calendar.get(Calendar.YEAR) - 25
        calendar.set(Calendar.YEAR, defaultYear)

        binding.etDateOfBirth.setOnClickListener {
            hasUserInteracted = true
            val datePickerDialog = DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    calendar.set(year, month, dayOfMonth)
                    val selectedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        .format(calendar.time)
                    binding.etDateOfBirth.setText(selectedDate)
                    validateDateOfBirth()
                    validateInputs()
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )

            val maxDate = System.currentTimeMillis()
            val minDate = Calendar.getInstance().apply {
                add(Calendar.YEAR, -100)
            }.timeInMillis

            datePickerDialog.datePicker.maxDate = maxDate
            datePickerDialog.datePicker.minDate = minDate

            datePickerDialog.show()
        }
    }

    private fun setupInputValidations() {
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Only mark as interacted when user types something
                if (s?.isNotEmpty() == true) {
                    hasUserInteracted = true
                }
            }

            override fun afterTextChanged(s: Editable?) {
                if (hasUserInteracted) {
                    validateInputs()
                }
            }
        }

        with(binding) {
            etFirstName.addTextChangedListener(textWatcher)
            etLastName.addTextChangedListener(textWatcher)
            etEmail.addTextChangedListener(textWatcher)
            etDateOfBirth.addTextChangedListener(textWatcher)

            spinnerGender.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, vixew: View?, position: Int, id: Long) {
                    if (position > 0) { // Ensures interaction with the dropdown
                        hasUserInteracted = true
                        validateGender()
                        validateInputs()
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        }
    }



    private fun validateLastName() {
        with(binding) {
            val lastName = etLastName.text.toString()
            tilLastName.error = when {
                lastName.isEmpty() -> "Last name is required"
                lastName.length < 2 -> "Last name is too short"
                else -> null
            }
        }
    }

    private fun validateFirstName() {
        with(binding) {
            val firstName = etFirstName.text.toString()
            tilFirstName.error = when {
                firstName.isEmpty() -> "First name is required"
                firstName.length < 2 -> "First name is too short"
                else -> null
            }
        }
    }

    private fun validateEmail() {
        with(binding) {
            val email = etEmail.text.toString()
            val emailPattern = "[a-zA-Z0-9._-]+@[a-z0-9]+\\.[a-z]{2,}"

            tilEmail.error = when {
                email.isEmpty() -> "Email is required"
                !email.matches(emailPattern.toRegex()) -> "Invalid email format"
                else -> null
            }
        }
    }

    private fun validateDateOfBirth() {
        with(binding) {
            val dateOfBirth = etDateOfBirth.text.toString()

            tilDateOfBirth.error = when {
                dateOfBirth.isEmpty() -> "Date of Birth is required"
                !isValidAge(dateOfBirth) -> "You must be 18 years or older to create an account"
                else -> null
            }
        }
    }

    private fun validateGender() {
        with(binding) {
            genderDropdown.error = if (spinnerGender.selectedItemPosition == 0) {
                "Please select your gender"
            } else null
        }
    }

    private var hasUserInteracted = false


    private fun validateInputs(): Boolean {
        if (!hasUserInteracted) return false // Skip validation if no interaction

        with(binding) {
            validateFirstName()
            validateLastName()
            validateEmail()
            validateDateOfBirth()
            validateGender()

            val isFirstNameValid = !etFirstName.text.isNullOrEmpty() && tilFirstName.error == null
            val isLastNameValid = !etLastName.text.isNullOrEmpty() && tilLastName.error == null
            val isEmailValid = !etEmail.text.isNullOrEmpty() && tilEmail.error == null
            val isGenderValid = spinnerGender.selectedItemPosition > 0 && genderDropdown.error == null
            val isDobValid = !etDateOfBirth.text.isNullOrEmpty() && tilDateOfBirth.error == null

            val isValid = isFirstNameValid &&
                    isLastNameValid &&
                    isEmailValid &&
                    isGenderValid &&
                    isDobValid

            btnNext.isEnabled = isValid
            return isValid
        }
    }


    private fun isValidAge(dateOfBirth: String): Boolean {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return try {
            val dob = dateFormat.parse(dateOfBirth) ?: return false
            val today = Calendar.getInstance()
            val birthDate = Calendar.getInstance().apply { time = dob }

            var age = today.get(Calendar.YEAR) - birthDate.get(Calendar.YEAR)
            if (today.get(Calendar.MONTH) < birthDate.get(Calendar.MONTH) ||
                (today.get(Calendar.MONTH) == birthDate.get(Calendar.MONTH) && today.get(Calendar.DAY_OF_MONTH) < birthDate.get(Calendar.DAY_OF_MONTH))) {
                age--
            }

            age >= 18
        } catch (e: Exception) {
            false
        }
    }

    private fun restoreUserData() {
        val sharedPreferences = getSharedPreferences(USER_DATA_PREFS, Context.MODE_PRIVATE)

        with(binding) {
            etFirstName.setText(sharedPreferences.getString("first_name", ""))
            etLastName.setText(sharedPreferences.getString("last_name", ""))
            etEmail.setText(sharedPreferences.getString("email", ""))
            etDateOfBirth.setText(sharedPreferences.getString("dob", ""))
            val gender = sharedPreferences.getString("gender", "Select Gender")
            val genderPosition = when (gender) {
                "Male" -> 1
                "Female" -> 2
                "Other" -> 3
                else -> 0
            }
            spinnerGender.setSelection(genderPosition)

            // Ensure button is only enabled if all fields are non-empty
            btnNext.isEnabled = !etFirstName.text.isNullOrEmpty() &&
                    !etLastName.text.isNullOrEmpty() &&
                    !etEmail.text.isNullOrEmpty() &&
                    !etDateOfBirth.text.isNullOrEmpty() &&
                    spinnerGender.selectedItemPosition > 0
        }
    }


    private fun saveUserData() {
        val sharedPreferences = getSharedPreferences(USER_DATA_PREFS, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        with(binding) {
            editor.putString(KEY_FIRST_NAME, etFirstName.text.toString())
            editor.putString(KEY_LAST_NAME, etLastName.text.toString())
            editor.putString(KEY_EMAIL, etEmail.text.toString())
            editor.putString(KEY_DOB, etDateOfBirth.text.toString())
            editor.putString(KEY_GENDER, spinnerGender.selectedItem.toString())
        }
        editor.apply()
    }


    private fun setupClickListeners() {
        binding.btnNext.setOnClickListener {
            if (validateInputs()) {
                saveUserData()
                startActivity(Intent(this, ProfileSetupActivity::class.java))
                finish()
            }
        }

        binding.btnBack.setOnClickListener {
            startActivity(Intent(this, WelcomeActivity::class.java))
            finish()
        }
    }
}