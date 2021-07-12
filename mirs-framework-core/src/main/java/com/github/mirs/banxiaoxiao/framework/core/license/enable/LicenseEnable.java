package com.github.mirs.banxiaoxiao.framework.core.license.enable;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.github.mirs.banxiaoxiao.framework.core.boot.EnableInitializer;
import com.github.mirs.banxiaoxiao.framework.core.license.LicenseDataLoader;
import com.github.mirs.banxiaoxiao.framework.core.license.loader.DccLicenseDataLoader;

/**
 * @author zcy 2019年6月3日
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@EnableInitializer(LicenseInitializer.class)
public @interface LicenseEnable {

    Class<? extends LicenseDataLoader> licenseDataLoader() default DccLicenseDataLoader.class;
}
