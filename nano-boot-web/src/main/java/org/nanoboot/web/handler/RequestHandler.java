package org.nanoboot.web.handler;

import org.nanoboot.annotation.Annotation.PathVariable;
import org.nanoboot.annotation.Annotation.RequestParam;
import org.nanoboot.annotation.Annotation.RequestBody;
import org.nanoboot.core.ApplicationContext;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 请求处理器，负责解析和处理HTTP请求
 */
public class RequestHandler {

    private final ApplicationContext applicationContext;

    public RequestHandler(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    /**
     * 解析方法参数
     */
    public Object[] resolveMethodParameters(Method method, String uri, String queryString, String requestBody) {
        Parameter[] parameters = method.getParameters();
        Object[] args = new Object[parameters.length];

        for (int i = 0; i < parameters.length; i++) {
            Parameter param = parameters[i];

            // 处理@PathVariable注解
            if (param.isAnnotationPresent(PathVariable.class)) {
                PathVariable pathVar = param.getAnnotation(PathVariable.class);
                String paramName = pathVar.value();

                // 从URI中提取路径变量值
                // 这里简化处理，实际应该根据路由模式解析
                args[i] = extractPathVariable(uri, paramName);
            }
            // 处理@RequestParam注解
            else if (param.isAnnotationPresent(RequestParam.class)) {
                RequestParam reqParam = param.getAnnotation(RequestParam.class);
                String paramName = reqParam.value();

                // 从查询字符串中获取参数值
                String paramValue = extractRequestParam(queryString, paramName);

                if (paramValue == null && reqParam.required()) {
                    throw new IllegalArgumentException("Required parameter '" + paramName + "' is missing");
                }

                if (paramValue == null) {
                    paramValue = reqParam.defaultValue();
                }

                args[i] = convertValue(paramValue, param.getType());
            }
            // 处理@RequestBody注解
            else if (param.isAnnotationPresent(RequestBody.class)) {
                args[i] = requestBody; // 简化处理，实际应进行JSON转换
            }
            // 其他参数类型可以根据需要扩展
            else {
                // 尝试通过类型查找Bean
                try {
                    args[i] = applicationContext.getBean(param.getType());
                } catch (Exception e) {
                    args[i] = null; // 或者提供默认值
                }
            }
        }

        return args;
    }

    /**
     * 从URI中提取路径变量
     */
    private String extractPathVariable(String uri, String paramName) {
        // 简化实现：假设URI格式为 /api/users/{id}
        Pattern pattern = Pattern.compile("\\{([^}]+)\\}");
        Matcher matcher = pattern.matcher(uri);

        // 在实际应用中，这需要与路由模式匹配
        // 这里只是示例，实际需要更复杂的路由解析
        return null;
    }

    /**
     * 从查询字符串中提取请求参数
     */
    private String extractRequestParam(String queryString, String paramName) {
        if (queryString == null || queryString.isEmpty()) {
            return null;
        }

        // 分割查询字符串，处理多个&连接的参数
        String[] pairs = queryString.split("&");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=", 2); // 最多分割为2部分，避免对值中的=进行分割
            if (keyValue.length >= 1) {
                String key = keyValue[0];
                String value = keyValue.length > 1 ? keyValue[1] : "";

                // 解码URL参数
                try {
                    key = java.net.URLDecoder.decode(key, "UTF-8");
                    value = java.net.URLDecoder.decode(value, "UTF-8");
                } catch (java.io.UnsupportedEncodingException e) {
                    // 如果解码失败，使用原始值
                }

                if (key.equals(paramName)) {
                    return value;
                }
            }
        }

        return null;
    }

    /**
     * 类型转换
     */
    private Object convertValue(String value, Class<?> targetType) {
        if (value == null) return null;

        if (targetType == String.class) {
            return value;
        } else if (targetType == Integer.class || targetType == int.class) {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                return 0; // 或者抛出异常
            }
        } else if (targetType == Long.class || targetType == long.class) {
            try {
                return Long.parseLong(value);
            } catch (NumberFormatException e) {
                return 0L; // 或者抛出异常
            }
        } else if (targetType == Boolean.class || targetType == boolean.class) {
            return Boolean.parseBoolean(value);
        } else if (targetType == Double.class || targetType == double.class) {
            try {
                return Double.parseDouble(value);
            } catch (NumberFormatException e) {
                return 0.0; // 或者抛出异常
            }
        } else if (targetType == Float.class || targetType == float.class) {
            try {
                return Float.parseFloat(value);
            } catch (NumberFormatException e) {
                return 0.0f; // 或者抛出异常
            }
        } else {
            return value; // 默认返回字符串
        }
    }
}