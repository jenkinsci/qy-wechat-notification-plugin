package com.itwake.jenkins.dto;

import com.itwake.jenkins.NotificationUtil;
import com.itwake.jenkins.model.NotificationConfig;
import hudson.model.AbstractBuild;
import hudson.model.ParameterValue;
import hudson.model.ParametersAction;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 开始构建信息
 * @author jiaju
 * @email jiaju@dvnuo.com
 * @date 2019/7/13 16:00
 */
public class BuildBeginInfo {

    /**
     * 请求参数
     */
    Map params = new HashMap<String, Object>();

    /**
     * 预计时间，毫秒
     */
    Long durationTime = 0L;

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

    public BuildBeginInfo(String projectName, AbstractBuild<?, ?> build, NotificationConfig config){
        //获取请求参数
        List<ParametersAction> parameterList = build.getActions(ParametersAction.class);
        if(parameterList!=null && parameterList.size()>0){
            for(ParametersAction p : parameterList){
                for(ParameterValue pv : p.getParameters()){
                    this.params.put(pv.getName(), pv.getValue());
                }
            }
        }
        //预计时间
        if(build.getProject().getEstimatedDuration()>0){
            this.durationTime = build.getProject().getEstimatedDuration();
        }
        //控制台地址
        String jenkinsUrl = NotificationUtil.getJenkinsUrl();
        String buildUrl = build.getUrl();
        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append(jenkinsUrl);
        if(!jenkinsUrl.endsWith("/")){
            urlBuilder.append("/");
        }
        urlBuilder.append(buildUrl);
        if(!buildUrl.endsWith("/")){
            urlBuilder.append("/");
        }
        urlBuilder.append("console");
        this.consoleUrl = urlBuilder.toString();
        //工程名称
        this.projectName = projectName;
        //环境名称
        if(config.topicName!=null){
            topicName = config.topicName;
        }
    }

    public String toJSONString(){
        //参数组装
        StringBuffer paramBuffer = new StringBuffer();
        for(Object key : params.keySet()){
            paramBuffer.append(key);
            paramBuffer.append("=");
            paramBuffer.append(params.get(key));
            paramBuffer.append(", ");
        }
        if(paramBuffer.length()==0){
            paramBuffer.append("无");
        }else{
            paramBuffer.deleteCharAt(paramBuffer.length()-2);
        }

        //耗时预计
        String durationTimeStr = "无";
        if(durationTime>0){
            Long l = durationTime / (1000 * 60);
            durationTimeStr = l + "分钟";
        }

        //组装内容
        StringBuilder content = new StringBuilder();
        if(StringUtils.isNotEmpty(topicName)){
            content.append(this.topicName);
        }
        content.append("<font color=\"info\">【" + this.projectName + "】</font>开始构建\n");
        content.append(" >构建参数：<font color=\"comment\">" + paramBuffer.toString() + "</font>\n");
        content.append(" >预计用时：<font color=\"comment\">" +  durationTimeStr + "</font>\n");
        content.append(" >[查看控制台](" + this.consoleUrl + ")");

        Map markdown = new HashMap<String, Object>();
        markdown.put("content", content.toString());

        Map data = new HashMap<String, Object>();
        data.put("msgtype", "markdown");
        data.put("markdown", markdown);

        String req = JSONObject.fromObject(data).toString();
        return req;
    }



}
