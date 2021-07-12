package com.github.mirs.banxiaoxiao.framework.jdbc.ibatis;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.autoconfigure.ConfigurationCustomizer;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.MybatisXMLLanguageDriver;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import com.github.mirs.banxiaoxiao.framework.core.boot.InitializeException;
import com.github.mirs.banxiaoxiao.framework.core.log.TComLogs;
import com.github.mirs.banxiaoxiao.framework.jdbc.ibatis.intercept.comparator.AnnotationAwareInterceptOrderComparator;
import com.github.mirs.banxiaoxiao.framework.jdbc.jdbc.DataSourceProperties;
import org.apache.commons.collections.CollectionUtils;
import org.apache.ibatis.builder.xml.XMLConfigBuilder;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.EnumOrdinalTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;
import org.apache.ibatis.type.TypeReference;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

@EnableTransactionManagement
@EnableConfigurationProperties({DataSourceProperties.class, IbatisProperties.class})
public class IbatisAutoConfiguration {

    @Autowired
    IbatisProperties mybatisProperties;

    private List<Class<?>> ibatisDefaultHandler;

    public IbatisAutoConfiguration() {
        // 基础数据类型handler暂时不支持业务上自定义
        ibatisDefaultHandler = new ArrayList<Class<?>>();
        ibatisDefaultHandler.add(Boolean.class);
        ibatisDefaultHandler.add(boolean.class);
        ibatisDefaultHandler.add(Byte.class);
        ibatisDefaultHandler.add(byte.class);
        ibatisDefaultHandler.add(Short.class);
        ibatisDefaultHandler.add(short.class);
        ibatisDefaultHandler.add(Integer.class);
        ibatisDefaultHandler.add(int.class);
        ibatisDefaultHandler.add(Long.class);
        ibatisDefaultHandler.add(long.class);
        ibatisDefaultHandler.add(Float.class);
        ibatisDefaultHandler.add(float.class);
        ibatisDefaultHandler.add(Double.class);
        ibatisDefaultHandler.add(double.class);
        ibatisDefaultHandler.add(Reader.class);
        ibatisDefaultHandler.add(String.class);
        ibatisDefaultHandler.add(BigInteger.class);
        ibatisDefaultHandler.add(BigDecimal.class);
        ibatisDefaultHandler.add(InputStream.class);
        ibatisDefaultHandler.add(Byte[].class);
        ibatisDefaultHandler.add(byte[].class);
        ibatisDefaultHandler.add(Object.class);
        ibatisDefaultHandler.add(Date.class);
        ibatisDefaultHandler.add(java.sql.Date.class);
        ibatisDefaultHandler.add(java.sql.Time.class);
        ibatisDefaultHandler.add(java.sql.Timestamp.class);
    }

