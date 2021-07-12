/**
 *
 */
package com.github.mirs.banxiaoxiao.framework.core.drpc.filter;

import com.github.mirs.banxiaoxiao.framework.common.util.UUID;
import org.apache.dubbo.common.Constants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.*;

/**
 * @author erxiao 2017年3月30日
 */
@Activate(group = Constants.PROVIDER, order = -4000)
public class DrpcContextFilter implements Filter {

    /**
     * (non-Javadoc)
     *
     * @see Filter#invoke(Invoker, Invocation)
     */
    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        Object requestId = RpcContext.getContext().getAttachment(com.github.mirs.banxiaoxiao.framework.core.config.Constants.DRPC_REQUESTID);
        if (requestId == null) {
            RpcContext.getContext().setAttachment(com.github.mirs.banxiaoxiao.framework.core.config.Constants.DRPC_REQUESTID, UUID.random19());
        }
        return invoker.invoke(invocation);
    }

}
