package org.jenkinsci.plugins.qywechat.model;

import hudson.util.Secret;

/**
 * 配置项
 * @author jiaju
 */
public class NotificationConfig {

    /**
     * 企业微信WebHook地址
     */
    public String webhookUrl = "";
    /**
     * 通知用户ID
     */
    public String mentionedId = "";
    /**
     * 通知用户手机
     */
    public String mentionedMobile = "";

    /**
     * 是否显示change log
     */
    public boolean showChangeLog = false;

    /**
     * 更多信息备注
     */
    public String moreInfo = "";
    /**
     * 主题名称
     */
    public String topicName = "";

    /**
     * 使用代理
     */
    public boolean useProxy = false;
    /**
     * 代理主机
     */
    public String proxyHost = "";
    /**
     * 代理端口
     */
    public int proxyPort = 8080;
    /**
     * 代理用户名
     */
    public String proxyUsername = "";
    /**
     * 代理密码
     */
    public Secret proxyPassword = null;

    /**
     * 仅在失败通知 艾特通知
     */
    public boolean failNotify = false;


    /**
     * 仅失败的时候发送企业微信通知
     */
    public boolean onlyFailSendQyWechatNotify = false;
}
