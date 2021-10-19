package com.example.myihmapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class DisplayActivity extends AppCompatActivity {

    private static final String TAG = "DisplayActivity";
    TextView recap;
    String displayText;
    @Override


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dispaly);

        //Get the user info from intent Parceable Extra, and use the User methods to get each type of info
        recap = findViewById(R.id.displayInfo);
        displayText = getString(R.string.recap) + ": \n";
        Intent intent = getIntent();
        if (intent != null){
            User user = intent.getParcelableExtra("User");
            if (user != null){
                    Log.i(TAG,"user not null");
                    displayText += "\n \n" + getString(R.string.nom) + ": " + user.getNom() ;
                    displayText += "\n \n" + getString(R.string.prenom) + ": " + user.getPrenom() ;
                    displayText += "\n \n" + getString(R.string.date_naissance) + ": " + user.getDate();
                    displayText += "\n \n" + getString(R.string.ville_naissance) + ": " + user.getVille();
                    displayText += "\n \n" + getString(R.string.departement) + ": " + user.getDepartement();
                    ArrayList<String> phoneNumbers = user.getPhoneNumbers();
                    if (phoneNumbers.size() > 0 ) {
                        displayText += "\n \n" + getString(R.string.numero_telephone) + ": ";
                        for (int i = 0; i < phoneNumbers.size(); i++) {
                            displayText += phoneNumbers.get(i) + " / ";
                        }
                    }
                }
        }
        recap.setText(displayText);
        recap.setMaxLines(20);
    }

    public void onClickBack(View view) {
        Intent intent = new Intent (this, MainActivity.class);
        startActivity(intent);
    }
}