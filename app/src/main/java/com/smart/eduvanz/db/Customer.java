package com.smart.eduvanz.db;

public class Customer {
    int id;
    String accId;
    int isFaceEnabled = 0;
    int isFingerEnabled = 0;
    int isMPinEnabled = 0;
    String loginDate;
    int loginStatus;
    String logoutDate;
    String mobile;
    public Customer(){}
    public Customer(int id,String accId,String mobile,int isFaceEnabled,int isFingerEnabled,int isMPinEnabled,String loginDate,String logoutDate,int loginStatus)
    {
        this.id = id;
        this.accId = accId;
        this.isFaceEnabled = isFaceEnabled;
        this.isFingerEnabled = isFingerEnabled;
        this.isMPinEnabled = isMPinEnabled;
        this.logoutDate = logoutDate;
        this.loginDate = loginDate;
        this.loginStatus = loginStatus;
        this.mobile = mobile;
    }
    public int getId()
    {
        return this.id;
    }
    public String getAccId()
    {
        return this.accId;
    }
    public void setAccId(String accId)
    {
        this.accId = accId;
    }
    public int getIsFaceEnabled()
    {
        return isFaceEnabled;
    }
    public void setIsFaceEnabled(int flag)
    {
        this.isFaceEnabled = flag;
    }
    public int getIsFingerEnabled()
    {
        return this.isFingerEnabled;
    }
    public void setIsFingerEnabled(int flag)
    {
        this.isFingerEnabled = flag;
    }
    public int getIsMPinEnabled()
    {
        return this.isMPinEnabled;
    }
    public void setIsMPinEnabled(int flag)
    {
        this.isMPinEnabled = flag;
    }
    public void setLoginStatus(int status)
    {
        this.loginStatus = status;
    }
    public int getLoginStatus()
    {
        return this.loginStatus;
    }
    public void setLoginDate(String d1)
    {

    }
    public void setMobile(String mobile)
    {
        this.mobile = mobile;
    }
    public String getMobile()
    {
        return this.mobile;
    }
}
