package com.dbvertex.dilsayproject.session;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import java.util.ArrayList;
import java.util.HashMap;

public class SessionManager {
    private SharedPreferences pref;
    private Editor editor;
    private static final String PREF_NAME = "DilSayPref";
    public static final String IS_LOGIN = "IsLoggedIn";
    private static final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";


    public static final String KEY_EMAIL = "emp_email";
    public static final String KEY_FACEBOOKID = "user_id";
    public static final String KEY_USERNAME = "username";
    public static final String KEY_FACEBOOKDP = "profilepic";


    public static final String KEY_MOBILENUMBER = "mobile";
    public static final String KEY_COUNTRYCODE = "countrycode";
    public static final String KEY_DATEOFBIRTH = "dob";
    public static final String KEY_GENDER = "gender";
    public static final String KEY_USERID = "userid";

    public static final String KEY_IMAGE1="im1";
    public static final String KEY_IMAGE2="im2";
    public static final String KEY_IMAGE3="im3";
    public static final String KEY_IMAGE4="im4";
    public static final String KEY_IMAGE5="im5";
    public static final String KEY_FROM="from";

    public static final String KEY_STEP = "step";
    public static final String KEY_INTEREST = "interest";

    public static final String KEY_LOOKINGFOR = "lookingfor";
    public static final String KEY_COMMUNITY = "community";
    public static final String KEY_CAREER = "career";
    public static final String KEY_EDUCATION = "education";
    public static final String KEY_HEIGHT= "height";
    public static final String KEY_RAISEDIN = "raisedin";
    public static final String KEY_RELIGION = "religion";
    public static final String KEY_FOODPREF = "foodpref";
    public static final String KEY_SMOKE = "smoke";
    public static final String KEY_DRINK = "drink";
    public static final String KEY_LOCATION = "location";
    public static final String KEY_REWINDID = "rewindid";
    public static final String KEY_LATITUDE = "latitude";
    public static final String KEY_LONGITUDE = "longitude";
    public static final String KEY_DESCRIPTION="desscription";
    public static final String KEY_DETAILPAGE_USERID = "USRID";
    public static final String KEY_FILTER_RELIGION_NAME="religionname";
    public static final String KEY_FILTER_MIN_HEIGHT="minh";
    public static final String KEY_FILTER_MAX_HEIGHT="maxh";
    public static final String KEY_FILTER_COMMUNITY="community";
    public static final String KEY_FILTER_EDUCATION="education";
    public static final String KEY_FILTER_CAREER="career";
    public static final String KEY_FILTER_RAISEDIN="raisedin";
    public static final String KEY_FILTER_LOOKINGFOR="lookingfor";
    public static final String KEY_FILTER_STARTAGE="startage";
    public static final String KEY_FILTER_ENDAGE="endage";


    public void setFilterStartEndAge(String startAge,String endAge) {
        editor.putString(KEY_FILTER_STARTAGE, startAge);
        editor.putString(KEY_FILTER_ENDAGE, endAge);

        editor.commit();
    }

    public HashMap<String, String> getFilterStartEndAge() {
        HashMap<String, String> user = new HashMap<>();
        user.put(KEY_FILTER_STARTAGE, pref.getString(KEY_FILTER_STARTAGE, ""));
        user.put(KEY_FILTER_ENDAGE, pref.getString(KEY_FILTER_ENDAGE, ""));

        return user;
    }

    public void setFilterLookingFor(String filterLookingfor) {
        editor.putString(KEY_FILTER_LOOKINGFOR, filterLookingfor);
        editor.commit();
    }

    public HashMap<String, String> getFilterLookingFor() {
        HashMap<String, String> user = new HashMap<>();
        user.put(KEY_FILTER_LOOKINGFOR, pref.getString(KEY_FILTER_LOOKINGFOR, ""));
        return user;
    }


    public void setFilterRaisedIn(String filterRaisedIn) {
        editor.putString(KEY_FILTER_RAISEDIN, filterRaisedIn);
        editor.commit();
    }

    public HashMap<String, String> getFilterRaisedIn() {
        HashMap<String, String> user = new HashMap<>();
        user.put(KEY_FILTER_RAISEDIN, pref.getString(KEY_FILTER_RAISEDIN, ""));
        return user;
    }

