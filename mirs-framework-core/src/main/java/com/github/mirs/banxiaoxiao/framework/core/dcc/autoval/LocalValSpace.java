package com.github.mirs.banxiaoxiao.framework.core.dcc.autoval;


public class LocalValSpace extends ValSpace {

    /** */
    private static final long serialVersionUID = -1430988668519640962L;

    @Override
    public String getNamespace() {
        return AppDefaultSpace.namespace() +"_local";
    }
}
