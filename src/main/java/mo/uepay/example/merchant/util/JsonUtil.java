package mo.uepay.example.merchant.util;

import com.google.common.collect.Maps;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.springframework.cglib.beans.BeanMap;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 *
 */
public class JsonUtil {

    private JsonUtil() {
    }

    private static final GsonBuilder gsonBuilde = new GsonBuilder();


    public static final Gson gson = gsonBuilde.disableHtmlEscaping().create();


    public static final <T> T toObject(String jsonstr, Class<T> classOfT) {
        return gson.fromJson(jsonstr, classOfT);
    }

    public static final String toJson(Object obj) {
        return gson.toJson(obj);
    }

    public static final String toJsonWithUnderscores(Object obj) {
        return gsonBuilde.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create().toJson(obj);
    }

    public static final <T> T toObjectWithUnderscores(String jsonstr, Class<T> classOfT) {
        return gsonBuilde.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create().fromJson(jsonstr, classOfT);
    }

    public static final <T> T toObjectWithUnderscores(String jsonstr, Type typeOfT) {
        return gsonBuilde.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create().fromJson(jsonstr, typeOfT);
    }

    /**
     * 将JSON字符串解析为SortedMap
     *
     * @param params
     * @return
     */
    public static final SortedMap<String, String> toSortedMap(String params) {
        Type mtype = new TypeToken<TreeMap<String, String>>() {
        }.getType();
        try {
            TreeMap<String, String> paramsMap = gson.fromJson(params, mtype);
            return paramsMap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 將JSON字符串解析為Map
     *
     * @param params
     * @return
     */
    public static final Map<String, String> toMap(String params) {
        Type mtype = new TypeToken<Map<String, String>>() {
        }.getType();
        try {
            return gson.fromJson(params, mtype);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * 從Map中解析實體
     *
     * @param map
     * @param clazz
     * @return
     */
    public static final <T> T fromSortedMap(SortedMap<String, String> map, Class<T> clazz) {
        if (map == null || map.size() < 1) {
            return null;
        }
        try {
            Object obj = clazz.newInstance();
            Field[] fieldArr = clazz.getDeclaredFields();
            for (Field field : fieldArr) {
                if (!field.getName().equals("serialVersionUID")) {
                    field.setAccessible(true);
                    field.set(obj, map.get(field.getName()));
                }
            }
            return clazz.cast(obj);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 将对象装换为map
     *
     * @param bean
     * @return
     */
    public static <T> Map<String, Object> beanToMap(T bean) {
        Map<String, Object> map = Maps.newHashMap();
        if (bean != null) {
            BeanMap beanMap = BeanMap.create(bean);
            for (Object key : beanMap.keySet()) {
                map.put(key + "", beanMap.get(key));
            }
        }
        return map;
    }

    public static int length(String value) {
        int valueLength = 0;
        String chinese = "[\u0391-\uFFE5]";
        /* 获取字段值的长度，如果含中文字符，则每个中文字符长度为3，否则为1 */
        for (int i = 0; i < value.length(); i++) {
            /* 获取一个字符 */
            String temp = value.substring(i, i + 1);
            /* 判断是否为中文字符 */
            if (temp.matches(chinese)) {
                /* 中文字符长度为2 */
                valueLength += 3;
            } else {
                /* 其他字符长度为1 */
                valueLength += 1;
            }
        }
        return valueLength;
    }

    public static String substring(String string, int endIndex) throws UnsupportedEncodingException {
        // 获取字符串的编码
        byte[] buffer = string.getBytes("UTF-8");
        int count = 0; // 字符个数
        for (int i = 0; i < endIndex; i++) {
            if (buffer[i] < 0 && i + 2 < endIndex) {
                count++;  // 點位加1
                i++; //跳過中文
            } else if (buffer[i] > 0) {
                count++; // 非中文，字符数加一
            }
            System.out.println(i);
        }
        System.out.println("-------------------");
        return string.substring(0, count);
    }

    public static void main(String[] args) {
        try {

            System.out.println((JsonUtil.substring("ABC文23", 5)));
            System.out.println(length(JsonUtil.substring("ABC文23", 5)));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

}
