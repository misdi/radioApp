package com.ypyglobal.xradio.gdpr;

/**
 * @author:YPY Global
 * @Email: bl911vn@gmail.com
 * @Website: http://ypyglobal.com
 * Created by YPY Global on 2/25/19.
 */
class GDPRModel {

    private String publisherId;
    private String urlPolicy;
    private String testId;

    GDPRModel(String publisherId, String urlPolicy, String testId) {
        this.publisherId = publisherId;
        this.urlPolicy = urlPolicy;
        this.testId = testId;
    }

    String getPublisherId() {
        return publisherId;
    }

    String getUrlPolicy() {
        return urlPolicy;
    }

    String getTestId() {
        return testId;
    }

}
