package com.example.nabeel.todoapplication.auth;

import android.content.Intent;
import android.os.SharedMemory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.nabeel.todoapplication.R;
import com.example.nabeel.todoapplication.Utils.ShareMemory;
import com.example.nabeel.todoapplication.Utils.UtilsProgressbar;
import com.example.nabeel.todoapplication.activities.MainActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.kaopiz.kprogresshud.KProgressHUD;

public class Login extends AppCompatActivity implements View.OnClickListener {

    ImageView user_profile;
    Button btn_login;
    TextView tv_name;
    ShareMemory shareMemory;
    int RC_SIGN_IN = 7;
    KProgressHUD kProgressHUD;
    GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initilization();

    }

    private void initilization() {

        shareMemory = ShareMemory.getmInstence();
        tv_name = findViewById(R.id.tv_name);
        user_profile = findViewById(R.id.user_profile);
        btn_login = findViewById(R.id.btn_login);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("483015273854-2uvl75bgknjrmftib169pun25fe09aps.apps.googleusercontent.com")
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        btn_login.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                gmailLogin();
                break;
        }
    }

    private void gmailLogin() {

        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {

        kProgressHUD = UtilsProgressbar.showProgressDialog(this);

        kProgressHUD.show();

        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.

            shareMemory.setUserID(account.getId());
            tv_name.setText(account.getDisplayName());
            Glide.with(this).load(account.getPhotoUrl().toString()).into(user_profile);

            Intent intent = new Intent(Login.this, MainActivity.class);
            startActivity(intent);

            kProgressHUD.dismiss();

        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            kProgressHUD.dismiss();
            Toast.makeText(this, "Some went wrong", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();

        if (!shareMemory.getUserID().equals("")) {

            Intent intent = new Intent(Login.this, MainActivity.class);
            startActivity(intent);

        }

    }
}
