/*
 * iNamik Text Tables for Java
 * 
 * Copyright (C) 2016 David Farrell (DavidPFarrell@yahoo.com)
 *
 * Licensed under The MIT License (MIT), see LICENSE.txt
 */
package com.github.mirs.banxiaoxiao.framework.core.util.textab.cell;

import com.github.mirs.banxiaoxiao.framework.core.util.textab.cell.base.FunctionWithHeight;

import java.util.Collection;

public final class TopAlign extends FunctionWithHeight
{
    public static final TopAlign INSTANCE = new TopAlign();

    @Override
    public Collection<String> apply(Integer height, Collection<String> cell) {
        cell = BottomTruncate.INSTANCE.apply(height, cell);
        cell = BottomPad     .INSTANCE.apply(height, cell);
        return cell;
    }

}
