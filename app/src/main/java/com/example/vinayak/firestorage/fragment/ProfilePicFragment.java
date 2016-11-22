package com.example.vinayak.firestorage.fragment;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vinayak.firestorage.R;
import com.example.vinayak.firestorage.model.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;


public class ProfilePicFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    Button logoutButton;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    ArrayList<User> usersList = null;
    public static String uid;
    private ProgressDialog pd;

    private ImageView imageViewUser;
    TextView textViewName;
    private static String imageString = null;
    private static String downloadURL = null;

    private static String[] PERMISSIONS_STORAGE = {
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private static final int REQUEST_EXTERNAL_STORAGE = 1;


    public ProfilePicFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile_pic, container, false);
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
        textViewName = (TextView) getActivity().findViewById(R.id.textViewNam);
        imageViewUser = (ImageView) getActivity().findViewById(R.id.imageViewProfilePic);
        usersList = new ArrayList<User>();

        pd = new ProgressDialog(getActivity());
        pd.setMessage("Uploading Profile Picture...");

        final FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if(firebaseUser != null) {
            uid = firebaseUser.getUid();
            DatabaseReference userFromDb = mDatabase.child("users");

            userFromDb.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (usersList.size() == 0) {
                        for (DataSnapshot userSnapShot : dataSnapshot.getChildren()) {
                            Log.d("demo", userSnapShot.getRef().toString() + " " + userSnapShot.getKey());
                            if (userSnapShot.getKey().equals(uid)) {
                                textViewName.setText(((String) userSnapShot.
                                        child("fullName").getValue()).toUpperCase());

                                Picasso.with(getContext()).load(((String) userSnapShot.
                                        child("imageUrl").getValue())).fit().into(imageViewUser);

                                imageViewUser.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        int permission = ActivityCompat.checkSelfPermission(getActivity(),
                                                android.Manifest.permission.WRITE_EXTERNAL_STORAGE);

                                        if (permission != PackageManager.PERMISSION_GRANTED) {
                                            ActivityCompat.requestPermissions(
                                                    getActivity(),
                                                    PERMISSIONS_STORAGE,
                                                    REQUEST_EXTERNAL_STORAGE
                                            );
                                        }
                                        Intent intent = new Intent(Intent.ACTION_PICK,
                                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                        intent.setType("image/*");
                                        startActivityForResult(intent, 0);
                                    }
                                });
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 1 && data!=null){
            Uri selectedImageUri = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };
            Cursor cursor = getActivity().getContentResolver().query(selectedImageUri,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String imgDecodableString = cursor.getString(columnIndex);
            imageString = imgDecodableString;
//        imageViewUser.setImageBitmap(BitmapFactory.decodeFile(imgDecodableString));
            cursor.close();

            if(imageString!=null) {
                Bitmap bitmap = BitmapFactory.decodeFile(imageString);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG,100,baos);
                byte[] data1 = baos.toByteArray();

                String path = "firestorage/" + uid + ".png";
                pd.show();
                StorageReference firestorageRef = storage.getReference(path);
                UploadTask uploadTask = firestorageRef.putBytes(data1);
                uploadTask.addOnSuccessListener(getActivity(), new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        pd.dismiss();
                        Log.d("demo", "In onSuccess "+ taskSnapshot.getDownloadUrl().toString());
                        downloadURL = taskSnapshot.getDownloadUrl().toString();
                        mDatabase.child("users").child(uid).child("imageUrl").setValue(downloadURL);
                    }
                });
            }
        }else{
            Toast.makeText(getActivity(),"You didn't select any image",Toast.LENGTH_SHORT).show();
        }
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction();
    }
}