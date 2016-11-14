package com.example.vinayak.firestorage;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;


public class UserFragment extends Fragment {

    private ImageView imageViewUser;

    private OnFragmentInteractionListener mListener;
    Button logoutButton;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    public UserFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user, container, false);
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
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        imageViewUser = (ImageView) getActivity().findViewById(R.id.ivUserProfile);

        FirebaseUser firebaseUser = mAuth.getCurrentUser();

        if(firebaseUser != null) {
            String uid = firebaseUser.getUid();
            DatabaseReference userFromDb = mDatabase.child("users").child(uid);
            userFromDb.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    Picasso.with(getActivity()).load(user.getImageUrl()).into(imageViewUser);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            Log.d("demo","Log");
        }

        logoutButton = (Button) getActivity().findViewById(R.id.buttonLogout);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                mListener.onFragmentInteraction();

            }
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction();
    }
}
