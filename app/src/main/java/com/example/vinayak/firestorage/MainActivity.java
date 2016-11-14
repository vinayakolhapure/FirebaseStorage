package com.example.vinayak.firestorage;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements LoginFragment.OnFragmentInteractionListener,
        SignupFragment.OnFragmentInteractionListener, UserFragment.OnFragmentInteractionListener {

    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();

        if(firebaseUser != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.container, new UserFragment(), "user_fragment")
                    .commit();
        } else {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.container, new LoginFragment(), "login_fragment")
                    .commit();
        }
    }

    @Override
    public void goToUserFragment() {
        getSupportFragmentManager().popBackStack();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, new UserFragment(), "user_fragment")
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void goToSignUpFragment() {
        getSupportFragmentManager().popBackStack();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, new SignupFragment(), "signup_fragment")
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void goToUserFromSignup() {
        getSupportFragmentManager().popBackStack();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, new UserFragment(), "user_fragment")
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onFragmentInteraction() {
        getSupportFragmentManager().popBackStack();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, new LoginFragment(), "login_fragment")
                .addToBackStack(null)
                .commit();
    }
}
