package com.github.mirs.banxiaoxiao.framework.core.dcc.autoval;

import com.github.mirs.banxiaoxiao.framework.core.config.BeeClientLocalProperties;
import com.github.mirs.banxiaoxiao.framework.common.util.StringUtil;

import java.io.*;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * 
 * @author zcy 2019年4月23日
 */
public class LocalAppProperties extends BeeClientLocalProperties {

    /** */
    private static final long serialVersionUID = 7067741678841408643L;

    private String coustemPropertiesFile;

    public LocalAppProperties(String coustemPropertiesFile) {
        this.coustemPropertiesFile = coustemPropertiesFile;
    }

    public void load() {
        loadDefaultFromClass();
        loadDefaultFromCoustemProperties();
        loadDefaultFromBeeProperties();
        loadDefaultFromAppProperties();
    }

    protected void loadDefaultFromCoustemProperties() {
        if (!StringUtil.isBlank(this.coustemPropertiesFile)) {
            try {
                InputStream coustemProperties = new FileInputStream(this.coustemPropertiesFile);
                load(coustemProperties);
            } catch (FileNotFoundException e) {
                //
            }
        }
    }

    public synchronized void save() throws IOException {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(this.coustemPropertiesFile);
            super.store(out, "#bee dcc auto value");
        } catch(FileNotFoundException e){
            File coustemFile = new File(this.coustemPropertiesFile);
            if (!coustemFile.exists()) {
                coustemFile.getParentFile().mkdirs();
            }
            out = new FileOutputStream(this.coustemPropertiesFile);
            super.store(out, "#bee dcc auto value");
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }

    private final LinkedHashSet<Object> keys = new LinkedHashSet<Object>();

    @Override
    public Enumeration<Object> keys() {
        return Collections.enumeration(keys);
    }

    @Override
    public synchronized Object put(Object key, Object value) {
        keys.add(key);
        return super.put(key, value);
    }

    @Override
    public Set<Object> keySet() {
        return keys;
    }

    @Override
    public Set<String> stringPropertyNames() {
        Set<String> set = new LinkedHashSet<String>();
        for (Object key : this.keys) {
            set.add((String) key);
        }
        return set;
    }
}
