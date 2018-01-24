package com.happyshop.controller.backend;

import com.happyshop.common.Const;
import com.happyshop.common.ResponseCode;
import com.happyshop.common.ServerResponse;
import com.happyshop.pojo.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;

/**
 * Created by Administrator on 2018/1/23.
 */
@Controller
public class PageManageController {

    @RequestMapping("/{page}")
    public String showPage(HttpSession session,@PathVariable String page) {
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return "login";
        }
        return page;
    }
}
