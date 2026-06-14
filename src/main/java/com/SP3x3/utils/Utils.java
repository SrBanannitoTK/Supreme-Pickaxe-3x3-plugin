package com.SP3x3.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.md_5.bungee.api.ChatColor;

public class Utils {
   public static String colorize(String msg) {
      for(Matcher match = Pattern.compile("#[a-fA-F0-9]{6}").matcher(msg); match.find(); match = Pattern.compile("#[a-fA-F0-9]{6}").matcher(msg)) {
         String color = msg.substring(match.start(), match.end());
         msg = msg.replace(color, String.valueOf(ChatColor.of(color)));
      }

      return ChatColor.translateAlternateColorCodes('&', msg);
   }
}