    @Bean
    @Primary
    @ConditionalOnMissingBean
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource, ApplicationContext context) throws IOException {
        MybatisSqlSessionFactoryBean bean = new MybatisSqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        Configuration c = new Configuration();
        c.setLogPrefix("bee.dao.");
        ResourcePatternResolver rr = new PathMatchingResourcePatternResolver();
        if (mybatisProperties.getConfigLocation() != null && mybatisProperties.getConfigLocation().trim().length() > 0) {
//            Configuration configuration = loadConfiguration(rr.getResources(mybatisProperties.getConfigLocation()));
            MybatisConfiguration configuration = loadMybatisConfiguration(rr.getResources(mybatisProperties.getConfigLocation()));
            bean.setConfiguration(configuration);
        }
        if (mybatisProperties.getMapperLocations() != null && mybatisProperties.getMapperLocations().trim().length() > 0) {
            bean.setMapperLocations(resolveMapperLocations(mybatisProperties.getMapperLocations()));
        }
        if (mybatisProperties.getTypeAliasesPackage() != null && mybatisProperties.getTypeAliasesPackage().trim().length() > 0) {
            bean.setTypeAliasesPackage(mybatisProperties.getTypeAliasesPackage());
        }

        if (this.mybatisProperties.getConfigurationProperties() != null) {
            bean.setConfigurationProperties(this.mybatisProperties.getConfigurationProperties());
        }
        if (mybatisProperties.getTypeEnumsPackage() != null && mybatisProperties.getTypeEnumsPackage().trim().length() > 0) {
            bean.setTypeEnumsPackage(mybatisProperties.getTypeEnumsPackage());
        }
        //配置全局配置
        if (null != mybatisProperties.getGlobalConfig()) {
            bean.setGlobalConfig(mybatisProperties.getGlobalConfig());
        }

        SqlSessionFactory sqlSessionFactory = null;
        try {
            sqlSessionFactory = bean.getObject();
            Map<String, Object> intercepts = context.getBeansWithAnnotation(Intercepts.class);
            if (intercepts != null) {
                Collection<Object> interceptsOriginal = sortByOrder(intercepts.values());
                for (Object interceptor : interceptsOriginal) {
                    sqlSessionFactory.getConfiguration().addInterceptor((Interceptor) interceptor);
                }
            }
        } catch (Exception e) {
            throw new InitializeException("create SqlSessionFactory fail", e);
        }
        return sqlSessionFactory;
    }


    public Resource[] resolveMapperLocations(String mapperAddress) {

        ResourcePatternResolver resourceResolver = new PathMatchingResourcePatternResolver();
        List<String> mapperLocations = new ArrayList<>();
        String[] mapperArrays = mapperAddress.split(",");
        mapperLocations.addAll(Arrays.asList(mapperArrays));
        List<Resource> resources = new ArrayList();
        if (mapperLocations != null) {
            for (String mapperLocation : mapperLocations) {
                try {
                    Resource[] mappers = resourceResolver.getResources(mapperLocation);
                    resources.addAll(Arrays.asList(mappers));
                } catch (IOException e) {
                    // ignore
                }
            }
        }
        return resources.toArray(new Resource[resources.size()]);
    }

    //sort by order
    private Collection<Object> sortByOrder(Collection<Object> targets) {
        List<Object> haveOrder = targets.stream().filter(e -> AnnotationAwareInterceptOrderComparator.haveOrder(e)).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(haveOrder)) {
            return targets;
        }

        AnnotationAwareInterceptOrderComparator.sort(haveOrder);

        List<Object> nonOrder = targets.stream().filter(e -> !AnnotationAwareInterceptOrderComparator.haveOrder(e)).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(nonOrder)) {
            nonOrder.addAll(haveOrder);
            return nonOrder;
        }

        return haveOrder;
    }


    @SuppressWarnings("unchecked")
