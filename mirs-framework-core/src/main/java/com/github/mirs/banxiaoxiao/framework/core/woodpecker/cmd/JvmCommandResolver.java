package com.github.mirs.banxiaoxiao.framework.core.woodpecker.cmd;

import java.util.HashMap;
import java.util.Map.Entry;

import org.crsh.cli.descriptor.Format;
import org.crsh.cli.impl.descriptor.IntrospectionException;
import org.crsh.command.BaseCommand;
import org.crsh.lang.impl.java.ClassShellCommand;
import org.crsh.lang.spi.CommandResolution;
import org.crsh.plugin.CRaSHPlugin;
import org.crsh.shell.ErrorKind;
import org.crsh.shell.impl.command.spi.Command;
import org.crsh.shell.impl.command.spi.CommandException;
import org.crsh.shell.impl.command.spi.CommandResolver;

import com.github.mirs.banxiaoxiao.framework.core.woodpecker.cmd.bean.BeanCommand;
import com.github.mirs.banxiaoxiao.framework.core.woodpecker.cmd.trans.TransCommand;

public class JvmCommandResolver extends CRaSHPlugin<CommandResolver> implements CommandResolver {

    private static final HashMap<String, Class<? extends BaseCommand>> commands = new HashMap<String, Class<? extends BaseCommand>>();
    private static final HashMap<String, String> descriptions = new HashMap<String, String>();

    static {
        commands.put("bean", BeanCommand.class);
        commands.put("trans", TransCommand.class);
        descriptions.put("bean", "bean快速访问池");
        descriptions.put("trans", "传送脚本命令");
      }
    
    @Override
    public void init() {
    }

    @Override
    public Iterable<Entry<String, String>> getDescriptions() {
        return descriptions.entrySet();
    }

    @Override
    public Command<?> resolveCommand(String name) throws CommandException, NullPointerException {
        final Class<? extends BaseCommand> systemCommand = commands.get(name);
        if (systemCommand != null) {
            return createCommand(systemCommand).getCommand();
        }
        return null;
    }

    private <C extends BaseCommand> CommandResolution createCommand(final Class<C> commandClass) throws CommandException {
        final ClassShellCommand<C> shellCommand;
        final String description;
        try {
            shellCommand = new ClassShellCommand<C>(commandClass);
            description = shellCommand.describe(commandClass.getSimpleName(), Format.DESCRIBE);
        } catch (IntrospectionException e) {
            throw new CommandException(ErrorKind.SYNTAX, "Invalid cli annotation in command " + commandClass.getSimpleName(), e);
        }
        return new CommandResolution() {

            @Override
            public String getDescription() {
                return description;
            }

            @Override
            public Command<?> getCommand() throws CommandException {
                return shellCommand;
            }
        };
    }

    @Override
    public CommandResolver getImplementation() {
        return this;
    }
}
