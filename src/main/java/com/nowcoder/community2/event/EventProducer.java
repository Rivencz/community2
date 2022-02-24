package com.nowcoder.community2.event;

import com.alibaba.fastjson.JSONObject;
import com.nowcoder.community2.entity.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class EventProducer {
    @Autowired
    private KafkaTemplate kafkaTemplate;

//    处理事件
    public void fireEvent(Event event){
//        发送到对应主题上，内容为event的JSON格式字符串
        kafkaTemplate.send(event.getTopic(), JSONObject.toJSONString(event));
    }
}
