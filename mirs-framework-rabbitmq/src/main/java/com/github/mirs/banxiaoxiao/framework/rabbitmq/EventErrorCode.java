package com.github.mirs.banxiaoxiao.framework.rabbitmq;

/**
 * @author erxiao 2017年2月8日
 */
public class EventErrorCode {
	public static final  int SUCCEED=0x0000;   //事件处理成功
	public static final  int INVALID_MESSAGE=0x0001;    //非法请求
	public static final  int CHANNEL_INITIALIZE_EXCEPTION=0x0002;    //mq初始化异常
	public static final  int NETWORK_EXCEPTION=0x0003;    //网络异常
	
	public static final  int PUBLISH_EXCEPTION =0x0101;//事件发布处理异常
	public static final  int PUBLISH_TIMEOUT=0x0102;    //事件处理超时

	public static final  int SUBSCRIBE_EXCEPTION =0x0201;//事件订阅处理异常
	public static final  int SUBSCRIBE_LISTEN_EXCEPTION =0x0202;//事件订阅监听异常
	public static final  int SUBSCRIBE_LISTEN_INVALID =0x0203;//无效监听器
}
