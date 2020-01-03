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
     * 仅在失败@
     */
    public boolean failNotify = false;

    /**
     * 发送开始构建消息
     */
    public boolean startBuild = false;

    /**
     * 仅在失败时发送
     */
    public boolean failSend = false;

    /**
     * 仅在成功时发送
     */
    public boolean successSend = false;

    /**
     * 仅在构建中断时发送
     */
    public boolean aboutSend = false;

    /**
     * 仅在不稳定构建时发送
     */
    public boolean unstableSend = false;
}
