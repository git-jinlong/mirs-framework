package com.github.mirs.banxiaoxiao.framework.common.util;

/**
 * <pre>
 * 将多个int正整型合并为一个long型进行存储，并通过xor运算计算两组int是否匹配
 * MultiEnumVal64 int正整数取值必须大于0，0值用于MultiVal64本身特殊计算使用，<=0表示不参与匹配计算。
 * 如：业务中需要根据行人是否戴眼镜、是否戴帽子、衣服颜色来进行查询
 * 是否戴眼镜取值： 1 未知 ; 2 戴眼镜 ; 3 不带眼镜 ;
 * 是否戴帽子取值： 1 未知 ; 2 戴帽子 ; 3 不带帽子 ;
 * 衣服的颜色取值： 1 未知 ; 2 红色 ; 3 绿色 ; 4 白色 ;
 * </br>
 * 假设某一个抓拍体型数据为：眼镜=2; 帽子=1; 颜色=4
 * 1、构建MultiVal64数据
 * <code>
 *    MultiEnumVal64 mv64 = new MultiEnumVal64(3,3,4);  //创建MultiVal64对象，定义每一种属性的最大取值 ，定义的顺序为：眼镜、帽子、颜色
 *    mv64.build(2,1,4);                        //构造数据
 *    long longVal = mv64.getLong();            //获取long值，可存储于数据库           
 * </code>  
 * 2、java编码匹配MultiVal64数据 ，沿用1、的例子
 * a、场景1查询条件是：戴着帽子且戴着眼镜
 * <code>
 *    MultiEnumVal64 mv642 = new MultiEnumVal64(3,3,4);  //创建MultiVal64对象，定义每一种属性的最大取值  ，定义的顺序为：眼镜、帽子、颜色
 *    mv642.build(2,2,-1);                       //构造数据，-1表示不参与匹配，原始数据不论取何值都可以匹配成功
 *    System.out.printl(mv642.xorTrue(mv64));    //匹配数据，该场景返回false，因为是否戴眼镜项匹配不成功
 * </code>
 * b、场景1查询条件是：戴着眼镜穿白色衣服
 * <code>
 *    MultiEnumVal64 mv643 = new MultiEnumVal64(3,3,4);  //创建MultiVal64对象，定义每一种属性的最大取值  ，定义的顺序为：眼镜、帽子、颜色
 *    mv643.build(2,-1,4);                       //构造数据，-1表示不参与匹配，原始数据不论取何值都可以匹配成功
 *    System.out.printl(mv643.xorTrue(mv64));    //匹配数据，该场景返回true
 * </code>
 * 3、数据库sql写法1，以场景2.->b为例
 * <code>
 * String sql = "";
 * if(mv643.isHasIgnoreVal()) {
 *    sql = "select * from table where column_name (";
 *    for (int[] pos : mv643.getIgnorePos()) {
 *       if (pos[0] > 0 || pos[1] > 0) {
 *           for (int start = pos[1]; start <= pos[0]; start++) {
 *               sql + "& ~(1 << " + start + ")";
 *           }
 *       }
 *     }
 *     sql += ") ^ " + mv643.getLong() + " = 0;";
 * } else {
 *    sql = "select * from table where column_name ^ " + mv643.getLong() + " =0;";
 * }
 * </code>
 * 4、数据库sql写法2，以场景2.->b为例
 * <code>
 * String sql = "";
 * if(mv643.isHasIgnoreVal()) {
 *    sql = "select * from table where (column_name & ~";
 *    sql += mv643.getComparVal();
 *    sql += ") ^ " + mv643.getLong() + " = 0;";
 * } else {
 *    sql = "select * from table where column_name ^ " + mv643.getLong() + " =0;";
 * }
 * </code>
 * </pre>
 * 
 * @author zcy 2019年3月22日
 */
public class MultiEnumVal64 {

    private int[] config;

    private int[][] ignorePos;

    private long longVal;

    private int length;

    private boolean hasIgnoreVal = false;

