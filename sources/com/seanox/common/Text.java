/**
 *  LIZENZBEDINGUNGEN - Seanox Software Solutions ist ein Open-Source-Projekt,
 *  im Folgenden Seanox Software Solutions oder kurz Seanox genannt.
 *  Diese Software unterliegt der Version 2 der GNU General Public License.
 *
 *  Seanox Commons, Advanced Programming Interface
 *  Copyright (C) 2017 Seanox Software Solutions
 *
 *  This program is free software; you can redistribute it and/or modify it
 *  under the terms of version 2 of the GNU General Public License as published
 *  by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT
 *  ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 *  FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 *  more details.
 *
 *  You should have received a copy of the GNU General Public License along
 *  with this program; if not, write to the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package com.seanox.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *  Utilities for text and strings.
 *  
 *  <h3>Features:</h3>
 *  <ul>
 *    <li>Computation of phonetic values</li>
 *    <li>Escaping and unescaping</li>
 *    <li>Decoration and undecoration of ISO control characters</li>
 *    <li>Natural sorting</li>
 *    <li>Splitting and replacement</li>
 *  </ul>
 *  
 *  <h3>Principles:</h3>
 *  <ul>
 *    <li>
 *      Is passed {@code null} as value, then {@code null} is also returned.
 *    </li>
 *    <li>
 *      Is passed {@code null} as functional parameter, then it is used as not
 *      specified or thrown an exception.
 *    </li>
 *  </ul>
 *  <br>
 *  TextUtils 1.2 20180109<br>
 *  Copyright (C) 2018 Seanox Software Solutions<br>
 *  All rights reserved.
 *
 *  @author  Seanox Software Solutions
 *  @version 1.2 20180109
 */
public class Text {
    
    /** Naturally sort comparator */
    public static final Comparator<String> NATURAL_COMPARATOR = new Text.NaturalComparator();
    
    /** Constructor, creates a new TextUtils object. */
    private Text() {
    }
    
