package com.github.mirs.banxiaoxiao.framework.core.woodpecker.config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.crsh.plugin.CRaSHPlugin;
import org.crsh.plugin.PluginContext;
import org.crsh.plugin.PluginDiscovery;
import org.crsh.plugin.PluginLifeCycle;
import org.crsh.plugin.ServiceLoaderDiscovery;
import org.crsh.vfs.FS;
import org.crsh.vfs.spi.AbstractFSDriver;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.SpringVersion;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;

import com.github.mirs.banxiaoxiao.framework.core.log.TComLogs;
import com.github.mirs.banxiaoxiao.framework.core.spring.SpringContextHolder;

public class CrshBootstrapBean extends PluginLifeCycle {

    @Autowired
    private ListableBeanFactory beanFactory;

    @Autowired
    private Environment environment;

    @Autowired
    private ShellProperties properties;

    @Autowired
    private ResourcePatternResolver resourceLoader;

    private FileAlterationMonitor monitor;

    @PreDestroy
    public void destroy() {
        stop();
    }

    @PostConstruct
    public void init() throws Exception {
        FS commandFileSystem = createFileSystem(this.properties.getCommandPathPatterns(), this.properties.getDisabledCommands());
        FS configurationFileSystem = createFileSystem(this.properties.getConfigPathPatterns(), new String[0]);
        PluginDiscovery discovery = new BeanFactoryFilteringPluginDiscovery(this.resourceLoader.getClassLoader(), this.beanFactory,
                this.properties.getDisabledPlugins());
        PluginContext context = new PluginContext(discovery, createPluginContextAttributes(), commandFileSystem, configurationFileSystem,
                this.resourceLoader.getClassLoader());
        context.refresh();
        start(context);
        if (this.properties.getCommandPathPatterns() != null) {
            ScriptResourceListener scriptResourceListener = new ScriptResourceListener(context);
            // 开启一个监视器，监听频率是5s一次
            monitor = new FileAlterationMonitor(5000);
            for (String cmdDir : this.properties.getCommandPathPatterns()) {
                Resource[] res = SpringContextHolder.get().getResources(getDirPath(cmdDir));
                if(res != null) {
                    for(Resource re : res) {
                        try {
                            String cmdPath = re.getFile().getAbsolutePath();
                            if(!cmdDir.startsWith("classpath")) {
                                CommandPathHolder.setCmdDir(cmdPath);
                            }
                            FileAlterationObserver observer = new FileAlterationObserver(cmdPath);
                            // 给观察者添加监听事件
                            observer.addListener(scriptResourceListener);
                            monitor.addObserver(observer);
                        } catch(Throwable e) {
                            TComLogs.info(e.getMessage());
                        }
                    }
                }
            }
            monitor.start();
        }
    }
    
    private String getDirPath(String dirPatterns) {
        if (dirPatterns.lastIndexOf("\\") > 0) {
            dirPatterns = dirPatterns.substring(0, dirPatterns.lastIndexOf("\\"));
        }
        if (dirPatterns.lastIndexOf("/") > 0) {
            dirPatterns = dirPatterns.substring(0, dirPatterns.lastIndexOf("/"));
        }
        return dirPatterns;
    }

    protected FS createFileSystem(String[] pathPatterns, String[] filterPatterns) {
        Assert.notNull(pathPatterns, "PathPatterns must not be null");
        Assert.notNull(filterPatterns, "FilterPatterns must not be null");
        FS fileSystem = new FS();
        for (String pathPattern : pathPatterns) {
            try {
                fileSystem.mount(new SimpleFileSystemDriver(new DirectoryHandle(pathPattern, this.resourceLoader, filterPatterns)));
            } catch (IOException ex) {
                throw new IllegalStateException("Failed to mount file system for '" + pathPattern + "'", ex);
            }
        }
        return fileSystem;
    }

    protected Map<String, Object> createPluginContextAttributes() {
        Map<String, Object> attributes = new HashMap<String, Object>();
        String bootVersion = CrshConfiguration.class.getPackage().getImplementationVersion();
        if (bootVersion != null) {
            attributes.put("spring.boot.version", bootVersion);
        }
        attributes.put("spring.version", SpringVersion.getVersion());
        if (this.beanFactory != null) {
            attributes.put("spring.beanfactory", this.beanFactory);
        }
        if (this.environment != null) {
            attributes.put("spring.environment", this.environment);
        }
        return attributes;
    }

    private static class ScriptResourceListener extends FileAlterationListenerAdaptor {

        private PluginContext context;

        private ScriptResourceListener(PluginContext context) {
            this.context = context;
        }

        @Override
        public void onFileChange(File file) {
            TComLogs.info("script file change {}", file);
            try {
                context.refresh();
            } catch (Throwable e) {
                TComLogs.error("refresh script {} error", e, file);
            }
        }

        @Override
        public void onFileCreate(final File file) {
            TComLogs.info("script file created {}", file);
            try {
                context.refresh();
            } catch (Throwable e) {
                TComLogs.error("created script {} error", e, file);
            }
        }

        @Override
        public void onFileDelete(final File file) {
            TComLogs.info("script file deleted {}", file);
            try {
                context.refresh();
            } catch (Throwable e) {
                TComLogs.error("deleted script {} error", e, file);
            }
        }

        @Override
        public void onDirectoryChange(File directory) {
            TComLogs.info("change script dir {}", directory);
            try {
                context.refresh();
            } catch (Throwable e) {
                TComLogs.error("refresh script {} error", e, directory);
            }
        }

