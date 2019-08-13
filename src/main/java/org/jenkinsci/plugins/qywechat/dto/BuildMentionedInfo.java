package org.jenkinsci.plugins.qywechat.dto;

import org.jenkinsci.plugins.qywechat.model.NotificationConfig;
import hudson.model.Result;
import hudson.model.Run;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * `@`用户的通知
 * @author jiaju
 */
public class BuildMentionedInfo {

    /**
     * 通知ID
     */
    private String mentionedId = "";

    /**
     * 通知手机号码
     */
    private String mentionedMobile = "";

    public BuildMentionedInfo(Run<?, ?> run, NotificationConfig config){
        //通知ID
        if(config.mentionedId!=null){
            mentionedId = config.mentionedId;
        }
        //通知手机号码
        if(config.mentionedMobile!=null) {
            mentionedMobile = config.mentionedMobile;
        }
    }

    public String toJSONString(){
        List<String> mentionedIdList = new ArrayList<>();
        if(StringUtils.isNotEmpty(mentionedId)){
            String []ids = mentionedId.split(",");
            for(String id : ids){
                if("all".equals(id.toLowerCase())){
                    id = "@all";
                }
                mentionedIdList.add(id);
            }
        }
        List<String> mentionedMobileList = new ArrayList<>();
        if(StringUtils.isNotEmpty(mentionedMobile)){
            String []mobiles = mentionedMobile.split(",");
            for(String mobile : mobiles){
                if("all".equals(mobile.toLowerCase())){
                    mobile = "@all";
                }
                mentionedMobileList.add(mobile);
            }
        }

        Map text = new HashMap<String, Object>();
        text.put("mentioned_list", mentionedIdList);
        text.put("mentioned_mobile_list", mentionedMobileList);

        Map data = new HashMap<String, Object>();
        data.put("msgtype", "text");
        data.put("text", text);

        String req = JSONObject.fromObject(data).toString();
        return req;
    }

}
