package org.apache.ibatis.binding;

import org.apache.ibatis.builder.annotation.MapperAnnotationBuilder;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;

import java.util.HashSet;
import java.util.Set;

public class MapperRegistry {

  private Configuration config;
  private Set<Class<?>> knownMappers = new HashSet<Class<?>>();

  public MapperRegistry(Configuration config) {
    this.config = config;
  }

  /**
   * 返回的是一个 MapperProxy代理
   */
  public <T> T getMapper(Class<T> type, SqlSession sqlSession) {
    if (!knownMappers.contains(type))
      throw new BindingException("Type " + type + " is not known to the MapperRegistry.");
    try {
      return MapperProxy.newMapperProxy(type, sqlSession);
    } catch (Exception e) {
      throw new BindingException("Error getting mapper instance. Cause: " + e, e);
    }
  }

  public boolean hasMapper(Class<?> type) {
    return knownMappers.contains(type);
  }

  public void addMapper(Class<?> type) {
	//type只能是接口
    if (type.isInterface()) {
      //一旦重复，就抛异常
      if (knownMappers.contains(type)) {
        throw new BindingException("Type " + type + " is already known to the MapperRegistry.");
      }
      boolean loadCompleted = false;
      try {
        knownMappers.add(type);
        // It's important that the type is added before the parser is run
        // otherwise the binding may automatically be attempted by the
        // mapper parser.  If the type is already known, it won't try.
        MapperAnnotationBuilder parser = new MapperAnnotationBuilder(config, type);
        
        //完成mapper类对应的xml文件的解析
        //完成mapper类的注解解析
        parser.parse();
        loadCompleted = true;
      } finally {
        if (!loadCompleted) {
          knownMappers.remove(type);
        }
      }
    }
  }
}
