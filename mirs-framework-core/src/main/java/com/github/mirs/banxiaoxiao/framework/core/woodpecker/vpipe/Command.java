package com.github.mirs.banxiaoxiao.framework.core.woodpecker.vpipe;

import com.github.mirs.banxiaoxiao.framework.common.util.UUID;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serializable;
import java.security.Principal;

/**
 * @author zcy 2019年6月4日
 */
public class Command implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -8213646726061859047L;

    private String commandId;

    private String command;

    private Principal principal;

    private int width;

    private int height;

    public Command() {
        this.command = UUID.random19();
    }

    public String getCommandId() {
        return commandId;
    }

    public void setCommandId(String commandId) {
        this.commandId = commandId;
    }

    public Command(Principal principal) {
        this.principal = principal;
    }

    public Command(Principal principal, String command) {
        super();
        this.command = command;
        this.principal = principal;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public Principal getPrincipal() {
        return principal;
    }

    public void setPrincipal(Principal principal) {
        this.principal = principal;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
