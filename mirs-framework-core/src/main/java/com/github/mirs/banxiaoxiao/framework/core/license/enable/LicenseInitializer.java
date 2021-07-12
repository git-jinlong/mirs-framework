package com.github.mirs.banxiaoxiao.framework.core.license.enable;

import java.lang.annotation.Annotation;

import org.springframework.context.ConfigurableApplicationContext;

import com.github.mirs.banxiaoxiao.framework.core.boot.InitializeException;
import com.github.mirs.banxiaoxiao.framework.core.boot.ModuleInitializer;
import com.github.mirs.banxiaoxiao.framework.core.license.License;
import com.github.mirs.banxiaoxiao.framework.core.license.LicenseCheckerManager;
import com.github.mirs.banxiaoxiao.framework.core.license.check.AppAuthorizedTimeChecker;
import com.github.mirs.banxiaoxiao.framework.core.license.check.LocalServerChecker;
import com.github.mirs.banxiaoxiao.framework.core.license.check.ScriptChecker;

public class LicenseInitializer implements ModuleInitializer {

    @Override
    public void init(ConfigurableApplicationContext appContext) throws InitializeException {
        
    }

    @Override
    public void init(Annotation enableAnno, ConfigurableApplicationContext appContext) throws InitializeException {
        LicenseEnable licenseEnable = (LicenseEnable) enableAnno;
        appContext.getBeanFactory().registerSingleton("license", License.get());
        registerBean(AppAuthorizedTimeChecker.class, appContext);
        registerBean(LocalServerChecker.class, appContext);
        registerBean(ScriptChecker.class, appContext);
        registerBean(LicenseCheckerManager.class, appContext);
        registerBean(licenseEnable.licenseDataLoader(), appContext);
    }
}
