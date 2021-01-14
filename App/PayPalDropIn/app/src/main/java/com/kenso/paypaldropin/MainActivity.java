package com.kenso.paypaldropin;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.braintreepayments.api.dropin.DropInActivity;
import com.braintreepayments.api.dropin.DropInRequest;
import com.braintreepayments.api.dropin.DropInResult;
import com.braintreepayments.api.models.PaymentMethodNonce;
import com.kenso.paypaldropin.Model.BraintreeToken;
import com.kenso.paypaldropin.Model.BraintreeTransaction;
import com.kenso.paypaldropin.retrofit.RetrofitClient;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import cz.msebera.android.httpclient.Header;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import com.kenso.paypaldropin.retrofit.MyBraintreeAPI;


public class MainActivity extends AppCompatActivity {

    private static final String SERVER_BASE = "YOUR-SERVER.COM";
    private AsyncHttpClient client = new AsyncHttpClient();
    private String clientToken;
    private static String myTokenizationKey = "sandbox_q7nn3k9v_ppsghhty72ktv86b";
    private static final int REQUEST_CODE = 101;

    private Button btn_submit;
    private EditText edit_amount;

    CompositeDisposable compositeDisposable = new CompositeDisposable();
    MyBraintreeAPI myBraintreeAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Init
        myBraintreeAPI = RetrofitClient.getInstance().create(MyBraintreeAPI.class);
        btn_submit = (Button) findViewById(R.id.btn_submit);
        edit_amount = (EditText) findViewById(R.id.edit_amount);

        //Event Listener
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitPayment();
            }
        });

        //Get token
        compositeDisposable.add(myBraintreeAPI.getToken().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<BraintreeToken>() {
                    @Override
                    public void accept(BraintreeToken braintreeToken) throws Exception {
                        if (braintreeToken.isSuccess()) {
                            Toast.makeText(MainActivity.this, "Payment is ready to submit.", Toast.LENGTH_SHORT).show();
                            btn_submit.setEnabled(true);
                            clientToken = braintreeToken.getClientToken();
                        }

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toast.makeText(MainActivity.this, throwable.getMessage()+"", Toast.LENGTH_SHORT).show();
                    }
                }));
        //getToken();
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }

    private void submitPayment() {
        DropInRequest dropInRequest = new DropInRequest()
                .clientToken(clientToken);
        startActivityForResult(dropInRequest.getIntent(this), REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                DropInResult result = data.getParcelableExtra(DropInResult.EXTRA_DROP_IN_RESULT);
                PaymentMethodNonce nonce = result.getPaymentMethodNonce();

                //After receiving nonce, make the payment with API
                if (!TextUtils.isEmpty(edit_amount.getText().toString())) {
                    String amount = edit_amount.getText().toString();

                    compositeDisposable.add(myBraintreeAPI
                            .submitPayment(amount, nonce.getNonce())
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Consumer<BraintreeTransaction>() {
                                @Override
                                public void accept(BraintreeTransaction braintreeTransaction) throws Exception {
                                    if (braintreeTransaction.isSuccess()) {
                                        Toast.makeText(MainActivity.this, braintreeTransaction.getTransaction().getId()+"", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(MainActivity.this, "Payment failed!", Toast.LENGTH_SHORT).show();
                                    }

                                }
                            }, new Consumer<Throwable>() {
                                @Override
                                public void accept(Throwable throwable) throws Exception {
                                    Toast.makeText(MainActivity.this, throwable.getMessage()+"", Toast.LENGTH_SHORT).show();
                                }
                            })
                    );
                }

            }
        }
    }

    /*public void onBraintreeSubmit(View v) {
        // Pass the client token for getting payment nonce
        //DropInRequest dropInRequest = new DropInRequest()
        //        .clientToken(clientToken);
        DropInRequest dropInRequest = new DropInRequest()
                .clientToken(myTokenizationKey);
        startActivityForResult(dropInRequest.getIntent(this), REQUEST_CODE);
    }*/

    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                DropInResult result = data.getParcelableExtra(DropInResult.EXTRA_DROP_IN_RESULT);

                // use the result to update your UI and send the payment method nonce to your server
                RequestParams requestParams = new RequestParams();
                requestParams.put("payment_method_nonce", result.getPaymentMethodNonce().getNonce());
                requestParams.put("amount", "10.00");

                client.post(SERVER_BASE + "/checkout", requestParams, new TextHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String responseString) {

                    }
                });
            } else if (resultCode == RESULT_CANCELED) {
                // the user canceled
            } else {
                // handle errors here, an exception may be available in
                Exception error = (Exception) data.getSerializableExtra(DropInActivity.EXTRA_ERROR);
            }
        }
    }*/

    /*private void getToken() {
        // Request for client token from server
        client.get(SERVER_BASE + "/client_token", new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                clientToken = responseString;

            }
        });
    }*/
}