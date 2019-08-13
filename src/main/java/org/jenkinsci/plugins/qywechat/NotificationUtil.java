package org.jenkinsci.plugins.qywechat;

import com.arronlong.httpclientutil.HttpClientUtil;
import com.arronlong.httpclientutil.common.HttpConfig;
import com.arronlong.httpclientutil.exception.HttpProcessException;
import org.jenkinsci.plugins.qywechat.model.NotificationConfig;
import hudson.EnvVars;
import hudson.util.Secret;
import jenkins.model.Jenkins;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClients;

/**
 * 工具类
 * @author jiaju
 */
public class NotificationUtil {

    /**
     * 推送信息
     * @param url
     * @param data
     */
    public static String push(String url, String data, NotificationConfig buildConfig) throws HttpProcessException {
        HttpConfig httpConfig;
        //使用代理请求
        if(buildConfig.useProxy){
            HttpClient httpClient;
            HttpHost proxy = new HttpHost(buildConfig.proxyHost, buildConfig.proxyPort);
            //用户密码
            if(StringUtils.isNotEmpty(buildConfig.proxyUsername) && buildConfig.proxyPassword != null){
                // 设置认证
                CredentialsProvider provider = new BasicCredentialsProvider();
                provider.setCredentials(new AuthScope(proxy), new UsernamePasswordCredentials(buildConfig.proxyUsername, Secret.toString(buildConfig.proxyPassword)));
                httpClient = HttpClients.custom().setDefaultCredentialsProvider(provider).setProxy(proxy).build();
            }else{
                httpClient = HttpClients.custom().setProxy(proxy).build();
            }
            //代理请求
            httpConfig = HttpConfig.custom().client(httpClient).url(url).json(data).encoding("utf-8");
        }else{
            //普通请求
            httpConfig = HttpConfig.custom().url(url).json(data).encoding("utf-8");
        }

        String result = HttpClientUtil.post(httpConfig);
        return result;
    }

    /**
     * 获取Jenkins地址
     * @return
     */
    public static String getJenkinsUrl() {
        String jenkinsUrl = Jenkins.getInstance().getRootUrl();
        if (jenkinsUrl != null && jenkinsUrl.length() > 0 && !jenkinsUrl.endsWith("/")) {
            jenkinsUrl = jenkinsUrl + "/";
        }
        return jenkinsUrl;
    }

    /**
     * 替换多值环境变量
     * @param val
     * @param envVars
     * @return
     */
    public static String replaceMultipleEnvValue(String val, EnvVars envVars) {
        String []vals = val.split(",");
        StringBuilder builder = new StringBuilder();
        for(String v : vals){
            v = replaceEnvValue(v, envVars);
            builder.append(v);
            builder.append(",");
        }
        if(builder.length()>0){
            builder.deleteCharAt(builder.length()-1);
        }
        return builder.toString();
    }

    /**
     * 替换环境变量
     * @param key
     * @param envVars
     * @return
     */
    public static String replaceEnvValue(String key, EnvVars envVars) {
        String val = key;
        if (key.contains("$")){
            key = key.trim();
            key = key.replaceFirst("\\$", "");
            if(key.startsWith("{") && key.endsWith("}")){
                key = key.substring(1, key.length()-2);
            }
            if(envVars.containsKey(key)){
                return envVars.get(key);
            }
            return val;
        }else {
            return key;
        }
    }

}
