package com.Tahiliani.service;

import com.Tahiliani.domain.VerificationType;
import com.Tahiliani.model.User;
import com.Tahiliani.model.VerificationCode;

public interface VerificationCodeService {
    VerificationCode sendVerificationCode(User user, VerificationType verificationType);



    VerificationCode getVerificationCodeById(Long id) throws Exception;
    VerificationCode getVerificationCodeByUser(Long userId);
   
    void deleteVerificationCodeById(VerificationCode verificationCode);


}
