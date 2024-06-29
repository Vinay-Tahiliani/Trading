package com.Tahiliani.service;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private JavaMailSender javaMailSender;
    public void sendVerificationOtpEmail(String email,String otp){
        ///CODE FROM HERE
    }
}
