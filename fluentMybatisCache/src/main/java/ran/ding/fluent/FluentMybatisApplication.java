package ran.ding.fluent;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * @author roiding
 * @date 2021/9/2 0002 8:03
 * @Description:
 */
@SpringBootApplication
@MapperScan({"ran.ding.fluent.mapper"})
public class FluentMybatisApplication {
    public static void main(String[] args) {
        SpringApplication.run(FluentMybatisApplication.class,args);
    }
}
