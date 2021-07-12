package com.github.mirs.banxiaoxiao.framework.core.dcc.leader;

import com.github.mirs.banxiaoxiao.framework.common.util.StringUtil;
import com.github.mirs.banxiaoxiao.framework.common.util.UUID;
import com.github.mirs.banxiaoxiao.framework.core.dcc.AbstractDccApp;
import com.github.mirs.banxiaoxiao.framework.core.dcc.DccClient;
import com.github.mirs.banxiaoxiao.framework.core.dcc.NodeListener;

import java.util.*;

/**
 * 基于zk的leader选举器，非公平选举，看运气
 *
 * @author zcy 2018年9月12日
 */
public class LeaderSelector extends AbstractDccApp implements NodeListener {

    private Map<String, Post> posts;

    public LeaderSelector() {
        this(null);
    }

    public LeaderSelector(DccClient dccClient) {
        super("leaders");
        this.posts = new HashMap<String, Post>();
        setDccClient(dccClient);
    }

    /**
     * 颁发一张竞选凭证
     *
     * @return
     */
    public String issuedVoucher() {
        return UUID.random19();
    }

    /**
     * 注册报名 参加竞选，返回成功当选的竞选凭证
     *
     * @param postName
     * @param voucher
     * @param listener
     * @return
     */
    public synchronized String vote(String postName, String voucher, LeaderSelectorListener listener) {
        Post post = this.posts.get(postName);
        if (post == null) {
            post = new Post(postName);
            this.posts.put(postName, post);
        }
        Campaigner campaigner = new Campaigner(voucher, listener);
        post.addCampaigner(campaigner);
        String path = genVotePostPath(postName);
        getDccClient().registNodeListener(path, this);
        return doVote(post);
    }

    private String doVote(Post post) {
        Campaigner campaigner = post.chooseCampaigner();
        if (campaigner != null) {
            String path = genVotePostPath(post.getPostName());
            getDccClient().writernxTemp(path, campaigner.getVoucher());
            String voucher = getDccClient().readStringData(path);
            if (!StringUtil.isBlank(voucher)) {
                post.notifyVoteSuccess(voucher);
                return voucher;
            } else {
                return doVote(post);
            }
        }
        return null;
    }

    private String genVotePostPath(String postName) {
        return genChildPath(postName);
    }

    @Override
    public synchronized void onDestroy(String postName) {
        postName = super.pickLeafPath(postName);
        // 如果岗位leader失效，重新竞选
        Post post = this.posts.get(postName);
        if (post != null) {
            doVote(post);
        }
    }

    @Override
    public void onConstruct(String nodeName) {
    }

    @Override
    public void onRefreshChildren(List<String> children) {
    }

    static class Post {

        private String postName;

        private List<Campaigner> campaigners;

        public Post(String postName) {
            this.postName = postName;
        }

        public String getPostName() {
            return postName;
        }

        public void setPostName(String postName) {
            this.postName = postName;
        }

        public List<Campaigner> getCampaigners() {
            return campaigners;
        }

        public void setCampaigners(List<Campaigner> campaigners) {
            this.campaigners = campaigners;
        }

        public void addCampaigner(Campaigner campaigner) {
            if (this.campaigners == null) {
                this.campaigners = new ArrayList<Campaigner>();
            }
            this.campaigners.add(campaigner);
        }

        public Campaigner chooseCampaigner() {
            if (this.campaigners == null || this.campaigners.size() == 0) {
                return null;
            }
            Random random = new Random();
            int n = random.nextInt(this.campaigners.size());
            return this.campaigners.get(n);
        }

        public void notifyVoteSuccess(String voucher) {
            if (this.campaigners != null) {
                for (Campaigner campaigner : this.campaigners) {
                    campaigner.notifyVoteSuccess(voucher);
                    ;
                }
            }
        }
    }

    static class Campaigner {

        private LeaderSelectorListener listener;

        private String voucher;

        public Campaigner(String voucher, LeaderSelectorListener listener) {
            this.listener = listener;
            this.voucher = voucher;
        }

        public LeaderSelectorListener getListener() {
            return listener;
        }

        public void setListener(LeaderSelectorListener listener) {
            this.listener = listener;
        }

        public String getVoucher() {
            return voucher;
        }

        public void notifyVoteSuccess(String voucher) {
            if (this.listener != null) {
                this.listener.voteSuccess(voucher);
            }
        }
    }
}