//    private <T> Configuration loadConfiguration(Resource[] rs) throws IOException {
//        Configuration configuration = null;
//        if (rs != null) {
//            for (Resource r : rs) {
//                TComLogs.info("ibatis xml conf : {}", r);
//                XMLConfigBuilder xmlConfigBuilder = new XMLConfigBuilder(r.getInputStream(), null, null);
//                xmlConfigBuilder.parse();
//                if (configuration == null) {
//                    configuration = xmlConfigBuilder.getConfiguration();
//                } else {
//                    Configuration temp = xmlConfigBuilder.getConfiguration();
//                    if (temp.getTypeAliasRegistry().getTypeAliases() != null) {
//                        for (Entry<String, Class<?>> typeAliases : temp.getTypeAliasRegistry().getTypeAliases().entrySet()) {
//                            configuration.getTypeAliasRegistry().registerAlias(typeAliases.getKey(), typeAliases.getValue());
//                        }
//                    }
//                    if (temp.getTypeHandlerRegistry().getTypeHandlers() != null) {
//                        for (TypeHandler typeHandler : temp.getTypeHandlerRegistry().getTypeHandlers()) {
//                            boolean isDefaultHandler = false;
//                            if (typeHandler instanceof TypeReference) {
//                                try {
//                                    TypeReference<T> typeReference = (TypeReference<T>) typeHandler;
//                                    if (ibatisDefaultHandler.contains(typeReference.getRawType())) {
//                                        isDefaultHandler = true;
//                                    }
//                                } catch (Throwable t) {
//                                    // maybe users define the TypeReference with a different type and are not assignable, so just ignore it
//                                    TComLogs.warn("TypeHadler getRawType error", t);
//                                }
//                            }
//                            if (!isDefaultHandler) {
//                                TComLogs.debug("register type handler {}", typeHandler);
//                                configuration.getTypeHandlerRegistry().register(typeHandler);
//                            }
//                        }
//                    }
//                }
//            }
//        }
//        return configuration;
//    }

    private <T> MybatisConfiguration loadMybatisConfiguration(Resource[] rs) throws IOException {
        MybatisConfiguration result = null;
        Configuration configuration = null;
        if (rs != null) {
            for (Resource r : rs) {
                TComLogs.info("ibatis xml conf : {}", r);
                XMLConfigBuilder xmlConfigBuilder = new XMLConfigBuilder(r.getInputStream(), null, null);
                xmlConfigBuilder.parse();
                if (configuration == null) {
                    configuration = xmlConfigBuilder.getConfiguration();
                } else {
                    Configuration temp = xmlConfigBuilder.getConfiguration();
                    if (temp.getTypeAliasRegistry().getTypeAliases() != null) {
                        for (Entry<String, Class<?>> typeAliases : temp.getTypeAliasRegistry().getTypeAliases().entrySet()) {
                            configuration.getTypeAliasRegistry().registerAlias(typeAliases.getKey(), typeAliases.getValue());
                        }
                    }
                    if (temp.getTypeHandlerRegistry().getTypeHandlers() != null) {
                        for (TypeHandler typeHandler : temp.getTypeHandlerRegistry().getTypeHandlers()) {
                            boolean isDefaultHandler = false;
                            if (typeHandler instanceof TypeReference) {
                                try {
                                    TypeReference<T> typeReference = (TypeReference<T>) typeHandler;
                                    if (ibatisDefaultHandler.contains(typeReference.getRawType())) {
                                        isDefaultHandler = true;
                                    }
                                } catch (Throwable t) {
                                    // maybe users define the TypeReference with a different type and are not assignable, so just ignore it
                                    TComLogs.warn("TypeHandler getRawType error", t);
                                }
                            }
                            if (!isDefaultHandler) {
                                TComLogs.debug("register type handler {}", typeHandler);
                                configuration.getTypeHandlerRegistry().register(typeHandler);
                            }
                        }
                    }
                }
            }
        }
        if (null == configuration) {
            return null;
        }
        //TODO 暂时未提供global config，后续支持

        MybatisConfiguration mybatisConfiguration = new MybatisConfiguration();
        BeanUtils.copyProperties(configuration, mybatisConfiguration);


        // 弥补属性拷贝无法拷贝的弊端
        for (Entry<String, Class<?>> typeAliases : configuration.getTypeAliasRegistry().getTypeAliases().entrySet()) {
            mybatisConfiguration.getTypeAliasRegistry().registerAlias(typeAliases.getKey(), typeAliases.getValue());
        }
        for (TypeHandler typeHandler : configuration.getTypeHandlerRegistry().getTypeHandlers()) {
            boolean isDefaultHandler = false;
            if (typeHandler instanceof TypeReference) {
                try {
                    TypeReference<T> typeReference = (TypeReference<T>) typeHandler;
                    if (ibatisDefaultHandler.contains(typeReference.getRawType())) {
                        isDefaultHandler = true;
                    }
                } catch (Throwable t) {
                    // maybe users define the TypeReference with a different type and are not assignable, so just ignore it
                    TComLogs.warn("TypeHadler getRawType error", t);
                }
            }
            if (!isDefaultHandler) {
                TComLogs.debug("register type handler {}", typeHandler);
                mybatisConfiguration.getTypeHandlerRegistry().register(typeHandler);
            }
        }

        mybatisConfiguration.getTypeHandlerRegistry().setDefaultEnumTypeHandler(EnumOrdinalTypeHandler.class);
        mybatisConfiguration.setDefaultScriptingLanguage(MybatisXMLLanguageDriver.class);
        mybatisConfiguration.setJdbcTypeForNull(JdbcType.NULL);
        mybatisConfiguration.setMapUnderscoreToCamelCase(true);
        mybatisConfiguration.setCacheEnabled(false);

        return mybatisConfiguration;
    }

    @Bean
    @Primary
    @ConditionalOnMissingBean
    public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sqlSessionFactory) {
        ExecutorType executorType = this.mybatisProperties.getExecutorType();
        if (executorType != null) {
            return new SqlSessionTemplate(sqlSessionFactory, executorType);
        } else {
            return new SqlSessionTemplate(sqlSessionFactory);
        }
    }

    @Bean
    @ConditionalOnMissingBean
    @Primary
    public DataSourceTransactionManager transactionManager(DataSource dataSource) {
        DataSourceTransactionManager transactionManager = new DataSourceTransactionManager();
        transactionManager.setDataSource(dataSource);
        return transactionManager;
    }

    /**
     * mybatis-plus 分页配置
     */

    /**
     * 新的分页插件,一缓和二缓遵循mybatis的规则,需要设置 MybatisConfiguration#useDeprecatedExecutor = false 避免缓存出现问题(该属性会在旧插件移除后一同移除)
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        //加载配置，默认配置mariadb
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.getDbType(mybatisProperties.getDataBaseType())));
        //乐观锁
        interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());

        return interceptor;
    }

    @Bean
    public ConfigurationCustomizer configurationCustomizer() {
        return configuration -> configuration.setUseDeprecatedExecutor(false);
    }


}
