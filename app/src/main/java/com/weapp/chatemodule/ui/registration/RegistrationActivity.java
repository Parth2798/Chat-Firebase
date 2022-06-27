package com.weapp.chatemodule.ui.registration;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.weapp.chatemodule.R;
import com.weapp.chatemodule.callbacks.CommonBtnClick;
import com.weapp.chatemodule.databinding.ActivityRegistrationBinding;
import com.weapp.chatemodule.models.Status;
import com.weapp.chatemodule.models.User;
import com.weapp.chatemodule.ui.BaseActivity;
import com.weapp.chatemodule.util.Logger;

import java.io.IOException;
import java.util.Date;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegistrationActivity extends BaseActivity<ActivityRegistrationBinding> implements CommonBtnClick {

    private RegistrationViewModel authViewModel;

    private static final int GALLERY_CODE = 0x123;

    private Bitmap bitmap;
    private Uri imgPath;
    private String verifyOtp = "";

    private final Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    @Override
    protected int getLayoutId() {
        return R.layout.activity_registration;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding.setOnCLick(this);

        authViewModel = new ViewModelProvider(this, factory).get(RegistrationViewModel.class);
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
        if (v == binding.btnRegistration) {
            String fName = Objects.requireNonNull(binding.etfUserName.getText()).toString();
            String lName = Objects.requireNonNull(binding.etlUserName.getText()).toString();
            String username = Objects.requireNonNull(binding.etUserName.getText()).toString();
            String password = Objects.requireNonNull(binding.etPassword.getText()).toString();
            String rePassword = Objects.requireNonNull(binding.etRePassword.getText()).toString();

            if (fName.isEmpty()) {
                Toast.makeText(activity, "Password Enter First Name.", Toast.LENGTH_SHORT).show();
                return;
            } else if (lName.isEmpty()) {
                Toast.makeText(activity, "Password Enter Last Name.", Toast.LENGTH_SHORT).show();
                return;
            } else if (!password.equals(rePassword)) {
                Toast.makeText(activity, "Password Dose not match.", Toast.LENGTH_SHORT).show();
                return;
            } else if (bitmap == null || imgPath == null) {
                Toast.makeText(activity, "Please Select Profile Image.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (validate(username, password)) {
                Status status = new Status(true, new Date().getTime());
                User user = new User("", Objects.requireNonNull(binding.etfUserName.getText()).toString(), Objects.requireNonNull(binding.etlUserName.getText()).toString()
                        , "", binding.etUserName.getText().toString(), binding.etPassword.getText().toString()
                        , binding.etfUserName.getText().toString() + new Date().getTime(), imgPath, status);

                authViewModel.signUp(this, user);

            } else {
                Toast.makeText(this, "Invalid email or empty password", Toast.LENGTH_SHORT).show();
            }
        } else if (v == binding.btnPhone) {
            String fName = Objects.requireNonNull(binding.etfUserName.getText()).toString();
            String lName = Objects.requireNonNull(binding.etlUserName.getText()).toString();
            String phone = Objects.requireNonNull(binding.etlMobile.getText()).toString();
            String otp = Objects.requireNonNull(binding.etOtp.getText()).toString();

            if (fName.isEmpty()) {
                Toast.makeText(activity, "Password Enter First Name.", Toast.LENGTH_SHORT).show();
                return;
            } else if (lName.isEmpty()) {
                Toast.makeText(activity, "Password Enter Last Name.", Toast.LENGTH_SHORT).show();
                return;
            } else if (phone.isEmpty()) {
                Toast.makeText(activity, "Password Enter Mobile Number.", Toast.LENGTH_SHORT).show();
                return;
            } else if (bitmap == null || imgPath == null) {
                Toast.makeText(activity, "Please Select Profile Image.", Toast.LENGTH_SHORT).show();
                return;
            } else if ((!binding.btnPhone.getText().toString().equals(getResources().getString(R.string.send_verification_code))) && otp.isEmpty()) {
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
                Status status = new Status(true, new Date().getTime());
                User user = new User("", Objects.requireNonNull(binding.etfUserName.getText()).toString(), Objects.requireNonNull(binding.etlUserName.getText()).toString()
                        , phone, "", "", binding.etfUserName.getText().toString() + new Date().getTime(), imgPath, status);

                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verifyOtp, otp);
                authViewModel.verifyOTP(user, this, credential);
            }

        } else if (v == binding.profile) {
            Intent galleryIntent = new Intent(Intent.ACTION_PICK);
            galleryIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
            activity.startActivityForResult(galleryIntent, GALLERY_CODE);
        }
    }

    private boolean validate(String emailStr, String password) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
        return password.length() > 0 && matcher.find();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_CODE && null != data) {
            Uri selectedImage = data.getData();
            String ImagePath = "";
            if (selectedImage != null) {
                String[] projection = {MediaStore.Images.Media.DATA};
                Cursor cursor = activity.getContentResolver().query(selectedImage, projection, null, null, null);
                if (cursor != null) {
                    cursor.moveToFirst();
                    int column_index = cursor.getColumnIndex(projection[0]);
                    ImagePath = cursor.getString(column_index);
                    cursor.close();
                }

                Logger.e("File Path:  ", "Path:  " + ImagePath);

                imgPath = selectedImage;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                    binding.profile.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }
}
