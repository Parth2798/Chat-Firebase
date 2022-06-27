package com.weapp.chatemodule.ui.login;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.weapp.chatemodule.R;
import com.weapp.chatemodule.callbacks.CommonBtnClick;
import com.weapp.chatemodule.databinding.ActivityLoginBinding;
import com.weapp.chatemodule.ui.BaseActivity;
import com.weapp.chatemodule.ui.registration.RegistrationActivity;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginActivity extends BaseActivity<ActivityLoginBinding> implements CommonBtnClick {

    private LoginViewModel authViewModel;

    private final Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
    private String verifyOtp = "";

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding.setOnCLick(this);

        authViewModel = new ViewModelProvider(this, factory).get(LoginViewModel.class);

        authViewModel.setObserve(this);

        binding.radio.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioEmail) {
                binding.mainEmail.setVisibility(View.VISIBLE);
                binding.mainPhone.setVisibility(View.GONE);
            } else if (checkedId == R.id.radioPhone) {
                binding.mainEmail.setVisibility(View.GONE);
                binding.mainPhone.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void ClickData(View v) {
        if (v == binding.btnLogin || v == binding.ok) {
            String username = Objects.requireNonNull(binding.etUserName.getText()).toString();
            String password = Objects.requireNonNull(binding.etPassword.getText()).toString();

            if (validate(username, password)) {
                authViewModel.authenticateUser(this, username, password);
            } else {
                Toast.makeText(this, "Invalid email or empty password", Toast.LENGTH_SHORT).show();
            }

        } else if (v == binding.btnSignUp) {
            authViewModel.sendIntent(this, RegistrationActivity.class);

        } else if (v == binding.btnForgotPassword) {
            String username = Objects.requireNonNull(binding.etUserName.getText()).toString();
            if (validate(username, "cscg")) {
                authViewModel.resetPassword(this, username);
            } else {
                Toast.makeText(this, "Invalid email", Toast.LENGTH_SHORT).show();
            }
        } else if (v == binding.btnPhone) {
            String phone = Objects.requireNonNull(binding.etlMobile.getText()).toString();
            String otp = Objects.requireNonNull(binding.etOtp.getText()).toString();

            if (phone.isEmpty()) {
                Toast.makeText(activity, "Password Enter Mobile Number.", Toast.LENGTH_SHORT).show();
                return;
            }
            if ((!binding.btnPhone.getText().toString().equals(getResources().getString(R.string.send_verification_code))) && otp.isEmpty()) {
                Toast.makeText(activity, "Please Enter OTP.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (binding.btnPhone.getText().toString().equals(getResources().getString(R.string.send_verification_code))) {
                authViewModel.signUpWithPhone(this, phone, (v1, o) -> {
                    if (o != null) {
                        if (o instanceof String) {
                            verifyOtp = (String) o;
                            binding.bgEtOtp.setVisibility(View.VISIBLE);
                            binding.btnPhone.setText(getResources().getString(R.string.verifyOtp));
                        }
                    }
                });
            } else {
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verifyOtp, otp);
                authViewModel.verifyOTP(this, credential);
            }
        }
    }

    private boolean validate(String emailStr, String password) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
        return password.length() > 0 && matcher.find();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        authViewModel.onStop();
        finish();
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }
}
