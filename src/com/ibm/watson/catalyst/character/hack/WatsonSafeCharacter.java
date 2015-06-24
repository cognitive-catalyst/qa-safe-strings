package com.ibm.watson.catalyst.character.hack;

import java.util.regex.Pattern;

import com.ibm.icu.text.Transliterator;

import org.apache.commons.lang3.StringEscapeUtils;

public class WatsonSafeCharacter {
  
  /**
   * An IBM open source object for mapping UTF-8 to ASCII since Watson can only handle ASCII characters.
   */
  private static final Transliterator transliterator = Transliterator.getInstance("Any-Latin; Latin-ASCII");
  
  /**
   * Matches hyphens with spaces around them since sometimes spaces around hyphens are removed during ingestion.
   */
//  private static final Pattern HYPHEN = Pattern.compile(" - ");
  
  /**
   * Find occurrences of multiple lines and replace them
   */
  private static final Pattern WHITESPACE = Pattern.compile("(\\s(\\s)?)\\1+");
  
  /**
   * Decodes HTML encoded text
   * @param aString the string to replace decimal code points in
   * @return the string with the decimal code points replaced
   */
  public static String unescapeHtml4(final String aString) {
    return StringEscapeUtils.unescapeHtml4(aString);
  }
  
  /**
   * Transforms UTF-8 into Watson-safe ASCII.
   * @param aString the string to transform
   */
  public static String transform(final String aString) {
    String result = aString;
    result = WHITESPACE.matcher(aString).replaceAll("$1");
    result = unescapeHtml4(result);
    result = transliterator.transliterate(result);
//    result = HYPHEN.matcher(result).replaceAll("-");
    result = result.trim();
    return result;
  }
  
  /**
   * Example of using the watsonTransform method
   * @param args
   */
  public static void main(String[] args) {
    System.out.println(transform(""));
    System.out.println(transform("I cän  ©onv&#235;&reg;t - things &#038; stuff: ½. &#x00AE;"));
  }
  
}
