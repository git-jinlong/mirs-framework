package com.github.mirs.banxiaoxiao.framework.core.woodpecker.cmd.bean;

import com.github.mirs.banxiaoxiao.framework.common.util.JsonUtils;
import com.github.mirs.banxiaoxiao.framework.common.util.ReflectionUtils;
import com.github.mirs.banxiaoxiao.framework.common.util.StringUtil;
import com.github.mirs.banxiaoxiao.framework.core.spring.SpringContextHolder;
import com.github.mirs.banxiaoxiao.framework.core.util.textab.Cell;
import com.github.mirs.banxiaoxiao.framework.core.util.textab.GridTable;
import com.github.mirs.banxiaoxiao.framework.core.util.textab.SimpleTable;
import com.github.mirs.banxiaoxiao.framework.core.util.textab.grid.Border;
import com.github.mirs.banxiaoxiao.framework.core.woodpecker.cmd.bean.BeanManager.Bean;
import org.crsh.cli.Argument;
import org.crsh.cli.Command;
import org.crsh.cli.Option;
import org.crsh.cli.Usage;
import org.crsh.command.BaseCommand;
import org.crsh.command.InvocationContext;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Collection;

/**
 * @author zcy 2020年3月2日
 */
public class BeanCommand extends BaseCommand {

    BeanManager manager = BeanManager.getInstance();

    @Command
    @Usage("添加bean到快速访问池")
    @SuppressWarnings({"rawtypes", "unchecked"})
    public void add(InvocationContext context, @Argument String bean, @Argument String alias) throws IOException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException {
        Object targetObject = null;
        if (bean.startsWith("spring.")) {
            String className = bean.substring(7);
            try {
                Class clazz = Class.forName(className);
                targetObject = SpringContextHolder.get().getBean(clazz);
            } catch (ClassNotFoundException e) {
                context.append("bean class " + className + " not found");
            }
        } else {
            int mi = bean.lastIndexOf(".");
            String className = bean.substring(0, mi);
            String getMethod = bean.substring(mi + 1);
            if (getMethod.contains("(")) {
                getMethod = getMethod.substring(0, getMethod.indexOf("("));
            }
            try {
                Class clazz = Class.forName(className);
                Method getMethoda = ReflectionUtils.getMethod(clazz, getMethod);
                targetObject = getMethoda.invoke(null, null);
            } catch (ClassNotFoundException | NoSuchMethodException e) {
                context.append("bean class " + className + " not found");
            }
        }
        int index = manager.add(targetObject, alias);
        if (targetObject == null) {
            context.append("bean " + bean + " not found");
        } else {
            context.append("bean ").append(targetObject.toString());
            context.append(" 序号为[" + index).append("]你也可以通过该序号快捷访问");
        }
        newLine(context);
    }

    @Command
    @Usage("查看快速访问池类别")
    @SuppressWarnings("rawtypes")
    public void list(InvocationContext context) throws Exception {
        Collection<Bean> beans = manager.getAll();
        SimpleTable s = SimpleTable.of().nextRow().nextCell().addLine("bean").nextCell().addLine("index").nextCell().addLine("alias");
        for (Bean bean : beans) {
            s.nextRow().nextCell().addLine(bean.getTarget().toString()).nextCell().addLine("" + bean.getIndex()).nextCell()
                    .addLine(bean.getAlias() == null ? "" : bean.getAlias());
        }
        GridTable g = s.toGrid();
        g = Border.of(Border.Chars.of('+', '-', '|')).apply(g);
        StringWriter sw = new StringWriter();
        PrintWriter out = new PrintWriter(sw);
        g = g.apply(Cell.Functions.TOP_ALIGN).apply(Cell.Functions.LEFT_ALIGN);
        int isHeader = 0;
        for (String line : g.toCell()) {
            if (isHeader == 1) {
                line = "[[b;#000;#AAAAAA]" + line + "][[b;#000;#AAAAAA]]";
            }
            out.println(line);
            isHeader++;
        }
        out.flush();
        context.append(sw.toString());
    }

    @Command
    @Usage("执行bean的方法")
    @SuppressWarnings("rawtypes")
    public void call(InvocationContext context, @Option(names = {"i", "index"}) Integer index, @Option(names = {"a", "alias"}) String alias,
                     @Option(names = {"m", "method"}) String method, @Option(names = {"p", "parameters"}) String args) throws Exception {
        Object targetBean = get(index, alias);
        if (targetBean == null) {
            context.append("bean not found");
            newLine(context);
            return;
        }
        Method[] methods = targetBean.getClass().getDeclaredMethods();
        Method targetMethod = null;
        for (Method m : methods) {
            if (StringUtil.equals(m.getName(), method)) {
                targetMethod = m;
            }
        }
        if (targetMethod == null) {
            context.append("bean ").append(targetBean.toString()).append(" method ").append(method == null ? "null" : method).append(" not found");
        } else {
            Object[] parameters = new Object[targetMethod.getParameters().length];
            if (parameters.length == 1) {
                parameters[0] = JsonUtils.fromJson(targetMethod.getParameters()[0].getType(), args);
            } else if (parameters.length > 1) {
                String[] jsonArgs = null;
                if (args != null) {
                    if (args.startsWith("[")) {
                        args = args.substring(1);
                    }
                    if (args.endsWith("]")) {
                        args = args.substring(0, args.length() - 1);
                    }
                    jsonArgs = args.split(",");
                } else {
                    jsonArgs = new String[]{};
                }
                for (int i = 0; i < targetMethod.getParameters().length; i++) {
                    Parameter p = targetMethod.getParameters()[i];
                    Object pObject = JsonUtils.fromJson(p.getType(), jsonArgs[i]);
                    parameters[i] = pObject;
                }
            }
            Object result = targetMethod.invoke(targetBean, parameters);
            context.append("call method ").append(method).append(" result is ").append(JsonUtils.toJson(result));
        }
        newLine(context);
    }

    @Command
    @Usage("获取bean属性值")
    @SuppressWarnings("rawtypes")
    public void get(InvocationContext context, @Option(names = {"i", "index"}) Integer index, @Option(names = {"a", "alias"}) String alias,
                    @Option(names = {"f", "field"}) String field) throws Exception {
        Object targetBean = get(index, alias);
        if (targetBean == null) {
            context.append("bean not found");
        } else {
            Object fieldValue = ReflectionUtils.getField(targetBean, field);
            String fieldValueJson = JsonUtils.toJson(fieldValue);
            context.append(fieldValueJson);
        }
        newLine(context);
    }

    private Object get(Integer index, String alias) {
        Object targetBean = null;
        if (index != null) {
            targetBean = manager.getByIndex(index);
        } else if (alias != null) {
            targetBean = manager.getByAlias(alias);
        }
        return targetBean;
    }

    private void newLine(InvocationContext context) throws IOException {
        context.append("\n");
    }
}
