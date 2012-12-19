package com.snow.xpgc.web;

import com.britesnow.snow.util.AnnotationMap;
import com.britesnow.snow.web.RequestContext;
import com.britesnow.snow.web.param.annotation.WebParam;
import com.britesnow.snow.web.param.resolver.annotation.WebParamResolver;
import com.google.inject.Singleton;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

@Singleton
public class ObjectResolver {
    @WebParamResolver(annotatedWith = WebObject.class)
    public Object resolveWebModel(AnnotationMap annotationMap, Class paramType, RequestContext rc) throws IllegalAccessException, InstantiationException, InvocationTargetException {
        WebObject webObject = annotationMap.get(WebObject.class);
        Class clazz = paramType;
        String prefix = webObject.prefix();
        Object obj = clazz.newInstance();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
                String name = field.getName();
                String value = rc.getParam(prefix + name);
                if (value != null && !value.trim().equals("")) {
                    BeanUtils.setProperty(obj, name, value);
                }
        }
        return obj;
    }
}
