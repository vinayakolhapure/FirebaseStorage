package com.example.vinayak.firestorage.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.vinayak.firestorage.R;
import com.example.vinayak.firestorage.model.User;
import com.example.vinayak.firestorage.adapter.ContactsAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class UserFragment extends Fragment {

    private ImageView imageViewUser;

    private OnFragmentInteractionListener mListener;
    Button logoutButton;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    ArrayList<User> usersList = null;
    ContactsAdapter adapter;
    ListView listView;
    TextView textViewWelcome;

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
        textViewWelcome = (TextView) getActivity().findViewById(R.id.textViewWelcom);
        imageViewUser = (ImageView) getActivity().findViewById(R.id.ivUserProfile);
        listView = (ListView) getActivity().findViewById(R.id.listView);
        usersList = new ArrayList<User>();

        final FirebaseUser firebaseUser = mAuth.getCurrentUser();

        if(firebaseUser != null) {
            final String uid = firebaseUser.getUid();
            DatabaseReference userFromDb = mDatabase.child("users");

            userFromDb.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(usersList.size() == 0) {
                        for (DataSnapshot userSnapShot : dataSnapshot.getChildren()) {
                            User user = new User();
                            Log.d("demo", userSnapShot.getRef().toString() + " " + userSnapShot.getKey());
                            if (!userSnapShot.getKey().equals(uid)) {
                                user.setFullName((String) userSnapShot.child("fullName").getValue());
                                user.setImageUrl((String) userSnapShot.child("imageUrl").getValue());
                                user.setEmail((String) userSnapShot.child("email").getValue());
                                user.setPassword((String) userSnapShot.child("password").getValue());
                                usersList.add(user);
                            } else {
                                textViewWelcome.setText(((String) userSnapShot.
                                        child("fullName").getValue()).toUpperCase() + "'s " + "Contacts");
                                Picasso.with(getContext()).load(((String) userSnapShot.
                                        child("imageUrl").getValue())).fit().into(imageViewUser);

                                imageViewUser.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        mListener.goToProfilePicFragment();
                                    }
                                });
                            }
                        }
                        adapter = new ContactsAdapter(getActivity(),R.layout.item_row_contacts,
                                usersList);
                        listView.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }

        logoutButton = (Button) getActivity().findViewById(R.id.buttonLogout);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                mListener.onFragmentInteraction();

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(usersList.size() > 0) {
                    DatabaseReference userFromDb = mDatabase.child("users");
                    final User userClicked = usersList.get(position);
                    userFromDb.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                                String userEmail = (String) userSnapshot.
                                        child("email").getValue();
                                if(userClicked.getEmail().equals(userEmail)) {
                                    String uid = mAuth.getCurrentUser().getUid();
                                    String clickedUid = userSnapshot.getKey();
                                    Log.d("demo", uid + "---" + " " + clickedUid);
                                    mListener.goToMessageFragment(uid,clickedUid);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
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
        void goToProfilePicFragment();
        void goToMessageFragment(String uid, String clickedUserUid);
    }
}