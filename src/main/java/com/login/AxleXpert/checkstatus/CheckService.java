package com.login.AxleXpert.checkstatus;


import org.springframework.stereotype.Service;

@Service
public class CheckService {

    public String health(){
            return "healthy";
    }

    public String greeting(){
        return "welcome to the Axlexpert";
    }

}
