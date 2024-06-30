package com.Tahiliani.service;

import com.Tahiliani.domain.VerificationType;
import com.Tahiliani.model.User;
import com.Tahiliani.model.VerificationCode;
import com.Tahiliani.repository.VerificationCodeRepository;
import com.Tahiliani.utils.OtpUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

public class VerificationCodeServiceImpl implements VerificationCodeService {
    @Autowired
    private VerificationCodeRepository verificationCodeRepository;
    @Override
    public VerificationCode sendVerificationCode(User user, VerificationType verificationType) {
        VerificationCode verificationCode1=new VerificationCode();
        verificationCode1.setOtp(OtpUtils.generateOtp());
        verificationCode1.setVerificationType(verificationType);
        verificationCode1.setUser(user);
        return verificationCodeRepository.save(verificationCode1);
    }


    @Override
    public VerificationCode getVerificationCodeById(Long id) throws Exception {
        Optional<VerificationCode> verificationCode=verificationCodeRepository.findById(id);
        if (verificationCode.isPresent()){
            return verificationCode.get();
        }
        throw new Exception("verification code not found");
    }

    @Override
    public VerificationCode getVerificationCodeByUser(Long userId) {
        return verificationCodeRepository.findByUserId(userId);
    }

    @Override
    public void deleteVerificationCodeById(VerificationCode verificationCode) {
        verificationCodeRepository.delete(verificationCode);

    }
}
