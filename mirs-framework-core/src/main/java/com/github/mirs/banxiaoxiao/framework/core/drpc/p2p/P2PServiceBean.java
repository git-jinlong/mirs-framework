package com.github.mirs.banxiaoxiao.framework.core.drpc.p2p;

import com.github.mirs.banxiaoxiao.framework.core.drpc.spring.DServiceBean;

/**
 * @author zcy 2019年4月2日
 * @param <T>
 */
public class P2PServiceBean<T> extends DServiceBean<T> {
 
    /** */
    private static final long serialVersionUID = -413093119647775308L;

    public P2PServiceBean() {
        super();
        setGroupLoaderClass(ClientIdGroupLoader.class);
    }
}
