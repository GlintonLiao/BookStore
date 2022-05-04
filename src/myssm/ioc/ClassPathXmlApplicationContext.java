package myssm.ioc;

import myssm.util.StringUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class ClassPathXmlApplicationContext implements BeanFactory {

    private Map<String, Object> beanMap = new HashMap<>();
    private String path = "applicationContext.xml";
    public ClassPathXmlApplicationContext() {
        this("applicationContext.xml");
    }
    public ClassPathXmlApplicationContext(String path) {
        if (StringUtil.isEmpty(path)) {
            throw new RuntimeException("IOC 容器的配置文件没有指定");
        }
        try {
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(path);
            // 1. 创建 DocumentBuilderFactory
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            // 2. 创建 DocumentBuilder 对象
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            // 3. 创建 Document 对象
            Document document = documentBuilder.parse(inputStream);
            // 4. 获取 bean 节点
            NodeList beanNodeList = document.getElementsByTagName("bean");
            for (int i = 0; i < beanNodeList.getLength(); i++) {
                Node beadNode = beanNodeList.item(i);
                if (beadNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element beanElement = (Element)beadNode;
                    String beadId = beanElement.getAttribute("id");
                    String className = beanElement.getAttribute("class");
                    Class beanClass = Class.forName(className);
                    // 创建 bean 实例
                    Object beanObj = beanClass.newInstance();
                    // 将 bean 对象保存到 map 中
                    beanMap.put(beadId, beanObj);
                    // 此时 bean 与 bean 之间还没有依赖关系
                }
            }
            // 5. 组装 bean 之间的依赖关系
            for (int i = 0; i < beanNodeList.getLength(); i++) {
                Node beanNode = beanNodeList.item(i);
                if (beanNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element beanElement = (Element)beanNode;
                    String beadId = beanElement.getAttribute("id");
                    NodeList beanChildNodeList = beanElement.getChildNodes();
                    for (int j = 0; j < beanChildNodeList.getLength(); j++) {
                        Node beanChildNode = beanChildNodeList.item(j);
                        if (beanChildNode.getNodeType() == Node.ELEMENT_NODE && "property".equals(beanChildNode.getNodeName())) {
                            Element propertyElement = (Element)beanChildNode;
                            String propertyName = propertyElement.getAttribute("name");
                            String propertyRef = propertyElement.getAttribute("ref");
                            // 1. 找到 propertyRef 中对应的实例
                            Object refObj = beanMap.get(propertyRef);
                            // 2. 将 refObj 设置到当前 bean 对应的实例 property 属性上
                            Object beanObj = beanMap.get(beadId);
                            Class beanClazz = beanObj.getClass();
                            Field propertyField = beanClazz.getDeclaredField(propertyName);
                            propertyField.setAccessible(true);
                            propertyField.set(beanObj, refObj);
                        }
                    }
                }
            }
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Object getBean(String id) {
        return beanMap.get(id);
    }
}
