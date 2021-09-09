package com.task.ui;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;
import com.task.R;
import com.task.data.remote.ApiService;
import com.task.data.remote.ApiServiceGenerator;
import com.task.databinding.ActivitySignInBinding;
import com.task.model.BaseResponse;
import com.task.model.Data;
import com.task.ui.base.BaseActivity;
import com.task.utilities.ActivityUtils;
import com.task.utilities.ValidationUtils;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

public class SignInActivity extends BaseActivity<ActivitySignInBinding> {

    public static final String TAG = SignInActivity.class.getSimpleName();

    private String emailString;
    private String passwordString;
    private String fcmTokenString          = "NJjMmJkNDAxCnBhY2thZ2VOYW1lPWNvbS5jYXJ0by5hZHZhbmNlZC5rb3RsaW4Kb25saW5lT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected ActivitySignInBinding getViewBinding() {
        return ActivitySignInBinding.inflate(getLayoutInflater());
    }

    @Override
    protected void initializeObject() {
    }

    @Override
    protected void initializeToolBar() {
    }

    @Override
    protected void initializeCallbackListener() {
    }

    @Override
    protected void addTextChangedListener() {
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
                int emailValidCode = ValidationUtils.isValidEmail(viewBinding.emailTextInputEditText.getText().toString());
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
                int passwordValidCode = ValidationUtils.isValidPassword(viewBinding.passwordTextInputEditText.getText().toString());
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
    }

    @Override
    protected void setOnClickListener() {
        viewBinding.appSignInMaterialButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appSignIn();
            }
        });

        viewBinding.appSignUpLinkMaterialButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchSignUpScreen();
            }
        });
    }

    public void appSignIn() {
        ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(SignInActivity.this);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Please wait...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);

        emailString             = viewBinding.emailTextInputEditText.getText().toString();
        passwordString          = viewBinding.passwordTextInputEditText.getText().toString();

        if (validation(emailString, passwordString) == null)
        {
            ApiService apiService = ApiServiceGenerator.createService(SignInActivity.this, ApiService.class);

            Observable<Response<BaseResponse<Data>>> observable = apiService.signIn(emailString,passwordString, fcmTokenString);
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
            Toast.makeText(getApplicationContext(), validation(emailString, passwordString), Toast.LENGTH_SHORT).show();
        }
    }

    public void launchSignUpScreen() {
        ActivityUtils.launchActivity(SignInActivity.this, SignUpActivity.class);
    }

    public String validation(String email, String password) {
        int emailValidCode              = ValidationUtils.isValidEmail(email);
        int passwordValidCode           = ValidationUtils.isValidPassword(password);

        if(emailValidCode > 0)
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
        return null;
    }
}