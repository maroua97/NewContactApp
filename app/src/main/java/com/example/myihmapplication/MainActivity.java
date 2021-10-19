package com.example.myihmapplication;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.core.content.res.ResourcesCompat;

import android.annotation.SuppressLint;
import android.app.FragmentTransaction;
import android.app.SearchManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.InputType;
import android.text.Layout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static android.media.ThumbnailUtils.extractThumbnail;
import static java.lang.Integer.getInteger;


public class MainActivity extends AppCompatActivity implements CommentPicFragment.OnSaveCommentInterface {
    private EditText nomEditText;
    private String nomText;
    private EditText prenomEditText;
    private String prenomText;
    private EditText villeEditText;
    private String villeText;
    private LinearLayout phoneLayout;
    private Spinner departementSpinner;
    private String departementString;
    private int departementIndex;
    private TextView dateView;
    private String dateText;
    private ArrayList<String> mPhoneNumbers = new ArrayList<String>();

    private Bitmap thumbnail = null;

    static final int REQUEST_IMAGE_CAPTURE = 0;
    private String TAG = "MainActivity";
    private ImageView imageView;
    private Uri photoURI;
    private File photoFile = null;

    static final int REQUEST_DATE = 1;

    private CommentPicFragment commentFragment;

    private SharedPreferences preferences;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Get metrics
        Log.i("onCreate", String.valueOf(this.getResources().getDisplayMetrics()));

        //So that the keyboard stau hidden
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        //Get views
        nomEditText = findViewById(R.id.nomText);
        prenomEditText= findViewById(R.id.prenomText);
        villeEditText = findViewById(R.id.villeText);
        dateView = findViewById(R.id.dateText);
        phoneLayout = findViewById(R.id.phoneLayout);
        departementSpinner = findViewById(R.id.departementSpinner);
        imageView = findViewById(R.id.imageView);

        //Restore Fields from preferences
        preferences = getApplicationContext().getSharedPreferences("com.example.myihmapplication-bI5oqQ71l12GSyysN6l2tw", Context.MODE_PRIVATE);

        nomText = preferences.getString("Nom", nomText);
        nomEditText.setText(nomText);

        prenomText = preferences.getString("Prenom", prenomText);
        prenomEditText.setText(prenomText);

        villeText = preferences.getString("Ville", villeText);
        villeEditText.setText(villeText);

        dateText = preferences.getString("Date", dateText);
        dateView.setText(dateText);

        departementIndex = preferences.getInt("Departement", departementIndex);
        departementSpinner.setSelection(departementIndex);

