package com.itwake.jenkins;

import com.itwake.jenkins.dto.BuildBeginInfo;
import com.itwake.jenkins.dto.BuildMentionedInfo;
import com.itwake.jenkins.dto.BuildOverInfo;
import com.itwake.jenkins.model.NotificationConfig;
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

public class QyWechatNotification extends Publisher implements SimpleBuildStep {

    private String webhookUrl;

    private String mentionedId;

    private String mentionedMobile;

    private boolean failNotify;

    private String projectName;

    @Extension
    public static final DescriptorImpl DESCRIPTOR = new DescriptorImpl();

    @DataBoundConstructor
    public QyWechatNotification() {
    }

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
        if(StringUtils.isEmpty(config.webhookUrl)){
            return true;
        }
        this.projectName = build.getProject().getFullDisplayName();
        BuildBeginInfo buildInfo = new BuildBeginInfo(this.projectName, build, config);

        String req = buildInfo.toJSONString();
        listener.getLogger().println("推送通知" + req);

        String []webhookUrls = config.webhookUrl.split(",");
        for (String webhookUrl : webhookUrls){
            push(listener.getLogger(), webhookUrl, req, config);
        }
        return true;
    }

    @Override
    public void perform(Run<?, ?> run, FilePath workspace, Launcher launcher, TaskListener listener) throws InterruptedException, IOException {
        NotificationConfig config = getConfig(run.getEnvironment(listener));
        if(StringUtils.isEmpty(config.webhookUrl)){
            return;
        }
        Result result = run.getResult();

        //设置当前项目名称
        if(run instanceof AbstractBuild){
            this.projectName = ((AbstractBuild)run).getProject().getFullDisplayName();
        }

        //run.getTimestampString()
        BuildOverInfo buildInfo = new BuildOverInfo(this.projectName, run, config);

        String req = buildInfo.toJSONString();
        listener.getLogger().println("推送通知" + req);

        //推送
        push(listener.getLogger(), config.webhookUrl, req, config);

        listener.getLogger().println("项目运行结果[" + result + "]");
        //运行不成功
        if(result==null){
            return;
        }
        if(!result.equals(Result.SUCCESS) || !config.failNotify){
            BuildMentionedInfo consoleInfo = new BuildMentionedInfo(run, config);

            req = consoleInfo.toJSONString();
            listener.getLogger().println("推送通知" + req);

            String []webhookUrls = config.webhookUrl.split(",");
            for (String webhookUrl : webhookUrls){
                push(listener.getLogger(), webhookUrl, req, config);
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
        try {
            String msg = NotificationUtil.push(url, data, config);
            logger.println("通知结果" + msg);
        }catch (HttpProcessException e){
            logger.println("通知异常" + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.BUILD;
    }

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
}

