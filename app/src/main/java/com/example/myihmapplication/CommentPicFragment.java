package com.example.myihmapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.app.Fragment;;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CommentPicFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CommentPicFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    private Button saveComment;
    private EditText writeComment;
    public OnSaveCommentInterface save;


    public CommentPicFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static CommentPicFragment newInstance(String param1, String param2) {
        CommentPicFragment fragment = new CommentPicFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_comment_pic, container, false);
    }

    @Override
    public void onViewCreated(View view,  Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        saveComment = view.findViewById(R.id.save_comment);
        writeComment = view.findViewById(R.id.edit_comment);
        View.OnClickListener onSaveComment = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (writeComment.getText().toString() != ""){
                    try {
                        File commentFile = StorageUtils.createFileTxt(getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "comment");
                        FileOutputStream fos = new FileOutputStream(commentFile);
                        fos.write(writeComment.getText().toString().getBytes());
                        fos.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    save.onSaveComment();
                }
            }
        };

        saveComment.setOnClickListener(onSaveComment);
    }

    @Override
    public void onAttach (Context context) {
        super.onAttach(context);
        try {
            save = (OnSaveCommentInterface) context;
        } catch (ClassCastException castException) {
            Log.i("CommentPicFragment" +
                    "", "onAttach Exception");
        }
    }

    public interface OnSaveCommentInterface {
        void onSaveComment ();
    }
}