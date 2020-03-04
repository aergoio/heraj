/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.transaction;

import static org.slf4j.LoggerFactory.getLogger;

import hera.api.model.BigNumber;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.slf4j.Logger;

/**
 * <p>
 * Custom json formatter for java object to remove dependency to json handling library. It converts
 * following java types into json type.
 * </p>
 * 
 * <pre>
 *   - {@link List} -&gt; json array
 *   - {@link Map} -&gt; json object
 *   - null -&gt; json null
 *   - {@link String} -&gt; json string
 *   - {@link Number} -&gt; json number
 *   - {@link Boolean} -&gt; json boolean
 * </pre>
 * 
 * <p>
 * It also convert aergo-specific model {@link BigNumber} like
 * </p>
 * 
 * <pre>
 *   BigNumber with value "1000" -&gt; { {@link BigNumber#BIGNUM_JSON_KEY}: "1000" }
 * </pre>
 * 
 *
 * @author taeiklim
 *
 */
public class JsonResolver {

  /**
   * Lookup table used for determining which output characters in 7-bit ASCII range need to be
   * quoted. This is from jackson CharTypes class.
   */
  static final int[] sOutputEscapes128;

  static {
    int[] table = new int[128];
    for (int i = 0; i < 32; ++i) {
      table[i] = -1;
    }
    /*
     * Others (and some within that range too) have explicit shorter sequences
     */
    table['"'] = '"';
    table['\\'] = '\\';
    // Escaping of slash is optional, so let's not add it
    table[0x08] = 'b';
    table[0x09] = 't';
    table[0x0C] = 'f';
    table[0x0A] = 'n';
    table[0x0D] = 'r';
    sOutputEscapes128 = table;
  }

  public static final String JSON_START = "{ ";
  public static final String JSON_END = " }";
  public static final String JSON_ARRAY_START = "[ ";
  public static final String JSON_ARRAY_END = " ]";
  public static final String JSON_NEXT = ",";

  protected static final Logger logger = getLogger(JsonResolver.class);



  /**
   * Convert java {@link List} into json array form.
   *
   * @param args an arguments in list
   * @return a json array in string form
   */
  public static String asJsonArray(final List<Object> args) {
    final StringBuilder sb = new StringBuilder();
    sb.append(JSON_ARRAY_START);
    for (int i = 0; i < args.size(); ++i) {
      if (i != 0) {
        sb.append(JSON_NEXT);
      }
      final Object arg = args.get(i);
      sb.append(toJsonValue(arg));
    }
    sb.append(JSON_ARRAY_END);
    return sb.toString();
  }

  /**
   * Convert java {@link Map} into json object form.
   *
   * @param object an object in a java map form
   * @return a json object in string form
   */
  public static String asJsonObject(final Map<String, Object> object) {
    final StringBuilder sb = new StringBuilder();
    sb.append(JSON_START);
    int index = 0;
    for (final Entry<String, Object> element : object.entrySet()) {
      if (index != 0) {
        sb.append(JSON_NEXT);
      }
      final String key = element.getKey();
      final String value = toJsonValue(element.getValue());
      sb.append(asJsonString(key) + ":" + value);
      ++index;
    }
    sb.append(JSON_END);
    return sb.toString();
  }

  /**
   * Convert java type into corresponding json value.
   *
   * @param object a json value in a java type
   * @return a json value in string form
   */
  @SuppressWarnings("unchecked")
  public static String toJsonValue(final Object object) {
    String ret;
    logger.trace("Next json value: {}", object);
    if (object instanceof List) {
      ret = asJsonArray((List<Object>) object);
    } else if (object instanceof Map) {
      ret = asJsonObject((Map<String, Object>) object);
    } else if (object instanceof String) {
      ret = asJsonString((String) object);
    } else if (null == object) {
      ret = asJsonNull();;
    } else if (object instanceof Integer
        || object instanceof Long
        || object instanceof Float
        || object instanceof Double) {
      ret = asJsonNumber((Number) object);
    } else if (object instanceof Boolean) {
      ret = asJsonBoolean((Boolean) object);
    } else if (object instanceof BigNumber) {
      ret = ((BigNumber) object).toJson();
    } else {
      throw new IllegalArgumentException("Cannot convert argument type: " + object.getClass()
          + " into aergo argument json format");
    }

    return ret;
  }

  /**
   * Convert java null into json null.
   *
   * @return a json null
   */
  public static String asJsonNull() {
    return "null";
  }

  /**
   * Convert java {@link String} into json string.
   *
   * @param target a target in java string type
   * @return a json string
   */
  public static String asJsonString(final String target) {
    final StringBuilder sb = new StringBuilder();
    sb.append('"');
    appendQuoted(sb, target);
    sb.append('"');
    return sb.toString();
  }

  protected static void appendQuoted(StringBuilder sb, String content) {
    final int[] escCodes = sOutputEscapes128;
    int escLen = escCodes.length;
    for (int i = 0, len = content.length(); i < len; ++i) {
      char c = content.charAt(i);
      if (c >= escLen || escCodes[c] == 0) {
        sb.append(c);
      } else {
        sb.append('\\');
        int escCode = escCodes[c];
        sb.append((char) escCode);
      }
    }
  }

  /**
   * Convert java {@link Number} into json number.
   *
   * @param target a target in java number type
   * @return a json number
   */
  public static String asJsonNumber(final Number target) {
    return target.toString();
  }

  /**
   * Convert java {@link Boolean} into json boolean.
   *
   * @param target a target in java boolean type
   * @return a json boolean
   */
  public static String asJsonBoolean(final Boolean target) {
    return target.toString();
  }

  public static String asJsonForm(final String name, final List<Object> args) {
    final Map<String, Object> map = new HashMap<>();
    map.put("Name", name);
    map.put("Args", args);
    return JsonResolver.asJsonObject(map);
  }

}
