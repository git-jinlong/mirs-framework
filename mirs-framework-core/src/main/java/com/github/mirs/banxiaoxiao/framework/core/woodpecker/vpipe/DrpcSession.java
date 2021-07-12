package com.github.mirs.banxiaoxiao.framework.core.woodpecker.vpipe;

import java.io.IOException;
import java.io.Serializable;
import java.security.Principal;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

import org.crsh.shell.Shell;
import org.crsh.shell.ShellProcessContext;
import org.crsh.shell.ShellResponse;
import org.crsh.text.Color;
import org.crsh.text.Screenable;
import org.crsh.text.Style;

import com.github.mirs.banxiaoxiao.framework.core.log.TComLogs;

/**
 * @author zcy 2019年6月4日
 */
public class DrpcSession implements ShellProcessContext, Serializable {

    /** */
    private static final long serialVersionUID = 1604989202552832072L;

    private Principal principal;

    private Shell shell;

    private long lastUpdateTime;

    private int width;

    private int height;

    private List<String> result = new ArrayList<String>();

    private StringBuilder buffer = new StringBuilder();

    private static final EnumMap<Color, String> COLOR_MAP = new EnumMap<Color, String>(Color.class);
    static {
        COLOR_MAP.put(Color.black, "#000");
        COLOR_MAP.put(Color.blue, "#0000AA");
        COLOR_MAP.put(Color.cyan, "#00AAAA");
        COLOR_MAP.put(Color.green, "#00AA00");
        COLOR_MAP.put(Color.magenta, "#AA00AA");
        COLOR_MAP.put(Color.white, "#AAAAAA");
        COLOR_MAP.put(Color.yellow, "#AAAA00");
        COLOR_MAP.put(Color.red, "#AA0000");
    }

    /** . */
    private Style style = Style.reset;

    public DrpcSession(Principal principal) {
        super();
        this.principal = principal;
    }

    public Principal getPrincipal() {
        return principal;
    }

    public void setPrincipal(Principal principal) {
        this.principal = principal;
    }

    public Shell getShell() {
        return shell;
    }

    public void setShell(Shell shell) {
        this.shell = shell;
    }

    public long getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(long lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    @Override
    public int hashCode() {
        return this.principal == null ? 0 : this.principal.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof DrpcSession)) {
            return false;
        }
        DrpcSession other = (DrpcSession) obj;
        return other.getPrincipal().equals(getPrincipal());
    }

    public void refresh() {
        this.lastUpdateTime = System.currentTimeMillis();
    }

    public void appendResult(String result) {
        this.result.add(result);
    }

    public List<String> getResult() {
        return result;
    }

    public List<String> getResultAndClean() {
        List<String> temp = result;
        result = new ArrayList<String>();
        return temp;
    }

    public void setResult(List<String> result) {
        this.result = result;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    @Override
    public boolean takeAlternateBuffer() throws IOException {
        return false;
    }

    @Override
    public boolean releaseAlternateBuffer() throws IOException {
        return false;
    }

    @Override
    public String getProperty(String propertyName) {
        return null;
    }

    @Override
    public String readLine(String msg, boolean echo) throws IOException, InterruptedException, IllegalStateException {
        return null;
    }

    @Override
    public int getWidth() {
        return this.width;
    }

    @Override
    public int getHeight() {
        return this.height;
    }

    @Override
    public void flush() {
        if (buffer.length() > 0) {
            appendResult(buffer.toString());
            buffer.setLength(0);
        }
    }

    @Override
    public Appendable append(char c) throws IOException {
        return append(Character.toString(c));
    }

    @Override
    public Appendable append(CharSequence s) throws IOException {
        return append(s, 0, s.length());
    }

    @Override
    public Appendable append(CharSequence csq, int start, int end) throws IOException {
        if (start < end) {
            if (style.equals(Style.reset)) {
                buffer.append(csq, start, end);
            } else {
                Style.Composite composite = (Style.Composite) style;
                buffer.append("[[");
                if (composite.getUnderline() == Boolean.TRUE) {
                    buffer.append('u');
                }
                if (composite.getBold() == Boolean.TRUE) {
                    buffer.append('b');
                }
                buffer.append(';');
                if (composite.getForeground() != null) {
                    buffer.append(COLOR_MAP.get(composite.getForeground()));
                }
                buffer.append(';');
                if (composite.getBackground() != null) {
                    buffer.append(COLOR_MAP.get(composite.getBackground()));
                }
                buffer.append(']');
                while (start < end) {
                    char c = csq.charAt(start++);
                    if (c == ']') {
                        buffer.append("\\]");
                    } else {
                        buffer.append(c);
                    }
                }
                buffer.append(']');
            }
            if (csq.toString().endsWith("\n") || csq.toString().endsWith("\r\n")) {
                flush();
            }
        }
        return this;
    }

    @Override
    public Screenable cls() throws IOException {
        buffer.append("\033[");
        buffer.append("2J");
        buffer.append("\033[");
        buffer.append("1;1H");
        return null;
    }

    @Override
    public Screenable append(Style style) throws IOException {
        this.style = style.merge(style);
        return this;
    }

    @Override
    public void end(ShellResponse response) {
        flush();
        String msg = response.getMessage();
        if (response instanceof ShellResponse.Error) {
            msg = "[[;#AA0000;]" + msg + "][[;#AA0000;]]\n";
        }
        if (msg.length() > 0) {
            appendResult(msg);
        }
    }
}
