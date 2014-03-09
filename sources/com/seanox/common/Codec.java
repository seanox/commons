/**
 *  LIZENZBEDINGUNGEN - Seanox Software Solutions ist ein Open-Source-Projekt,
 *  im Folgenden Seanox Software Solutions oder kurz Seanox genannt. Diese
 *  Software unterliegt der Version 2 der GNU General Public License.
 *
 *  Seanox Commons, Advanced Programming Interface
 *  Copyright (C) 2013 Seanox Software Solutions
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

/**
 *  Base class for various forms of coding.<br>
 *  <br>
 *  Codec 1.2013.0314<br>
 *  Copyright (C) 2013 Seanox Software Solutions<br>
 *  All rights reserved.
 *
 *  @author  Seanox Software Solutions
 *  @version 1.2013.0314
 */
public class Codec {

    /** Constant for de-/encoding NONE */
    public static final int NONE = 0;

    /** Constant for de-/encoding MIME */
    public static final int MIME = 1;

    /** Constant for de-/encoding UTF8 */
    public static final int UTF8 = 2;

    /** Constant for de-/encoding BASE64 */
    public static final int BASE64 = 3;

    /** Constant for de-/encoding DOT */
    public static final int DOT = 4;

    /** Constructor, to obstruct creating a instance of this class. */
    private Codec() {

        return;
    }

    /**
     *  Kodiert den String entsprechend dem Modus.
     *  R&uuml;ckgabe der kodierte String.
     *  @param  string der zu kodierende String
     *  @param  mode   der Kodierungsmodus
     *  @return der kodierte String
     *  @see    #NONE
     *  @see    #MIME
     *  @see    #UTF8
     *  @see    #BASE64
     */
    public static String encode(String string, int mode) {

        //null wird nicht kodiert und gibt ein leeren String zurueck
        if (string == null) return "";

        return new String(Codec.encode(string.getBytes(), mode));
    }

    /**
     *  Encodes the ByteArray in accordance with the mode.
     *  Return the encoded byte array.
     *  @param  bytes to be encoded bytes
     *  @param  mode  coding mode
     *  @return the encoded ByteArray
     *  @see    #NONE
     *  @see    #MIME
     *  @see    #UTF8
     *  @see    #BASE64
     */
    public static byte[] encode(byte[] bytes, int mode) {

        byte[] buffer;
        byte[] data;
        char[] array;

        int    code;
        int    cursor;
        int    length;
        int    loop;
        int    value;

        //null is not encoded and returns an empty byte array back
        if (bytes == null) return new byte[0];

        //the BASE64 stream is established
        array = ("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=").toCharArray();

        switch (mode) {

            case Codec.MIME : {

                //MIME Encoding
                buffer = new byte[(length = bytes.length) *3];

                for (loop = cursor = 0; loop < length; loop++) {

                    code = bytes[loop] & 0xFF;

                    //#&:=/-.?~*_ 0123456789 ABCDEFGHIJKLMNOPQRSTUVWXYZ abcdefghijklmnopqrstuvwxyz
                    //35 38 42 45 46 47 48-57 58 61 63 64 65-90 95 97-122 126
                    if (code != 35 && code != 38 && code != 42 && (code < 45 || code > 58) && code != 61 && (code < 63 || code > 90) && code != 95 && (code < 97 || code > 122) && code != 126) {

                        buffer[cursor++] = 37;

                        value = code >> 4;
                        buffer[cursor++] = (byte)((value > 9) ? value +55 : value +48);

                        value = code ^ (value << 4);
                        buffer[cursor++] = (byte)((value > 9) ? value +55 : value +48);

                    } else {

                        buffer[cursor++] = (byte)code;
                    }
                }

                //the ByteArray is corrected in the size
                data   = buffer;
                buffer = new byte[cursor];

                System.arraycopy(data, 0, buffer, 0, cursor);

                break;
            }

            case Codec.UTF8 : {

                //UTF8 Encoding
                buffer = new byte[(length = bytes.length) *6];

                for (loop = cursor = 0; loop < length; loop++) {

                    code = bytes[loop] & 0xFF;

                    if (code < 0x80) buffer[cursor++] = (byte)code;

                    if (code > 0x7F && code < 0x800) {

                        buffer[cursor++] = (byte)(code >> 0x06 | 0xC0);
                        buffer[cursor++] = (byte)(code & 0x3F | 0x80);
                    }

                    if (code > 0x7FF && code < 0x10000) {buffer[cursor++] = (byte)(code >> 0x0C | 0xE0);

                        buffer[cursor++] = (byte)(code >> 0x06 & 0x3F | 0x80);
                        buffer[cursor++] = (byte)(code & 0x3F | 0x80);
                    }

                    if (code > 0xFFFF) {

                        buffer[cursor++] = (byte)(code >> 0x12 | 0xF0);
                        buffer[cursor++] = (byte)(code >> 0x0C & 0x3F | 0x80);
                        buffer[cursor++] = (byte)(code >> 0x06 & 0x3F | 0x80);
                        buffer[cursor++] = (byte)(code & 0x3F | 0x80);
                    }
                }

                //the ByteArray is corrected in the size
                data   = buffer;
                buffer = new byte[cursor];

                System.arraycopy(data, 0, buffer, 0, cursor);

                break;
            }

            case Codec.BASE64 : {

                //BASE64 Encoding
                buffer = new byte[(length = bytes.length) *4];

                for (loop = cursor = 0; loop < length; loop += 3) {

                    code = bytes[loop] & 0xFF;

                    code = (code << 0x08) | (((loop +1) < length) ? (bytes[loop +1] & 0xFF) : 0);
                    code = (code << 0x08) | (((loop +2) < length) ? (bytes[loop +2] & 0xFF) : 0);

                    buffer[cursor +3] = (byte)array[((loop +2) < length ? (code & 0x3F) : 0x40)];

                    code >>= 0x06;

                    buffer[cursor +2] = (byte)array[((loop +1) < length ? (code & 0x3F) : 0x40)];
                    buffer[cursor +1] = (byte)array[(code >>= 0x06) & 0x3F];
                    buffer[cursor +0] = (byte)array[(code >>= 0x06) & 0x3F];

                    cursor += 4;
                }

                //the ByteArray is corrected in the size
                data   = buffer;
                buffer = new byte[cursor];

                System.arraycopy(data, 0, buffer, 0, cursor);

                break;
            }

            default : {

                buffer = bytes;

                break;
            }
        }

        return buffer;
    }

