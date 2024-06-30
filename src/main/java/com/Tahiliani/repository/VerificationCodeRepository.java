package com.Tahiliani.repository;

import com.Tahiliani.model.VerificationCode;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VerificationCodeRepository extends JpaRepository<VerificationCode,Long> {
    public VerificationCode findByUserId(Long userID);
}
