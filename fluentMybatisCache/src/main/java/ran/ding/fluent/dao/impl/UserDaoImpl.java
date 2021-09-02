package ran.ding.fluent.dao.impl;

import org.springframework.cache.annotation.*;
import org.springframework.stereotype.Repository;
import ran.ding.fluent.dao.base.UserBaseDao;
import ran.ding.fluent.dao.intf.UserDao;
import ran.ding.fluent.entity.UserEntity;
import ran.ding.fluent.wrapper.UserQuery;

import java.util.List;

/**
 * UserDaoImpl: 数据操作接口实现
 * <p>
 * 这只是一个减少手工创建的模板文件
 * 可以任意添加方法和实现, 更改作者和重定义类名
 * <p/>@author Powered By Fluent Mybatis
 */
@Repository
@CacheConfig(cacheNames = "user")
public class UserDaoImpl extends UserBaseDao implements UserDao {

    @Override
    @Cacheable(key = "'userList'",unless = "#result.size() == 0")
    public List<UserEntity> getAll() {
        UserQuery query = new UserQuery();
        return mapper.listEntity(query);
    }

    @Override
    @Cacheable(key = "'user- '.concat(#guid)",unless = "#result == null")
    public UserEntity getOneByGuid(String guid) {
        UserQuery query=new UserQuery()
                .where()
                .guid().eq(guid)
                .end();
        return mapper.findOne(query);
    }

    @Override
    @CachePut(key = "'user- '.concat(#entity.guid)",unless = "#result == null")
    public UserEntity addUser(UserEntity entity) {
        mapper.save(entity);
        return entity;
    }

    @Override
    @Caching(evict = {@CacheEvict(key = "'user- '.concat(#guid)"),@CacheEvict(key = "'userList'")})
    public void deleteOneByGuid(String guid) {
        UserQuery query=new UserQuery()
                .where()
                .guid().eq(guid)
                .end();
        mapper.delete(query);
    }
}
