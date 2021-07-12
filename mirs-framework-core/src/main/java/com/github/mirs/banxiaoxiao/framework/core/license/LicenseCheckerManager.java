package com.github.mirs.banxiaoxiao.framework.core.license;

import com.github.mirs.banxiaoxiao.framework.common.util.StringUtil;
import com.github.mirs.banxiaoxiao.framework.common.util.TaskExecutors;
import com.github.mirs.banxiaoxiao.framework.core.license.loader.DccLicenseDataLoader;
import com.github.mirs.banxiaoxiao.framework.core.log.TComLogs;
import com.github.mirs.banxiaoxiao.framework.core.script.Script;
import com.github.mirs.banxiaoxiao.framework.core.script.ScriptVariableProcessor;
import com.github.mirs.banxiaoxiao.framework.core.spring.SpringContextHolder;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.SpringApplication;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author zcy 2019年6月3日
 */
public class LicenseCheckerManager {

    public final String CHECKER_ENABLE_KEY = "checker.enable";

    @Resource
    private License license;

    @Resource
    private List<LicenseChecker> checkers;

    @Resource
    private LicenseDataLoader loader;

    @Resource
    private Script script;

    @PostConstruct
    public void check() {
        this.license = License.get();
        if (this.loader != null) {
            this.license.setLicenseDataLoader(this.loader);
        } else {
            this.license.setLicenseDataLoader(new DccLicenseDataLoader());
        }
        doCheck();
        // 每30分钟检测一次授权
        TaskExecutors.submit(new Runnable() {

            @Override
            public void run() {
                doCheck();
            }
        }, 30, 30, TimeUnit.MINUTES);
    }

    public void doCheck() {
        try {
            this.license.init();
            // 检查是否开启了授权校验
            if (!checkerEnable()) {
                TComLogs.warn("disenable authorization checker");
                return;
            }
            if (checkers != null) {
                for (LicenseChecker checker : checkers) {
                    checker.check(license);
                }
            }
        } catch (LicenseException e) {
            TComLogs.error("Invalidation of authorization, stop service ", e);
            SpringApplication.exit(SpringContextHolder.get(), new ExitCodeGenerator() {

                @Override
                public int getExitCode() {
                    return -1;
                }
            });
            System.exit(-1);
        } catch (Throwable e) {
            TComLogs.error("", e);
        }
    }

    private boolean checkerEnable() {
        String enableScript = this.license.getString(CHECKER_ENABLE_KEY);
        if (StringUtil.isBlank(enableScript)) {
            return true;
        }
        Object obj = script.eval(enableScript, new ScriptVariableProcessor() {

            @Override
            public Object process(String var) {
                return license.getString(var);
            }
        });
        if (obj != null && obj.equals(false)) {
            return false;
        } else {
            return true;
        }
    }
}
