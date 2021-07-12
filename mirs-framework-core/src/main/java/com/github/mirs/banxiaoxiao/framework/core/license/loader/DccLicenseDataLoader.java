package com.github.mirs.banxiaoxiao.framework.core.license.loader;

import java.util.Base64;

import com.github.mirs.banxiaoxiao.framework.core.dcc.AbstractDccApp;
import com.github.mirs.banxiaoxiao.framework.core.license.LicenseDataLoader;
import com.github.mirs.banxiaoxiao.framework.core.license.LicenseException;

/**
 * @author zcy 2019年6月4日
 */
public class DccLicenseDataLoader extends AbstractDccApp implements LicenseDataLoader {

    public static final String LICENSE_KEY = "license";

    public DccLicenseDataLoader() {
        super(LICENSE_KEY);
    }

    @Override
    public byte[] load() {
        try {
            String data = readData(getRoot());
            return Base64.getDecoder().decode(data);
        } catch (Throwable e) {
            throw new LicenseException("load license data from dcc fail", e);
        }
    }
    
    public void writeLicense(byte[] data) {
        String base64data = Base64.getEncoder().encodeToString(data);
        this.writeData(getRoot(), base64data);
    }
}
