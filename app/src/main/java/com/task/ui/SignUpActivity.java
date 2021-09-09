package com.task.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import com.rilixtech.widget.countrycodepicker.Country;
import com.rilixtech.widget.countrycodepicker.CountryCodePicker;
import com.task.BuildConfig;
import com.task.R;
import com.task.data.remote.ApiService;
import com.task.data.remote.ApiServiceGenerator;
import com.task.data.remote.RequestUtils;
import com.task.databinding.ActivitySignUpBinding;
import com.task.model.BaseResponse;
import com.task.model.Data;
import com.task.ui.base.BaseActivity;
import com.task.utilities.ActivityUtils;
import com.task.utilities.FileProviderUtils;
import com.task.utilities.ImageAndVideoUtils;
import com.task.utilities.ImplicitIntentUtils;
import com.task.utilities.MemoryUnitUtils;
import com.task.utilities.RealPathUtils;
import com.task.utilities.SharedFileUtils;
import com.task.utilities.ValidationUtils;
import com.task.utilities.compress.ConfigureCompression;
import com.task.utilities.permissionutils.ManagePermission;
import com.task.utilities.permissionutils.PermissionDialog;
import com.task.utilities.permissionutils.PermissionName;
import com.task.utilities.string.StringUtils;
import java.io.File;
import java.util.Objects;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Response;

public class SignUpActivity extends BaseActivity<ActivitySignUpBinding> {

    public static final String TAG = SignUpActivity.class.getSimpleName();

    private String pictureRealPathString;
    private String firstNameString;
    private String lastNameString;
    private String genderString;
    private String countryCodeString = "91";
    private String phoneNumberString;
    private String emailString;
    private String passwordString;
    private String confirmPasswordString;
    private String fcmTokenString;

    private boolean isTermsAndConditionsAccept = false;

    private static final int SELECT_IMAGE_REQUEST_CODE                        = 1001;

    private static final int MULTIPLE_PERMISSION_REQUEST_CODE                 = 1002;
    private static final int MULTIPLE_PERMISSIONS_FROM_SETTING_REQUEST_CODE   = 1003;
    public static final String[] MULTIPLE_PERMISSIONS                           =
            {
                    PermissionName.CAMERA,
                    PermissionName.READ_EXTERNAL_STORAGE
            };

    private ManagePermission managePermission;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected ActivitySignUpBinding getViewBinding() {
        return ActivitySignUpBinding.inflate(getLayoutInflater());
    }

    @Override
    protected void initializeObject() {
        managePermission = new ManagePermission(SignUpActivity.this);
    }

    @Override
    protected void initializeToolBar() {
    }

    @Override
    protected void initializeCallbackListener() {
    }

