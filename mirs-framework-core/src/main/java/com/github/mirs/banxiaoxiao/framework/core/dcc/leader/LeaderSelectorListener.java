package com.github.mirs.banxiaoxiao.framework.core.dcc.leader;


/**
 * @author zcy 2018年9月12日
 */
public interface LeaderSelectorListener {

    /**
     * 竞选成功的leader 凭证，leader失效重新竞选结束后会触发该回调
     *
     * @param voucher 当选leader的凭证，每个listener可和选举器颁发的凭证进行比较，看是否是自己
     */
    public void voteSuccess(String voucher);

}
