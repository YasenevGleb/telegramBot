package com.example.telegrambots.ability;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

public class Verifiered implements HostnameVerifier {

    public boolean verify(String arg0, SSLSession arg1) {
        return true;
    }



}