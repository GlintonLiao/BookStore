package myssm.listeners;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import myssm.ioc.BeanFactory;
import myssm.ioc.ClassPathXmlApplicationContext;

// 监听上下文启动，在上下文启动的时候去创建 IOC 容器，然后将其保存到 application 作用域
// 后面中央控制器再从 application 作用域中去获取 IOC 容器
@WebListener
public class ContextLoaderListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        // 1. 获取 ServletContext 对象
        ServletContext application = servletContextEvent.getServletContext();
        // 2. 获取上下文的初始化参数
        String path = application.getInitParameter("contextConfigLocation");
        // 3. 创建 IOC 容器
        BeanFactory beanFactory = new ClassPathXmlApplicationContext(path);
        // 4. 将 IOC 容器保存到 application 作用域
        application.setAttribute("beanFactory", beanFactory);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }
}
