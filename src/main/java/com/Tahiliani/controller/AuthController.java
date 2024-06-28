package com.Tahiliani.controller;

import com.Tahiliani.config.JwtProvider;
import com.Tahiliani.model.User;
import com.Tahiliani.repository.UserRepository;
import com.Tahiliani.response.AuthResponse;
import com.Tahiliani.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Security;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CustomUserDetailsService customUserDetailsService;
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

}
