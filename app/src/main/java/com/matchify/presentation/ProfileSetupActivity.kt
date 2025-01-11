package com.matchify.presentation

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.view.children
import com.google.android.material.chip.Chip
import com.matchify.R
import com.matchify.databinding.ActivityProfileSetupBinding
import com.matchify.presentation.auth.signup.SignUpState
import com.matchify.presentation.auth.signup.SignUpViewModel
import com.matchify.presentation.base.BaseActivity
import com.matchify.utils.ImageUtils
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class ProfileSetupActivity : BaseActivity() {
    private lateinit var binding: ActivityProfileSetupBinding
    private val viewModel: SignUpViewModel by viewModels()
    private lateinit var locationAdapter: ArrayAdapter<String>

    private var imageUri: Uri? = null
    private var firstName: String = ""
    private var lastName: String = ""
    private var email: String = ""
    private var gender: String = ""
    private var dob: Date? = null

    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { handleImageSelection(it) }
    }

    private val requestPermissions = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.values.all { it }) {
            pickImage.launch("image/*")
        } else {
            showError("Permissions required to select image")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileSetupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        retrieveUserDataFromSharedPreferences()
        setupLocationDropdown()
        setupInterestsChips()
        setupClickListeners()
        setupInputValidations()
        observeViewModel()

        binding.btnCreateAccount.isEnabled = false
    }

    private fun setupLocationDropdown() {
        val locations = arrayOf(
            "Select Location",
            "New York, USA",
            "London, UK",
            "Paris, France",
            "Tokyo, Japan",
            "Sydney, Australia"
        )

        locationAdapter = object : ArrayAdapter<String>(
            this,
            android.R.layout.simple_spinner_item,
            locations
        ) {
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

        binding.spinnerLocation.adapter = locationAdapter

        binding.spinnerLocation.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                hasUserInteracted = true
                validateLocation()
                validateInputs()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
            }
        }
    }

    private var hasUserInteracted = false

    private fun validateLocation() {
        with(binding) {
            locationDropDown.error = if (spinnerLocation.selectedItemPosition == 0) {
                "Please select your location"
            } else null
        }
    }

    private fun retrieveUserDataFromSharedPreferences() {
        val sharedPreferences = getSharedPreferences(SignUpActivity.USER_DATA_PREFS, Context.MODE_PRIVATE)

        firstName = sharedPreferences.getString(SignUpActivity.KEY_FIRST_NAME, "") ?: ""
        lastName = sharedPreferences.getString(SignUpActivity.KEY_LAST_NAME, "") ?: ""
        email = sharedPreferences.getString(SignUpActivity.KEY_EMAIL, "") ?: ""
        gender = sharedPreferences.getString(SignUpActivity.KEY_GENDER, "") ?: ""

        val dobString = sharedPreferences.getString(SignUpActivity.KEY_DOB, "") ?: ""
        dob = try {
            when {
                dobString.matches(Regex("\\d+")) -> Date(dobString.toLong())
                dobString.matches(Regex("\\d{4}-\\d{2}-\\d{2}")) ->
                    SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(dobString)
                else -> null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun setupInterestsChips() {
        val interests = listOf(
            "Music", "Sports", "Travel", "Reading",
            "Movies", "Food", "Art", "Photography",
            "Gaming", "Fashion", "Technology", "Fitness"
        )

        interests.forEach { interest ->
            val chip = Chip(this).apply {
                text = interest
                isCheckable = true
                chipStrokeWidth = 1f
                chipStrokeColor = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.primary))
                chipBackgroundColor = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.white))
                setTextColor(ContextCompat.getColor(context, R.color.primary))

                setOnCheckedChangeListener { chip, isChecked ->
                    if (isChecked) {
                        val selectedCount = getSelectedInterests().size
                        if (selectedCount > 5) {
                            chip.isChecked = false
                            showStatusDialog(
                                isSuccess = false,
                                title = "",
                                message = "Maximum of 5 interests allowed",
                                buttonText = "OKAY"
                            )
                        } else {
                            updateChipAppearance(chip as Chip, true)
                        }
                    } else {
                        updateChipAppearance(chip as Chip, false)
                    }
                    validateInputs()
                }
            }
            binding.chipGroupInterests.addView(chip)
        }
    }

    private fun updateChipAppearance(chip: Chip, isChecked: Boolean) {
        if (isChecked) {
            chip.chipBackgroundColor =
                ColorStateList.valueOf(ContextCompat.getColor(this, R.color.primary))
            chip.setTextColor(ContextCompat.getColor(this, R.color.white))
        } else {
            chip.chipBackgroundColor =
                ColorStateList.valueOf(ContextCompat.getColor(this, R.color.white))
            chip.setTextColor(ContextCompat.getColor(this, R.color.primary))
        }
    }

    private fun setupInputValidations() {
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s?.isNotEmpty() == true) {
                    hasUserInteracted = true
                }
            }
            override fun afterTextChanged(s: Editable?) {
                if (hasUserInteracted) {
                    validateInputs()
                }            }
        }

        with(binding) {
            etPasscode.addTextChangedListener(textWatcher)
            etConfirmPasscode.addTextChangedListener(textWatcher)
            spinnerLocation.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, vixew: View?, position: Int, id: Long) {
                    if (position > 0) { // Ensures interaction with the dropdown
                        hasUserInteracted = true
                        validateLocation()
                        validateInputs()
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        }
    }

    private fun validatePasscodes() {
        with(binding) {
            val passcode = etPasscode.text.toString()
            val confirmPasscode = etConfirmPasscode.text.toString()

            tilPasscode.error = null
            tilConfirmPasscode.error = null

            when {
                passcode.isEmpty() -> {
                    tilPasscode.error = "Passcode is required"
                }
                passcode.length != 6 -> {
                    tilPasscode.error = "Passcode must be 6 digits"
                }
            }

            when {
                confirmPasscode.isEmpty() -> {
                    tilConfirmPasscode.error = "Confirm passcode is required"
                }
                passcode.isNotEmpty() && confirmPasscode != passcode -> {
                    tilConfirmPasscode.error = "Passcodes do not match"
                }
            }
        }
    }

    private fun validateInputs(): Boolean {
        with(binding) {
            validatePasscodes()
            validateLocation()

            val hasValidLocation = spinnerLocation.selectedItemPosition > 0 && locationDropDown.error == null
            val hasValidPasscode = etPasscode.text?.toString()?.length == 6
            val hasMatchingPasscodes = etPasscode.text?.toString() == etConfirmPasscode.text?.toString()
            val hasProfilePicture = imageUri != null
            val selectedInterests = getSelectedInterests()
            val hasValidInterests = selectedInterests.isNotEmpty() && selectedInterests.size <= 5

            val isValid = hasValidLocation &&
                    hasValidPasscode &&
                    hasMatchingPasscodes &&
                    hasProfilePicture &&
                    hasValidInterests

            btnCreateAccount.isEnabled = isValid
            btnCreateAccount.isClickable = isValid

            return isValid
        }
    }

    private fun getSelectedInterests(): List<String> {
        return binding.chipGroupInterests
            .children
            .filter { (it as Chip).isChecked }
            .map { (it as Chip).text.toString() }
            .toList()
    }

    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
            finish()
        }

        binding.btnUploadPhoto.setOnClickListener {
            checkPermissionsAndPickImage()
        }

        binding.btnCreateAccount.setOnClickListener {
            if (validateForm()) {
                showLoading("Creating your account...")
                createProfile()
            }
        }
    }

    private fun checkPermissionsAndPickImage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions.launch(arrayOf(Manifest.permission.READ_MEDIA_IMAGES))
        } else {
            requestPermissions.launch(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE))
        }
    }

    private fun validateForm(): Boolean {
        var isValid = true
        with(binding) {
            // Use spinner location validation
            if (spinnerLocation.selectedItemPosition == 0) {
                locationDropDown.error = "Please select your location"
                isValid = false
            }

            val passcode = etPasscode.text.toString()
            if (passcode.length != 6) {
                tilPasscode.error = "Passcode must be 6 digits"
                isValid = false
            }

            if (passcode != etConfirmPasscode.text.toString()) {
                tilConfirmPasscode.error = "Passcodes do not match"
                isValid = false
            }

            if (imageUri == null) {
                showStatusDialog(
                    isSuccess = false,
                    title = "Missing Image",
                    message = "Please select a profile picture",
                    buttonText = "OK"
                )
                isValid = false
            }

            val selectedInterests = getSelectedInterests()
            if (selectedInterests.isEmpty()) {
                showStatusDialog(
                    isSuccess = false,
                    title = "Missing Interests",
                    message = "Please select at least one interest",
                    buttonText = "OK"
                )
                isValid = false
            }
        }
        return isValid
    }

    private fun handleImageSelection(uri: Uri) {
        try {
            val inputStream = contentResolver.openInputStream(uri)
            val bytes = inputStream?.readBytes()

            if (bytes == null) {
                showError("Failed to read image")
                return
            }

            if (bytes.size > 1024 * 1024) {
                showError("Image size must be less than 1MB")
                return
            }

            imageUri = uri
            binding.ivProfilePicture.setImageURI(uri)
            ImageUtils.uriToBase64(this, uri)?.let { base64String ->
                viewModel.updateProfilePicture(base64String)
            }
            validateInputs()

        } catch (e: Exception) {
            showError("Failed to process image")
        }
    }

    private fun createProfile() {
        val formattedDob = dob?.let {
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(it)
        } ?: ""

        viewModel.updateUserData(
            firstName = firstName,
            lastName = lastName,
            email = email,
            gender = gender,
            dob = formattedDob,
            location = binding.spinnerLocation.selectedItem.toString(),
            passcode = binding.etPasscode.text.toString().toInt(),
            interests = getSelectedInterests()
        )
        viewModel.createProfile()
    }

    private fun observeViewModel() {
        viewModel.signUpState.observe(this) { state ->
            when (state) {
                is SignUpState.Loading -> {
                    showLoading("Creating your account...")
                    binding.btnCreateAccount.isEnabled = false
                }
                is SignUpState.Success -> {
                    hideLoading()
                    // Directly clear SharedPreferences here
                    getSharedPreferences(SignUpActivity.USER_DATA_PREFS, Context.MODE_PRIVATE)
                        .edit()
                        .clear()
                        .apply()

                    showStatusDialog(
                        isSuccess = true,
                        title = "Success!",
                        message = state.message,
                        buttonText = "Login to your account"
                    ) {
                        // Added flags to clear activity stack
                        startActivity(Intent(this, LoginActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        })
                        finishAffinity()
                    }
                }
                is SignUpState.Error -> {
                    hideLoading()
                    binding.btnCreateAccount.isEnabled = true
                    showStatusDialog(
                        isSuccess = false,
                        title = "Account Creation Failed",
                        message = state.message,
                        buttonText = "Try Again"
                    )
                }
                else -> {
                    binding.btnCreateAccount.isEnabled = true
                }
            }
        }
    }

    private fun clearUserData() {
        getSharedPreferences(SignUpActivity.USER_DATA_PREFS, Context.MODE_PRIVATE)
            .edit()
            .clear()
            .apply()
    }
}