package org.jenkinsci.plugins.qywechat;

import org.jenkinsci.plugins.qywechat.model.NotificationConfig;
import com.arronlong.httpclientutil.exception.HttpProcessException;
import hudson.model.AbstractProject;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Publisher;
import hudson.util.FormValidation;
import hudson.util.Secret;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

import java.util.logging.Logger;

/**
 * 数据绑定
 * @author jiaju
 */
@Symbol("qyWechatNotification")
public class DescriptorImpl extends BuildStepDescriptor<Publisher> {

    private static final Logger logger = Logger.getLogger(DescriptorImpl.class.getName());

    private NotificationConfig config = new NotificationConfig();

    public DescriptorImpl() {
        super(QyWechatNotification.class);
        load();
    }

    public String getWebhookUrl() {
        return config.webhookUrl;
    }

    public void setWebhookUrl(String webhookUrl) {
        config.webhookUrl = webhookUrl;
    }

    public String getTopicName() {
        return config.topicName;
    }

    public void setTopicName(String topicName) {
        config.topicName = topicName;
    }

    public String getMentionedId() {
        return config.mentionedId;
    }

    public void setMentionedId(String mentionedId) {
        config.mentionedId = mentionedId;
    }

    public String getMentionedMobile() {
        return config.mentionedMobile;
    }

    public void setMentionedMobile(String mentionedMobile) {
        config.mentionedMobile = mentionedMobile;
    }

    public boolean isUseProxy() {
        return config.useProxy;
    }

    public void setUseProxy(boolean useProxy) {
        config.useProxy = useProxy;
    }

    public boolean isFailNotify() {
        return config.failNotify;
    }

    public void setFailNotify(boolean failNotify) {
        config.failNotify = failNotify;
    }

    public String getProxyHost() {
        return config.proxyHost;
    }

    public void setProxyHost(String proxyHost) {
        config.proxyHost = proxyHost;
    }

    public int getProxyPort() {
        return config.proxyPort;
    }

    public void setProxyPort(int proxyPort) {
        config.proxyPort = proxyPort;
    }

    public String getProxyUsername() {
        return config.proxyUsername;
    }

    public void setProxyUsername(String proxyUsername) {
        config.proxyUsername = proxyUsername;
    }

    public String getProxyPassword() {
        return Secret.toString(config.proxyPassword);
    }

    public void setProxyPassword(String proxyPassword) {
        config.proxyPassword = Secret.fromString(proxyPassword);
    }

    public boolean isFailSend() {
        return config.failSend;
    }

    public void setFailSend(boolean failSend) {
        config.failSend = failSend;
    }

    public boolean isSuccessSend() {
        return config.successSend;
    }

    public void setSuccessSend(boolean successSend) {
        config.successSend = successSend;
    }

    public boolean isAboutSend() {
        return config.aboutSend;
    }

    public void setAboutSend(boolean aboutSend) {
        config.aboutSend = aboutSend;
    }

    public boolean isUnstableSend() {
        return config.unstableSend;
    }

    public void setUnstableSend(boolean unstableSend) {
        config.unstableSend = unstableSend;
    }

    public boolean isStartBuild() {
        return config.startBuild;
    }

    public void setStartBuild(boolean startBuild) {
        config.startBuild = startBuild;
    }

    /**
     * 获取配置，不用于保存
     * @return
     */
    public NotificationConfig getUnsaveConfig(){
        NotificationConfig unsaveConfig = new NotificationConfig();

        unsaveConfig.webhookUrl = config.webhookUrl;
        unsaveConfig.mentionedId = config.mentionedId;
        unsaveConfig.mentionedMobile = config.mentionedMobile;
        unsaveConfig.topicName = config.topicName;

        unsaveConfig.useProxy = config.useProxy;
        unsaveConfig.proxyHost = config.proxyHost;
        unsaveConfig.proxyPort = config.proxyPort;
        unsaveConfig.proxyUsername = config.proxyUsername;
        unsaveConfig.proxyPassword = config.proxyPassword;

        unsaveConfig.failNotify = config.failNotify;
        unsaveConfig.failSend = config.failSend;
        unsaveConfig.aboutSend = config.aboutSend;
        unsaveConfig.successSend = config.successSend;
        unsaveConfig.unstableSend = config.unstableSend;
        unsaveConfig.startBuild = config.startBuild;

        return unsaveConfig;
    }

    /**
     * 测试代理连接
     * @param proxyHost
     * @param proxyPort
     * @param proxyUsername
     * @param proxyPassword
     * @return
     */
    public FormValidation doTestProxy(@QueryParameter String proxyHost, @QueryParameter int proxyPort, @QueryParameter String proxyUsername, @QueryParameter String proxyPassword){
        String TEST_URL = "https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key=0";

        if(StringUtils.isEmpty(proxyHost)){
            return FormValidation.error("服务器不能为空");
        }

        NotificationConfig buildConfig = new NotificationConfig();
        buildConfig.useProxy = true;
        buildConfig.proxyHost = proxyHost;
        buildConfig.proxyPort = proxyPort;
        buildConfig.proxyUsername = proxyUsername;
        buildConfig.proxyPassword = Secret.fromString(proxyPassword);
        try{
            NotificationUtil.push(TEST_URL, "", buildConfig);
            return FormValidation.ok("测试成功");
        } catch (HttpProcessException e) {
            e.printStackTrace();
            return FormValidation.error("连接异常" + e.getMessage());
        }
    }

    @Override
    public boolean isApplicable(Class<? extends AbstractProject> aClass) {
        return true;
    }

    @Override
    public boolean configure(StaplerRequest req, JSONObject json) throws FormException {
        config.webhookUrl = json.getString("webhookUrl");
        config.topicName = json.getString("topicName");
        config.mentionedId = json.getString("mentionedId");
        config.mentionedMobile = json.getString("mentionedMobile");
        config.useProxy = json.get("useProxy")!=null;
        if(config.useProxy && json.get("useProxy") instanceof JSONObject){
            JSONObject jsonObject = json.getJSONObject("useProxy");
            config.proxyHost = jsonObject.getString("proxyHost");
            config.proxyPort = jsonObject.getInt("proxyPort");
            config.proxyUsername = jsonObject.getString("proxyUsername");
            config.proxyPassword = Secret.fromString(jsonObject.getString("proxyPassword"));
        }
        save();
        return super.configure(req, json);
    }

    @Override
    public String getDisplayName() {
        return "企业微信通知";
    }

    @Override
    public String getHelpFile() {
        return "/plugin/qy-wechat-notification/help.html";
    }

}
