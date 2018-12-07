package com.example.aldiandika.voltagemonitoring.util;

import android.os.AsyncTask;
import android.widget.Toast;

import com.example.aldiandika.voltagemonitoring.dataTransfer;
import com.example.aldiandika.voltagemonitoring.server.Server;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Create extends AsyncTask<String, String, String>{

    JSONParser jsonParser = new JSONParser();
    String url_create = Server.serverURL + "/store";

    public static final String TAG_SUCCESS = "success";
    public static final String TAG_V_SATU = "voltage_satu";
    public static final String TAG_V_DUA = "voltage_dua";
    public static final String TAG_V_TIGA = "voltage_tiga";
    public static final String TAG_V_EMPAT = "voltage_empat";
    public static final String TAG_I = "arus";
    public static final String TAG_P = "daya";

    public static String valueVsatu = "";
    public static String valueVdua = "";
    public static String valueVtiga = "";
    public static String valueVempat = "";
    public static String valueI = "";
    public static String valueP = "";

    public int status_kirimDB; //0 = "Gagal database", 1 = "Gagal Koneksi", 2 = "Sukses"

    protected void switchVar(){
        dataTransfer dataTransfer = new dataTransfer();
        valueVsatu = dataTransfer.value_VSatu;
        valueVdua = dataTransfer.value_VDua;
        valueVtiga = dataTransfer.value_VTiga;
        valueVempat = dataTransfer.value_VEmpat;
        valueI = dataTransfer.value_I;
        valueP = dataTransfer.value_P;
    }

    @Override
    protected String doInBackground(String... strings) {
        switchVar();
        List<NameValuePair> params = new ArrayList<>();

        params.add(new BasicNameValuePair(TAG_V_SATU,valueVsatu));
        params.add(new BasicNameValuePair(TAG_V_DUA,valueVdua));
        params.add(new BasicNameValuePair(TAG_V_TIGA,valueVtiga));
        params.add(new BasicNameValuePair(TAG_V_EMPAT,valueVempat));
        params.add(new BasicNameValuePair(TAG_I,valueI));
        params.add(new BasicNameValuePair(TAG_P,valueP));

        JSONObject json = jsonParser.makeHttpRequest(url_create, "GET", params);

        try{
            int success = json.getInt(TAG_SUCCESS);

            if(success==0){
                return "gagal database";
            }
        }catch (JSONException e){
            e.printStackTrace();
            return "gagal koneksi";
        }

        return "sukses";
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        if(s.equalsIgnoreCase("gagal database")){
            status_kirimDB = 0;
        }else if(s.equalsIgnoreCase("gagal koneksi")){
            status_kirimDB = 1;
        }else if(s.equalsIgnoreCase("sukses")){
            status_kirimDB = 2;
        }

    }
}
