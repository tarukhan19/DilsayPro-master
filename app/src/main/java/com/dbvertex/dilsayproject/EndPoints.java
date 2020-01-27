package com.dbvertex.dilsayproject;

import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;

public class EndPoints
{

    public static final String BASE_URL ="https://dilsay.app/webservice/";
    public static final String LOGIN= BASE_URL+"Register/login";
    public static final String SIGNUP= BASE_URL+"Register/user_registration";
    public static final String OTP_VERIFY= BASE_URL+"Register/verify_otp";
    public static final String UPDATE_MOBILE_NO= BASE_URL+"Register/update_mobile";
    public static final String UPLOAD_PROFILE_IMAGE= BASE_URL+"Register/upload_profile_image";
    public static final String LOADLIST= BASE_URL+"Register/get_detail_list";
    public static final String LOADCOMMUNITYLIST= BASE_URL+"user/get_community_list";
    public static final String LOADCAREERLIST= BASE_URL+"user/get_career_list";
    public static final String LOADEDUCATIONLIST= BASE_URL+"user/get_education_list";
    public static final String LOADHEIGHT= BASE_URL+"user/get_height_list";
    public static final String LOADRAISEDIN= BASE_URL+"user/get_raised_in_list";
    public static final String LOADRELIGION= BASE_URL+"user/get_religion_list";
    public static final String INTERESELIST= BASE_URL+"user/get_intrest_list";
    public static final String LIFESTYLE_LIST= BASE_URL+"user/get_lifestyle_list";
    public static final String LOCATION_LIST= BASE_URL+"user/get_location_list";
    public static final String UPLOAD_LOCDESC= BASE_URL+"Register/select_location";
    public static final String UPLOAD_INTEREST= BASE_URL+"Register/choose_intrest";
    public static final String LOGOUT = BASE_URL+"Register/logout";
    public static final String CONTACT_US =BASE_URL+"Register/contact_us" ;
    public static final String DELETE_ACCOUNT=BASE_URL+"Register/delete_account";
    public static final String ABOUTUS=BASE_URL+"Register/about_us";
    public static final String TERMS_CONDITION=BASE_URL+"Register/terms_condition";
    public static final String PRIVACY_POLICY="https://dilsay.app/Api/privacy_policy";
    public static final String PLANS=BASE_URL+"Register/get_plan_list";
    public static final String PROMOCODE = BASE_URL+"Register/apply_promo_code";
    public static final String DASHBOARD = BASE_URL+"/Register/dashboard";
    public static final String GETQUOTES = BASE_URL+"/Register/getquotes";
    public static final String PLANSUBMIT = BASE_URL+"/Register/subscribe_plan";
    public static final String FAQ = BASE_URL+"/Register/faq_list";
    public static final String LIKE_PROFILE = BASE_URL+"/Like/like_profile";
    public static final String DISLIKE_PROFILE = BASE_URL+"/Like/dislike_profile";
    public static final String THINKLATER_PROFILE = BASE_URL+"/Like/think_later_profile";
    public static final String REDO_PROFILE = BASE_URL+"/Register/rewind";
    public static final String UPDATE_PROFILE = BASE_URL+"/user/update_user_profile";

    public static final String LISTING = BASE_URL+"/register/get_like_dislike_think_list";
    public static final String VERIFYINSTA = BASE_URL+"/Register/verify_insta";
    public static final String LOADLOOKINGFOR = BASE_URL+"/user/get_looking_for_list";
    public static final String PROFILEDETAIL = BASE_URL+"/Register/get_profile_detail";
    public static final String REPORTABUSE = BASE_URL+"/Register/report_abuse";
    public static final String CHAT_SEND= BASE_URL+"Register/chat_add";
    //sender_id , receiver_id , message , image
    public static final String CHAT_LIST= BASE_URL+"Register/chat_list";
    //user_id
    public static final String CHAT_LOAD= BASE_URL+"Register/chat_load";
    //sender_id , receiver_id
    public static final String FILTER= BASE_URL+"Filter/free_user_filter";

