package com.smart.eduvanz.session;

import org.json.JSONObject;

public class AppStorage {
    private static AppStorage objAppStorage = null;
    public String apiUrl = "https://eduvanz-api.herokuapp.com/api/";
    //public String apiUrl = "http://192.168.1.9:3000/api/";
    public String cust_name = "";
    public String cust_id="";
    public int logId = 0;
    public String otp;
    public String mob;
    public int isEditMob = 0;
    JSONObject jsonObject;
    public int apiCallStatus;
    public static AppStorage getInstance()
    {
        if (objAppStorage == null) {
            objAppStorage = new AppStorage();
        }
        return objAppStorage;
    }
    public String getApiUrl()
    {
        return this.apiUrl;
    }
    public int getLogId()
    {
        return this.logId;
    }
    public void setLogId(int logId)
    {
        this.logId = logId;
    }
    public String getOtp()
    {
        return this.otp;
    }
    public void setOtp(String otp)
    {
        this.otp = otp;
    }
    public void setMob(String mob)
    {
        this.mob = mob;
    }
    public String getMob()
    {
        return this.mob;
    }
    public void setCust_id(String cust_id)
    {
        this.cust_id = cust_id;
    }
    public String getCust_id()
    {
        return this.cust_id;
    }
    public void setCust_name(String cust_name)
    {
        this.cust_name = cust_name;
    }
    public String getCust_name()
    {
        return this.cust_name;
    }
    public void setJsonObject(JSONObject jsonObject)
    {
        this.jsonObject = jsonObject;
    }
    public JSONObject getJsonObject()
    {
        return this.jsonObject;
    }
    public void setApiCallStatus(int status)
    {
        this.apiCallStatus = status;
    }
    public int getApiCallStatus()
    {
        return this.apiCallStatus;
    }
    public int getIsEditMob()
    {
        return isEditMob;
    }
    public void setIsEditMob(int flag)
    {
        this.isEditMob = flag;
    }
}
