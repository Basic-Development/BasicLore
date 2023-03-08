package com.vestriamc.basiclore.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FormatUtil {

    static final char COLOR_CHAR = '\u00A7';

    // Returns a string containing all arguments provided beyond the specified index.
    //      Excludes the argument at that index.
    public static String getInputAfterIndex(@NotNull final String[] args,
                                            final int start) {
        final StringBuilder b = new StringBuilder();
        for (int i = (start + 1); i < args.length; i++) {
            b.append(args[i]).append(" ");
        }
        return b.toString().trim();
    }

    // Takes in a string containing color formatting in many possible formats.
    // Returns a Component with the text styled according to the formatting of the String input.
    public static Component formatInput(String unformattedString) {
        return LegacyComponentSerializer.legacyAmpersand().deserialize(unformattedString)
                .decoration(TextDecoration.ITALIC, false);
    }

    public static String formatInputLegacy(String unformattedString) {
        return ChatColor.translateAlternateColorCodes('&', translateHexColorCodes("&#", "", unformattedString));
    }

    public static String translateHexColorCodes(String startTag, String endTag, String message) {
        final Pattern hexPattern = Pattern.compile(startTag + "([A-Fa-f0-9]{6})" + endTag);
        Matcher matcher = hexPattern.matcher(message);
        StringBuilder b = new StringBuilder(message.length() + 4 * 8);
        while (matcher.find()) {
            String group = matcher.group(1);
            matcher.appendReplacement(b, COLOR_CHAR + "x"
                    + COLOR_CHAR + group.charAt(0) + COLOR_CHAR + group.charAt(1)
                    + COLOR_CHAR + group.charAt(2) + COLOR_CHAR + group.charAt(3)
                    + COLOR_CHAR + group.charAt(4) + COLOR_CHAR + group.charAt(5)
            );
        }
        return matcher.appendTail(b).toString();
    }

}
