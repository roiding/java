package ran.ding.redis;

import lombok.Data;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.RedisZSetCommands;
import org.springframework.data.redis.core.*;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.Serializable;
import java.util.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MyConfigRedisTemplateTest {
 
    @Autowired
    private RedisTemplate redisTemplate; //在MyRedisConfig文件中配置了redisTemplate的序列化之后， 客户端也能正确显示键值对了
 
    @Test
    public void testStr(){
        redisTemplate.opsForValue().set("wujinxing", "lige");
    }
    @Test
    public void testHash(){
        Map<String, Object> map = new HashMap<>();
        for (int i=0; i<10; i++){
            User user = new User();
            user.setId(i);
            user.setName(String.format("测试%d", i));
            user.setAge(i+10);
            map.put(String.valueOf(i),user);
        }
        redisTemplate.opsForHash().putAll("测试", map);
    }
    @Test
    public void testList(){
        //链表从左到右的顺序为v10, v8, v6, v4, v2
        redisTemplate.opsForList().leftPushAll("list1", "v2","v4","v6","v8","v10");
        //链表从左到右的顺序为v1, v3, v5, v7, v9
        redisTemplate.opsForList().rightPushAll("list2", "v1","v3","v5","v7","v9");

        //绑定list2操作链表
        BoundListOperations listOps = redisTemplate.boundListOps("list2");
        Object result1 = listOps.rightPop();//从右边弹出一个成员

        Object result2 = listOps.index(1); //获取定位元素, 下标从0开始

        listOps.leftPush("v0"); //从左边插入链表

        Long size = listOps.size();//求链表长

        List element = listOps.range(0, size-2); //求链表区间成员

    }

    @Test
    public void testSet(){
        //重复的元素不会被插入
        redisTemplate.opsForSet().add("set1", "v1","v1","v3","v5","v7","v9");
        redisTemplate.opsForSet().add("set2", "v2","v4","v6","v5","v10","v10");

        //绑定sert1集合操作
        BoundSetOperations setOps = redisTemplate.boundSetOps("set1");
        setOps.add("v11", "v13");
        setOps.remove("v1", "v3");
        Set set = setOps.members();//返回所有元素

        Long size = setOps.size();//求成员数

        Set inner = setOps.intersect("set2"); //求交集
        setOps.intersectAndStore("set2", "set1_set2");//求交集并用新的集合保存

        Set diff = setOps.diff("set2"); //求差集
        setOps.diffAndStore("set2","set1-set2"); //求差集并用新的集合保存

        Set union = setOps.union("set2"); //求并集
        setOps.unionAndStore("set2", "set1=set2"); //求并集并用新的集合保存

    }
    @Test
    public void testZset(){
        Set<ZSetOperations.TypedTuple<String>> typedTupleSet = new HashSet<>();
        for(int i=1; i<=9; i++){
            //分数
            double score = i*0.1;
            //创建一个TypedTuple对象, 存入值和分数
            ZSetOperations.TypedTuple typedTuple = new DefaultTypedTuple<String>("value" + i, score);
            typedTupleSet.add(typedTuple);
        }

        //往有序集合插入元素
        redisTemplate.opsForZSet().add("zset1", typedTupleSet);
        //绑定zset1有序集合操作
        BoundZSetOperations<String, String> zSetOps = redisTemplate.boundZSetOps("zset1");
        zSetOps.add("value10", 0.26);
        Set<String> setRange = zSetOps.range(1,6);

        //按分数排序获取有序集合
        Set<String> setScore = zSetOps.rangeByScore(0.2, 0.6);

        //定义值范围
        RedisZSetCommands.Range range = new RedisZSetCommands.Range();
        range.gt("value3"); //大于value3
        //range.gte("value3"); //大于等于value3
        //range.lt("value8"); //小于value8
        range.lte("value8"); //小于等于value8

        //按值排序, 注意这个排序是按字符串排序
        Set<String> setLex = zSetOps.rangeByLex(range);

        zSetOps.remove("value9", "value2");  //删除元素
        Double score = zSetOps.score("value8"); //求分数

        //在下标区间 按分数排序, 同时返回value和score
        Set<ZSetOperations.TypedTuple<String>> rangeSet = zSetOps.rangeWithScores(1,6);

        //在下标区间 按分数排序, 同时返回value和score
        Set<ZSetOperations.TypedTuple<String>> scoreSet = zSetOps.rangeByScoreWithScores(1,6);

        //按从大到小排序
        Set<String> reverseSet = zSetOps.reverseRange(2, 8);
    }

    @Test
    public void testMulti(){
        redisTemplate.opsForValue().set("key1", 1);
        //不能确定 是不是因为没有FunctionInterface的原因，intelij报错 google无答案
        List list = (List) redisTemplate.execute((RedisOperations operations)->{
            operations.watch("key1");
            operations.multi(); //开始事务
            operations.opsForValue().set("key2", 2);
            operations.opsForValue().increment("key1", 1);
            //获取的值将为null, 因为redis知识把命令放入队列
            Object value2 = operations.opsForValue().get("key2");
            System.out.println("命令在队列, 所以value2为null [ " + value2 + " ] ");
            operations.opsForValue().set("key3", 3);
            Object value3 = operations.opsForValue().get("key3");
            System.out.println("命令在队列, 所以value3为null [ " + value3 + " ] ");

            //执行exce()命令,将先判断key1是否在监控后被其他客户端修改过, 如果是则不执行事务, 否则就执行事务
            return operations.exec(); //提交事务
        });
        System.out.println(list);
    }

    @Data
    static class User implements Serializable {
    private int id;
    private String name;
    private long age;
    }
}