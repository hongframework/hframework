package com.hframework.common.client.mail;

import com.hframework.common.util.StringUtils;
import com.hframework.common.springext.properties.PropertyConfigurerUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.util.Date;
import java.util.Map;
import java.util.Properties;

/**
 * 邮件工具类
 */
public class MailUtils {

    public static final String SEPARATED = ";";

    private static Logger logger = LoggerFactory.getLogger(MailUtils.class);

    private static JavaMailSenderImpl javaMailSender;

    static {
        if (javaMailSender == null) {
            logger.debug("MailUtils Init");
            javaMailSender = new JavaMailSenderImpl();
            //设定mail server
            javaMailSender.setHost(PropertyConfigurerUtils.getProperty("mail.server"));
            // 设定mail登录用户
            javaMailSender.setUsername(PropertyConfigurerUtils.getProperty("mail.username"));
            // 设定mail登录密码
            javaMailSender.setPassword(PropertyConfigurerUtils.getProperty("mail.password"));
            Properties prop = new Properties();
            // 服务器是否认证用户名和密码
            prop.put("mail.smtp.auth", PropertyConfigurerUtils.getProperty("mail.auth"));
            // 服务连接超时设置
            prop.put("mail.smtp.timeout", PropertyConfigurerUtils.getProperty("mail.timeout"));
            javaMailSender.setJavaMailProperties(prop);
        }
    }

    public static void sendMail(Email email) throws MessagingException {
        //建立邮件消息,发送简单邮件和html邮件的区别
        MimeMessage mailMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(mailMessage, true, PropertyConfigurerUtils.getProperty("mail.encoding"));
        //设置收件人
        if (email.getTo().indexOf(SEPARATED) == -1) {
            messageHelper.setTo(email.getTo());
        } else {
            messageHelper.setTo(email.getTo().split(SEPARATED));
        }
        //设置抄送人
        if (StringUtils.isNotBlank(email.getCc())) {
            if (email.getCc().indexOf(SEPARATED) == -1) {
                messageHelper.setCc(email.getCc());
            } else {
                messageHelper.setCc(email.getCc().split(SEPARATED));
            }
        }
        //设置暗送人
        if (StringUtils.isNotBlank(email.getBcc())) {
            if (email.getBcc().indexOf(SEPARATED) == -1) {
                messageHelper.setBcc(email.getBcc());
            } else {
                messageHelper.setBcc(email.getBcc().split(SEPARATED));
            }
        }
        //设置发送日期
        messageHelper.setSentDate(new Date());
        //设置寄件人
        messageHelper.setFrom(email.getFrom() == null ? PropertyConfigurerUtils.getProperty("mail.from") : email.getFrom());
        //设置主题
        messageHelper.setSubject(email.getSubject());
        // 设置图片
        Map<String, String> images = email.getImages();
        if (images != null && images.size() > 0) {
            for (String key : images.keySet()) {
                messageHelper.addInline(key, new ClassPathResource(images.get(key)));
            }
        }
        // 设置附件
        Map<String, File> attachments = email.getAttachments();
        if (attachments != null && attachments.size() > 0) {
            for (String key : attachments.keySet()) {
                messageHelper.addAttachment(key, new FileSystemResource(attachments.get(key)));
            }
        }
        //true 表示启动HTML格式的邮件
        if (email.getText() != null && email.getText().trim().length() > 0) {
            messageHelper.setText(email.getText(), true);
        }
        //发送邮件
        javaMailSender.send(mailMessage);
    }

    public static void main(String[] args) {

    }
}