package cn.edu.swpu.cins.event.analyse.platform.model.threadhold;

import cn.edu.swpu.cins.event.analyse.platform.model.persistence.User;
import org.springframework.stereotype.Component;

/**
 * Created by LLPP on 2017/8/18.
 */
@Component
public class UserHolder {

    private ThreadLocal<User> user = new ThreadLocal<>();

    public void put(User user){
        this.user.set(user);
    }

    public User get(){
        return this.user.get();
    }
}
