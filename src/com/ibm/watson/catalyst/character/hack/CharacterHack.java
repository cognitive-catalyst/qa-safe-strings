package com.ibm.watson.catalyst.character.hack;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ibm.icu.text.Transliterator;

public class CharacterHack {
  
  /**
   * An IBM open source object for mapping UTF-8 to ASCII since Watson can only handle ASCII characters.
   */
  private static final Transliterator transliterator = Transliterator.getInstance("Latin-ASCII");
  
  /**
   * Matches hyphens with spaces around them since spaces around hyphens are removed during ingestion.
   */
  private static final Pattern HYPHEN = Pattern.compile(" - ");
  
  /**
   * Matches decimal code points as in HTML since those are replaced with their ASCII equivalents during ingestion.
   */
  private static final Pattern DECIMALCODE = Pattern.compile("&#[0-9]+;");
  
  /**
   * Converts a decimal code point to its UTF-8 character
   * @param aString the string with the decimal code, for example: &#038; becomes &
   * @return a length-one string with the character
   */
  public static String dec2char(String aString) {
    int decCodePoint = Integer.parseInt(aString.substring(2, 5));
    return Character.toString((char) decCodePoint);
  }
  
  /**
   * Removes decimal code points (which sometimes are used in HTML) and converts them to their UTF-8 symbol
   * @param aString the string to replace decimal code points in
   * @return the string with the decimal code points replaced
   */
  public static String removeDecimal(final String aString) {
    StringBuffer sb = new StringBuffer();
    Matcher m = DECIMALCODE.matcher(aString);
    
    while (m.find()) {
      String replacement = dec2char(m.group());
      m.appendReplacement(sb, replacement);
    }
    m.appendTail(sb);
    
    return sb.toString();
  }
  
  /**
   * Transforms UTF-8 into Watson-safe ASCII.
   * @param aString the string to transform
   */
  public static String transform(final String aString) {
    String result = aString;
    result = removeDecimal(result);
    result = transliterator.transliterate(result);
    result = HYPHEN.matcher(result).replaceAll("-");
    return result;
  }
  
  /**
   * Example of using the watsonTransform method
   * @param args
   */
  public static void main(String[] args) {
    System.out.println(transform("I cän ©onvert - things &#038; stuff: ½."));
  }
  
}