    /**
     * 每一组int取值的最大值
     * 
     * @param maxValConfig
     */
    public MultiEnumVal64(int... maxValConfig) {
        this.config = new int[maxValConfig.length];
        this.ignorePos = new int[maxValConfig.length][2];
        for (int index = 0; index < maxValConfig.length; index++) {
            int maxVal = maxValConfig[index];
            if (maxVal < 0) {
                throw new IllegalArgumentException("value must be greater than 0");
            }
            int tempLength = Integer.toBinaryString(maxVal).length();
            this.config[index] = tempLength;
            length += tempLength;
        }
        if (length + 1 > 63) {
            throw new IllegalArgumentException(length + " over the maximum digit 62");
        }
    }

    /**
     * 数值<=0 表示不参与计算
     * 
     * @param values
     * @return
     */
    public MultiEnumVal64 build(int... values) {
        if (values.length != this.config.length) {
            throw new IllegalArgumentException("value array length and config mismatch,config length is " + this.config.length
                    + " , but value array length is " + values.length);
        }
        StringBuilder binaryStr = new StringBuilder("1");
        int preLength = 1;
        for (int i = 0; i < values.length; i++) {
            int binaryLength = this.config[i];
            int maxConfigVal = (1 << binaryLength);
            int val = values[i];
            if (val > maxConfigVal) {
                throw new IllegalArgumentException("the " + i + " value should be less than or equal to " + maxConfigVal);
            }
            if (val <= 0) {
                hasIgnoreVal = true;
                for (int j = 0; j < binaryLength; j++) {
                    binaryStr.append("0");
                }
                ignorePos[i][0] = this.length - preLength;
                ignorePos[i][1] = this.length - preLength - (this.config[i] - 1);
            } else {
                String binaryVal = Integer.toBinaryString(val);
                for (int j = 0; j < binaryLength - binaryVal.length(); j++) {
                    binaryStr.append("0");
                }
                binaryStr.append(binaryVal);
                ignorePos[i][0] = -1;
                ignorePos[i][1] = -1;
            }
            preLength += binaryLength;
        }
        this.longVal = Long.parseLong(binaryStr.toString(), 2);
        return this;
    }

    public MultiEnumVal64 build(long val) {
        this.longVal = val;
        build(getVals());
        return this;
    }

    public long getLong() {
        return longVal;
    }

    public int[] getVals() {
        int[] values = new int[this.config.length];
        String binary = Long.toBinaryString(getLong());
        int binaryLength = binary.length();
        int endIndex = binaryLength;
        for (int i = this.config.length - 1; i >= 0; i--) {
            int startIndex = endIndex - this.config[i];
            if (startIndex < 1) {
                if (endIndex > 1) {
                    startIndex = 1;
                    values[i] = Integer.parseInt(binary.substring(startIndex, endIndex), 2);
                } else {
                    values[i] = -1;
                }
            } else {
                values[i] = Integer.parseInt(binary.substring(startIndex, endIndex), 2);
            }
            if (values[i] == 0) {
                values[i] = -1;
            }
            endIndex = startIndex;
        }
        return values;
    }

    public int[][] getIgnorePos() {
        return ignorePos;
    }
    
    public int getLength() {
        return length;
    }

    public long getComparVal() {
        long m1 = 0L;
        if (hasIgnoreVal) {
            for (int[] pos : ignorePos) {
                if (pos[0] > 0 || pos[1] > 0) {
                    for (int start = pos[1]; start <= pos[0]; start++) {
                        m1 = m1 ^ (1L << start);
                    }
                }
            }
        }
        return m1;
    }

    public long transform(long val) {
        return val &~ getComparVal();
    }
    
    public long xor(long val) {
        long v1 = transform(val);
        return v1 ^ getLong();
    }

    public long xor(MultiEnumVal64 mv) {
        return xor(mv.getLong());
    }

    public boolean xorTrue(long val) {
        return xor(val) == 0;
    }

    public boolean xorTrue(MultiEnumVal64 mv) {
        return xor(mv) == 0;
    }

    public boolean isHasIgnoreVal() {
        return hasIgnoreVal;
    }

}
