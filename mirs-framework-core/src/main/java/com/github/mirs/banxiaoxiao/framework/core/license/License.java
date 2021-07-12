package com.github.mirs.banxiaoxiao.framework.core.license;

import com.github.mirs.banxiaoxiao.framework.common.shell.ShellUtils;
import com.github.mirs.banxiaoxiao.framework.common.util.MultiTypeValMap;
import com.github.mirs.banxiaoxiao.framework.core.log.TComLogs;
import org.slf4j.helpers.MessageFormatter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * @author zcy 2019年5月31日
 */
public final class License extends MultiTypeValMap {

    /**
     *
     */
    private static final long serialVersionUID = -5520061786135596348L;

    private static License INSTANCE = new License();

    private LicenseDataLoader loader;

    /**
     * 服务器id
     */
    private List<String> uuids;

    private License() {
    }

    public static License get() {
        return INSTANCE;
    }

    public static License create() {
        return new License();
    }

    public void setLicenseDataLoader(LicenseDataLoader loader) {
        this.loader = loader;
    }

    public void put(String key, Object val) {
        throw new UnsupportedOperationException();
    }

    public void putAll(Map<String, Object> vals) {
        throw new UnsupportedOperationException();
    }

    /**
     *
     */
    public void init() {
        try {
            if (this.uuids != null) {
                this.uuids.clear();
            }
            getVals().clear();
            byte[] licenseData = this.loader.load();
            byte[] decryptData = decrypt(licenseData);
            if (decryptData == null || decryptData.length == 0) {
                throw new LicenseException("init License data fail, not found data");
            }
            Properties properties = new Properties();
            properties.load(new ByteArrayInputStream(decryptData));
            properties.forEach((k, v) -> {
                super.put(k.toString(), v);
            });
            try {
                this.uuids = new ArrayList<String>();
                String systemuid = BlackBox.decodesystemid(licenseData);
                if (systemuid != null) {
                    //支持license逗号和换行符切割
                    for (String uid : systemuid.split("[\n,]")) {
                        this.uuids.add(uid);
                    }
                }
            } catch (Throwable e) {
                TComLogs.error("decode systemid fail", e);
            }
        } catch (LicenseException e) {
            throw e;
        } catch (IOException e) {
            throw new LicenseException("init License data fail", e);
        }
    }

    public String getLocalUid() {
        List<String> result = ShellUtils.exec("dmidecode -s system-uuid");
        return result == null || result.size() == 0 ? "" : result.get(0);
    }

    public List<String> getUuids() {
        return uuids;
    }

    public void setUuids(List<String> uuids) {
        this.uuids = uuids;
    }

    public void checkContains(String key, String val) throws LicenseException {
        String licenseData = getString(key);
        if (licenseData == null) {
            throwLicenseAuthException(key);
        } else {
            for (String licenseItem : licenseData.split(",")) {
                if (!licenseItem.equals(val)) {
                    throwLicenseAuthException(key);
                }
            }
        }
    }

    public void checkEquals(String key, String val) throws LicenseException {
        String licenseData = getString(key);
        if (licenseData == null || !licenseData.equals(val)) {
            throwLicenseAuthException(key);
        }
    }

    public <T extends Number> void checkEquals(String key, T val) throws LicenseException {
        Long licenseData = getLong(key, null);
        if (licenseData == null || licenseData != val.longValue()) {
            throwLicenseAuthException(key);
        }
    }

    /**
     * 验证是否在授权数值是否在范围内
     */
    public <T extends Number> void checkRange(String key, T val1, T val2) throws LicenseException {
        Long licenseData = getLong(key, null);
        if (licenseData == null || val1 == null || val2 == null) {
            throwLicenseAuthException(key);
        }
        if (licenseData < val1.longValue() || licenseData > val2.longValue()) {
            throwLicenseAuthException("{} out of auth range", key);
        }
    }

    /**
     * 验证授权数值是否>val
     */
    public <T extends Number> void checkGreater(String key, T val) throws LicenseException {
        Long licenseData = getLong(key, null);
        if (licenseData == null || val == null) {
            throwLicenseAuthException(key);
        }
        if (licenseData <= val.longValue()) {
            throwLicenseAuthException(key);
        }
    }

    /**
     * 验证授权数值是否>=val
     */
    public <T extends Number> void checkGreaterAndEqu(String key, T val) throws LicenseException {
        Long licenseData = getLong(key, null);
        if (licenseData == null || val == null) {
            throwLicenseAuthException(key);
        }
        if (licenseData < val.longValue()) {
            throwLicenseAuthException(key);
        }
    }

    /**
     * 验证授权数值是否<val
     */
    public <T extends Number> void checkLess(String key, T val) throws LicenseException {
        Long licenseData = getLong(key, null);
        if (licenseData == null || val == null) {
            throwLicenseAuthException(key);
        }
        if (licenseData >= val.longValue()) {
            throwLicenseAuthException(key);
        }
    }

    /**
     * 验证授权数值是否<=val
     */
    public <T extends Number> void checkLessAndEqu(String key, T val) throws LicenseException {
        Long licenseData = getLong(key, null);
        if (licenseData == null || val == null) {
            throwLicenseAuthException(key);
        }
        if (licenseData > val.longValue()) {
            throwLicenseAuthException(key);
        }
    }

    private void throwLicenseAuthException(String key) {
        throwLicenseAuthException("{} unauthorized", key);
    }

    private void throwLicenseAuthException(String formatMsg, Object... propertys) {
        String msg = MessageFormatter.arrayFormat(formatMsg, propertys).getMessage();
        throw new LicenseException(msg);
    }

    /**
     * 解密数据
     */
    public byte[] decrypt(byte[] data) {
        if (data == null) {
            return null;
        }
        byte[] resultData = BlackBox.decrypt(data);
        return resultData;
    }
}
