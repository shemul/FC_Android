package com.csform.android.fcloud.data;

/**
 * allow this class to distingues the data of a review
 * Created by shemul on 26/09/2015.
 */
public class Review {

    public String _id ;
    public String f_id ;
    public String f_name ;
    public String f_email ;
    public String f_dept;
    public String f_status ;


    public Review(String _id, String f_id, String f_name, String f_email, String f_status , String f_dept_n) {
        this._id = _id;
        this.f_id = f_id;
        this.f_name = f_name;
        this.f_email = f_email;
        this.f_status = f_status;
        this.f_dept = f_dept_n;
    }
}