    public void setFilterCareer(String filterCareer) {
        editor.putString(KEY_FILTER_CAREER, filterCareer);
        editor.commit();
    }

    public HashMap<String, String> getFilterCareer() {
        HashMap<String, String> user = new HashMap<>();
        user.put(KEY_FILTER_CAREER, pref.getString(KEY_FILTER_CAREER, ""));
        return user;
    }


    public void setFilterReligion(String filterReligion) {
        editor.putString(KEY_FILTER_RELIGION_NAME, filterReligion);
        editor.commit();
    }

    public HashMap<String, String> getFilterReligion() {
        HashMap<String, String> user = new HashMap<>();
        user.put(KEY_FILTER_RELIGION_NAME, pref.getString(KEY_FILTER_RELIGION_NAME, ""));
        return user;
    }

    public void setFilterCommunity(String minage) {
        editor.putString(KEY_FILTER_COMMUNITY, minage);
        editor.commit();
    }

    public HashMap<String, String> getFilterCommunity() {
        HashMap<String, String> user = new HashMap<>();
        user.put(KEY_FILTER_COMMUNITY, pref.getString(KEY_FILTER_COMMUNITY, ""));
        return user;
    }

    public void setFilterEducation(String maxage) {
        editor.putString(KEY_FILTER_EDUCATION, maxage);
        editor.commit();
    }

    public HashMap<String, String> getFilterEducation() {
        HashMap<String, String> user = new HashMap<>();
        user.put(KEY_FILTER_EDUCATION, pref.getString(KEY_FILTER_EDUCATION, ""));
        return user;
    }
    public void setFilterMaxHeight(String maxHeight) {
        editor.putString(KEY_FILTER_MAX_HEIGHT, maxHeight);
        editor.commit();
    }

    public HashMap<String, String> getFilterMaxHeight() {
        HashMap<String, String> user = new HashMap<>();
        user.put(KEY_FILTER_MAX_HEIGHT, pref.getString(KEY_FILTER_MAX_HEIGHT, ""));
        return user;
    }
    public void setFilterMinHeight(String minHeight) {
        editor.putString(KEY_FILTER_MIN_HEIGHT, minHeight);
        editor.commit();
    }

    public HashMap<String, String> getFilterMinHeight() {
        HashMap<String, String> user = new HashMap<>();
        user.put(KEY_FILTER_MIN_HEIGHT, pref.getString(KEY_FILTER_MIN_HEIGHT, ""));
        return user;
    }


    public SessionManager(Context context) {
        int PRIVATE_MODE = 0;
        pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }



    public void setProfileDetailUserId(String userid) {
        editor.putString(KEY_DETAILPAGE_USERID, userid);
        editor.commit();
    }

    public HashMap<String, String> getProfileDetailUserId() {
        HashMap<String, String> user = new HashMap<>();
        user.put(KEY_DETAILPAGE_USERID, pref.getString(KEY_DETAILPAGE_USERID, ""));
        return user;
    }





    public void setLookingfor(String lookingfor) {
        editor.putString(KEY_LOOKINGFOR, lookingfor);
        editor.commit();
    }

    public HashMap<String, String> getLookingFor() {
        HashMap<String, String> user = new HashMap<>();
        user.put(KEY_LOOKINGFOR, pref.getString(KEY_LOOKINGFOR, ""));
        return user;
    }

    public void setCommunity(String community) {
        editor.putString(KEY_COMMUNITY, community);
        editor.commit();
    }

    public HashMap<String, String> getCommunity() {
        HashMap<String, String> user = new HashMap<>();
        user.put(KEY_COMMUNITY, pref.getString(KEY_COMMUNITY, ""));
        return user;
    }

    public void setCareer(String career) {
        editor.putString(KEY_CAREER, career);
        editor.commit();
    }

    public HashMap<String, String> getCareer() {
        HashMap<String, String> user = new HashMap<>();
        user.put(KEY_CAREER, pref.getString(KEY_CAREER, ""));
        return user;
    }

    public void setEducation(String education) {
        editor.putString(KEY_EDUCATION,education );
        editor.commit();
    }

    public HashMap<String, String> getEducation() {
        HashMap<String, String> user = new HashMap<>();
        user.put(KEY_EDUCATION, pref.getString(KEY_EDUCATION, ""));
        return user;
    }

