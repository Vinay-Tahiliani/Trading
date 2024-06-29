package com.Tahiliani.service;

import com.Tahiliani.model.TwoFactorAuth;
import com.Tahiliani.model.TwoFactorOTP;
import com.Tahiliani.model.User;

public interface TwoFactorOtpService {
    TwoFactorOTP createTwoFactorOtp(User user,String otp,String jwt);
    TwoFactorOTP findByUser(Long userid);
    TwoFactorOTP findBYId(String id);
    boolean verifyTwoFactorOtp(TwoFactorOTP twoFactorOTP,String otp);
    void deleteTwoFactorOtp(TwoFactorOTP twoFactorOTP);
}
