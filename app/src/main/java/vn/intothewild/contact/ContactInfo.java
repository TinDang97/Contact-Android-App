package vn.intothewild.contact;

import android.graphics.Bitmap;
import android.net.Uri;

import java.io.InputStream;
import java.util.List;

/**
 * Created by Tin on 4/17/2017.
 */

public class ContactInfo {
    private String id;
    private String name;
    private Bitmap avatar;
    private List<String> phone;
    private List<String> phoneType;
    private List<String> email;
    private List<String> emailType;

    public ContactInfo(String id, String name, Bitmap avatar, List<String> phone) {
        this.id = id;
        this.name = name;
        this.avatar = avatar;
        this.phone = phone;
    }

    public ContactInfo(String id, String name, Bitmap avatar, List<String> phone, List<String> phoneType, List<String> email, List<String> emailType) {
        this.id = id;
        this.name = name;
        this.avatar = avatar;
        this.phone = phone;
        this.phoneType = phoneType;
        this.email = email;
        this.emailType = emailType;
    }

    public List<String> getEmailType() {
        return emailType;
    }

    public void setEmailType(List<String> emailType) {
        this.emailType = emailType;
    }

    public void addPhone(String phone){
        this.phone.add(phone);
    }

    public void addEmail(String email){
        this.email.add(email);
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAvatar(Bitmap avatar) {
        this.avatar = avatar;
    }

    public void setPhone(List<String> phone) {
        this.phone = phone;
    }

    public void setEmail(List<String> email) {
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Bitmap getAvatar() {
        return avatar;
    }

    public List<String> getPhone() {
        return phone;
    }

    public List<String> getEmail() {
        return email;
    }

    public String toString(){
        return name;
    }

    public List<String> getPhoneType() {
        return phoneType;
    }

    public void setPhoneType(List<String> phoneType) {
        this.phoneType = phoneType;
    }
}
