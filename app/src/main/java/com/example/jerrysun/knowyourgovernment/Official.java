package com.example.jerrysun.knowyourgovernment;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jerrysun on 4/30/17.
 */

public class Official implements Parcelable {

    private String office;
    private String name;
    private String address;
    private String party;
    private String phone;
    private String url;
    private String email;
    private String photoUrl;
    private Map<String,String> channels;

    public Official(){
        this.office = "office";
        this.name = "name";
        this.address = "address";
        this.party = "party";
        this.phone = "phone";
        this.url = "url";
        this.email = "email";
        this.photoUrl = "photoUrl";
        this.channels = new HashMap<>();
        channels.put("type","id");
    }

    public Official(String office,
                    String name,
                    String address,
                    String party,
                    String phone,
                    String url,
                    String email,
                    String photoUrl,
                    Map<String,String> channels){

        this.office = office;
        this.name = name;
        this.address = address;
        this.party = party;
        this.phone = phone;
        this.url = url;
        this.email = email;
        this.photoUrl = photoUrl;
        this.channels = channels;
    }

    public void setOffice(String office) {
        this.office = office;
    }

    public String getOffice() {
        return office;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public void setParty(String party) {
        this.party = party;
    }

    public String getParty() {
        return party;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhone() {
        return phone;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setChannels(Map<String,String> channels) {
        this.channels = channels;
    }

    public Map<String, String> getChannels() {
        return channels;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(office);
        dest.writeString(name);
        dest.writeString(address);
        dest.writeString(party);
        dest.writeString(phone);
        dest.writeString(url);
        dest.writeString(email);
        dest.writeString(photoUrl);
        dest.writeMap(channels);
    }

    public static final Parcelable.Creator<Official> CREATOR = new Parcelable.Creator<Official>(){
        public Official createFromParcel(Parcel in){
            return new Official(in);
        }

        public Official[] newArray(int size){
            return new Official[size];
        }
    };

    private Official(Parcel in){
        this.office = in.readString();
        this.name = in.readString();
        this.address = in.readString();
        this.party = in.readString();
        this.phone = in.readString();
        this.url = in.readString();
        this.email = in.readString();
        this.photoUrl = in.readString();
        this.channels = (Map<String, String>) in.readHashMap(getClass().getClassLoader());
    }
}
