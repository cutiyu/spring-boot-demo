package com.xkcoding.mq.rocketmq;

/**
 * @Describe:
 * @Created by tangfeng 2023/12/6 10:47
 */
public class MqCnst {

    public static final String ENDPOINTS = "http://http://192.168.1.26:9878";
    public static final String ACCESS_KEY = null;
    public static final String SECRET_KEY = null;


    /**
     * topic
     * Topic名称长度限制为3~64个字符，只能包含英文、数字、短划线（-）以及下划线（_）
     * 全局范围内不能和已有的Topic名称或Group ID重复
     */
    public final static String topic_aigc = "ai_camera_topic_aigc";
    public final static String topic_ts_aigc = "ai_camera_ts_topic_aigc";
    public final static String topic_delay_aigc = "ai_camera_delay_topic_aigc";

    /**
     * tag
     * • 键（Key）的最大长度：64个Unicode字符，区分大小写。
     * • 值（Value）的最大长度：64个Unicode字符，区分大小写。
     * • 每个资源的最大标签数为20。
     * • 键（Key）不支持aliyun、acs:开头，不允许包含http://和https://，不允许为空字符串。
     * • 值（Value）不允许包含http://和https://，允许为空字符串。
     */

    //public final static String tag_check = "tag_check";
    public final static String tag_check_prod = "check_prod";
    public final static String tag_train_prod = "train_prod";
    public final static String tag_infer_prod = "infer_prod";

    public final static String tag_check_test = "check_test";
    public final static String tag_train_test = "train_test";
    public final static String tag_infer_test = "infer_test";


    /**
     * 消费组
     * Group ID推荐以“GID_”或“GID-”开头，长度限制为2～64个字符，只能包含英文、数字、短划线（-）以及下划线（_）
     */
    public final static String group_consumer_check_test = "GID_ai_camera_check_test";
    public final static String group_consumer_train_test = "GID_ai_camera_train_test";
    public final static String group_consumer_infer_test = "GID_ai_camera_infer_test";

    public final static String group_consumer_check_prod = "GID_ai_camera_check_prod";
    public final static String group_consumer_train_prod = "GID_ai_camera_train_prod";
    public final static String group_consumer_infer_prod = "GID_ai_camera_infer_prod";

    public final static String group_consumer_aigc_test = "GID_ai_camera_test";
    public final static String group_consumer_aigc_prod = "GID_ai_camera_prod";

    public final static String group_consumer_aigc = "GID_ai_camera";
    public final static String group_consumer_ts_aigc = "GID_ai_camera_ts";
    public final static String group_consumer_delay_aigc = "GID_ai_camera_delay";

    /**
     * topic:tag
     */
    public final static String aigc_topic_tag_check_prod = topic_aigc+":"+ tag_check_prod;
    public final static String aigc_topic_tag_train_prod =topic_aigc+":"+ tag_train_prod;
    public final static String aigc_topic_tag_infer_prod =topic_aigc+":"+ tag_infer_prod;

    public final static String aigc_topic_tag_check_test = topic_aigc+":"+tag_check_test;
    public final static String aigc_topic_tag_train_test =topic_aigc+":"+tag_train_test;
    public final static String aigc_topic_tag_infer_test =topic_aigc+":"+tag_infer_test;




    public static void main(String[] args) {
        String s = "camera_tag_check";
        System.out.println(s.length()*4);
    }

}
