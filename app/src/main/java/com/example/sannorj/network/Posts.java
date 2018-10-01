package com.example.sannorj.network;


public class Posts
{
    public String uid, time, date, postimage, description, profileimage, fullname,category,name,out;

    public Posts()
    {

    }

    public Posts(String uid, String time, String date, String postimage, String description, String profileimage, String fullname,String category,String name,String out) {
        this.uid = uid;
        this.time = time;
        this.date = date;
        this.postimage = postimage;
        this.description = description;
        this.profileimage = profileimage;
        this.fullname = fullname;
        this.category= category;
        this.name=name;
        this.out=out;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPostimage() {
        return postimage;
    }

    public void setPostimage(String postimage) {
        this.postimage = postimage;
    }
/**------------------------------------------------------------**/
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSelection() {
        return category;
    }

    public void setSelection(String category) {
        this.category = category;
    }



    public String getOutcome() {
        return out;
    }

    public void setOutcome(String out) {
        this.out = out;
    }


 /**-----------------------------------------------------------**/

    public String getProfileimage() {
        return profileimage;
    }

    public void setProfileimage(String profileimage) {

        this.profileimage = profileimage;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {

        this.fullname = fullname;
    }
}
