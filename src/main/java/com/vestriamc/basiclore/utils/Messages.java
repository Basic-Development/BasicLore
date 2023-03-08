package com.vestriamc.basiclore.utils;

import com.vestriamc.basiclore.BasicLore;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public final class Messages {

    private Messages() {}

    public static final TextColor MSG_COLOR = TextColor.color(0x50ffc0);
    public static final TextColor ERR_COLOR = TextColor.color(0xff002f);
    private static final int REPEATED_SEPARATOR_LENGTH = 12;
    private static final String LEFT_ARROW = "≺";
    private static final String RIGHT_ARROW = "≻";
    private static final String ELLIPSIS = "┈";
    public static Component prefix = BasicLore.miniMessage.deserialize(
            "<aqua>[</aqua>"
                    + "<bold><gradient:#50ffc0:#c050ff>BasicLore</gradient></bold>"
                    + "<aqua>]</aqua> "
    );

    private static Component format(@NotNull final String message,
                                    @NotNull final TextColor color) {
        return prefix.append(
                Component.text(message, color)
                        .decoration(TextDecoration.ITALIC, false)
                        .decoration(TextDecoration.BOLD, false)
        );
    }

    private static Component formatNoPrefix(@NotNull final String message,
                                    @NotNull final TextColor color) {
        return Component.text(message, color)
                        .decoration(TextDecoration.ITALIC, false)
                        .decoration(TextDecoration.BOLD, false);
    }

    public static Component errNoPermission() {
        return format("You don't have permission to perform this command!", ERR_COLOR);
    }

    public static Component errConsoleSender() {
        return format("This command cannot be performed by Console!", ERR_COLOR);
    }

    public static List<Component> help(@NotNull final Player player) {
        List<Component> help = new ArrayList<>();

        if (player.hasPermission("lore.add")) {
            help.add(formatNoPrefix("/lore add <lore>", MSG_COLOR));
        }
        if (player.hasPermission("lore.edit")) {
            help.add(formatNoPrefix("/lore edit <lineNum> <newLore>", MSG_COLOR));
        }
        if (player.hasPermission("lore.glow")) {
            help.add(formatNoPrefix("/lore glow", MSG_COLOR));
        }
        if (player.hasPermission("lore.remove")) {
            help.add(formatNoPrefix("/lore remove <lineNum>", MSG_COLOR));
        }
        if (player.hasPermission("lore.rename")) {
            help.add(formatNoPrefix("/lore rename <itemName>", MSG_COLOR));
        }
        if (player.hasPermission("lore.tag")) {
            help.add(formatNoPrefix("/lore tag [optionalCustomTag]", MSG_COLOR));
        }
        if (player.hasPermission("lore.glow")) {
            help.add(formatNoPrefix("/lore unglow", MSG_COLOR));
        }
        if (player.hasPermission("lore.unname")) {
            help.add(formatNoPrefix("/lore unname", MSG_COLOR));
        }

        if (help.size() == 0) {
            help.add(format("You don't have permission to perform any BasicLore Commands!", ERR_COLOR));
        }
        help.add(0,
                format(">>> Commands <<<", MSG_COLOR)
                .decoration(TextDecoration.BOLD, true)
                .decoration(TextDecoration.ITALIC, false)
        );

        return help;
    }

    public static Component errInvalidArgument(@NotNull final String argType,
                                               @NotNull final String invalidArg) {
        return format("Invalid argument " + invalidArg + " for input " + argType + "!", ERR_COLOR);
    }

    public static Component errNoHeldItem() {
        return format("You must be holding an item in your main hand to perform this operation!", ERR_COLOR);
    }

    public static Component errNoItemMeta() {
        return format("If you see this error message, please let me know ~~~~exactly~~~~ what you did to get it."
                + "You encountered an item with no metadata.", ERR_COLOR);
    }

    public static Component infoRemovedItemName(@NotNull final Component oldItemName) {
        return format("Removed the display name ", MSG_COLOR)
                .append(oldItemName)
                .append(Component.text(" from your item", MSG_COLOR));
    }

    public static Component infoRenamedItem(@NotNull final String newItemName) {
        return format("Renamed your held item to ", MSG_COLOR)
                .append(LegacyComponentSerializer.legacySection().deserialize(newItemName));
    }

    public static Component errMissingArgument(@NotNull final String argType) {
        return format("Missing argument for input " + argType + "!", ERR_COLOR);
    }

    public static Component errNoLoreToEdit() {
        return format("This item doesn't have any lore to edit! To add lore to the item, use /lore add <lore>.", ERR_COLOR);
    }

    public static Component errNoLoreAtLine(final int lineToEdit) {
        return format("There is no lore present at line " + lineToEdit + "!", ERR_COLOR);
    }

    public static Component infoEditedLore(@NotNull final String oldLoreLine,
                                           @NotNull final String formattedLoreLine) {
        return format("Edited lore from ", MSG_COLOR)
                .append(LegacyComponentSerializer.legacySection().deserialize(oldLoreLine))
                .append(Component.text(" to ", MSG_COLOR))
                .append(LegacyComponentSerializer.legacySection().deserialize(formattedLoreLine));
    }

    public static Component errNoLoreToRemove() {
        return format("This item doesn't have any lore to be able to remove!", ERR_COLOR);
    }

    public static Component infoRemovedLore(final int lineNumber,
                                            @NotNull final String oldLoreLine) {
        return format("Removed lore ", MSG_COLOR)
                .append(LegacyComponentSerializer.legacySection().deserialize(oldLoreLine))
                .append(Component.text(" from line " + lineNumber + "! ", MSG_COLOR));
    }

    public static Component infoAddedLore(@NotNull final String formattedLoreLine) {
        return format("Added lore to your item: ", MSG_COLOR)
                .append(LegacyComponentSerializer.legacySection().deserialize(formattedLoreLine));

    }

    public static Component infoAddedLore(@NotNull final Component formattedLoreLine) {
        return format("Added lore to your item: ", MSG_COLOR)
                .append(formattedLoreLine);
    }

    public static Component errNoNameToRemove() {
        return format("This item doesn't have a display name to remove!", ERR_COLOR);
    }

    public static Component infoAddedGlowing() {
        return format("Added the glow effect to your item!", MSG_COLOR);
    }

    public static Component infoRemovedGlowing() {
        return format("Removed the glow effect to your item!", MSG_COLOR);
    }

    public static Component separator(@NotNull final TextColor color) {
        Component end = Component.text(ELLIPSIS, color, TextDecoration.STRIKETHROUGH);
        Component divider = Component.text(LEFT_ARROW + RIGHT_ARROW, color, TextDecoration.BOLD);

        Component middle = Component.text(ELLIPSIS.repeat(REPEATED_SEPARATOR_LENGTH), color, TextDecoration.STRIKETHROUGH);

        return end.decoration(TextDecoration.STRIKETHROUGH, true)
                .append(divider.decoration(TextDecoration.STRIKETHROUGH, false))
                .append(middle.decoration(TextDecoration.STRIKETHROUGH, true))
                .append(divider.decoration(TextDecoration.STRIKETHROUGH, false))
                .append(end.decoration(TextDecoration.STRIKETHROUGH, true)).
                decoration(TextDecoration.ITALIC, false);

    }

    public static Component separator() {
        TextColor color = NamedTextColor.GRAY;
        Component end = Component.text(ELLIPSIS, color, TextDecoration.STRIKETHROUGH);
        Component divider = Component.text(LEFT_ARROW + RIGHT_ARROW, color, TextDecoration.BOLD);

        Component middle = Component.text(ELLIPSIS.repeat(REPEATED_SEPARATOR_LENGTH), color, TextDecoration.STRIKETHROUGH);

        return end.decoration(TextDecoration.STRIKETHROUGH, true)
                .append(divider.decoration(TextDecoration.STRIKETHROUGH, false))
                .append(middle.decoration(TextDecoration.STRIKETHROUGH, true))
                .append(divider.decoration(TextDecoration.STRIKETHROUGH, false))
                .append(end.decoration(TextDecoration.STRIKETHROUGH, true)).
                decoration(TextDecoration.ITALIC, false);

    }

    public static Component separator(@NotNull final String title) {
        TextColor color = NamedTextColor.GRAY;
        Component end = Component.text(ELLIPSIS, color, TextDecoration.STRIKETHROUGH);
        Component divider = Component.text(LEFT_ARROW + RIGHT_ARROW, color, TextDecoration.BOLD);

        int titleLength = title.length();
        int paddingLength = (REPEATED_SEPARATOR_LENGTH - titleLength) / 2;

        Component padding = Component.text(ELLIPSIS.repeat(paddingLength), color, TextDecoration.STRIKETHROUGH);
        Component formattedTitle = Component.text(title, NamedTextColor.WHITE, TextDecoration.BOLD);

        return end.decoration(TextDecoration.STRIKETHROUGH, true)
                .append(divider.decoration(TextDecoration.STRIKETHROUGH, false))
                .append(padding.decoration(TextDecoration.STRIKETHROUGH, true))
                .append(formattedTitle.decoration(TextDecoration.STRIKETHROUGH, false))
                .append(padding.decoration(TextDecoration.STRIKETHROUGH, true))
                .append(divider.decoration(TextDecoration.STRIKETHROUGH, false))
                .append(end.decoration(TextDecoration.STRIKETHROUGH, true)).
                decoration(TextDecoration.ITALIC, false);


    }
}