        Set<String> pnSet = null;
        pnSet = preferences.getStringSet("NumerosTelephones", pnSet);
        if (pnSet != null) {
            mPhoneNumbers = new ArrayList<String>(pnSet);
            setPhonesOnRestore(mPhoneNumbers);
        }

    }

    //We save data entered in Preferences when going back and the OutState Bundle when we rotate phone
    protected void onSaveInstanceState(Bundle outState) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        if (nomEditText.getText().toString().trim().length() > 0) {
            outState.putString("Nom", nomEditText.getText().toString());
            editor.putString("Nom", nomEditText.getText().toString());
        }
        if (prenomEditText.getText().toString().trim().length() > 0) {
            outState.putString("Prenom", prenomEditText.getText().toString());
            editor.putString("Prenom", prenomEditText.getText().toString());
        }
        if (villeEditText.getText().toString().trim().length() > 0) {
            outState.putString("Ville", villeEditText.getText().toString());
            editor.putString("Ville", villeEditText.getText().toString());
        }
        if (dateView.getText().toString().trim().length() > 0) {
            outState.putString("Date", dateView.getText().toString());
            editor.putString("Date", dateView.getText().toString());
        }
        if (departementSpinner.getSelectedItemPosition() != 0) {
            outState.putInt("Departement", departementSpinner.getSelectedItemPosition());
            editor.putInt("Departement", departementSpinner.getSelectedItemPosition());
        }
        ArrayList<String> telephones = getPhoneNumbers();
        if (telephones.size() > 0) {
            outState.putStringArrayList("NumerosTelephones", telephones);
            //The only acceptable way to put a string list in preferences is through a set so we transform the arraylist to a set
            Set<String> set = new HashSet<String>();
            set.addAll(telephones);
            editor.putStringSet("NumerosTelephones", set);
        }
        //there is no putParceable equivalent method for editor
        if(thumbnail != null){
            outState.putParcelable("Image", thumbnail);
        }
        editor.commit();
        super.onSaveInstanceState(outState);
    }

    //Restore the data
    @RequiresApi(api = Build.VERSION_CODES.M)
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        mPhoneNumbers = savedInstanceState.getStringArrayList("NumerosTelephones");
        setPhonesOnRestore(mPhoneNumbers);
        nomEditText.setText(savedInstanceState.getString("Nom"));
        prenomEditText.setText(savedInstanceState.getString("Prenom"));
        villeEditText.setText(savedInstanceState.getString("Ville"));
        dateView.setText(savedInstanceState.getString("Date"));
        departementSpinner.setSelection(savedInstanceState.getInt("Departement"));
        imageView.setImageBitmap(savedInstanceState.getParcelable("Image"));

        nomText = preferences.getString("Nom", nomText);
        nomEditText.setText(nomText);

        prenomText = preferences.getString("Prenom", prenomText);
        prenomEditText.setText(prenomText);

        villeText = preferences.getString("Ville", villeText);
        villeEditText.setText(villeText);

        dateText = preferences.getString("Date", dateText);
        dateView.setText(dateText);

        departementIndex = preferences.getInt("Departement", departementIndex);
        departementSpinner.setSelection(departementIndex);

        //Restore the set of Strings to ArrayList
        Set<String> pnSet = null;
        pnSet = preferences.getStringSet("NumerosTelephones", pnSet);
        if (pnSet != null ) {
            mPhoneNumbers = new ArrayList<String>(pnSet);
            setPhonesOnRestore(mPhoneNumbers);
        }
    }

    //onClick method when click on the button "Valider"
    public void onValidate(View view){
        //displaySnackbar();
        Intent intent = new Intent(this, DisplayActivity.class);
        ArrayList<String> phoneNumbers = getPhoneNumbers();
        User user = new User(prenomEditText.getText().toString(), nomEditText.getText().toString(),
                villeEditText.getText().toString(), dateView.getText().toString(),
                departementSpinner.getSelectedItem().toString(), phoneNumbers);
        intent.putExtra("User", user);
        startActivity(intent);
    }

    //onClick method when click on the button "Ajouter un numéro de téléphone"
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void onAddPhoneNumber(View view){
        setPhoneNumbers();
    } // We call the method setPhoneNumbers that is also called in restoring them

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    //onClick method for menu button "Resaisir les champs"
    public void resetAction (MenuItem item) {
        nomEditText.getText().clear();
        prenomEditText.getText().clear();
        villeEditText.getText().clear();
        dateView.setText(null);
        phoneLayout.removeAllViews();
        departementSpinner.setSelection(0);
        imageView.setImageBitmap(null);
    }

    //onClick method for menu button "A propos de la ville de naissance"
    public void searchCity (MenuItem item) {
        if (villeEditText.getText().toString().trim().length() > 0){
            String ville = villeEditText.getText().toString();
            Uri query = Uri.parse("http://fr.wikipedia.org/?search="+ville);
            Intent searchIntent = new Intent(Intent.ACTION_VIEW, query);
            //Start implicit activity
            try {
                startActivity(searchIntent);
            } catch (ActivityNotFoundException e) {
                Log.i("searchCity", "found Exception");
            }
        }
    }

    //onClick method for menu button "Partager la ville de naissance"
    public void sendCity (MenuItem item) {
        if (villeEditText.getText().toString().trim().length() > 0) {
            String ville = villeEditText.getText().toString();
            Intent sendIntent = new Intent(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, ville);
            sendIntent.setType("text/plain");
            String title = getResources().getString(R.string.send_city);
            Intent chooser = Intent.createChooser(sendIntent, title);

            //Start implicit activity
            if (sendIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(chooser);
            }
        }
    }

    //onClick method for button with calendar icon to pick Day of birth
    public void pickDate(View view){
        Intent dateIntent = new Intent(Intent.ACTION_PICK);
        if (dateView.getText().toString().trim().length() > 0){
            dateIntent.putExtra("Date", dateView.getText().toString());
        }
        try {
            startActivityForResult(dateIntent, REQUEST_DATE);
        } catch (ActivityNotFoundException e) {
            Log.i("dateIntent", "found Exception");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // For picking day of birth
        if (requestCode == REQUEST_DATE && resultCode == RESULT_OK) {
            String date = data.getStringExtra("Date");
            dateView.setText(date);
            Log.i("onActivityResult", date);
        }
        //For taking pic and saving it
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bitmap imageBitmap = null;
            try {
                imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoURI);
            } catch (IOException e) {
                e.printStackTrace();
            }
            thumbnail = extractThumbnail(imageBitmap, imageView.getWidth(), imageView.getHeight());
            imageView.setImageBitmap(thumbnail);
            try {
                File thumbnailFile = StorageUtils.createFile(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "thumbnails");
                FileOutputStream fos = new FileOutputStream(thumbnailFile);
                thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.flush();
                fos.close();
                fos = new FileOutputStream(photoFile);
                imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.flush();
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //onClick method for button with camera icon to take pic
    public void onTakePic(View view) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            try {
                photoFile = StorageUtils.createFile(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "pics");
            } catch (IOException ex) {
                Log.i(TAG, "Exception taking pics");
            }

            if (photoFile != null) {
                photoURI = FileProvider.getUriForFile(this,
                        "com.example.myihmapplication.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    //onClick method for button with message icon to comment pic
    public void onCommentPic(View view) {
        //We created a fragment for this purpose
        commentFragment = new CommentPicFragment();
        FragmentTransaction tx = getFragmentManager ().beginTransaction ();
        tx.replace(R.id.myConstraintLayout, commentFragment) ;
        tx.commit ();
    }

    //method used to add phone number ro layout
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void setPhoneNumbers(){
        // if phoneLayout is empty add text "Numéro de Téléphone"
        if (phoneLayout.getChildCount() == 0){
            TextView phoneStaticText = new TextView(this);
            phoneStaticText.setText(R.string.numero_telephone);
            phoneLayout.addView(phoneStaticText);
        }

        // Add an horizantal layout where we put EditText for the phone number, a delete button and a dial one
        LinearLayout phoneNumber = new LinearLayout(this);
        phoneNumber.setOrientation(LinearLayout.HORIZONTAL);

        //Prepare the phoneNumber Layout children
        EditText phoneEditText = new EditText(this);
        phoneEditText.setHint(R.string.numero_telephone_text);
        phoneEditText.setInputType(InputType.TYPE_CLASS_PHONE);

        //Layout parameters for delete and dial buttons
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.height = 100;
        params.width = 100;

        Button phoneDeleteButton = new Button(this);
        phoneDeleteButton.setForeground(this.getResources().getDrawable(R.drawable.ic_delete));
        phoneDeleteButton.setBackground(this.getResources().getDrawable(R.drawable.button_border));
        phoneDeleteButton.setLayoutParams(params);

        //Create empty text view to separate between elements to layout:
        TextView empty1 = new TextView(this);
        empty1.setText("    ");

        TextView empty2 = new TextView(this);
        empty2.setText("    ");

        Button phoneDialButton = new Button(this);
        phoneDialButton.setForeground(this.getResources().getDrawable(R.drawable.ic_call));
        phoneDialButton.setBackground(this.getResources().getDrawable(R.drawable.button_border));
        phoneDialButton.setLayoutParams(params);

        //Define the delete phone number listener
        View.OnClickListener onDeleteListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(phoneLayout.getChildCount() == 2){
                    phoneLayout.removeAllViews();
                }

                if(phoneLayout.getChildCount() > 2){
                    LinearLayout layout = (LinearLayout) v.getParent();
                    phoneLayout.removeView(layout);
                }
            }
        };

        //Define the dial phone number listener
        View.OnClickListener onDialListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (phoneEditText.getText().toString().trim().length() > 0) {
                    String phone = phoneEditText.getText().toString();
                    Intent phoneIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone));

                    // Check if an Activity exists to perform this action.
                    try {
                        startActivity(phoneIntent);
                    } catch (ActivityNotFoundException activityException) {
                        Log.i("Dial", "found exception ");
                    }
                }
            }
        };

        //Set the onClick listeners
        phoneDeleteButton.setOnClickListener(onDeleteListener);
        phoneDialButton.setOnClickListener(onDialListener);

        //Add finally the edit text, delete and dial buttons to their layout
        phoneNumber.addView(phoneEditText);
        phoneNumber.addView(empty1);
        phoneNumber.addView(phoneDeleteButton);
        phoneNumber.addView(empty2);
        phoneNumber.addView(phoneDialButton);

        //Add the phone Number Layout to its general layout
        phoneLayout.addView(phoneNumber);
    }

    //Get the phone numbers from the edit text and put them in an ArrayList<String>
    public ArrayList<String> getPhoneNumbers () {
        ArrayList<String> phoneNumbers = new ArrayList<String> ();
        if (phoneLayout.getChildCount() > 0) {
            for (int i = 1; i < phoneLayout.getChildCount(); i++) {
                LinearLayout phoneNumberLayout = (LinearLayout) phoneLayout.getChildAt(i);
                EditText phoneNumber = (EditText) phoneNumberLayout.getChildAt(0);
                if (phoneNumber.getText().toString().trim().length() >  0) {
                    String phone = String.valueOf(phoneNumber.getText());
                    if (!phoneNumbers.contains(phone)) {
                        phoneNumbers.add(String.valueOf(phoneNumber.getText()));
                    }
                }
            }
        }
        return phoneNumbers;
    }

    //Set the phone nummbers in their layout after getting the phone numbers list in the onRestore/onCreate
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void setPhonesOnRestore(ArrayList<String> phones) {
        if (phones != null) {
            int nbPhones = phones.size();
            for (int i = 0; i < nbPhones; i++) {
                if (phoneLayout.getChildCount() != nbPhones + 1){
                    setPhoneNumbers();
                }
                Log.i("onRestore()", String.valueOf(phoneLayout.getChildCount()));
                LinearLayout phoneNumberLayout = (LinearLayout) phoneLayout.getChildAt(i+1);
                EditText phoneNumber = (EditText) phoneNumberLayout.getChildAt(0);
                phoneNumber.setText(phones.get(i));
            }
        }
    }

    //Snackbar method
    public void displaySnackbar (){
        nomText = nomEditText.getText().toString();
        prenomText = prenomEditText.getText().toString();
        villeText = villeEditText.getText().toString();
        dateText = dateView.getText().toString();
        departementString = departementSpinner.getSelectedItem().toString();
        String textToShow = "Récapitulatif: Nom: " + nomText + " Prénom: " + prenomText
                + " Date de naissance: " + dateText + " Ville de naissance: " + villeText
                + " Département: " + departementString;
        if (phoneLayout.getChildCount() > 0) {
            for (int i = 1; i < phoneLayout.getChildCount(); i++) {
                LinearLayout phoneNumberLayout = (LinearLayout) phoneLayout.getChildAt(i);
                EditText phoneNumber = (EditText) phoneNumberLayout.getChildAt(0);
                textToShow += " Numéro de téléphone " + i + ": "+ phoneNumber.getText().toString();
            }
        }
        Snackbar snackbar = Snackbar.make(findViewById(R.id.myConstraintLayout),
                textToShow, Snackbar.LENGTH_LONG);

        View snackbarView = snackbar.getView();
        TextView textView = (TextView) snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setMaxLines(10); // Change your max lines

        Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout) snackbar.getView();
        layout.setMinimumHeight(300);


        View.OnClickListener onDismissListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar.dismiss();
            }
        };
        snackbar.setAction(R.string.action_text, onDismissListener);
        snackbar.show();
    }

    //onSaveComment method from CommentPicFragment.onSaveCommentInterface
    @Override
    public void onSaveComment() {
        FragmentTransaction tx = getFragmentManager ().beginTransaction ();
        tx.hide(commentFragment) ;
        tx.commit () ;
    }
}