    /**
     *  Dekodiert den String entsprechend dem Modus.
     *  R&uuml;ckgabe der Dekodierte String.
     *  @param  string zu dekodierender String
     *  @param  mode   Dekodierungsmodus
     *  @return der dekodierte String
     *  @see    #NONE
     *  @see    #MIME
     *  @see    #UTF8
     *  @see    #BASE64
     *  @see    #DOT
     */
    public static String decode(String string, int mode) {

        //null wird nicht dekodiert und gibt ein leeren String zurueck
        if (string == null) return "";

        return new String(Codec.decode(string.getBytes(), mode));
    }

    /**
     *  Decodes the ByteArray in accordance with the mode.
     *  Return the decoded byte array.
     *  @param  bytes to be encoded bytes
     *  @param  mode  coding mode
     *  @return the encoded ByteArray
     *  @see    #NONE
     *  @see    #MIME
     *  @see    #UTF8
     *  @see    #BASE64
     */
    public static byte[] decode(byte[] bytes, int mode) {

        String  stream;
        String  string;

        byte[]  buffer;
        byte[]  data;

        boolean control;

        int     code;
        int     count;
        int     cursor;
        int     length;
        int     loop;
        int     pattern;

        //null is not encoded and returns an empty byte array back
        if (bytes == null) return new byte[0];

        length = bytes.length;
        buffer = new byte[length];

        switch (mode) {

            case Codec.MIME : {

                for (loop = count = 0; loop < length; loop++) {

                    //the ASCII code is determined
                    code = bytes[loop] & 0xFF;

                    if (code == 43) code = 32;

                    //the hex code is converted to ASCII characters
                    if (code == 37) {

                        try {code = Integer.parseInt(new String(bytes, ++loop, 2), 16);
                        } catch (Throwable throwable) {code = -1;}

                        loop++;
                    }

                    if (code >= 0) buffer[count++] = (byte)code;
                }

                break;
            }

            case Codec.UTF8 : {

                for (loop = count = pattern = cursor = 0, control = false; loop < length; loop++) {

                    //the ASCII code is determined
                    code = bytes[loop] & 0xFF;

                    if (code >= 0xC0 && code <= 0xC3) control = true;

                    //decoding the bytes as UTF-8, the pattern 10xxxxxx is
                    //extended by 6 bits
                    if ((code & 0xC0) == 0x80) {

                        pattern = (pattern << 0x06) | (code & 0x3F);

                        if (--cursor == 0) {buffer[count++] = (byte)pattern; control = false;}

                    } else {

                        pattern = 0;
                        cursor  = 0;

                        //0xxxxxxx (7Bit/0Byte) be used directly
                        if (((code & 0x80) == 0x00) || !control) {buffer[count++] = (byte)code; control = false;}

                        //110xxxxx (5Bit/1Byte), 1110xxxx (4Bit/2Byte),
                        //11110xxx (3Bit/3Byte), 111110xx (2Bit/4Byte),
                        //1111110x (1Bit/5Byte)
                        if (cursor == 0 && (code & 0xE0) == 0xC0) {cursor = 1; pattern = code & 0x1F;}
                        if (cursor == 0 && (code & 0xF0) == 0xE0) {cursor = 2; pattern = code & 0x0F;}
                        if (cursor == 0 && (code & 0xF8) == 0xF0) {cursor = 3; pattern = code & 0x07;}
                        if (cursor == 0 && (code & 0xFC) == 0xF8) {cursor = 4; pattern = code & 0x03;}
                        if (cursor == 0 && (code & 0xFE) == 0xFC) {cursor = 5; pattern = code & 0x01;}
                    }
                }

                break;
            }

            case Codec.BASE64 : {

                for (loop = count = cursor = pattern = 0; loop < length; loop++) {

                    //the ASCII code is determined
                    code = bytes[loop] & 0xFF;

                    //00-0F -1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
                    //10-1F -1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
                    //20-2F -1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,62,-1,-1,-1,63,
                    //30-3F 52,53,54,55,56,57,58,59,60,61,-1,-1,-1,-1,-1,-1,
                    //40-4F -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9,10,11,12,13,14,
                    //50-5F 15,16,17,18,19,20,21,22,23,24,25,-1,-1,-1,-1,-1,
                    //60-6F -1,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40,
                    //70-7F 41,42,43,44,45,46,47,48,49,50,51,-1,-1,-1,-1,-1,
                    //80-8F -1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
                    //90-9F -1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
                    //A0-AF -1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
                    //B0-BF -1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
                    //C0-CF -1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
                    //D0-DF -1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
                    //E0-EF -1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
                    //F0-FF -1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1

                    if (!((code == 43) || (code == 47) || ((code >= 48) && (code <= 57)) || ((code >= 65) && (code <= 90)) || ((code >= 97) && (code <= 122)))) code = -1;

                    if (code == 43) code = 62;
                    if (code == 47) code = 63;
                    if (code >= 48 && code <= 57)  code +=  4;
                    if (code >= 65 && code <= 90)  code -= 65;
                    if (code >= 97 && code <= 122) code -= 71;

                    if (code != -1) {

                        switch (cursor) {

                            case 0 : {++cursor; break;}
                            case 1 : {buffer[count++] = (byte)((pattern << 0x02) | ((code & 0x30) >> 0x04)); ++cursor; break;}
                            case 2 : {buffer[count++] = (byte)(((pattern & 0x0F) << 0x04) | ((code & 0x3C) >> 0x02)); ++cursor; break;}
                            case 3 : {buffer[count++] = (byte)(((pattern & 0x03) << 0x06) | code); cursor = 0; break;}
                        }

                        pattern = code;
                    }
                }

                break;
            }

            case Codec.DOT : {

                //path is switched to slash
                string = new String(bytes).replace('\\', '/').trim();

                //multiple slashes are summarized
                while ((cursor = string.indexOf("//")) >= 0) {

                    string = string.substring(0, cursor).concat(string.substring(cursor +1));
                }

                //path will balanced /abc/./def/../ghi -> /abc/ghi
                //path will balanced /.
                if (string.endsWith("/.")) string = string.concat("/");

                while ((cursor = string.indexOf("/./")) >= 0) {

                    string = string.substring(0, cursor).concat(string.substring(cursor +2));
                }

                //string will balanced /..
                if (string.endsWith("/..")) string = string.concat("/");

                while ((cursor = string.indexOf("/../")) >= 0) {

                    stream = string.substring(cursor +3);
                    string   = string.substring(0, cursor);

                    cursor = string.lastIndexOf("/");
                    cursor = (cursor < 0) ? 0 : cursor;
                    string   = string.substring(0, cursor).concat(stream);
                }

                //multiple slashes are summarized
                while ((cursor = string.indexOf("//")) >= 0) {

                    string = string.substring(0, cursor).concat(string.substring(cursor +1));
                }

                count = (buffer = string.getBytes()).length;

                break;
            }

            default : {

                //keine Decodierung
                count = (buffer = bytes).length;

                break;
            }
        }

        //das ByteArray wird eingerichtet
        data = new byte[count];

        //the ByteArray is corrected in the size
        System.arraycopy(buffer, 0, data, 0, count);

        return data;
    }
}