    public String forProductPost(String urlString, JSONObject params, byte[][] photoArray)
    {

        HttpURLConnection connection = null;
        DataOutputStream outputStream = null;
        InputStream inputStream = null;
        String twoHyphens = "--";
        String boundary = "*****" + Long.toString(System.currentTimeMillis()) + "*****";
        String lineEnd = "\r\n";
        String result = "";
        String filename=Long.toString(System.currentTimeMillis());

        try
        {
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Connection", "Keep-Alive");
            System.setProperty("http.keepAlive", "false");
            connection.setRequestProperty("User-Agent", "Android Multipart HTTP Client 1.0");
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
            outputStream = new DataOutputStream(connection.getOutputStream());

            if (photoArray != null)
            {
                for (byte arr[] : photoArray)
                {
                    outputStream.writeBytes(twoHyphens + boundary + lineEnd);
                    outputStream.writeBytes("Content-Disposition: form-data; name=\"profile_image" +  "\"; filename=\"ph" + filename + ".jpg\"" + lineEnd);
                    outputStream.writeBytes("Content-Transfer-Encoding: binary" + lineEnd);
                    outputStream.writeBytes(lineEnd);
                    for (byte b : arr)
                    {
                        outputStream.write(b);
                    }
                    outputStream.writeBytes(lineEnd);
                }
            }

            // Upload POST Data
            Iterator<String> keys = params.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                String value = params.get(key).toString();
                outputStream.writeBytes(twoHyphens + boundary + lineEnd);
                outputStream.writeBytes("Content-Disposition: form-data; name=\"" + key + "\"" + lineEnd);
                outputStream.writeBytes("Content-Type: text/plain" + lineEnd);
                outputStream.writeBytes(lineEnd);
                outputStream.writeBytes(value);
                outputStream.writeBytes(lineEnd);
            }

            outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

            inputStream = connection.getInputStream();

            result = this.convertStreamToString(inputStream);
            inputStream.close();
            outputStream.flush();
            outputStream.close();

            return result;
        }
        catch (Exception e) {
            e.printStackTrace();
            Log.e("REg Error ", e.getMessage());
        }
        return result;
    }




    public String forProdPost(String urlString,  byte[] profilepic)
    {

        HttpURLConnection connection = null;
        DataOutputStream outputStream = null;
        InputStream inputStream = null;

        String twoHyphens = "--";
        String boundary = "*****" + Long.toString(System.currentTimeMillis()) + "*****";
        String lineEnd = "\r\n";

        String result = "";
        String filename=Long.toString(System.currentTimeMillis());
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        ////Log.e("videolist1",videoList+"");
        ////Log.e("photoArray1",photoArray+"");

        try {
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();

            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Connection", "Keep-Alive");
            System.setProperty("http.keepAlive", "false");
            connection.setRequestProperty("User-Agent", "Android Multipart HTTP Client 1.0");
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

            outputStream = new DataOutputStream(connection.getOutputStream());
            if (profilepic != null) {
                outputStream.writeBytes(twoHyphens + boundary + lineEnd);
                outputStream.writeBytes("Content-Disposition: form-data; name=\"profile_image\"; filename=\"image.png\"" + lineEnd);
                //outputStream.writeBytes("Content-Type: " + fileMimeType + lineEnd);
                outputStream.writeBytes("Content-Transfer-Encoding: binary" + lineEnd);

                Log.e("profilepic",profilepic+"");

                outputStream.writeBytes(lineEnd);

                for (byte b : profilepic) {
                    outputStream.write(b);
                }

                outputStream.writeBytes(lineEnd);
            }




//            // Upload POST Data
//            Iterator<String> keys = params.keys();
//            while (keys.hasNext()) {
//                String key = keys.next();
//                String value = params.get(key).toString();
//
//                outputStream.writeBytes(twoHyphens + boundary + lineEnd);
//                outputStream.writeBytes("Content-Disposition: form-data; name=\"" + key + "\"" + lineEnd);
//                outputStream.writeBytes("Content-Type: text/plain" + lineEnd);
//                outputStream.writeBytes(lineEnd);
//                outputStream.writeBytes(value);
//                outputStream.writeBytes(lineEnd);
//            }

            outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

            inputStream = connection.getInputStream();

            result = this.convertStreamToString(inputStream);
            inputStream.close();
            outputStream.flush();
            outputStream.close();

            return result;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("REg Error ", e.getMessage());
        }
        return result;
    }

    private String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }


    public String forProfile(String freelancerPersonalProfileUrl, JSONObject ob)
    {

        HttpURLConnection connection = null;
        DataOutputStream outputStream = null;
        InputStream inputStream = null;

        String twoHyphens = "--";
        String boundary = "*****" + Long.toString(System.currentTimeMillis()) + "*****";
        String lineEnd = "\r\n";

        String result = "";
        String filename=Long.toString(System.currentTimeMillis());
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;


        try {
            URL url = new URL(freelancerPersonalProfileUrl);
            connection = (HttpURLConnection) url.openConnection();

            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("User-Agent", "Android Multipart HTTP Client 1.0");
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

            outputStream = new DataOutputStream(connection.getOutputStream());


//            for (String arr : interestsentrray)
//            {
//
//                outputStream.writeBytes(twoHyphens + boundary + lineEnd);
//                outputStream.writeBytes("Content-Disposition: form-data; name=\"intrests[]" +  "\"" + lineEnd);
//                outputStream.writeBytes(lineEnd);
//                outputStream.writeBytes(arr);
//                outputStream.writeBytes(lineEnd);
//
//
//            }



            // Upload POST Data
            Iterator<String> keys = ob.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                String value = ob.get(key).toString();
                outputStream.writeBytes(twoHyphens + boundary + lineEnd);
                outputStream.writeBytes("Content-Disposition: form-data; name=\"" + key + "\"" + lineEnd);
                outputStream.writeBytes("Content-Type: text/plain" + lineEnd);
                outputStream.writeBytes(lineEnd);
                outputStream.writeBytes(value);
                outputStream.writeBytes(lineEnd);
            }

            outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

            inputStream = connection.getInputStream();

            result = this.convertStreamToString(inputStream);
            inputStream.close();
            outputStream.flush();
            outputStream.close();

            return result;
        } catch (Exception e) {
            Log.e("REg Error ", e.getMessage());
            e.printStackTrace();
        }
        return result;

    }

}
