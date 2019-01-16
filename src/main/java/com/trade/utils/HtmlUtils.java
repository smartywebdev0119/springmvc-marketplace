package com.trade.utils;

public class HtmlUtils {

    public enum Color{
        RED, GREEN
    }

    public static String makeTextColorful(String text, Color color){

        if (color == Color.RED){

            return "<span style=\"color: red\">"+text+"</span>";

        } else if (color == Color.GREEN){

            return "<span style=\"color: green\">"+text+"</span>";
        }

        return text;
    }
}
