package com.example.vinayak.firestorage.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.vinayak.firestorage.model.Message;
import com.example.vinayak.firestorage.R;
import com.example.vinayak.firestorage.adapter.MessageAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class MessageFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    ListView listView;
    EditText msgText;
    ImageView imageSend;
    private DatabaseReference mDatabase;
    ArrayList<Message> messageList = null;
    MessageAdapter adapter;
    String uid;
    String clickedUid;
    int receivedMsgViewCounter = 0;

    public MessageFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_message, container, false);
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
        mDatabase = FirebaseDatabase.getInstance().getReference();
        listView = (ListView) getActivity().findViewById(R.id.listViewMsg);
        msgText = (EditText) getActivity().findViewById(R.id.editTextMessage);
        imageSend = (ImageView) getActivity().findViewById(R.id.imageSend);

        uid = this.getArguments().getString("UID");
        clickedUid = this.getArguments().getString("CLICKED_UID");

        messageList = new ArrayList<Message>();

        mDatabase.child("messages").child(uid+clickedUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("demo", "REF " + dataSnapshot.getRef());
                if(messageList.size() == 0) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Log.d("demo", "SNAPSHOT REF " + snapshot.getRef());
                        Log.d("demo", "Message Fragment For loop");
                        Message message = new Message();
                        message.setMsgText((String) snapshot.child("messageText").getValue());
                        message.setMsgDate((String) snapshot.child("messageDate").getValue());
                        message.setSenderId((String) snapshot.child("senderID").getValue());
                        message.setReceiverId((String) snapshot.child("receiverId").getValue());
                        messageList.add(message);
                    }
                    if(messageList.size()>0) {
                        Log.d("demo", "Message Fragment If Size > 0");
                        if(getActivity() != null) {
                            adapter = new MessageAdapter(getActivity(), R.layout.row_layout_msgs,
                                    messageList);
                            listView.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mDatabase.child("messages").child(clickedUid+uid).
                addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //if(receivedMsgViewCounter == 0) {
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Message message = new Message();
                        message.setMsgText((String) snapshot.child("messageText").getValue());
                        message.setMsgDate((String) snapshot.child("messageDate").getValue());
                        message.setSenderId((String) snapshot.child("senderID").getValue());
                        message.setReceiverId((String) snapshot.child("receiverId").getValue());
                        messageList.add(message);
                        Collections.sort(messageList,Message.DateOrder);
                    }
                    if(adapter!=null) {
                        adapter.notifyDataSetChanged();
                    } else {
                        adapter = new MessageAdapter(getActivity(), R.layout.row_layout_msgs,
                                messageList);
                        listView.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                    }
                //    receivedMsgViewCounter +=1;
                //}
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //String uid = this.getArguments().getString("UID");
        imageSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date today = Calendar.getInstance().getTime();
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-hh.mm.ss a");
                String formattedDate = formatter.format(today);

                Message message = new Message();
                message.setMsgDate(formattedDate);
                message.setMsgText(msgText.getText().toString());
                message.setReceiverId(clickedUid);
                message.setSenderId(uid);
                String key = mDatabase.child("messages").child(uid+clickedUid).push().getKey();
                mDatabase.child("messages").child(uid+clickedUid).child(key).
                        child("messageText").setValue(msgText.getText().toString());
                mDatabase.child("messages").child(uid+clickedUid).child(key).
                        child("senderId").setValue(uid);
                mDatabase.child("messages").child(uid+clickedUid).child(key).
                        child("receiverId").setValue(clickedUid);
                mDatabase.child("messages").child(uid+clickedUid).child(key).
                        child("messageDate").setValue(formattedDate);

                messageList.add(message);
                msgText.setText("");
                if(messageList.size()==1) {
                    adapter = new MessageAdapter(getActivity(),R.layout.row_layout_msgs,
                            messageList);
                    listView.setAdapter(adapter);
                }
                if(adapter != null) {
                    adapter.notifyDataSetChanged();
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
        void onMessageFragmentInteraction();
    }
}
