package com.example.vinayak.firestorage.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.vinayak.firestorage.R;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginFragment extends Fragment{

    EditText eEmail,ePwd;
    private FirebaseAuth mAuth;
    GoogleApiClient mGoogleApiClient;
    private static final int RC_SIGN_IN =9001;
    SignInButton signInButton;

    private OnFragmentInteractionListener mListener;
    LoginFragment loginFragmentContext;

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        signInButton = (SignInButton)getActivity().findViewById(R.id.sign_in_button);
        eEmail = (EditText) getActivity().findViewById(R.id.editTextEmail);
        ePwd = (EditText) getActivity().findViewById(R.id.editTextPwd);
        mAuth = FirebaseAuth.getInstance();
        Button buttonLogin= (Button) getActivity().findViewById(R.id.buttonLogin);
        Button createAcc = (Button) getActivity().findViewById(R.id.buttonCreate);

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = eEmail.getText().toString();
                String pwd = ePwd.getText().toString();
                if (!validateForm()) {
                    return;
                }
                mAuth.signInWithEmailAndPassword(email,pwd)
                        .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            mListener.goToUserFragment();
                        } else {
                            Toast.makeText(getActivity(), "Check Email-id and Password", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        createAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.goToSignUpFragment();
            }
        });
    }

    private boolean validateForm() {
        boolean valid = true;

        String email = eEmail.getText().toString();
        if (TextUtils.isEmpty(email)) {
            eEmail.setError("Required.");
            valid = false;
        } else {
            eEmail.setError(null);
        }

        String password = ePwd.getText().toString();
        if (TextUtils.isEmpty(password)) {
            ePwd.setError("Required.");
            valid = false;
        } else {
            ePwd.setError(null);
        }

        return valid;
    }

    public interface OnFragmentInteractionListener {

        void goToUserFragment();
        void goToSignUpFragment();
    }
}
