package cn.edu.swpu.cins.event.analyse.platform.service;

import cn.edu.swpu.cins.event.analyse.platform.exception.BaseException;
import cn.edu.swpu.cins.event.analyse.platform.model.persistence.User;

/**
 * Created by muyi on 17-5-23.
 */
public interface AuthService {

    /**
     * 登录方法
     * @param username
     * @param password
     * @return
     * @throws BaseException
     */
    public String userLogin(String username,String password) throws BaseException;


}
