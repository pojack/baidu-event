package cn.edu.swpu.cins.event.analyse.platform.controller;

import cn.edu.swpu.cins.event.analyse.platform.dao.UserDao;
import cn.edu.swpu.cins.event.analyse.platform.enums.UserEnum;
import cn.edu.swpu.cins.event.analyse.platform.exception.BaseException;
import cn.edu.swpu.cins.event.analyse.platform.model.persistence.User;
import cn.edu.swpu.cins.event.analyse.platform.model.view.JwtAuthenticationRequest;
import cn.edu.swpu.cins.event.analyse.platform.model.view.JwtAuthenticationResponse;
import cn.edu.swpu.cins.event.analyse.platform.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

/**
 * Created by muyi on 17-5-23.
 */
@RestController
@RequestMapping("/event")
public class AuthController {

    @Value("${jwt.header}")
    private String tokenHeader;

    @Autowired
    private AuthService authService;

    @Autowired
    private UserDao userDao;

    @PostMapping("/login")
    public ResponseEntity<?> createUserToken(
            @RequestBody JwtAuthenticationRequest request) throws AuthenticationException, BaseException {

        User user = userDao.queryByName(request.getUsername());
        if (user == null) {
            return new ResponseEntity<>(UserEnum.NO_USER.getMsg(), HttpStatus.OK);
        }
        final String token = authService.userLogin(request.getUsername(),request.getPassword());
        if(token!=null) {
            String username = user.getUsername();
            String role = user.getRole().substring(5);
            return new ResponseEntity<>(new JwtAuthenticationResponse(token, username, role),HttpStatus.OK);
        }
        return new ResponseEntity<>(UserEnum.WRONG_PASSWORD.getMsg(),HttpStatus.OK);
    }


}
