package com.alphalaneous.Exceptions;

public class OSNotSupportedException extends RuntimeException{

    public OSNotSupportedException(String errorMessage){
        super(errorMessage);
    }

}
