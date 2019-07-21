package com.itwake.jenkins.dto;

import com.itwake.jenkins.NotificationUtil;
import com.itwake.jenkins.model.NotificationConfig;
import hudson.model.Run;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 控制台链接信息
 * @author jiaju
 * @email jiaju@dvnuo.com
 * @date 2019/7/13 16:00
 */
public class BuildConsoleInfo {

    /**
     * 本次构建控制台地址
     */
    String consoleUrl;

    /**
     * 工程名称
     */
    String projectName;

    /**
     * 环境名称
     */
    String topicName = "";

    public BuildConsoleInfo(String projectName, Run<?, ?> run, NotificationConfig config){
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
    }

    public String toJSONString(){
        //组装内容
        StringBuilder content = new StringBuilder();
        content.append("[查看【" + this.projectName + "】控制台](" + this.consoleUrl + ")");

        Map markdown = new HashMap<String, Object>();
        markdown.put("content", content.toString());

        Map data = new HashMap<String, Object>();
        data.put("msgtype", "markdown");
        data.put("markdown", markdown);

        String req = JSONObject.fromObject(data).toString();
        return req;
    }



}
