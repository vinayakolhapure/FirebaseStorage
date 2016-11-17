package com.example.vinayak.firestorage;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.vinayak.firestorage.fragment.LoginFragment;
import com.example.vinayak.firestorage.fragment.MessageFragment;
import com.example.vinayak.firestorage.fragment.SignupFragment;
import com.example.vinayak.firestorage.fragment.UserFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements LoginFragment.OnFragmentInteractionListener,
        SignupFragment.OnFragmentInteractionListener, UserFragment.OnFragmentInteractionListener,
        MessageFragment.OnFragmentInteractionListener {

    private FirebaseAuth mAuth;
    private static int WELCOME_COUNT = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(WELCOME_COUNT == 0){
            WELCOME_COUNT +=1;
            Intent welcomeIntent = new Intent(MainActivity.this, Welcome.class);
            startActivity(welcomeIntent);
        }

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

    @Override
    public void goToMessageFragment(String uid, String clickedUserUid) {
        //getSupportFragmentManager().popBackStack();
        Bundle bundle = new Bundle();
        bundle.putString("UID", uid);
        bundle.putString("CLICKED_UID", clickedUserUid);
        MessageFragment fragment = new MessageFragment();
        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, fragment, "message_fragment")
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onMessageFragmentInteraction() {

    }
}