    @Override
    protected void addTextChangedListener() {
        /* Clear RadioGroup, unchecked all the RadioButton */
        viewBinding.genderRadioGroup.clearCheck();

        /* Add the Listener to the RadioGroup */
        viewBinding.genderRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                /*
                 * The flow will come here when
                 * any of the radio buttons in the radioGroup
                 * has been clicked
                 */
                //radioButton = (RadioButton)group.findViewById(checkedId);
            }
        });

        viewBinding.firstNameTextInputLayout.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence text, int start, int count, int after) {
                if(text.length() < 1)
                {
                    viewBinding.firstNameTextInputLayout.setErrorEnabled(true);
                    viewBinding.firstNameTextInputLayout.setError(getString(R.string.first_name_message_one));
                }
                else if(text.length() > 0)
                {
                    viewBinding.firstNameTextInputLayout.setError(null);
                    viewBinding.firstNameTextInputLayout.setErrorEnabled(false);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        viewBinding.lastNameTextInputLayout.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence text, int start, int count, int after) {
                if(text.length() < 1)
                {
                    viewBinding.lastNameTextInputLayout.setErrorEnabled(true);
                    viewBinding.lastNameTextInputLayout.setError(getString(R.string.last_name_message_one));
                }
                else if(text.length() > 0)
                {
                    viewBinding.lastNameTextInputLayout.setError(null);
                    viewBinding.lastNameTextInputLayout.setErrorEnabled(false);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        viewBinding.countryCodePicker.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected(Country selectedCountry) {
                countryCodeString = selectedCountry.getPhoneCode();
            }
        });

        viewBinding.phoneNumberTextInputLayout.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence text, int start, int count, int after) {
                if(text.length() < 1)
                {
                    viewBinding.phoneNumberTextInputLayout.setErrorEnabled(true);
                    viewBinding.phoneNumberTextInputLayout.setError(getString(R.string.phone_number_message_one));
                }
                else if(text.length() > 0)
                {
                    viewBinding.phoneNumberTextInputLayout.setError(null);
                    viewBinding.phoneNumberTextInputLayout.setErrorEnabled(false);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                int phoneValidCode = ValidationUtils.isPhoneNumberValid(countryCodeString, Objects.requireNonNull(viewBinding.phoneNumberTextInputEditText.getText()).toString());
                if (phoneValidCode > 0)
                {
                    if(phoneValidCode == 1)
                    {
                        viewBinding.phoneNumberTextInputLayout.setError(getString(R.string.phone_number_message_one));
                    }
                    else if(phoneValidCode == 2)
                    {
                        viewBinding.phoneNumberTextInputLayout.setError(getString(R.string.phone_number_message_two));
                    }
                    else if(phoneValidCode == 3)
                    {
                        viewBinding.phoneNumberTextInputLayout.setError(getString(R.string.phone_number_message_three));
                    }
                }
            }
        });

        viewBinding.emailTextInputLayout.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence text, int start, int count, int after) {
                if(text.length() < 1)
                {
                    viewBinding.emailTextInputLayout.setErrorEnabled(true);
                    viewBinding.emailTextInputLayout.setError(getString(R.string.email_message_one));
                }
                else if(text.length() > 0)
                {
                    viewBinding.emailTextInputLayout.setError(null);
                    viewBinding.emailTextInputLayout.setErrorEnabled(false);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                int emailValidCode = ValidationUtils.isValidEmail(Objects.requireNonNull(viewBinding.emailTextInputEditText.getText()).toString());
                if (emailValidCode > 0)
                {
                    if(emailValidCode == 1)
                    {
                        viewBinding.emailTextInputLayout.setError(getString(R.string.email_message_one));
                    }
                    else if(emailValidCode == 2)
                    {
                        viewBinding.emailTextInputLayout.setError(getString(R.string.email_message_two));
                    }
                }
            }
        });

        viewBinding.passwordTextInputLayout.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence text, int start, int count, int after) {
                if(text.length() < 1)
                {
                    viewBinding.passwordTextInputLayout.setErrorEnabled(true);
                    viewBinding.passwordTextInputLayout.setError(getString(R.string.password_message_one));
                }
                else if(text.length() > 0)
                {
                    viewBinding.passwordTextInputLayout.setError(null);
                    viewBinding.passwordTextInputLayout.setErrorEnabled(false);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                int passwordValidCode = ValidationUtils.isValidPassword(Objects.requireNonNull(viewBinding.passwordTextInputEditText.getText()).toString());
                if (passwordValidCode > 0)
                {
                    if(passwordValidCode == 1)
                    {
                        viewBinding.passwordTextInputLayout.setError(getString(R.string.password_message_one));
                    }
                    else if(passwordValidCode == 2)
                    {
                        viewBinding.passwordTextInputLayout.setError(getString(R.string.password_message_two));
                    }
                    else if(passwordValidCode == 3)
                    {
                        viewBinding.passwordTextInputLayout.setError(getString(R.string.password_message_three));
                    }
                    else if(passwordValidCode == 4)
                    {
                        viewBinding.passwordTextInputLayout.setError(getString(R.string.password_message_four));
                    }
                    else if(passwordValidCode == 5)
                    {
                        viewBinding.passwordTextInputLayout.setError(getString(R.string.password_message_five));
                    }
                    else if(passwordValidCode == 6)
                    {
                        viewBinding.passwordTextInputLayout.setError(getString(R.string.password_message_six));
                    }
                    else if(passwordValidCode == 7)
                    {
                        viewBinding.passwordTextInputLayout.setError(getString(R.string.password_message_seven));
                    }
                    else if(passwordValidCode == 8)
                    {
                        viewBinding.passwordTextInputLayout.setError(getString(R.string.password_message_eight));
                    }
                }
            }
        });

        viewBinding.confirmPasswordTextInputLayout.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence text, int start, int count, int after) {
                if(text.length() < 1)
                {
                    viewBinding.confirmPasswordTextInputLayout.setErrorEnabled(true);
                    viewBinding.confirmPasswordTextInputLayout.setError(getString(R.string.confirm_password_message_one));
                }
                else if(text.length() > 0)
                {
                    viewBinding.confirmPasswordTextInputLayout.setError(null);
                    viewBinding.confirmPasswordTextInputLayout.setErrorEnabled(false);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                int confirmPasswordValidCode = ValidationUtils.isValidConfirmPassword( viewBinding.passwordTextInputEditText.getText().toString().trim(),  viewBinding.confirmPasswordTextInputEditText.getText().toString().trim());
                if (confirmPasswordValidCode > 0)
                {
                    if(confirmPasswordValidCode == 1)
                    {
                        viewBinding.confirmPasswordTextInputLayout.setError(getString(R.string.password_message_one));
                    }
                    else if(confirmPasswordValidCode == 2)
                    {
                        viewBinding.confirmPasswordTextInputLayout.setError(getString(R.string.confirm_password_message_one));
                    }
                    else if(confirmPasswordValidCode == 3)
                    {
                        viewBinding.confirmPasswordTextInputLayout.setError(getString(R.string.confirm_password_message_two));
                    }
                    else if(confirmPasswordValidCode == 4)
                    {
                        viewBinding.confirmPasswordTextInputLayout.setError(getString(R.string.confirm_password_message_three));
                    }
                }
            }
        });
    }

    @Override
    protected void setOnClickListener() {
        viewBinding.selectProfilePictureFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (managePermission.hasPermission(MULTIPLE_PERMISSIONS))
                {
                    Log.e(TAG, "permission already granted");
                    showDialog(SignUpActivity.this);
                }
                else
                {
                    Log.e(TAG, "permission is not granted, request for permission");

                    ActivityCompat.requestPermissions(
                            SignUpActivity.this,
                            MULTIPLE_PERMISSIONS,
                            MULTIPLE_PERMISSION_REQUEST_CODE);
                }
            }
        });

        viewBinding.termsConditionAndPrivacyPolicyCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isTermsAndConditionsAccept = ((CheckBox) v).isChecked();
            }
        });

        viewBinding.termsConditionTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        viewBinding.privacyPolicyTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        viewBinding.appSignUpMaterialButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUp();
            }
        });

        viewBinding.signInLinkTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchSignInScreen();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode)
        {
            case MULTIPLE_PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0)
                {
                    for (int i = 0; i < grantResults.length; i++)
                    {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED)
                        {
                            String permission = permissions[i];

                            if(permission.equalsIgnoreCase(PermissionName.CAMERA))
                            {
                                boolean showRationale = managePermission.shouldShowRequestPermissionRationale(permission);
                                if (showRationale)
                                {
                                    Log.e(TAG, "camera permission denied");
                                    ActivityCompat.requestPermissions(
                                            SignUpActivity.this,
                                            MULTIPLE_PERMISSIONS,
                                            MULTIPLE_PERMISSION_REQUEST_CODE);
                                    return;
                                }
                                else
                                {
                                    Log.e(TAG, "camera permission denied and don't ask for it again");
                                    PermissionDialog.permissionDeniedWithNeverAskAgain(
                                            SignUpActivity.this,
                                            R.drawable.permission_ic_camera,
                                            "Camera Permission",
                                            "Kindly allow Camera Permission from Settings, without this permission the app is unable to provide create camera feature. Please turn on permissions at [Setting] -> [Permissions]>",
                                            permission,
                                            MULTIPLE_PERMISSIONS_FROM_SETTING_REQUEST_CODE);
                                    return;
                                }
                            }

                            if(permission.equalsIgnoreCase(PermissionName.READ_EXTERNAL_STORAGE))
                            {
                                boolean showRationale = managePermission.shouldShowRequestPermissionRationale(permission);
                                if (showRationale)
                                {
                                    Log.e(TAG, "manage external storage permission denied");

                                    ActivityCompat.requestPermissions(
                                            SignUpActivity.this,
                                            MULTIPLE_PERMISSIONS,
                                            MULTIPLE_PERMISSION_REQUEST_CODE);
                                    return;
                                }
                                else
                                {
                                    Log.e(TAG, "manage external storage permission denied and don't ask for it again");

                                    PermissionDialog.permissionDeniedWithNeverAskAgain(
                                            SignUpActivity.this,
                                            R.drawable.permission_ic_storage,
                                            "Read Storage Permission",
                                            "Kindly allow Read Storage Permission from Settings, without this permission the app is unable to provide file read feature. Please turn on permissions at [Setting] -> [Permissions]>",
                                            permission,
                                            MULTIPLE_PERMISSIONS_FROM_SETTING_REQUEST_CODE);
                                    return;
                                }
                            }
                        }
                    }

                    Log.e(TAG, "all permission granted, do the task");
                    showDialog(SignUpActivity.this);
                }
                else
                {
                    Log.e(TAG, "Unknown Error");
                }
                break;
            default:
                throw new RuntimeException("unhandled permissions request code: " + requestCode);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MULTIPLE_PERMISSIONS_FROM_SETTING_REQUEST_CODE)
        {
            if (managePermission.hasPermission(MULTIPLE_PERMISSIONS))
            {
                Log.e(TAG, "permission granted from settings");
                showDialog(SignUpActivity.this);
            }
            else
            {
                Log.e(TAG, "permission is not granted, request for permission, from settings");

                ActivityCompat.requestPermissions(
                        SignUpActivity.this,
                        MULTIPLE_PERMISSIONS,
                        MULTIPLE_PERMISSION_REQUEST_CODE);
            }
        }

        if (requestCode == ImageAndVideoUtils.CAPTURE_IMAGE_REQUEST_CODE)
        {
            if (resultCode == RESULT_OK)
            {
                String realPath = RealPathUtils.getRealPath(this, imageUri);

                File oldFile = new File(realPath);
                System.out.println("=========================OLD===========================" + MemoryUnitUtils.getReadableFileSize(oldFile.length()));

                /* this file is store in a File externalFilesDir = context.getExternalFilesDir("Compress"); directory */
                File newFile = ConfigureCompression.getInstance(this).compressToFile(oldFile);
                System.out.println("=========================NEW===========================" + MemoryUnitUtils.getReadableFileSize(newFile.length()));

                pictureRealPathString = newFile.getAbsolutePath();
                Bitmap bitmap = BitmapFactory.decodeFile(pictureRealPathString);

                if (bitmap != null)
                {
                    viewBinding.profilePictureCircleImageView.setImageBitmap(bitmap);
                }

                Toast.makeText(getApplicationContext(), "" + oldFile, Toast.LENGTH_LONG).show();
            }
            else if (resultCode == RESULT_CANCELED)
            {
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                {
                    ContentResolver contentResolver = getApplicationContext().getContentResolver();
                    ContentValues updateContentValue = new ContentValues();
                    updateContentValue.put(MediaStore.Images.Media.IS_PENDING, 1);
                    contentResolver.update(imageUri, updateContentValue, null, null);
                }

                Toast.makeText(getApplicationContext(), "User cancelled capture image", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(getApplicationContext(), "Sorry! Failed to capture image", Toast.LENGTH_SHORT).show();
            }
        }

        if (requestCode == SELECT_IMAGE_REQUEST_CODE)
        {
            if (resultCode == RESULT_OK)
            {
                if (data != null)
                {
                    /* Here read store permission require */
                    imageUri = data.getData();

                    if (imageUri != null)
                    {
                        String realPath = RealPathUtils.getRealPath(this, imageUri);

                        File oldFile = new File(realPath);
                        System.out.println("=========================OLD==========================="+MemoryUnitUtils.getReadableFileSize(oldFile.length()));

                        /* this file is store in a File externalFilesDir = context.getExternalFilesDir("Compress"); directory */
                        File newFile = ConfigureCompression.getInstance(this).compressToFile(oldFile);
                        System.out.println("=========================NEW==========================="+ MemoryUnitUtils.getReadableFileSize(newFile.length()));

                        pictureRealPathString = newFile.getAbsolutePath();
                        Bitmap bitmap = BitmapFactory.decodeFile(pictureRealPathString);

                        if (bitmap != null)
                        {
                            viewBinding.profilePictureCircleImageView.setImageBitmap(bitmap);
                        }

                        Toast.makeText(getApplicationContext(), "" + newFile, Toast.LENGTH_SHORT).show();
                    }
                }
            }
            else if (resultCode == RESULT_CANCELED)
            {
                Toast.makeText(getApplicationContext(), "User cancelled select image", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(getApplicationContext(), "Sorry! Failed to select image", Toast.LENGTH_SHORT).show();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void galleryIntent() {
        ImplicitIntentUtils.actionPickIntent(1, SignUpActivity.this, SELECT_IMAGE_REQUEST_CODE);
    }

    private void cameraIntent(String customDirectoryName, String extension, String fileName) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
        {
            Uri sourceUri = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL);

            ContentResolver contentResolver = getContentResolver();

            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.Images.Media.TITLE, fileName);
            contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, fileName + extension);
            contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");

            /* Add the date meta data to ensure the image is added at the front of the gallery */
            long millis = System.currentTimeMillis();
            contentValues.put(MediaStore.Images.Media.DATE_ADDED, millis / 1000L);
            contentValues.put(MediaStore.Images.Media.DATE_MODIFIED, millis / 1000L);

            contentValues.put(MediaStore.Images.Media.DATE_TAKEN, millis);
            contentValues.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES);
            contentValues.put(MediaStore.Images.Media.IS_PENDING, 0);

            imageUri = contentResolver.insert(sourceUri, contentValues);
        }
        else
        {
            File directory = SharedFileUtils.createDirectory(customDirectoryName, Environment.DIRECTORY_PICTURES);
            File file = SharedFileUtils.createFile(directory, extension, fileName);
            imageUri = FileProviderUtils.getFileUri(getApplicationContext(), file, BuildConfig.APPLICATION_ID);
        }

        ImageAndVideoUtils.cameraIntent(1, imageUri, SignUpActivity.this);
    }

    private void showDialog(Activity activity) {
        AlertDialog.Builder option = new AlertDialog.Builder(activity);
        option.setTitle("Select Action");
        String[] pictureDialogItems = {
                "Capture photo from camera",
                "Select photo from gallery",
                "Cancel"
        };

        option.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                cameraIntent("AppName",".jpg",""+System.currentTimeMillis());
                                break;
                            case 1:
                                galleryIntent();
                                break;
                            case 2:
                                dialog.dismiss();
                                break;
                        }
                    }
                });

        option.show();
    }

    private void signUp() {
        ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(SignUpActivity.this);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Please wait...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);

        firstNameString         = viewBinding.firstNameTextInputEditText.getText().toString();
        lastNameString          = viewBinding.lastNameTextInputEditText.getText().toString();

        /*
         * Get the Radio Button which is set
         * If no Radio Button is set, -1 will be returned
         */
        int selectedId = viewBinding.genderRadioGroup.getCheckedRadioButtonId();

        if (selectedId == -1)
        {
            genderString            = "";
        }
        else
        {
            RadioButton radioButton = (RadioButton) viewBinding.genderRadioGroup.findViewById(selectedId);
            genderString            = radioButton.getText().toString();
        }

        phoneNumberString       = viewBinding.phoneNumberTextInputEditText.getText().toString();
        emailString             = viewBinding.emailTextInputEditText.getText().toString();
        passwordString          = viewBinding.passwordTextInputEditText.getText().toString();
        confirmPasswordString   = viewBinding.confirmPasswordTextInputEditText.getText().toString();
        fcmTokenString          = "NJjMmJkNDAxCnBhY2thZ2VOYW1lPWNvbS5jYXJ0by5hZHZhbmNlZC5rb3RsaW4Kb25saW5lT";

        if (validation(pictureRealPathString,firstNameString, lastNameString, genderString, countryCodeString, phoneNumberString, emailString, passwordString, confirmPasswordString, isTermsAndConditionsAccept, fcmTokenString) == null)
        {
            ApiService apiService = ApiServiceGenerator.createService(SignUpActivity.this, ApiService.class);

            MultipartBody.Part profilePic       = RequestUtils.createMultipartBody("profilePic", pictureRealPathString);
            RequestBody firstNameRequestBody    = RequestUtils.createRequestBodyForString(firstNameString);
            RequestBody lastNameRequestBody     = RequestUtils.createRequestBodyForString(lastNameString);
            RequestBody genderRequestBody       = RequestUtils.createRequestBodyForString(genderString);
            RequestBody countryCodeRequestBody  = RequestUtils.createRequestBodyForString(countryCodeString);
            RequestBody phoneNumberRequestBody  = RequestUtils.createRequestBodyForString(phoneNumberString);
            RequestBody emailRequestBody        = RequestUtils.createRequestBodyForString(emailString);
            RequestBody passwordRequestBody     = RequestUtils.createRequestBodyForString(passwordString);
            RequestBody fcmTokenRequestBody     = RequestUtils.createRequestBodyForString(fcmTokenString);

            Observable<Response<BaseResponse<Data>>> observable = apiService.signUp(profilePic, firstNameRequestBody, lastNameRequestBody, genderRequestBody, countryCodeRequestBody, phoneNumberRequestBody, emailRequestBody, passwordRequestBody, fcmTokenRequestBody);
            Observer<Response<BaseResponse<Data>>> observer = new Observer<Response<BaseResponse<Data>>>() {

                @Override
                public void onSubscribe(Disposable disposable) {
                    progressDialog.show();
                }

                @Override
                public void onNext(Response<BaseResponse<Data>> response) {
                    progressDialog.dismiss();
                    if (response != null) {
                        if (response.body() != null && response.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onError(Throwable e) {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onComplete() {
                }
            };

            observable
                    .subscribeOn(Schedulers.io())
                    .unsubscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(observer);
        }
        else
        {
            Toast.makeText(getApplicationContext(), validation(pictureRealPathString,firstNameString, lastNameString, genderString, countryCodeString, phoneNumberString, emailString, passwordString, confirmPasswordString, isTermsAndConditionsAccept, fcmTokenString), Toast.LENGTH_SHORT).show();
        }
    }

    private String validation(String pictureRealPath, String firstName, String lastName, String gender, String countryCode, String phoneNumber, String email, String password, String confirmPassword, boolean isTermsAndConditionsAccept, String fcmToken) {
        int phoneValidCode              = ValidationUtils.isPhoneNumberValid(countryCode, phoneNumber);
        int emailValidCode              = ValidationUtils.isValidEmail(email);
        int passwordValidCode           = ValidationUtils.isValidPassword(password);
        int confirmPasswordValidCode    = ValidationUtils.isValidConfirmPassword(password, confirmPassword);

        if(StringUtils.isBlank(pictureRealPath))
        {
            return getString(R.string.picture_message_one);
        }
        else if(StringUtils.isBlank(firstName))
        {
            return getString(R.string.first_name_message_one);
        }
        else if(StringUtils.isBlank(lastName))
        {
            return getString(R.string.last_name_message_one);
        }
        else if(StringUtils.isBlank(gender))
        {
            return getString(R.string.gender_message_one);
        }
        else if(phoneValidCode > 0)
        {
            if(phoneValidCode == 1)
            {
                return getString(R.string.phone_number_message_one);
            }
            else if(phoneValidCode == 2)
            {
                return getString(R.string.phone_number_message_two);
            }
            else if(phoneValidCode == 3)
            {
                return getString(R.string.phone_number_message_three);
            }
        }
        else if(emailValidCode > 0)
        {
            if(emailValidCode == 1)
            {
                return getString(R.string.email_message_one);
            }
            else if(emailValidCode == 2)
            {
                return getString(R.string.email_message_two);
            }
        }
        else if(passwordValidCode > 0)
        {
            if(passwordValidCode == 1)
            {
                return getString(R.string.password_message_one);
            }
            else if(passwordValidCode == 2)
            {
                return getString(R.string.password_message_two);
            }
            else if(passwordValidCode == 3)
            {
                return getString(R.string.password_message_three);
            }
            else if(passwordValidCode == 4)
            {
                return getString(R.string.password_message_four);
            }
            else if(passwordValidCode == 5)
            {
                return getString(R.string.password_message_five);
            }
            else if(passwordValidCode == 6)
            {
                return getString(R.string.password_message_six);
            }
            else if(passwordValidCode == 7)
            {
                return getString(R.string.password_message_seven);
            }
            else if(passwordValidCode == 8)
            {
                return getString(R.string.password_message_eight);
            }
        }
        else if(confirmPasswordValidCode > 0)
        {
            if(confirmPasswordValidCode == 1)
            {
                return getString(R.string.password_message_one);
            }
            else if(confirmPasswordValidCode == 2)
            {
                return getString(R.string.confirm_password_message_one);
            }
            else if(confirmPasswordValidCode == 3)
            {
                return getString(R.string.confirm_password_message_two);
            }
            else if(confirmPasswordValidCode == 4)
            {
                return getString(R.string.confirm_password_message_three);
            }
        }
        else if(!isTermsAndConditionsAccept)
        {
            return getString(R.string.accept_term_and_condition_message);
        }

        return null;
    }

    private void launchSignInScreen() {
        ActivityUtils.launchActivity(SignUpActivity.this, SignInActivity.class);
    }
}