package com.github.mirs.banxiaoxiao.framework.jdbc.ibatis.enable;

import com.github.mirs.banxiaoxiao.framework.common.util.StringUtil;
import com.github.mirs.banxiaoxiao.framework.core.boot.InitializeException;
import com.github.mirs.banxiaoxiao.framework.core.boot.ModuleInitializer;
import com.github.mirs.banxiaoxiao.framework.core.config.BeeClientConfiguration;
import com.github.mirs.banxiaoxiao.framework.core.log.TComLogs;
import com.github.mirs.banxiaoxiao.framework.jdbc.ibatis.IbatisAutoConfiguration;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.plugin.Intercepts;
import org.mybatis.spring.mapper.ClassPathMapperScanner;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.TypeFilter;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zcy 2019年5月21日
 */
public class IbatisInitializer implements ModuleInitializer {

    @Override
    public void init(ConfigurableApplicationContext applicationContext) throws InitializeException {
    }

    @Override
    public void init(Annotation enableAnno, ConfigurableApplicationContext applicationContext) throws InitializeException {
        IbatisEnable anno = (IbatisEnable) enableAnno;
        String[] scanPackage = getScanPackages(applicationContext);
        registerBean(IbatisAutoConfiguration.class, applicationContext);
        if (anno.autoMapper()) {
            TComLogs.info("start searching @Mapper {}", StringUtil.join(scanPackage, ","));
            BeanDefinitionRegistry registry = (BeanDefinitionRegistry) applicationContext;
            ResourceLoader resourceLoader = (ResourceLoader) applicationContext;
            try {
                ClassPathMapperScanner scanner = new ClassPathMapperScanner(registry);
                scanner.setResourceLoader(resourceLoader);
                scanner.setAnnotationClass(Mapper.class);
                scanner.registerFilters();
                List<String> excludeMappers = getExcludeMappers();
                if (excludeMappers != null && excludeMappers.size() > 0) {
                    scanner.addExcludeFilter(new TypeFilter() {

                        @Override
                        public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory) throws IOException {
                            String className = metadataReader.getClassMetadata().getClassName();
                            for (String excludeMapper : excludeMappers) {
                                if (className.startsWith(excludeMapper)) {
                                    return true;
                                }
                            }
                            return false;
                        }
                    });
                }
                scanner.doScan(scanPackage);
            } catch (IllegalStateException ex) {
                TComLogs.error("Could not determine auto-configuration " + "package, automatic mapper scanning disabled.", ex);
            }
            TComLogs.info("finshed search for mappers and intercepts annotated with @Mapper");
        } else {
            TComLogs.info("Configured not to inject automatically @Mapper ");
        }
        if (anno.autoIntercepts()) {
            TComLogs.info("start searching @Intercepts {}", StringUtil.join(scanPackage, ","));
            registerBeanByAnnotationType(Intercepts.class, applicationContext);
            TComLogs.info("finshed search for intercepts annotated with @Intercepts");
        } else {
            TComLogs.info("Configured not to inject automatically @Intercepts ");
        }
    }

    private List<String> getExcludeMappers() {
        Class<?> applicationClass = BeeClientConfiguration.getLocalProperies().getApplicationClasss();
        List<ExcludeMapper> excludeMappers = new ArrayList<ExcludeMapper>();
        findAnnotationOnClass(applicationClass, ExcludeMapper.class, excludeMappers);
        List<String> excludeMapperStrings = new ArrayList<String>();
        for (ExcludeMapper excludeMapper : excludeMappers) {
            if (excludeMapper.exclude() != null) {
                TComLogs.info("finded @ExcludeMapper {}", StringUtil.join(excludeMapper.exclude()));
                for (String excludeMapperString : excludeMapper.exclude()) {
                    if (!excludeMapperStrings.contains(excludeMapperString)) {
                        excludeMapperStrings.add(excludeMapperString);
                    }
                }
            }
        }
        return excludeMapperStrings;
    }
}
