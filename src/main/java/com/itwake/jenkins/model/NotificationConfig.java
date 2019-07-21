package com.itwake.jenkins.model;

/**
 * @author jiaju
 * @email jiaju@dvnuo.com
 * @date 2019/7/14 16:35
 */
public class NotificationConfig {

    public String webhookUrl;
    public String mentionedId;
    public String mentionedMobile;
    public String topicName;

    public boolean useProxy;
    public String proxyHost;
    public int proxyPort;
    public String proxyUsername;
    public String proxyPassword;

    public boolean failNotify;

}
