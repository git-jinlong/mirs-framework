/*
 * iNamik Text Tables for Java
 * 
 * Copyright (C) 2016 David Farrell (DavidPFarrell@yahoo.com)
 *
 * Licensed under The MIT License (MIT), see LICENSE.txt
 */
package com.github.mirs.banxiaoxiao.framework.core.util.textab.line;

import com.github.mirs.banxiaoxiao.framework.core.util.textab.line.base.FunctionWithWidth;

public final class RightTruncate extends FunctionWithWidth
{
    public static final RightTruncate INSTANCE = new RightTruncate();

    @Override
    public String apply(Integer width, String line) {
        if (line.length() > width) {
            return line.substring(0, width);
        }
        return line;
    }

}
