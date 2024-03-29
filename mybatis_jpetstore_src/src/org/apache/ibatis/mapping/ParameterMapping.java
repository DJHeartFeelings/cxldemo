package org.apache.ibatis.mapping;

import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;
import org.apache.ibatis.type.TypeHandlerRegistry;

public class ParameterMapping {

  private Configuration configuration;

  private String property;
  private ParameterMode mode;
  private Class javaType = Object.class;
  private JdbcType jdbcType;
  private Integer numericScale;
  private TypeHandler typeHandler;
  private String resultMapId;
  private String jdbcTypeName;

  private ParameterMapping() {
  }

  public static class Builder {
    private ParameterMapping parameterMapping = new ParameterMapping();

    public Builder(Configuration configuration, String property, TypeHandler typeHandler) {
      parameterMapping.configuration = configuration;
      parameterMapping.property = property;
      parameterMapping.typeHandler = typeHandler;
      parameterMapping.mode = ParameterMode.IN;
    }

    public Builder(Configuration configuration, String property, Class javaType) {
      parameterMapping.configuration = configuration;
      parameterMapping.property = property;
      parameterMapping.javaType = javaType;
      parameterMapping.mode = ParameterMode.IN;
    }

    public Builder mode(ParameterMode mode) {
      parameterMapping.mode = mode;
      return this;
    }

    public Builder javaType(Class javaType) {
      parameterMapping.javaType = javaType;
      return this;
    }

    public Builder jdbcType(JdbcType jdbcType) {
      parameterMapping.jdbcType = jdbcType;
      return this;
    }

    public Builder numericScale(Integer numericScale) {
      parameterMapping.numericScale = numericScale;
      return this;
    }

    public Builder resultMapId(String resultMapId) {
      parameterMapping.resultMapId = resultMapId;
      return this;
    }

    public Builder typeHandler(TypeHandler typeHandler) {
      parameterMapping.typeHandler = typeHandler;
      return this;
    }

    public Builder jdbcTypeName(String jdbcTypeName) {
      parameterMapping.jdbcTypeName = jdbcTypeName;
      return this;
    }

    public ParameterMapping build() {
      resolveTypeHandler();
      return parameterMapping;
    }

    /**
     * typeHandler为null时且javaType不为null时，利用javaType寻找对应值
     */
    private void resolveTypeHandler() {
      if (parameterMapping.typeHandler == null) {
        if (parameterMapping.javaType != null) {
          Configuration configuration = parameterMapping.configuration;
          TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
          parameterMapping.typeHandler = typeHandlerRegistry.getTypeHandler(parameterMapping.javaType, parameterMapping.jdbcType);
        }
      }
    }


  }

  public String getProperty() {
    return property;
  }

  public ParameterMode getMode() {
    return mode;
  }

  public Class getJavaType() {
    return javaType;
  }

  public JdbcType getJdbcType() {
    return jdbcType;
  }

  public Integer getNumericScale() {
    return numericScale;
  }

  public TypeHandler getTypeHandler() {
    return typeHandler;
  }

  public String getResultMapId() {
    return resultMapId;
  }

  public String getJdbcTypeName() {
    return jdbcTypeName;
  }
	
}
