package org.jenkinsci.plugins.qywechat;

import com.arronlong.httpclientutil.HttpClientUtil;
import com.arronlong.httpclientutil.common.HttpConfig;
import com.arronlong.httpclientutil.exception.HttpProcessException;
import hudson.EnvVars;
import hudson.util.Secret;
import jenkins.model.Jenkins;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.jenkinsci.plugins.qywechat.model.NotificationConfig;

import javax.net.ssl.SSLContext;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

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
    public static String push(String url, String data, NotificationConfig buildConfig) throws HttpProcessException, KeyManagementException, NoSuchAlgorithmException {
        HttpConfig httpConfig;
        HttpClient httpClient;
        HttpClientBuilder httpClientBuilder = HttpClients.custom();
        if(url.startsWith("https")) {
            SSLContext sslContext = SSLContexts.custom().build();
            SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory(
                    sslContext,
                    new String[]{"TLSv1", "TLSv1.1", "TLSv1.2"},
                    null,
                    NoopHostnameVerifier.INSTANCE
            );
            httpClientBuilder.setSSLSocketFactory(sslConnectionSocketFactory);
        }
        //使用代理请求
        if(buildConfig.useProxy){
            HttpHost proxy = new HttpHost(buildConfig.proxyHost, buildConfig.proxyPort);
            //用户密码
            if(StringUtils.isNotEmpty(buildConfig.proxyUsername) && buildConfig.proxyPassword != null){
                // 设置认证
                CredentialsProvider provider = new BasicCredentialsProvider();
                provider.setCredentials(new AuthScope(proxy), new UsernamePasswordCredentials(buildConfig.proxyUsername, Secret.toString(buildConfig.proxyPassword)));
                httpClient = httpClientBuilder.setDefaultCredentialsProvider(provider).setProxy(proxy).build();
            }else{
                httpClient = httpClientBuilder.setProxy(proxy).build();
            }
            //代理请求
            httpConfig = HttpConfig.custom().client(httpClient).url(url).json(data).encoding("utf-8");
        }else{
            httpClient = httpClientBuilder.build();
            //普通请求
            httpConfig = HttpConfig.custom().client(httpClient).url(url).json(data).encoding("utf-8");
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
     * 将给定的原始字符串中的环境变量替换为环境变量结果
     */
    public static String replaceEnvs(String origin,EnvVars envVars){
        for (Map.Entry<String, String> entry : envVars.entrySet()) {
            String k = entry.getKey();
            String v = entry.getValue();
            origin = StringUtils.replace(origin, "${" + k + "}", v);
            origin = StringUtils.replace(origin, "$"+k, v);
        }
        return origin;
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
