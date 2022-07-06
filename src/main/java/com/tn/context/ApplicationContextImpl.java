package com.tn.context;

import com.tn.annotation.Bean;
import com.tn.exception.NoSuchBeanException;
import com.tn.exception.NoUniqueBeanDefinitionException;
import com.tn.exception.NoUniqueBeanException;
import lombok.SneakyThrows;
import org.apache.commons.lang.StringUtils;
import org.reflections.Reflections;

import java.util.HashMap;
import java.util.Map;

import static java.lang.String.format;
import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toMap;

public class ApplicationContextImpl implements ApplicationContext {

    private final Map<String, Object> nameToBeanMap = new HashMap<>();
    private final String packageName;

    public ApplicationContextImpl(String packageName) {
        this.packageName = packageName;
        init();
    }

    @Override
    @SneakyThrows
    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<T> beanType) {
        var beans = nameToBeanMap.values()
                                 .stream()
                                 .filter(type -> beanType.isAssignableFrom(type.getClass()))
                                 .toList();

        if (beans.isEmpty()) {
            throw new NoSuchBeanException(format("Bean of %s type not found", beanType.getSimpleName()));
        }

        if (beans.size() > 1) {
            throw new NoUniqueBeanException(format("Bean of %s type not unique", beanType.getSimpleName()));
        }

        return (T) beans.get(0);
    }

    @Override
    @SneakyThrows
    @SuppressWarnings("unchecked")
    public <T> T getBean(String name, Class<T> beanType) {
        Object bean = nameToBeanMap.get(name);

        if (isNull(bean) || !beanType.isAssignableFrom(bean.getClass())) {
            throw new NoSuchBeanException(
                format("Bean of %s type and %s name not found", beanType.getSimpleName(), name));
        }

        return (T) bean;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Map<String, T> getAllBeans(Class<T> beanType) {
        return nameToBeanMap.entrySet()
                            .stream()
                            .filter(entry -> beanType.isAssignableFrom(entry.getValue()
                                                                            .getClass()))
                            .collect(toMap(Map.Entry::getKey, entry -> (T) entry.getValue()));
    }

    @SneakyThrows
    private void init() {
        var reflections = new Reflections(packageName);
        var beanTypes = reflections.getTypesAnnotatedWith(Bean.class);
        for (var beanType : beanTypes) {
            var beanName = resolveBeanName(beanType);
            if (nameToBeanMap.containsKey(beanName)) {
                throw new NoUniqueBeanDefinitionException(format("Bean with %s name already exists", beanName));
            }
            nameToBeanMap.put(beanName, beanType.getConstructor()
                                                .newInstance());
        }
    }

    private String resolveBeanName(Class<?> type) {
        var nameFromAnnotation = type.getAnnotation(Bean.class)
                                     .name();
        var typeName = type.getSimpleName();
        return nameFromAnnotation.isBlank()
               ? StringUtils.replaceOnce(typeName, typeName.substring(0, 1), typeName.substring(0, 1)
                                                                                     .toLowerCase())
               : nameFromAnnotation;
    }

}
