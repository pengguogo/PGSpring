package com.pg.spring;

import com.pg.test.AppConfig;

import java.beans.Introspector;
import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ApplicationContext {

    public Map<String,BeanDefinition> beanDefinitionMap = new HashMap();

    public Map<String,Object> singletonMap = new HashMap();

    public ApplicationContext(Class<AppConfig> appConfigClass) {
        // 扫描
        scan(appConfigClass);

        //创建bean
        for (Map.Entry<String, BeanDefinition> entry : beanDefinitionMap.entrySet()){
            String beanName = entry.getKey();
            BeanDefinition beanDefinition = entry.getValue();
            if (!"prototype".equals(beanDefinition.scope)) {
                singletonMap.put(beanName,createBean(beanName,beanDefinition));
            }
            }
    }



    private  void scan(Class<AppConfig> appConfigClass) {
        // 获取配置类
        if (appConfigClass.isAnnotationPresent(ComponentScan.class)) {
            ComponentScan annotation = appConfigClass.getAnnotation(ComponentScan.class);
            // 扫描路径
            String value = annotation.value();
            System.out.println("扫描路径:"+value);
            // 转换为文件路径
            String path = value.replace(".", "/");

            ClassLoader classLoader = appConfigClass.getClassLoader();
            URL resource = classLoader.getResource(path);
            String resourcePath = resource.getPath();
            System.out.println("class path:"+ resourcePath);
            File file = new File(resourcePath);

            if (file.isDirectory()) {
                // 遍历文件
                for (File f : file.listFiles()) {
                    String absolutePath = f.getAbsolutePath();
                    System.out.println("file absolutePath:"+ absolutePath);
                    // 截取出类加载器可加载的路径
                    String filePath = absolutePath.substring(absolutePath.indexOf("com"),absolutePath.indexOf(".class")).replace("/",".");
                    System.out.println("file Path:"+ filePath);
                    try {
                        Class<?> clazz = classLoader.loadClass(filePath);
                        if (clazz.isAnnotationPresent(Component.class)) {
                            Component component = clazz.getAnnotation(Component.class);
                            String beanName = component.value();
                            if ("".equals(beanName)){
                                // beanName 为空，直接生成一个
                                beanName = Introspector.decapitalize(clazz.getSimpleName());
                            }

                            BeanDefinition beanDefinition = new BeanDefinition();
                            beanDefinition.setType(clazz);
                            beanDefinitionMap.put(beanName,beanDefinition);
                            // 单例 原型判断
                            if (clazz.isAnnotationPresent(Scope.class) && "prototype".equals(clazz.getAnnotation(Scope.class).value())) {
                                // 原型,每次获取时生成
                                beanDefinition.setScope("prototype");
                            }else {
                                beanDefinition.setScope("singleton");

                            }



                        }
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

        }
    }

    private  Object createBean(String beanName, BeanDefinition beanDefinition) {
        Class clazz = beanDefinition.type;
        Object bean = null;
        try {
            bean = clazz.getConstructor().newInstance();
            for (Field field : clazz.getDeclaredFields()) {
                if (field.isAnnotationPresent(Autowired.class)) {
                    field.setAccessible(true);
                    if(Objects.isNull(singletonMap.get(field.getName()))){
                        createBean(field.getName(),beanDefinitionMap.get(field.getName()));
                    }
                    field.set(bean,singletonMap.get(field.getName()));
                }
            }
           return bean;
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }



    }

    public Object getBean(String beanName) {
        if (!beanDefinitionMap.containsKey(beanName)){
            throw new NullPointerException();
        }
        BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
        Class clazz = beanDefinition.getType();
        // 单例 原型判断
        Scope scope = (Scope) clazz.getAnnotation(Scope.class);
        if (clazz.isAnnotationPresent(Scope.class) && "prototype".equals(scope.value())) {
            return createBean(beanName,beanDefinition);
        }else {
           return singletonMap.get(beanName);
        }

    }
}