    public void setHeight(String height) {
        editor.putString(KEY_HEIGHT,height );
        editor.commit();
    }

    public HashMap<String, String> getHeight() {
        HashMap<String, String> user = new HashMap<>();
        user.put(KEY_HEIGHT, pref.getString(KEY_HEIGHT, ""));
        return user;
    }


    public void setRaisedIn(String raisedin) {
        editor.putString(KEY_RAISEDIN,raisedin );
        editor.commit();
    }

    public HashMap<String, String> getRaisedIn() {
        HashMap<String, String> user = new HashMap<>();
        user.put(KEY_RAISEDIN, pref.getString(KEY_RAISEDIN, ""));
        return user;
    }


    public void setReligion(String religion ) {
        editor.putString(KEY_RELIGION, religion);
        editor.commit();
    }

    public HashMap<String, String> getReligion() {
        HashMap<String, String> user = new HashMap<>();
        user.put(KEY_RELIGION, pref.getString(KEY_RELIGION, ""));
        return user;
    }

    public void setInterest(String interest) {
        editor.putString(KEY_INTEREST,interest );
        editor.commit();
    }

    public HashMap<String, String> getInterest() {
        HashMap<String, String> user = new HashMap<>();
        user.put(KEY_INTEREST, pref.getString(KEY_INTEREST, ""));
        return user;
    }


    public void setLifestyle(String foodpref,String smoke,String drink)
    {
        editor.putString(KEY_FOODPREF,foodpref );
        editor.putString(KEY_SMOKE,smoke );
        editor.putString(KEY_DRINK, drink);
        editor.commit();
    }

    public HashMap<String, String> getLifestyle() {
        HashMap<String, String> user = new HashMap<>();
        user.put(KEY_FOODPREF, pref.getString(KEY_FOODPREF, ""));
        user.put(KEY_SMOKE, pref.getString(KEY_SMOKE, ""));
        user.put(KEY_DRINK, pref.getString(KEY_DRINK, ""));
        return user;
    }




    public void setLocation(String location,String latitude,String longitude) {
        editor.putString(KEY_LOCATION, location);
        editor.putString(KEY_LATITUDE, latitude);
        editor.putString(KEY_LONGITUDE, longitude);
        editor.commit();
    }

    public HashMap<String, String> getLocation() {
        HashMap<String, String> user = new HashMap<>();
        user.put(KEY_LOCATION, pref.getString(KEY_LOCATION, ""));
        user.put(KEY_LATITUDE, pref.getString(KEY_LATITUDE, ""));
        user.put(KEY_LONGITUDE, pref.getString(KEY_LONGITUDE, ""));

        return user;
    }






    public void setLoginSession() {
        editor.putBoolean(IS_LOGIN, true);
        editor.commit();
    }

    public HashMap<String, String> getLoginSession() {
        HashMap<String, String> user = new HashMap<>();
        return user;
    }

    public void logoutUser() {
        editor.clear();
        editor.commit();
    }


    public boolean isFirstTimeLaunch() {
        return pref.getBoolean(IS_FIRST_TIME_LAUNCH, false);
    }

    public void setFirstTimeLaunch(boolean b) {
        editor.putBoolean(IS_FIRST_TIME_LAUNCH, b);
        editor.apply();
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(IS_LOGIN, false);
    }




    public void setFacebookData(String email,  String username, String facebookid,String facebookDp) {

        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_USERNAME, username);
        editor.putString(KEY_FACEBOOKID, facebookid);
        editor.putString(KEY_FACEBOOKDP, facebookDp);

