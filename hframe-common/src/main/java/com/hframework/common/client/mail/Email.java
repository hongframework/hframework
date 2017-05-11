package com.hframework.common.client.mail;

import java.io.File;
import java.util.Map;

/**
 * 电子邮件
 */
public class Email {

    // 收件人
    private String to;
    // 抄送人
    private String cc;
    // 暗抄人
    private String bcc;
    // 寄件人
    private String from;
    // 主题
    private String subject;
    // 内容
    private String text;
    // 图片
    private Map<String, String> images;
    // 附件
    private Map<String, File> attachments;

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getCc() {
        return cc;
    }

    public void setCc(String cc) {
        this.cc = cc;
    }

    public String getBcc() {
        return bcc;
    }

    public void setBcc(String bcc) {
        this.bcc = bcc;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Map<String, String> getImages() {
        return images;
    }

    public void setImages(Map<String, String> images) {
        this.images = images;
    }

    public Map<String, File> getAttachments() {
        return attachments;
    }

    public void setAttachments(Map<String, File> attachments) {
        this.attachments = attachments;
    }

}
