package com.task.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.google.gson.JsonObject;
import com.task.R;
import com.task.data.remote.ApiService;
import com.task.data.remote.ApiServiceGenerator;
import com.task.data.remote.RemoteConfiguration;
import com.task.data.remote.glide.GlideCacheUtil;
import com.task.data.remote.glide.GlideImageLoader;
import com.task.data.remote.glide.GlideImageLoadingListener;
import com.task.data.remote.picasso.PicassoImageLoader;
import com.task.data.remote.picasso.PicassoImageLoadingListener;
import com.task.databinding.ActivityRequestTestBinding;
import com.task.ui.base.BaseActivity;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

public class RequestTestActivity extends BaseActivity<ActivityRequestTestBinding> {

    public static final String TAG = RequestTestActivity.class.getSimpleName();

    private Disposable subscribe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected ActivityRequestTestBinding getViewBinding() {
        return ActivityRequestTestBinding.inflate(getLayoutInflater());
    }

    @Override
    protected void initializeObject() {
    }

    @Override
    protected void initializeToolBar() {
    }

    @Override
    protected void initializeCallbackListener() {
        /*GlideCacheUtil.getInstance().clearAllCache(this);
        GlideImageLoader.load(
                this,
                "https://backend24.000webhostapp.com/Json/profile.jpg",
                R.drawable.user_placeholder,
                R.drawable.error_placeholder,
                viewBinding.imageView,
                new GlideImageLoadingListener() {
                    @Override
                    public void imageLoadSuccess() {
                        viewBinding.imageLoadingProgressBar.setVisibility(View.GONE);
                    }
                    @Override
                    public void imageLoadError() {
                        viewBinding.imageLoadingProgressBar.setVisibility(View.GONE);
                    }
                });*/

        PicassoImageLoader.load(
                this,
                "https://backend24.000webhostapp.com/Json/profile.jpg",
                R.drawable.user_placeholder,
                R.drawable.error_placeholder,
                viewBinding.imageView,
                new PicassoImageLoadingListener() {
                    @Override
                    public void imageLoadSuccess() {
                        viewBinding.imageLoadingProgressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void imageLoadError(Exception exception) {
                        Toast.makeText(getApplicationContext(), "An error occurred", Toast.LENGTH_SHORT).show();
                        viewBinding.imageLoadingProgressBar.setVisibility(View.GONE);
                    }
                });
    }

    @Override
    protected void addTextChangedListener() {
    }

    @Override
    protected void setOnClickListener() {
        viewBinding.requestMaterialButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //getSingleEmployee();
                getPage();
            }
        });

        viewBinding.cancelRequestMaterialButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispose(subscribe);
            }
        });
    }

    private void getSingleEmployee() {
        ApiService apiService = ApiServiceGenerator.createService(RequestTestActivity.this, ApiService.class);

        Observable<Response<JsonObject>> observable = apiService.getEmployee();
        Observer<Response<JsonObject>> observer = new Observer<Response<JsonObject>>() {

            @Override
            public void onSubscribe(Disposable disposable) {
                viewBinding.requestProgressBar.setVisibility(View.VISIBLE);
                subscribe = disposable;
            }

            @Override
            public void onNext(Response<JsonObject> response) {
                viewBinding.requestProgressBar.setVisibility(View.GONE);
                if (response != null) {
                    if (response.body() != null && response.isSuccessful()) {
                        viewBinding.responseTextView.setText(response.body().toString());
                    }
                }
            }

            @Override
            public void onError(Throwable e) {
                viewBinding.requestProgressBar.setVisibility(View.GONE);
                viewBinding.responseTextView.setText(e.getMessage());
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

    private void getPage() {
        ApiService apiService = ApiServiceGenerator.createService(RequestTestActivity.this, ApiService.class);

        Observable<Response<JsonObject>> observable = apiService.getPage(RemoteConfiguration.API_KEY, "1");
        Observer<Response<JsonObject>> observer = new Observer<Response<JsonObject>>() {

            @Override
            public void onSubscribe(Disposable disposable) {
                viewBinding.requestProgressBar.setVisibility(View.VISIBLE);
                subscribe = disposable;
            }

            @Override
            public void onNext(Response<JsonObject> response) {
                viewBinding.requestProgressBar.setVisibility(View.GONE);
                if (response != null) {
                    if (response.body() != null && response.isSuccessful()) {
                        viewBinding.responseTextView.setText(response.body().toString());
                    }
                }
            }

            @Override
            public void onError(Throwable e) {
                viewBinding.requestProgressBar.setVisibility(View.GONE);
                viewBinding.responseTextView.setText(e.getMessage());
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dispose(subscribe);
    }

    /**
     * unsubscribe
     *
     * @param disposable subscription information
     */
    public void dispose(Disposable disposable) {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
            Log.e(TAG, "Call dispose(Disposable disposable)");

            viewBinding.requestProgressBar.setVisibility(View.GONE);
            viewBinding.responseTextView.setText("CANCEL");
        }
    }
}