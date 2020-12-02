package com.nllk.megaweatherapplication;

import java.io.CharArrayWriter;
import java.lang.reflect.Array;
import java.util.Locale;

public class Transliteration
{
    public static String cyr2lat(char ch) {
        switch (ch) {
            case 'А':
                return "A";
            case 'Б':
                return "B";
            case 'В':
                return "V";
            case 'Г':
                return "G";
            case 'Д':
                return "D";
            case 'Е':
                return "E";
            case 'Ё':
                return "YO";
            case 'Ж':
                return "ZH";
            case 'З':
                return "Z";
            case 'И':
                return "I";
            case 'Й':
                return "J";
            case 'К':
                return "K";
            case 'Л':
                return "L";
            case 'М':
                return "M";
            case 'Н':
                return "N";
            case 'О':
                return "O";
            case 'П':
                return "P";
            case 'Р':
                return "R";
            case 'С':
                return "S";
            case 'Т':
                return "T";
            case 'У':
                return "U";
            case 'Ф':
                return "F";
            case 'Х':
                return "H";
            case 'Ц':
                return "C";
            case 'Ч':
                return "CH";
            case 'Ш':
                return "SH";
            case 'Щ':
                return "SCH";
            case 'Ъ':
                return "J";
            case 'Ы':
                return "I";
            case 'Ь':
                return "J";
            case 'Э':
                return "E";
            case 'Ю':
                return "YU";
            case 'Я':
                return "YA";
            default:
                return String.valueOf(ch);
        }
    }
    public static String cyr2lat(String s){
        StringBuilder sb = new StringBuilder(s.length()*2);
        for(char ch: s.toUpperCase().toCharArray()){
            sb.append(cyr2lat(ch));
        }
        return sb.toString();
    }
}
