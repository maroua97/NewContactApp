package com.example.myihmapplication;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class User implements Parcelable {
    private String prenom;
    private String nom;
    private String ville;
    private String date;
    private String departement;
    private ArrayList<String> phoneNumbers;

    public User(String prenom, String nom, String ville, String date,
                String departement, ArrayList<String> phoneNumbers) {
        this.prenom = prenom;
        this.nom = nom;
        this.ville = ville;
        this.date = date;
        this.departement = departement;
        this.phoneNumbers = phoneNumbers;
    }

    protected User(Parcel in) {
        prenom = in.readString();
        nom = in.readString();
        ville = in.readString();
        date = in.readString();
        departement = in.readString();
        phoneNumbers = in.createStringArrayList();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public String getPrenom() {
        return prenom;
    }

    public String getNom() {
        return nom;
    }

    public String getVille() {
        return ville;
    }

    public String getDate() {
        return date;
    }

    public String getDepartement() { return  departement; }

    public ArrayList<String> getPhoneNumbers() { return phoneNumbers; }

    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(prenom);
        parcel.writeString(nom);
        parcel.writeString(ville);
        parcel.writeString(date);
        parcel.writeString(departement);
        parcel.writeStringList(phoneNumbers);
    }
}