        @Override
        public void onDirectoryCreate(File directory) {
            TComLogs.info("create script dir {}", directory);
            try {
                context.refresh();
            } catch (Throwable e) {
                TComLogs.error("refresh script {} error", e, directory);
            }
        }

        @Override
        public void onDirectoryDelete(File directory) {
            TComLogs.info("delete script dir {}", directory);
            try {
                context.refresh();
            } catch (Throwable e) {
                TComLogs.error("refresh script {} error", e, directory);
            }
        }
    }

    private static class BeanFactoryFilteringPluginDiscovery extends ServiceLoaderDiscovery {

        private final ListableBeanFactory beanFactory;

        private final String[] disabledPlugins;

        BeanFactoryFilteringPluginDiscovery(ClassLoader classLoader, ListableBeanFactory beanFactory, String[] disabledPlugins)
                throws NullPointerException {
            super(classLoader);
            this.beanFactory = beanFactory;
            this.disabledPlugins = disabledPlugins;
        }

        @Override
        @SuppressWarnings("rawtypes")
        public Iterable<CRaSHPlugin<?>> getPlugins() {
            List<CRaSHPlugin<?>> plugins = new ArrayList<CRaSHPlugin<?>>();
            for (CRaSHPlugin<?> p : super.getPlugins()) {
                if (isEnabled(p)) {
                    plugins.add(p);
                }
            }
            Collection<CRaSHPlugin> pluginBeans = this.beanFactory.getBeansOfType(CRaSHPlugin.class).values();
            for (CRaSHPlugin<?> pluginBean : pluginBeans) {
                if (isEnabled(pluginBean)) {
                    plugins.add(pluginBean);
                }
            }
            return plugins;
        }

        protected boolean isEnabled(CRaSHPlugin<?> plugin) {
            Assert.notNull(plugin, "Plugin must not be null");
            if (ObjectUtils.isEmpty(this.disabledPlugins)) {
                return true;
            }
            Set<Class<?>> pluginClasses = ClassUtils.getAllInterfacesAsSet(plugin);
            pluginClasses.add(plugin.getClass());
            for (Class<?> pluginClass : pluginClasses) {
                if (!isEnabled(pluginClass)) {
                    return false;
                }
            }
            return true;
        }

        private boolean isEnabled(Class<?> pluginClass) {
            for (String disabledPlugin : this.disabledPlugins) {
                if (ClassUtils.getShortName(pluginClass).equalsIgnoreCase(disabledPlugin)
                        || ClassUtils.getQualifiedName(pluginClass).equalsIgnoreCase(disabledPlugin)) {
                    return false;
                }
            }
            return true;
        }
    }

    private static class SimpleFileSystemDriver extends AbstractFSDriver<ResourceHandle> {

        private final ResourceHandle root;

        SimpleFileSystemDriver(ResourceHandle handle) {
            this.root = handle;
        }

        @Override
        public Iterable<ResourceHandle> children(ResourceHandle handle) throws IOException {
            if (handle instanceof DirectoryHandle) {
                return ((DirectoryHandle) handle).members();
            }
            return Collections.emptySet();
        }

        @Override
        public long getLastModified(ResourceHandle handle) throws IOException {
            if (handle instanceof FileHandle) {
                return ((FileHandle) handle).getLastModified();
            }
            return -1;
        }

        @Override
        public boolean isDir(ResourceHandle handle) throws IOException {
            return handle instanceof DirectoryHandle;
        }

        @Override
        public String name(ResourceHandle handle) throws IOException {
            return handle.getName();
        }

        @Override
        public Iterator<InputStream> open(ResourceHandle handle) throws IOException {
            if (handle instanceof FileHandle) {
                return Collections.singletonList(((FileHandle) handle).openStream()).iterator();
            }
            return Collections.<InputStream> emptyList().iterator();
        }

        @Override
        public ResourceHandle root() throws IOException {
            return this.root;
        }
    }

    private abstract static class ResourceHandle {

        private final String name;

        ResourceHandle(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }
    }

    private static class FileHandle extends ResourceHandle {

        private final Resource resource;

        FileHandle(String name, Resource resource) {
            super(name);
            this.resource = resource;
        }

        public InputStream openStream() throws IOException {
            return this.resource.getInputStream();
        }

        public long getLastModified() {
            try {
                return this.resource.lastModified();
            } catch (IOException ex) {
                return -1;
            }
        }
    }

    private static class DirectoryHandle extends ResourceHandle {

        private final ResourcePatternResolver resourceLoader;

        private final String[] filterPatterns;

        private final AntPathMatcher matcher = new AntPathMatcher();

        DirectoryHandle(String name, ResourcePatternResolver resourceLoader, String[] filterPatterns) {
            super(name);
            this.resourceLoader = resourceLoader;
            this.filterPatterns = filterPatterns;
        }

        public List<ResourceHandle> members() throws IOException {
            Resource[] resources = this.resourceLoader.getResources(getName());
            List<ResourceHandle> files = new ArrayList<ResourceHandle>();
            for (Resource resource : resources) {
                if (!resource.getURL().getPath().endsWith("/") && !shouldFilter(resource)) {
                    files.add(new FileHandle(resource.getFilename(), resource));
                }
            }
            return files;
        }

        private boolean shouldFilter(Resource resource) {
            for (String filterPattern : this.filterPatterns) {
                if (this.matcher.match(filterPattern, resource.getFilename())) {
                    return true;
                }
            }
            return false;
        }
    }
}