        editor.commit();
    }

    // Get stored session data
    public HashMap<String, String> getFacebookData() {
        HashMap<String, String> user = new HashMap<>();
        user.put(KEY_EMAIL, pref.getString(KEY_EMAIL, ""));
        user.put(KEY_USERNAME, pref.getString(KEY_USERNAME, ""));
        user.put(KEY_FACEBOOKID, pref.getString(KEY_FACEBOOKID, ""));
        user.put(KEY_FACEBOOKDP, pref.getString(KEY_FACEBOOKDP, ""));

        return user;
    }


    public void setDescription(String description) {

        editor.putString(KEY_DESCRIPTION, description);
        editor.commit();
    }

    // Get stored session data
    public HashMap<String, String> getDescription() {
        HashMap<String, String> user = new HashMap<>();
        user.put(KEY_DESCRIPTION, pref.getString(KEY_DESCRIPTION, ""));
        return user;
    }


    public void setImage2(String image2,String from) {

        editor.putString(KEY_IMAGE2, image2);
        editor.putString(KEY_FROM, from);

        editor.commit();
    }

    // Get stored session data
    public HashMap<String, String> getImage2() {
        HashMap<String, String> user = new HashMap<>();
        user.put(KEY_IMAGE2, pref.getString(KEY_IMAGE2, ""));
        user.put(KEY_FROM, pref.getString(KEY_FROM, ""));

        return user;
    }


    public void setImage3(String image3,String from) {

        editor.putString(KEY_IMAGE3, image3);
        editor.putString(KEY_FROM, from);

        editor.commit();
    }

    // Get stored session data
    public HashMap<String, String> getImage3() {
        HashMap<String, String> user = new HashMap<>();
        user.put(KEY_IMAGE3, pref.getString(KEY_IMAGE3, ""));
        user.put(KEY_FROM, pref.getString(KEY_FROM, ""));

        return user;
    }

    public void setImage4(String image1,String from) {

        editor.putString(KEY_IMAGE4, image1);
        editor.putString(KEY_FROM, from);

        editor.commit();
    }

    // Get stored session data
    public HashMap<String, String> getImage4() {
        HashMap<String, String> user = new HashMap<>();
        user.put(KEY_IMAGE4, pref.getString(KEY_IMAGE4, ""));
        user.put(KEY_FROM, pref.getString(KEY_FROM, ""));

        return user;
    }


    public void setImage5(String image5,String from) {

        editor.putString(KEY_IMAGE5, image5);
        editor.putString(KEY_FROM, from);

        editor.commit();
    }

    // Get stored session data
    public HashMap<String, String> getImage5() {
        HashMap<String, String> user = new HashMap<>();
        user.put(KEY_IMAGE5, pref.getString(KEY_IMAGE5, ""));
        user.put(KEY_FROM, pref.getString(KEY_FROM, ""));

        return user;
    }


    public void setDetails(String dob,String gender) {
        editor.putString(KEY_DATEOFBIRTH, dob);
        editor.putString(KEY_GENDER, gender);

        editor.commit();
    }

    public HashMap<String, String> getDetails() {
        HashMap<String, String> user = new HashMap<>();
        user.put(KEY_DATEOFBIRTH, pref.getString(KEY_DATEOFBIRTH, ""));
        user.put(KEY_GENDER, pref.getString(KEY_GENDER, ""));

        return user;
    }

    public void setMobileNumber(String mobileNumber,String countrycode) {
        editor.putString(KEY_MOBILENUMBER, mobileNumber);
        editor.putString(KEY_COUNTRYCODE, countrycode);
        editor.commit();
    }

    public HashMap<String, String> getMobileNumber() {
        HashMap<String, String> user = new HashMap<>();
        user.put(KEY_COUNTRYCODE, pref.getString(KEY_COUNTRYCODE, ""));
        user.put(KEY_MOBILENUMBER, pref.getString(KEY_MOBILENUMBER, ""));
        return user;
    }


    public void setStep(String step) {
        editor.putString(KEY_STEP, step);
        editor.commit();
    }

    public HashMap<String, String> getStep() {
        HashMap<String, String> user = new HashMap<>();
        user.put(KEY_STEP, pref.getString(KEY_STEP, ""));

        return user;
    }


    public void setUserId(String userId) {
        editor.putString(KEY_USERID, userId);
        editor.commit();
    }

    public HashMap<String, String> getUserId() {
        HashMap<String, String> user = new HashMap<>();
        user.put(KEY_USERID, pref.getString(KEY_USERID, ""));

        return user;
    }

    public void setRewindId(String rewindId) {
        editor.putString(KEY_REWINDID, rewindId);
        editor.commit();
    }

    public HashMap<String, String> getRewindId() {
        HashMap<String, String> user = new HashMap<>();
        user.put(KEY_REWINDID, pref.getString(KEY_REWINDID, ""));

        return user;
    }








}