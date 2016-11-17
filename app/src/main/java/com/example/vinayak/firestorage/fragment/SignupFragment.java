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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.vinayak.firestorage.R;
import com.example.vinayak.firestorage.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

public class SignupFragment extends Fragment {

    private EditText editTextName,editTextEmail,editTextPass;
    private Button buttonSignup, buttonCancel;
    private ProgressDialog pd;
    private DatabaseReference mDatabase;
    private ImageView imageView;
    private FirebaseStorage storage = FirebaseStorage.getInstance();

    private FirebaseAuth mAuth;
    private static String imageString = null;
    private static String downloadURL = null;

    private static String[] PERMISSIONS_STORAGE = {
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private static final int REQUEST_EXTERNAL_STORAGE = 1;

    private OnFragmentInteractionListener mListener;

    public SignupFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_signup, container, false);
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
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        editTextEmail = (EditText) getActivity().findViewById(R.id.etSignupEmail);
        editTextName = (EditText) getActivity().findViewById(R.id.editTextName);
        editTextPass = (EditText) getActivity().findViewById(R.id.etSignupPwd);
        buttonSignup = (Button) getActivity().findViewById(R.id.buttonSignup);
        buttonCancel = (Button) getActivity().findViewById(R.id.buttonCancel);
        imageView = (ImageView) getActivity().findViewById(R.id.imageView);
        pd = new ProgressDialog(getActivity());
        pd.setMessage("Uploading Profile Picture...");

        imageView.setOnClickListener(new View.OnClickListener() {
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
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                startActivityForResult(intent, 0);
            }
        });

        buttonSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSignup();
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        Uri selectedImageUri = data.getData();
        String[] filePathColumn = { MediaStore.Images.Media.DATA };
        Cursor cursor = getActivity().getContentResolver().query(selectedImageUri,
                filePathColumn, null, null, null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String imgDecodableString = cursor.getString(columnIndex);
        imageString = imgDecodableString;
//      imageView.setImageBitmap(BitmapFactory.decodeFile(imgDecodableString));
        cursor.close();

    }

    private void onSignup() {
        final String name = editTextName.getText().toString();
        final String email = editTextEmail.getText().toString();
        final String password = editTextPass.getText().toString();

        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    Log.d("demo", "In OnSignup " + task.isSuccessful());
                    Toast.makeText(getActivity(), "Signed up: " + name, Toast.LENGTH_SHORT).show();
                    final User user = new User();
                    user.setFullName(name);
                    user.setEmail(email);
                    user.setPassword(password);
                    final String uid = mAuth.getCurrentUser().getUid();

                    if(imageString!=null) {
                        Bitmap bitmap = BitmapFactory.decodeFile(imageString);
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.PNG,100,baos);
                        byte[] data = baos.toByteArray();

                        String path = "firestorage/" + uid + ".png";
                        pd.show();
                        StorageReference firestorageRef = storage.getReference(path);
                        UploadTask uploadTask = firestorageRef.putBytes(data);
                        uploadTask.addOnSuccessListener(getActivity(), new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                pd.dismiss();
                                Log.d("demo", "In onSuccess "+ taskSnapshot.getDownloadUrl().toString());
                                downloadURL = taskSnapshot.getDownloadUrl().toString();
                                mDatabase.child("users").child(uid).child("imageUrl").setValue(downloadURL);
                                mDatabase.child("users").child(uid).child("fullName").setValue(user.getFullName());
                                mDatabase.child("users").child(uid).child("email").setValue(user.getEmail());
                                mDatabase.child("users").child(uid).child("password").setValue(user.getPassword());
                                mListener.goToUserFromSignup();
                            }
                        });
                    } else { //if imageUrl is null
                        mDatabase.child("users").child(uid).child("fullName").setValue(user.getFullName());
                        mDatabase.child("users").child(uid).child("email").setValue(user.getEmail());
                        mDatabase.child("users").child(uid).child("password").setValue(user.getPassword());
                        mDatabase.child("users").child(uid).child("imageUrl").setValue(null);
                        mListener.goToUserFromSignup();
                        Log.d("demo", user.toString());
                    }
                } else {
                    Log.d("demo", task.getException().toString());
                }
            }
        });
    }

    public interface OnFragmentInteractionListener {
        void goToUserFromSignup();
    }
}
