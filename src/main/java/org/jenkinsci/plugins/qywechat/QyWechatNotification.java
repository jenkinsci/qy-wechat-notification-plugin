package org.jenkinsci.plugins.qywechat;

import org.jenkinsci.plugins.qywechat.dto.BuildBeginInfo;
import org.jenkinsci.plugins.qywechat.dto.BuildMentionedInfo;
import org.jenkinsci.plugins.qywechat.dto.BuildOverInfo;
import org.jenkinsci.plugins.qywechat.model.NotificationConfig;
import com.arronlong.httpclientutil.exception.HttpProcessException;
import hudson.EnvVars;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.*;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Publisher;
import jenkins.tasks.SimpleBuildStep;
import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

import java.io.IOException;
import java.io.PrintStream;

/**
 * 企业微信构建通知
 * @author jiaju
 */
public class QyWechatNotification extends Publisher implements SimpleBuildStep {

    private String webhookUrl;

    private String mentionedId;

    private String mentionedMobile;

    private boolean failNotify;
    private boolean failSend = false;
    private boolean successSend = false;
    private boolean aboutSend = false;
    private boolean unstableSend = false;
    private boolean startBuild = false;

    private String projectName;

    @Extension
    public static final DescriptorImpl DESCRIPTOR = new DescriptorImpl();

    @DataBoundConstructor
    public QyWechatNotification() {
    }

    /**
     * 开始执行构建
     * @param build
     * @param listener
     * @return
     */
    @Override
    public boolean prebuild(AbstractBuild<?, ?> build, BuildListener listener) {
        EnvVars envVars;
        try {
            envVars = build.getEnvironment(listener);
        } catch (Exception e) {
            listener.getLogger().println("读取环境变量异常" + e.getMessage());
            envVars = new EnvVars();
        }
        NotificationConfig config = getConfig(envVars);
        if(StringUtils.isEmpty(config.webhookUrl) || !config.startBuild){
            return true;
        }
        this.projectName = build.getProject().getFullDisplayName();
        BuildBeginInfo buildInfo = new BuildBeginInfo(this.projectName, build, config);

        String req = buildInfo.toJSONString();
        listener.getLogger().println("推送通知" + req);

        //执行推送
        push(listener.getLogger(), config.webhookUrl, req, config);
        return true;
    }

    /**
     * 构建结束
     * @param run
     * @param workspace
     * @param launcher
     * @param listener
     * @throws InterruptedException
     * @throws IOException
     */
    @Override
    public void perform(Run<?, ?> run, FilePath workspace, Launcher launcher, TaskListener listener) throws InterruptedException, IOException {
        NotificationConfig config = getConfig(run.getEnvironment(listener));
        if(StringUtils.isEmpty(config.webhookUrl)){
            return;
        }
        Result result = run.getResult();

        if (    (result == Result.SUCCESS && config.successSend) ||
                (result == Result.ABORTED && config.aboutSend) ||
                (result == Result.FAILURE && config.failSend) ||
                (result == Result.UNSTABLE && config.unstableSend)) {
            //设置当前项目名称
            if(run instanceof AbstractBuild){
                this.projectName = run.getParent().getFullDisplayName() ;
            }

            //构建结束通知
            BuildOverInfo buildInfo = new BuildOverInfo(this.projectName, run, config);

            String req = buildInfo.toJSONString();
            listener.getLogger().println("推送通知" + req);

            //推送结束通知
            push(listener.getLogger(), config.webhookUrl, req, config);
            listener.getLogger().println("项目运行结果[" + result + "]");

            //运行不成功
            if(result==null){
                return;
            }

            //仅在失败的时候，才进行@
            if(!result.equals(Result.SUCCESS) || !config.failNotify){
                //没有填写UserId和手机号码
                if(StringUtils.isEmpty(config.mentionedId) && StringUtils.isEmpty(config.mentionedMobile)){
                    return;
                }

                //构建@通知
                BuildMentionedInfo consoleInfo = new BuildMentionedInfo(run, config);

                req = consoleInfo.toJSONString();
                listener.getLogger().println("推送通知" + req);
                //执行推送
                push(listener.getLogger(), config.webhookUrl, req, config);
            }
        }
    }

