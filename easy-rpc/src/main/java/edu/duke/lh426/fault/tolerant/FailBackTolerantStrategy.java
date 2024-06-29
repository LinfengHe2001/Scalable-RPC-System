package edu.duke.lh426.fault.tolerant;

import edu.duke.lh426.model.RpcResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * 降级到其他服务 - 容错策略
 */
@Slf4j
public class FailBackTolerantStrategy implements TolerantStrategy {

    @Override
    public RpcResponse doTolerant(Map<String, Object> context, Exception e) {
        log.info("降级处理异常", e);
        return new RpcResponse();
    }
}
