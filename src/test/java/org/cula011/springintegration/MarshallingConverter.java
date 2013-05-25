package org.cula011.springintegration;

import org.springframework.jms.support.converter.MessageConversionException;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.xml.transform.StringResult;
import org.springframework.xml.transform.StringSource;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;

public class MarshallingConverter implements MessageConverter
{
    Jaxb2Marshaller marshaller;

    @Override
    public Message toMessage(Object o, Session session) throws JMSException, MessageConversionException
    {
        StringResult stringResult = new StringResult();
        marshaller.marshal(o, stringResult);
        return session.createTextMessage(stringResult.toString());
    }

    @Override
    public Object fromMessage(Message message) throws JMSException, MessageConversionException
    {
        if (message instanceof TextMessage)
        {
            String messageText = ((TextMessage)message).getText();
            return marshaller.unmarshal(new StringSource(messageText));
        }

        return null;
    }

    public void setMarshaller(Jaxb2Marshaller marshaller)
    {
        this.marshaller = marshaller;
    }
}