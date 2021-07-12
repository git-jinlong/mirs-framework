package com.github.mirs.banxiaoxiao.framework.common.util;

import lombok.extern.slf4j.Slf4j;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.io.StringWriter;

/**
 * @author bc
 */
@Slf4j
public class JaxbUtil {

    public static String convertToXml(Object obj) {
        return convertToXml(obj, "UTF-8");
    }

    public static String convertToXml(Object obj, String encoding) {
        String result = null;
        try {
            JAXBContext context = JAXBContext.newInstance(obj.getClass());
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.setProperty(Marshaller.JAXB_ENCODING, encoding);

            StringWriter writer = new StringWriter();
            marshaller.marshal(obj, writer);
            result = writer.toString();
        } catch (Exception e) {
            log.error("convertToXml error", e);
        }
        return result;
    }


    public static <T> T converyToJavaBean(String xml, JAXBContext context) {
        T t = null;
        StringReader sr = null;
        try {
            sr = new StringReader(xml);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            t = (T) unmarshaller.unmarshal(sr);
        } catch (Exception e) {
            log.error("converyToJavaBean error", e);
        } finally {
            if (null != sr) {
                sr.close();
            }
        }

        return t;
    }
}
