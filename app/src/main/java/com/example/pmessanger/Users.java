package com.example.pmessanger;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.firebase.Timestamp;
import java.util.Date;

public class Users implements Parcelable {
    private String phone;
    private String userName;
    private Timestamp createdTimestamp;
    private String userId;
    private String profilePic;
    private String fcmToken;

    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    public Users() {
        // Default constructor required for calls to DataSnapshot.getValue(Users.class)
    }

    public Users(String phone, String userName, String userId, Timestamp createdTimestamp) {
        this.phone = phone;
        this.userName = userName;
        this.createdTimestamp = createdTimestamp;
        this.userId = userId;
    }

    protected Users(Parcel in) {
        phone = in.readString();
        userName = in.readString();
        userId = in.readString();
        profilePic = in.readString();
        long timestampMillis = in.readLong();
        createdTimestamp = new Timestamp(new Date(timestampMillis));
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(phone);
        dest.writeString(userName);
        dest.writeString(userId);
        dest.writeString(profilePic);
        dest.writeLong(createdTimestamp.toDate().getTime());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Users> CREATOR = new Creator<Users>() {
        @Override
        public Users createFromParcel(Parcel in) {
            return new Users(in);
        }

        @Override
        public Users[] newArray(int size) {
            return new Users[size];
        }
    };

    // Getters and Setters

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Timestamp getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(Timestamp createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }
}
