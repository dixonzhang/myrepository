package com.mama100.monitorcenter.jms;

import java.util.Iterator;
import java.util.Properties;

import javax.jms.ConnectionFactory;

/**
 * 生成MQ连接工厂的工厂类
 * 
 * @author Raphael
 * 
 */
public class OpenMQConnectionFactoryFactory {

    private Properties props = null;

    public void setProperties(Properties props) {
        this.props = props;
    }

    /**
     * 创建连接工厂
     * 
     * @return
     */
    public ConnectionFactory createConnectionFactory() {
        com.sun.messaging.ConnectionFactory cf = new com.sun.messaging.ConnectionFactory();
        try {
            Iterator<Object> it = props.keySet().iterator();
            while (it.hasNext()) {
                String name = (String) it.next();
                String value = props.getProperty(name);
                cf.setProperty(name, value);
            }
        } catch (Exception ex) {
            throw new RuntimeException(
                    "OpenMQConnectionFactoryFactory.createConnectionFactory() failed: "
                            + ex.getMessage(), ex);
        }
        return cf;
    }
}
