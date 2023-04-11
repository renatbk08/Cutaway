package com.example.cutaway;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;

public class BusinessCard implements Parcelable {
    private boolean isSelected = false;
    private byte[] decodedBytes;
    private Bitmap bitmap;
    private String firstName;
    private String lastName;
    private String company;
    private String phone;
    private String email;
    private Map<String, String> socialNetworks;


    public BusinessCard(Boolean isSelected, Bitmap bitmap, String firstName, String lastName, String company, String phone, String email) {
        this.isSelected = isSelected;
        this.bitmap = bitmap;
        this.firstName = firstName;
        this.lastName = lastName;
        this.company = company;
        this.phone = phone;
        this.email = email;
        this.socialNetworks = new HashMap<>();
    }
    public BusinessCard(Boolean isSelected, String firstName, String lastName, String company, String phone, String email) {
        this.isSelected = isSelected;
        this.firstName = firstName;
        this.lastName = lastName;
        this.company = company;
        this.phone = phone;
        this.email = email;
        this.socialNetworks = new HashMap<>();
    }
    protected BusinessCard(Parcel in) {
        firstName = in.readString();
        lastName = in.readString();
        company = in.readString();
        phone = in.readString();
        email = in.readString();
    }

    public static final Creator<BusinessCard> CREATOR = new Creator<BusinessCard>() {
        @Override
        public BusinessCard createFromParcel(Parcel in) {
            return new BusinessCard(in);
        }

        @Override
        public BusinessCard[] newArray(int size) {
            return new BusinessCard[size];
        }
    };

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isSelected() {return isSelected;}

    public Bitmap getBitmap() {return bitmap;}

    public void setBitmap(Bitmap bitmap) {this.bitmap = bitmap;}

    public byte[] getBytearray() { return decodedBytes;}

    public void setBytearray(byte[] bytearray) {this.decodedBytes = bytearray;}
    public String share() {
        StringBuilder sb = new StringBuilder();
        sb.append("Name: ").append(firstName).append(" ").append(lastName).append("\n");
        sb.append("Company: ").append(company).append("\n");
        sb.append("Phone: ").append(phone).append("\n");
        sb.append("Email: ").append(email).append("\n");
        sb.append("Social Networks: ").append("\n");
        for (Map.Entry<String, String> entry : socialNetworks.entrySet()) {
            sb.append("- ").append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }
        return sb.toString();
    }

    @Override
    public int describeContents() {
        return 0;
    }
    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(firstName);
        dest.writeString(lastName);
        dest.writeString(company);
        dest.writeString(phone);
        dest.writeString(email);
    }
}
