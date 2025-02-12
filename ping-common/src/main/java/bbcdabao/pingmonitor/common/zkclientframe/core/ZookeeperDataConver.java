package bbcdabao.pingmonitor.common.zkclientframe.core;

import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Iterator;

import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;

import bbcdabao.pingmonitor.common.zkdataobj.Template;
import bbcdabao.pingmonitor.common.zkdataobj.TemplateField;

/**
 * Zookeeper data conver utils
 */
public class ZookeeperDataConver {

    @FunctionalInterface
    public static interface IConvertToByte<T> {
        byte[] getData(T param);
    }

    @FunctionalInterface
    public static interface IConvertFromByte<T> {
        T getValue(byte[] param);
    }

    private static class Holder {
        private static final ZookeeperDataConver INSTANCE = new ZookeeperDataConver();
    }

    public static ZookeeperDataConver getInstance() {
        return Holder.INSTANCE;
    }

    private ZookeeperDataConver() {
    }

    public IConvertToByte<Integer> getConvertToByteForInteger() {
        return param -> {
            return Integer.toString(param).getBytes();
        };
    }

    public IConvertFromByte<Integer> getConvertFromByteForInteger() {
        return param -> {
            return Integer.valueOf(new String(param));
        };
    }

    public IConvertToByte<Long> getConvertToByteForLong() {
        return param -> {
            return Long.toString(param).getBytes();
        };
    }

    public IConvertFromByte<Long> getConvertFromByteForLong() {
        return param -> {
            return Long.valueOf(new String(param));
        };
    }

    public IConvertToByte<String> getConvertToByteForString() {
        return param -> {
            return param.getBytes(StandardCharsets.UTF_8);
        };
    }

    public IConvertFromByte<String> getConvertFromByteForString() {
        return param -> {
            return new String(param, StandardCharsets.UTF_8);
        };
    }

    public IConvertToByte<Template> getConvertToByteForTemplate() {
        JSONObject json = new JSONObject();
        return param -> {
            param.getFields().forEach(templateField -> {
                try {
                    json.put(templateField.getKey(), templateField.getType());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            });
            return json.toString().getBytes(StandardCharsets.UTF_8);
        };
    }

    public IConvertFromByte<Template> getConvertFromByteForTemplate() {
        return param -> {
            Template template = new Template();
            try {
                JSONObject json = new JSONObject(new String(param, StandardCharsets.UTF_8));
                Iterator<String> keys = json.keys();
                Collection<TemplateField> fields = template.getFields();
                while (keys.hasNext()) {
                    String key = keys.next();
                    Object value = json.get(key);
                    if (value instanceof String) {
                        TemplateField templateField = new TemplateField();
                        templateField.setKey(key);
                        templateField.setType((String) value);
                        fields.add(templateField);
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return template;
        };
    }
}
