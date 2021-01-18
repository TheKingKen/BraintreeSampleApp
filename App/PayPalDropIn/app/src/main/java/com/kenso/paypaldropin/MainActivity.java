package com.kenso.paypaldropin;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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

import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import com.kenso.paypaldropin.retrofit.MyBraintreeAPI;


public class MainActivity extends AppCompatActivity {

    private String clientToken;
    //private static String myTokenizationKey = "sandbox_q7nn3k9v_ppsghhty72ktv86b";
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
                            Log.d("clientToken",clientToken+"");
                        }

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toast.makeText(MainActivity.this, throwable.getMessage()+"", Toast.LENGTH_SHORT).show();
                        Log.d("Exception",throwable.getMessage()+"");
                    }
                }));
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
                    final String amount = edit_amount.getText().toString();

                    compositeDisposable.add(myBraintreeAPI
                            .submitPayment(amount, nonce.getNonce())
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Consumer<BraintreeTransaction>() {
                                @Override
                                public void accept(BraintreeTransaction braintreeTransaction) throws Exception {
                                    if (braintreeTransaction.isSuccess()) {
                                        //Toast.makeText(MainActivity.this, "Payment #" + braintreeTransaction.getTransaction().getId()+ " of amount $" + amount + " is completed.", Toast.LENGTH_SHORT).show();
                                        new AlertDialog.Builder(MainActivity.this)
                                                .setTitle("SUCCESS")
                                                .setMessage("Payment (#" + braintreeTransaction.getTransaction().getId()+ ") of amount $" + amount + " is completed.")
                                                .setNegativeButton(android.R.string.yes, null) // A null listener allows the button to dismiss the dialog and take no further action.
                                                .setIcon(android.R.drawable.ic_dialog_alert)
                                                .show();

                                        Log.d("Success", "Payment #" + braintreeTransaction.getTransaction().getId());
                                    } else {
                                        //Toast.makeText(MainActivity.this, "Payment failed!", Toast.LENGTH_SHORT).show();
                                        new AlertDialog.Builder(MainActivity.this)
                                                .setTitle("FAIL")
                                                .setMessage("Payment failed.\nStatus: "
                                                        + braintreeTransaction.getTransaction().getStatus()
                                                        + "\nResponseType:" + braintreeTransaction.getTransaction().getProcessorResponseType()
                                                        + "\nResponseText:" + braintreeTransaction.getTransaction().getProcessorResponseText())
                                                .setNegativeButton(android.R.string.yes, null) // A null listener allows the button to dismiss the dialog and take no further action.
                                                .setIcon(android.R.drawable.ic_dialog_alert)
                                                .show();

                                        Log.d("Fail", braintreeTransaction.getTransaction().getId()+"");
                                        Log.d("Fail", braintreeTransaction.getTransaction().getStatus());
                                        Log.d("Fail", braintreeTransaction.getTransaction().getAmount());
                                        Log.d("Fail", braintreeTransaction.getTransaction().getMerchantAccountId());
                                        Log.d("Fail", braintreeTransaction.getTransaction().getProcessorResponseType());
                                        Log.d("Fail", braintreeTransaction.getTransaction().getProcessorResponseCode());
                                        Log.d("Fail", braintreeTransaction.getTransaction().getProcessorResponseText());
                                    }

                                }
                            }, new Consumer<Throwable>() {
                                @Override
                                public void accept(Throwable throwable) throws Exception {
                                    Toast.makeText(MainActivity.this, throwable.getMessage()+"", Toast.LENGTH_SHORT).show();
                                    Log.d("Exception",throwable.getMessage()+"");
                                }
                            })
                    );
                }

            }
        }
    }

}