    /**
     *  Returns the phonetic value of the string in the specified language.<br>
     *  Supported languages: DE
     *  If {@code null} is passed, {@code null} is returned.
     *  @param  string string to be calculated
     *  @param  locale language
     *  @return the determined phonetic value
     *  @throws IllegalArgumentException
     *      For an unsupported language / locale.
     */
    public static String phonetic(String string, Locale locale) {

        StringBuffer result;
        String       digits;

        byte[]       bytes;
        int[]        codes;

        int          sound;
        int          count;
        int          loop ;

        Objects.requireNonNull(locale, "Locale required");

        if (string == null)
            return null;

        //nicht unterstuetzte Sprachen fuehren zur IllegalArgumentException
        if (!Locale.GERMAN.getLanguage().equals(locale.getLanguage()))
            throw new IllegalArgumentException("Language not supported");
        
        //der Datenpuffer wird eingerichtet
        result = new StringBuffer();

        //die Bytes des String werden ermitttelt
        bytes = string.trim().toLowerCase().getBytes();

        //das phonetische Muster wird eingerichtet (a-z)
        //Gruppierung:
        //  - 1 A: AU
        //  - 2 I: EIY
        //  - 3 P: BDPT  
        //  - 4 K: CGJK
        //  - 5 F: FVW
        //  - 6 L: L
        //  - 7 M: MN
        //  - 8 H:  
        //  - 9 Z: SZ
        //        ABCDEFGHIJKLMNOPQRSTUVWXYZ
        digits = "1343254 244677134893155929";

        for (loop = 0; loop < bytes.length; loop++) {
            
            sound = -1;

            //zur Analyse werden die aktuellen 3 Zeichen ermittelt
            codes = new int[3];
            if (bytes.length > loop +0)
                codes[0] = bytes[loop +0] & 0xFF;
            if (bytes.length > loop +1)
                codes[1] = bytes[loop +1] & 0xFF;
            if (bytes.length > loop +2)
                codes[2] = bytes[loop +2] & 0xFF;

            //zur Berechnung des phonetischen Werts werden nur a-z(h),ß,ae,oe,ue beruecksichtigt
            if ((codes[0] < 0x61 || codes[0] > 0x7A)
                    && codes[0] != 0x68 && codes[0] != 0xDF && codes[0] != 0xE4 && codes[0] != 0xF6 && codes[0] != 0xFC)
                continue;

            //phonetische Regel - folgt auf aou eiy wird daraus Gruppe 2
            if ((codes[0] == 0x61 || codes[0] == 0x6F || codes[0] == 0x75)
                    && (codes[1] == 0x65 || codes[1] == 0x69 || codes[1] == 0x79)) {
                sound = '2';
                loop++;
            }

            //Phonetische Regeln:
            //folgt auf s c h wird daraus Gruppe 0
            if (sound < 0 && codes[0] == 0x73 &&  codes[1] == 0x63 && codes[2] == 0x68) sound = '0';
            //aeoeue entsprechen der Gruppe 2
            if (sound < 0 && codes[0] == 0xE4 ||  codes[0] == 0xF6 || codes[0] == 0xFC) sound = '2';
            //folgt auf s tp wird daraus Gruppe 0
            if (sound < 0 && codes[0] == 0x73 && (codes[1] == 0x70 || codes[1] == 0x74)) sound = '0';
            //folgt auf c h wird daraus Gruppe 0
            if (sound < 0 && codes[0] == 0x63 &&  codes[1] == 0x68) sound = '0';
            //folgt auf p fh wird daraus Gruppe 5
            if (sound < 0 && codes[0] == 0x70 && (codes[1] == 0x66 || codes[1] == 0x68)) sound = '5';
            //ss entspricht der Gruppe 9
            if (sound < 0 && codes[0] == 0xDF) sound = '9';

            //der phonetische Wert wird ohne Doppelwerte erweitert (12221 -> 121)
            if (sound >= 0) {
                if (result.length() <= 0
                        || sound != result.charAt(result.length() -1))
                    if (sound != ' ')
                        result.append((char)sound);
                continue;
            }

            //der phonetische Wert wird ermittelt
            if ((codes[0] -= 0x60) > 0
                    && codes[0] <= digits.length())
                sound = digits.charAt(codes[0] -1);

            //der phonetische Wert wird ohne Doppelwerte erweitert (12221 -> 121)
            if (sound >= 0)
                if (result.length() <= 0
                        || sound != result.charAt(result.length() -1))
                    if (sound != ' ')
                        result.append((char)sound);
        }

        return result.toString();
    }    
    
    /**
     *  Splits this string around matches of the given regular expression.
     *  Repeated matches are not combined and creates empty entries in the array.
     *  If {@code null} is passed, {@code null} is returned.
     *  @param  string     string
     *  @param  expression the delimiting regular expression
     *  @return the array of strings computed by splitting this string around
     *          matches of the given regular expression
     */
    public static String[] split(String string, String expression) {

        if (string == null)
            return null;
        
        if (expression == null
                || expression.isEmpty())
            return new String[] {string};

        Pattern pattern = Pattern.compile(expression);
        List<String> result = new ArrayList<>();
        while (true) {
            Matcher matcher = pattern.matcher(string);
            if (!matcher.find()) {
                result.add(string);
                break;
            }
            result.add(string.substring(0, matcher.start()));
            string = string.substring(matcher.end());
        }
        
        return result.toArray(new String[0]);
    }

