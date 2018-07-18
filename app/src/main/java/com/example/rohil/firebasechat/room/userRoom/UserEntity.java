package com.example.rohil.firebasechat.room.userRoom;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by Rohil on 6/7/2017.
 */


@Entity (tableName = "userRoom")
public class UserEntity {

    @PrimaryKey
    String uid;

    @ColumnInfo(name = "name")
    String name;

    @ColumnInfo(name = "phone")
    String phone;

    @ColumnInfo(name = "status")
    String status;

    @ColumnInfo (name = "profilePic")
    String profilePic;

    public UserEntity(String uid, String name, String phone,String status, String profilePic) {
        this.uid = uid;
        this.name = name;
        this.phone = phone;
        this.status = status;
        this.profilePic = profilePic;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUid() {

        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }
}
