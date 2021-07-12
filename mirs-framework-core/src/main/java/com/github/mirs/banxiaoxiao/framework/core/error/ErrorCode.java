package com.github.mirs.banxiaoxiao.framework.core.error;

/**
 * 标准错误码接口，任何业务处理异常都必须实现错误码接口，并符合错误码格式规范
 * <table border="1" style="table-layout:fixed;width:800px;">
 * <tr>
 * <td>位置</td>
 * <td>1</td>
 * <td>2</td>
 * <td>3</td>
 * <td>4</td>
 * <td>5</td>
 * <td>6</td>
 * <td>7</td>
 * <td>8</td>
 * <td>9</td>
 * <td>10</td>
 * <td>11</td>
 * <td>12</td>
 * <td>13</td>
 * </tr>
 * <tr>
 * <td>示例</td>
 * <td>A</td>
 * <td>E</td>
 * <td>0</td>
 * <td>1</td>
 * <td>1</td>
 * <td>0</td>
 * <td>0</td>
 * <td>0</td>
 * <td>1</td>
 * <td>0</td>
 * <td>1</td>
 * <td>2</td>
 * <td>2</td>
 * </tr>
 * <tr>
 * <td>说明</td>
 * <td colspan="2">固定标识</td>
 * <td colspan="2">版本号</td>
 * <td>错误码类型</td>
 * <td>错误码级别</td>
 * <td colspan="3">系统编码</td>
 * <td colspan="4">错误编码</td>
 * </tr>
 * </table>
 * <p>
 * 错误码类型 : 0-业务服务处理错误；1-系统错误<br>
 * 错误码级别 : 0-常规信息；1-警告；2-错误；3-严重<br>
 * 系统编码 : 系统立项之初由架构师确定编码值<br>
 * 错误编码 : 系统owner定义，建议根据业务类型合理划分4位错误码
 * </p>
 * 
 * @author erxiao
 *
 */
public interface ErrorCode {

	/**
	 * 返回错误吗字符串，例如：AE01100010122
	 * 
	 * @return
	 */
	public String getCode();
	
	/**
	 * 错误描述
	 * 
	 * @return
	 */
	public String getMessage();
	
	public default BizException throwException() {
	    throw new BizException(this);
	}
	
}
