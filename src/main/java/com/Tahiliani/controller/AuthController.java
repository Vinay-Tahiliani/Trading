package com.Tahiliani.controller;

import com.Tahiliani.config.JwtProvider;
import com.Tahiliani.model.TwoFactorOTP;
import com.Tahiliani.model.User;
import com.Tahiliani.repository.UserRepository;
import com.Tahiliani.response.AuthResponse;
import com.Tahiliani.service.CustomUserDetailsService;
import com.Tahiliani.service.EmailService;
import com.Tahiliani.service.TwoFactorOtpService;
import com.Tahiliani.utils.OtpUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.security.Security;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CustomUserDetailsService customUserDetailsService;
    @Autowired
    private TwoFactorOtpService twoFactorOtpService;
    @Autowired
    private EmailService emailService;
    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> register(@RequestBody User user) throws Exception {

        User isEmailExist=userRepository.findByEmail(user.getEmail());
        if (isEmailExist!=null){
            throw new Exception("email is already used with another account");
        }
        User newUser=new User();
        newUser.setEmail(user.getEmail());
        newUser.setPassword(user.getPassword());
        newUser.setFullName(user.getFullName());
        User savedUser=userRepository.save(newUser);
        Authentication auth=new UsernamePasswordAuthenticationToken(user.getEmail(),user.getPassword());

        SecurityContextHolder.getContext().setAuthentication(auth);
        String jwt= JwtProvider.generateToken(auth);
        AuthResponse res=new AuthResponse();
        res.setJwt(jwt);
        res.setStatus(true);
        res.setMessage("register success");

        return new ResponseEntity<>(res, HttpStatus.CREATED);
    }

    @PostMapping("/signin")
    public ResponseEntity<AuthResponse> login(@RequestBody User user) throws Exception {

        String userName=user.getEmail();
        String password=user.getPassword();

        Authentication auth=authenticate(userName,password);

        SecurityContextHolder.getContext().setAuthentication(auth);
        String jwt= JwtProvider.generateToken(auth);
        User authUser =userRepository.findByEmail(userName);
        if (user.getTwoFactorAuth().isEnabled()){
            AuthResponse res =new AuthResponse();
            res.setMessage("Two Factor Auth is Enable");
            res.setTwoFactorAuthEnable(true);
            String otp= OtpUtils.generateOtp();
            TwoFactorOTP oldTwoFactorOTP=twoFactorOtpService.findByUser(authUser.getId());
            if(oldTwoFactorOTP!=null){
                twoFactorOtpService.deleteTwoFactorOtp(oldTwoFactorOTP);
            }
            TwoFactorOTP newTwoFactorOTP=twoFactorOtpService.createTwoFactorOtp(authUser,otp,jwt);
            emailService.sendVerificationOtpEmail(userName,otp);
            res.setSession(newTwoFactorOTP.getId());
            return new ResponseEntity<>(res,HttpStatus.ACCEPTED);
        }

        AuthResponse res=new AuthResponse();
        res.setJwt(jwt);
        res.setStatus(true);
        res.setMessage("login success");

        return new ResponseEntity<>(res, HttpStatus.CREATED);
    }

    private Authentication authenticate(String userName, String password) {
        UserDetails userDetails=customUserDetailsService.loadUserByUsername(userName);
        if (userDetails==null){
            throw new BadCredentialsException("invalid Username");
        }
        if (!password.equals(userDetails.getPassword())){
            throw new BadCredentialsException("invalid Password");
        }
        return new UsernamePasswordAuthenticationToken(userDetails,password,userDetails.getAuthorities());

    }

    @PostMapping("/two-factor/otp/{otp}")
    public ResponseEntity<AuthResponse> verifySiginOtp(@PathVariable String otp,@RequestParam String id) throws Exception {
        TwoFactorOTP twoFactorOTP=twoFactorOtpService.findBYId(id);
        if (twoFactorOtpService.verifyTwoFactorOtp(twoFactorOTP,otp)){
            AuthResponse res=new AuthResponse();
            res.setMessage("Two factor authenticcation verified");
            res.setTwoFactorAuthEnable(true);
            res.setJwt(twoFactorOTP.getJwt());
            return new ResponseEntity<>(res,HttpStatus.OK);

        }
        throw new Exception("invalid otp");

    }

}
