package org.jenkinsci.plugins.qywechat.dto;

import org.jenkinsci.plugins.qywechat.NotificationUtil;
import org.jenkinsci.plugins.qywechat.model.NotificationConfig;
import hudson.model.Result;
import hudson.model.Run;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 结束构建的通知信息
 * @author jiaju
 */
public class BuildOverInfo {

    /**
     * 使用时间，毫秒
     */
    private String useTimeString = "";

    /**
     * 本次构建控制台地址
     */
    private String consoleUrl;

    /**
     * 工程名称
     */
    private String projectName;

    /**
     * 环境名称
     */
    private String topicName = "";

    /**
     * 执行结果
     */
    private Result result;

    public BuildOverInfo(String projectName, Run<?, ?> run, NotificationConfig config){
        //使用时间
        this.useTimeString = run.getTimestampString();
        //控制台地址
        StringBuilder urlBuilder = new StringBuilder();
        String jenkinsUrl = NotificationUtil.getJenkinsUrl();
        if(StringUtils.isNotEmpty(jenkinsUrl)){
            String buildUrl = run.getUrl();
            urlBuilder.append(jenkinsUrl);
            if(!jenkinsUrl.endsWith("/")){
                urlBuilder.append("/");
            }
            urlBuilder.append(buildUrl);
            if(!buildUrl.endsWith("/")){
                urlBuilder.append("/");
            }
            urlBuilder.append("console");
        }
        this.consoleUrl = urlBuilder.toString();
        //工程名称
        this.projectName = projectName;
        //环境名称
        if(config.topicName!=null){
            topicName = config.topicName;
        }
        //结果
        result = run.getResult();
    }

    public String toJSONString(){
        //组装内容
        StringBuilder content = new StringBuilder();
        if(StringUtils.isNotEmpty(topicName)){
            content.append(this.topicName);
        }
        content.append("<font color=\"info\">【" + this.projectName + "】</font>构建" + getStatus() + "\n");
        content.append(" >构建用时：<font color=\"comment\">" +  this.useTimeString + "</font>\n");
        if(StringUtils.isNotEmpty(this.consoleUrl)) {
            content.append(" >[查看控制台](" + this.consoleUrl + ")");
        }

        Map markdown = new HashMap<String, Object>();
        markdown.put("content", content.toString());

        Map data = new HashMap<String, Object>();
        data.put("msgtype", "markdown");
        data.put("markdown", markdown);

        String req = JSONObject.fromObject(data).toString();
        return req;
    }

    private String getStatus(){
        if(null != result && result.equals(Result.FAILURE)){
            return "<font color=\"warning\">失败!!!</font>\uD83D\uDE2D";
        }else if(null != result && result.equals(Result.ABORTED)){
            return "<font color=\"warning\">中断!!</font>\uD83D\uDE28";
        }else if(null != result && result.equals(Result.UNSTABLE)){
            return "<font color=\"warning\">异常!!</font>\uD83D\uDE41";
        }else if(null != result && result.equals(Result.SUCCESS)){
            int max=successFaces.length-1,min=0;
            int ran = (int) (Math.random()*(max-min)+min);
            return "<font color=\"info\">成功~</font>" + successFaces[ran];
        }
        return "<font color=\"warning\">情况未知</font>";
    }

    String []successFaces = {
            "\uD83D\uDE0A", "\uD83D\uDE04", "\uD83D\uDE0E", "\uD83D\uDC4C", "\uD83D\uDC4D", "(o´ω`o)و", "(๑•̀ㅂ•́)و✧"
    };


}
