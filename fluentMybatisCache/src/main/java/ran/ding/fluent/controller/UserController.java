package ran.ding.fluent.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ran.ding.fluent.dao.intf.UserDao;
import ran.ding.fluent.entity.UserEntity;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author roiding
 * @date 2021/9/2 0002 8:05
 * @Description:
 */
@RestController
@RequestMapping("/user")
public class UserController {
    @Resource
    private UserDao userDao;

    @RequestMapping("/getAll")
    public List<UserEntity> getAll(){
        return userDao.getAll();
    }

    @RequestMapping("/getOneByGuid/{guid}")
    public UserEntity getOneByGuid(@PathVariable String guid){
        return userDao.getOneByGuid(guid);
    }

    @RequestMapping("/addOne")
    public String addOne(){
        UserEntity user=new UserEntity().setGuid("32D4000B-DB67-7EC2-6ED3-29D2911FFEC1")
                .setName("Mccoy, Tarik C.")
                .setPhone("049-018-1017")
                .setCity("Probolinggo")
                .setRegion("East Java")
                .setCvv("436");
        userDao.addUser(user);
        return "新增成功";
    }
    @RequestMapping("/deleteOne")
    public String deleteOne(){
        userDao.deleteOneByGuid("32D4000B-DB67-7EC2-6ED3-29D2911FFEC1");
        return "删除成功";
    }
}
