package com.alphalaneous;

public class InjectionStatus {

    boolean success;
    String error;
    int errorCode;

    public InjectionStatus(boolean success, String error, int errorCode){
        this.success = success;
        this.error = error;
        this.errorCode = errorCode;

    }

    public static InjectionStatus isSuccess(){
        return new InjectionStatus(true, null, 0);
    }

    public static InjectionStatus isFailure(String error, int errorCode){
        return new InjectionStatus(false, error, errorCode);
    }

    public String getError(){
        return error;
    }

    public int getErrorCode(){
        return errorCode;
    }

    public boolean isSuccessful(){
        return success;
    }
}