    /**
     *  Decodes all escape sequences ({@code \b \t \n \f \r \" \' \\}) three
     *  bytes of octal escape sequences ({@code \00-\FF}) after a
     *  backslash. The method is tollerant and keeps incorrect sequences.
     *  If {@code null} is passed, {@code null} is returned.
     *  @param  string string to be decoded 
     *  @return the decoded string
     */
    public static String unescape(String string) {
        
        byte[] bytes;
        byte[] codex;

        int    code;
        int    count;
        int    cache;
        int    index;
        int    length;
        int    loop;
        
        if (string == null)
            return null;
        
        codex = ("\"'\\bfnrt\"'\\\b\f\n\r\t").getBytes();
        
        length = string.length();
        bytes  = new byte[length *2];
        
        for (loop = count = 0; loop < length; loop++) {
            
            code = string.charAt(loop);

            if (code == '\\') {
                if (loop +1 < length) {
                    index = Arrays.binarySearch(codex, (byte)string.charAt(loop +1));
                    if (index >= 0 && index < 8) {
                        code = codex[index +8];                    
                        loop += 1;
                    } else if (loop +5 < length
                            && string.charAt(loop +1) == 'u') {
                        loop += 5;
                        try {
                            index = Integer.parseInt(string.substring(loop -4, loop), 16);
                            if (index > 0xFF)
                                bytes[count++] = (byte)((index >>> 8) & 0xFF);
                            bytes[count++] = (byte)(index & 0xFF);
                            continue;
                        } catch (NumberFormatException exception) {
                            loop -= 5;
                        }
                    } else {
                        for (cache = index = 0; index < 3 && loop +1 < length; index++) {
                            if (string.charAt(loop +1) < '0'
                                    || string.charAt(loop +1) > '7'
                                    || (cache << 3) +string.charAt(loop +1) - '0' > 0xFF)
                                break;
                            cache = (cache << 3) +string.charAt(loop +1) - '0';
                            code  = cache;
                            loop++;
                        }
                    }
                }
            }

            bytes[count++] = (byte)code;
        }
        
        return new String(Arrays.copyOfRange(bytes, 0, count));
    }

    /**
     *  Encodes the control characters: BS, HT, LF, FF, CR, ', ", \ and all
     *  characters outside the ASCII range 0x20-0x7F.
     *  The escape uses:
     *  <ul>
     *    <li>slash + ISO</li>
     *    <li>slash + three bytes octal (0x80-0xFF)</li>
     *    <li>slash + four bytes hexadecimal (0x100-0xFFFF)</li>
     *  </ul>
     *  If {@code null} is passed, {@code null} is returned.
     *  @param  string string to be escaped
     *  @return the escaped string
     */
    public static String escape(String string) {
        
        byte[] codex;
        byte[] codec;
        byte[] cache;

        int    code;
        int    count;
        int    cursor;
        int    length;
        int    loop;
        
        if (string == null)
            return null;   
        
        length = string.length();
        
        cache = new byte[length *6];
        
        codex = ("\b\t\n\f\r\"'\\btnfr\"'\\").getBytes();
        codec = ("0123456789ABCDEF").getBytes();
        
        for (loop = count = 0; loop < length; loop++) {
            
            code = string.charAt(loop);
            
            cursor = Arrays.binarySearch(codex, (byte)code);
            if (cursor >= 0 && cursor < 8) {
                cache[count++] = '\\';
                cache[count++] = codex[cursor +8];
            } else if (code > 0xFF) {
                cache[count++] = '\\';
                cache[count++] = 'u';
                cache[count++] = codec[(code >> 16) & 0xF];
                cache[count++] = codec[(code >>  8) & 0xF];
                cache[count++] = codec[(code >>  4) & 0xF];
                cache[count++] = codec[(code & 0xF)];                
            } else if (code < 0x20 || code > 0x7F) {
                cache[count++] = '\\';
                cache[count++] = (byte)(0x30 +((code >> 6) & 0x7));
                cache[count++] = (byte)(0x30 +((code >> 3) & 0x7));
                cache[count++] = (byte)(0x30 +(code & 0x7));
            } else cache[count++] = (byte)code;
        }
        
        return new String(Arrays.copyOfRange(cache, 0, count));          
    }
    
    /**
     *  Replaces characters case-insensitive  in a string.
     *  If {@code null} is passed, {@code null} is returned.
     *  @param  string  string to search through
     *  @param  search  search string
     *  @param  replace string to be replaced
     *  @return the replaced string
     */
    public static String replace(String string, String search, String replace) {

        String result;
        String buffer;

        int    cursor;
        
        if (string == null)
            return null;         
        
        if (search == null
                || search.isEmpty())
            return string;

        if (replace == null)
            return string;

        buffer = string.toLowerCase();
        search = search.toLowerCase();

        result = "";
        while ((cursor = buffer.indexOf(search)) >= 0) {
            result = result.concat(string.substring(0, cursor)).concat(replace);
            string = string.substring(cursor +search.length());
            buffer = buffer.substring(cursor +search.length());
        }

        if (string.length() > 0)
            result = result.concat(string);

        return result;
    }    
    
