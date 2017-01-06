package rr.json;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

class Convert {
    private static final Map<Class<?>, Function<String, Object>> converters = new HashMap<>();

    static {
        converters.put(short.class, input -> Short.parseShort(input));
        converters.put(Short.class, input -> Short.parseShort(input));
        converters.put(int.class, input -> Integer.parseInt(input));
        converters.put(Integer.class, input -> Integer.parseInt(input));
        converters.put(long.class, input -> Long.parseLong(input));
        converters.put(Long.class, input -> Long.parseLong(input));

        converters.put(float.class, input -> Float.parseFloat(input));
        converters.put(Float.class, input -> Float.parseFloat(input));
        converters.put(double.class, input -> Double.parseDouble(input));
        converters.put(Double.class, input -> Double.parseDouble(input));
    }

    public static Object toObject(String input, Class<?> inputType) {

        System.out.println(inputType.getTypeName());
        System.out.println(inputType.getCanonicalName());

        Function<String, Object> converter = converters.get(inputType);
        if (converter != null) {
            return converter.apply(input);
        }
        else {
            throw new IllegalArgumentException(inputType.getName() + " converter not found");
        }
    }
}
