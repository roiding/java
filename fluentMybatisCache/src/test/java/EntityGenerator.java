import cn.org.atool.fluent.mybatis.annotation.TableId;
import cn.org.atool.generator.FileGenerator;
import cn.org.atool.generator.annotation.Table;
import cn.org.atool.generator.annotation.Tables;
import org.junit.jupiter.api.Test;

/**
 * @author roiding
 * @date 2021/9/1 17:03
 * @Description:
 */
public class EntityGenerator {
    public static final String url = "jdbc:mysql://192.168.189.132:3306/test?useUnicode=true&characterEncoding=utf8";


    @Test
    public void generate() throws Exception {
        FileGenerator.build(Empty.class);
    }

    @Tables(
            // 设置数据库连接信息
            url = url, username = "root", password = "maodou38",
            // 设置entity类生成src目录, 相对于 user.dir
            srcDir = "src/main/java",
            // 设置base目录
            basePack = "ran.ding.fluent",
            // 设置dao接口和实现的src目录, 相对于 user.dir
            daoDir = "src/main/java",
            //按数据库定义顺序
            alphabetOrder=false,
            // 设置哪些表要生成Entity文件
            tables = {@Table(value = {"user"},seqName = "guid")}
    )
    static class Empty {
    }
}