    /**
     *  All control characters below ASCII 0x32 are escaped as ISO symbols.
     *  If {@code null} is passed, {@code null} is returned.
     *  @param  string string to decorate
     *  @return der decorated string
     */
    public static String decorate(String string) {

        if (string == null)
            return null;

        int index = 0;
        for (String decor : new String[] {"NUL", "SOH", "STX", "ETX", "EOT", "ENQ", "ACK", "BEL", "BS", "HT", "LF", "VT",
                "FF", "CR", "SO", "SI", "DLE", "DC1", "DC2", "DC3", "DC4", "NAK", "SYN", "ETB", "CAN", "EM", "SUB", "ESC",
                "FS", "GS", "RS", "US"})
            string = string.replace(String.valueOf((char)index++), ("[").concat(decor).concat("]"));

        return string;
    }
    
    /**
     *  All ISO symbols of control characters below ASCII 0x32 are unescaped.
     *  If {@code null} is passed, {@code null} is returned.
     *  @param  string string to decorate
     *  @return der decorated string
     */
    public static String undecorate(String string) {

        if (string == null)
            return null;

        int index = 0;
        for (String decor : new String[] {"NUL", "SOH", "STX", "ETX", "EOT", "ENQ", "ACK", "BEL", "BS", "HT", "LF", "VT",
                "FF", "CR", "SO", "SI", "DLE", "DC1", "DC2", "DC3", "DC4", "NAK", "SYN", "ETB", "CAN", "EM", "SUB", "ESC",
                "FS", "GS", "RS", "US"})
            string = Text.replace(string, ("[").concat(decor).concat("]"), String.valueOf((char)index++));

        return string;
    }
    
    /**
     *  Returns a string without any leading and trailing white spaces.
     *  The value {@code null} becomes an empty string. 
     *  @param  string string to be trimemd
     *  @return string without any leading and trailing white spaces
     */ 
    public static String trim(String string) {
        return Text.trim(string, false);
    }
    
    /**
     *  Returns a string without any leading and trailing white spaces.
     *  The value {@code null} becomes an empty string. If <i>nullable</i> is
     *  {@code true}, {@code null} is returned if the string is empty.
     *  @param  string   string to be trimemd
     *  @param  nullable {@code true} returns {@code null} if string is empty
     *  @return string without any leading and trailing white spaces
     *          or {@code null} is used <i>nullable</i> and the string is empty
     */     
    public static String trim(String string, boolean nullable) {
        
        if (string == null) {
            if (nullable)
                return null;
            string = "";
        } else string = string.trim();
        return string.isEmpty() && nullable ? null : string; 
    }

    /**
     *  Natural sorting of strings with alphanumeric content.
     *  @param  strings array to sort
     *  @return natural sorted string
     */
    public static String[] sortNatural(String... strings) {
        
        if (strings == null)
            return null;
        
        List<String> stringList = Arrays.asList(strings);
        Collections.sort(stringList, NATURAL_COMPARATOR);
        return stringList.toArray(new String[0]);
    }
    
    /** Naturally sort comparator */
    private static class NaturalComparator implements Comparator<String> {
        
        /**
         *  Normalizes the numeric fragments that they can be sorted.
         *  @param  string string to be escaped
         *  @return the normalized string
         */
        private static String normalize(String string) {
            
            string = Text.trim(string);
            String buffer = "";
            for (String fragment : string.split("(?:(?<=\\d)(?!\\d))|(?:(?<!\\d)(?=\\d))")) {
                try {
                    fragment = Long.valueOf(fragment).toString();
                    fragment = Long.toString(fragment.length(), 36).toUpperCase() + fragment;
                } catch (NumberFormatException exception) {
                }
                buffer += fragment;
            }
            return buffer;
        }

        @Override
        public int compare(String string1, String string2) {

            string1 = NaturalComparator.normalize(string1);
            string2 = NaturalComparator.normalize(string2);
            
            return string1.compareTo(string2);
        }
    }
}