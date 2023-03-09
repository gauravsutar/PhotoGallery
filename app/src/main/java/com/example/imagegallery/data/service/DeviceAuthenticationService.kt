package com.example.imagegallery.data.service

import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_WEAK
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.biometric.BiometricPrompt
import androidx.biometric.BiometricPrompt.AuthenticationCallback
import androidx.biometric.BiometricPrompt.AuthenticationResult
import androidx.biometric.BiometricPrompt.ERROR_CANCELED
import androidx.biometric.BiometricPrompt.ERROR_HW_NOT_PRESENT
import androidx.biometric.BiometricPrompt.ERROR_HW_UNAVAILABLE
import androidx.biometric.BiometricPrompt.ERROR_LOCKOUT
import androidx.biometric.BiometricPrompt.ERROR_LOCKOUT_PERMANENT
import androidx.biometric.BiometricPrompt.ERROR_NEGATIVE_BUTTON
import androidx.biometric.BiometricPrompt.ERROR_NO_BIOMETRICS
import androidx.biometric.BiometricPrompt.ERROR_NO_DEVICE_CREDENTIAL
import androidx.biometric.BiometricPrompt.ERROR_NO_SPACE
import androidx.biometric.BiometricPrompt.ERROR_SECURITY_UPDATE_REQUIRED
import androidx.biometric.BiometricPrompt.ERROR_TIMEOUT
import androidx.biometric.BiometricPrompt.ERROR_UNABLE_TO_PROCESS
import androidx.biometric.BiometricPrompt.ERROR_USER_CANCELED
import androidx.biometric.BiometricPrompt.ERROR_VENDOR
import androidx.biometric.BiometricPrompt.PromptInfo
import androidx.core.content.ContextCompat
import com.example.imagegallery.R
import javax.inject.Inject

class DeviceAuthenticationService @Inject constructor() {
    private val authenticator: Int
        get() {
            return if (Build.VERSION.SDK_INT in 28..29) {
                BIOMETRIC_WEAK or DEVICE_CREDENTIAL
            } else {
                BIOMETRIC_STRONG or DEVICE_CREDENTIAL
            }
        }

    fun authenticateWithBiometric(
        activity: AppCompatActivity,
        completion: (Boolean, String) -> Unit
    ) {
        val biometricManager = BiometricManager.from(activity)
        val (canAuthenticate, message) = canAuthenticateWithBiometric(activity, biometricManager)

        if (canAuthenticate) {
            performAuthentication(
                activity,
                completion
            )
        } else {
            completion(false, message)
        }
    }

    // region Private Methods
    private fun performAuthentication(
        activity: AppCompatActivity,
        completion: (Boolean, String) -> Unit
    ) {
        val executor = ContextCompat.getMainExecutor(activity)
        val biometricPrompt = BiometricPrompt(activity, executor,
            object : AuthenticationCallback() {
                override fun onAuthenticationError(
                    errorCode: Int,
                    errString: CharSequence
                ) {
                    super.onAuthenticationError(errorCode, errString)
                    val error = errorResult(activity, errorCode)
                    completion.invoke(false, error)
                }

                override fun onAuthenticationSucceeded(
                    result: AuthenticationResult
                ) {
                    super.onAuthenticationSucceeded(result)
                    completion.invoke(true, activity.getString(R.string.biometric_success_by_user))
                }
            }
        )

        val promptInfo = PromptInfo.Builder()
            .setTitle(activity.getString(R.string.biometric_prompt_title))
            .setAllowedAuthenticators(authenticator)
            .build()
        biometricPrompt.authenticate(promptInfo)
    }

    private fun canAuthenticateWithBiometric(
        context: Context,
        biometricManager: BiometricManager
    ): Pair<Boolean, String> {
        when (biometricManager.canAuthenticate(authenticator)) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                return Pair(true, context.getString(R.string.biometric_success_by_user))
            }

            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE, BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                return Pair(false, context.getString(R.string.biometric_hw_unavailable))
            }

            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                return Pair(false, context.getString(R.string.biometric_not_enrolled))
            }

            else -> {
                return Pair(false, context.getString(R.string.biometric_unknown_error))
            }
        }
    }

    private fun errorResult(context: Context, errorCode: Int): String {
        var errorMessage = context.getString(R.string.biometric_failure_title)
        when (errorCode) {
            ERROR_HW_UNAVAILABLE, ERROR_HW_NOT_PRESENT -> {
                errorMessage = context.getString(R.string.biometric_hw_unavailable)
            }
            ERROR_SECURITY_UPDATE_REQUIRED, ERROR_UNABLE_TO_PROCESS,
            ERROR_VENDOR, ERROR_CANCELED, ERROR_NO_SPACE, ERROR_TIMEOUT -> {
                errorMessage = context.getString(R.string.biometric_cancelled_by_system)
            }
            ERROR_NO_DEVICE_CREDENTIAL -> {
                errorMessage = context.getString(R.string.biometric_passcode_not_set)
            }
            ERROR_NEGATIVE_BUTTON, ERROR_USER_CANCELED -> {
                errorMessage = context.getString(R.string.biometric_cancelled_by_user)
            }
            ERROR_NO_BIOMETRICS -> {
                errorMessage = context.getString(R.string.biometric_not_enrolled)
            }
            ERROR_LOCKOUT_PERMANENT, ERROR_LOCKOUT -> {
                errorMessage = context.getString(R.string.biometric_locked)
            }
        }
        return errorMessage
    }
    // endregion
}
