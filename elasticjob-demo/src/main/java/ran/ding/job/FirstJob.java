package ran.ding.job;

import org.apache.shardingsphere.elasticjob.api.ShardingContext;
import org.apache.shardingsphere.elasticjob.simple.job.SimpleJob;
import org.springframework.stereotype.Component;

@Component
public class FirstJob implements SimpleJob {
    @Override
    public void execute(ShardingContext shardingContext) {
        switch (shardingContext.getShardingItem()) {
            case 0:
                // do something by sharding item 0
                System.out.println(0);
                // int a = 1 / 0;
                break;
            case 1:
                // do something by sharding item 1
                System.out.println(1);
                break;
            case 2:
                // do something by sharding item 2
                System.out.println(2);
                break;
            // case n: ...
        }
    }
}