    /**
     * 推送消息
     * @param logger
     * @param url
     * @param data
     * @param config
     */
    private void push(PrintStream logger, String url, String data, NotificationConfig config){
        String []urls;
        if(url.contains(",")){
            urls = url.split(",");
        }else{
            urls = new String[]{ url };
        }
        for(String u : urls){
            try {
                String msg = NotificationUtil.push(u, data, config);
                logger.println("通知结果" + msg);
            }catch (HttpProcessException e){
                logger.println("通知异常" + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @Override
    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.BUILD;
    }

    /**
     * 读取配置，将当前Job与全局配置整合
     * @param envVars
     * @return
     */
    public NotificationConfig getConfig(EnvVars envVars){
        NotificationConfig config = DESCRIPTOR.getUnsaveConfig();
        if(StringUtils.isNotEmpty(webhookUrl)){
            config.webhookUrl = webhookUrl;
        }
        if(StringUtils.isNotEmpty(mentionedId)){
            config.mentionedId = mentionedId;
        }
        if(StringUtils.isNotEmpty(mentionedMobile)){
            config.mentionedMobile = mentionedMobile;
        }
        config.failNotify = failNotify;
        config.aboutSend = aboutSend;
        config.unstableSend = unstableSend;
        config.failSend = failSend;
        config.successSend = successSend;
        config.startBuild = startBuild;

        //使用环境变量
        if(config.webhookUrl.contains("$")){
            String val = NotificationUtil.replaceMultipleEnvValue(config.webhookUrl, envVars);
            config.webhookUrl = val;
        }
        if(config.mentionedId.contains("$")){
            String val = NotificationUtil.replaceMultipleEnvValue(config.mentionedId, envVars);
            config.mentionedId = val;
        }
        if(config.mentionedMobile.contains("$")){
            String val = NotificationUtil.replaceMultipleEnvValue(config.mentionedMobile, envVars);
            config.mentionedMobile = val;
        }
        return config;
    }

    /** 下面为GetSet方法，当前Job保存时进行绑定 **/

    @DataBoundSetter
    public void setWebhookUrl(String webhookUrl) {
        this.webhookUrl = webhookUrl;
    }

    @DataBoundSetter
    public void setMentionedId(String mentionedId) {
        this.mentionedId = mentionedId;
    }

    @DataBoundSetter
    public void setMentionedMobile(String mentionedMobile) {
        this.mentionedMobile = mentionedMobile;
    }

    @DataBoundSetter
    public void setFailNotify(boolean failNotify) {
        this.failNotify = failNotify;
    }

    public boolean isFailSend() {
        return failSend;
    }

    @DataBoundSetter
    public void setFailSend(boolean failSend) {
        this.failSend = failSend;
    }

    public boolean isSuccessSend() {
        return successSend;
    }

    @DataBoundSetter
    public void setSuccessSend(boolean successSend) {
        this.successSend = successSend;
    }

    public boolean isAboutSend() {
        return aboutSend;
    }

    @DataBoundSetter
    public void setAboutSend(boolean aboutSend) {
        this.aboutSend = aboutSend;
    }

    public boolean isUnstableSend() {
        return unstableSend;
    }

    @DataBoundSetter
    public void setUnstableSend(boolean unstableSend) {
        this.unstableSend = unstableSend;
    }

    public boolean isStartBuild() {
        return startBuild;
    }

    @DataBoundSetter
    public void setStartBuild(boolean startBuild) {
        this.startBuild = startBuild;
    }

    public String getWebhookUrl() {
        return webhookUrl;
    }

    public String getMentionedId() {
        return mentionedId;
    }

    public String getMentionedMobile() {
        return mentionedMobile;
    }

    public boolean isFailNotify() {
        return failNotify;
    }
}

