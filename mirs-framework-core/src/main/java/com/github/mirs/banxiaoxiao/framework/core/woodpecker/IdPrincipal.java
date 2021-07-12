package com.github.mirs.banxiaoxiao.framework.core.woodpecker;

import java.io.Serializable;
import java.security.Principal;

import com.github.mirs.banxiaoxiao.framework.common.util.StringUtil;

/**
 * @author zcy 2020年2月28日
 */
public class IdPrincipal implements Principal, Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -3243227271883543906L;

    private String id;

    public IdPrincipal(String id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return this.id;
    }

    @Override
    public int hashCode() {
        return this.id == null ? 0 : this.id.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof IdPrincipal) {
            IdPrincipal temp = (IdPrincipal) obj;
            return StringUtil.equals(getName(), temp.getName());
        } else {
            return false;
        }
    }

    public String toString() {
        return this.id;
    }
}
