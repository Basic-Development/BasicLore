package com.vestriamc.basiclore.commandTabCompletion;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public final class LoreCommandCompletion implements TabCompleter {
    @Override
    public @Nullable List<String> onTabComplete(@NotNull final CommandSender sender,
                                                @NotNull final Command command,
                                                @NotNull final String label,
                                                @NotNull final String[] args) {

        if (args.length == 0) {
            return null;
        }

        List<String> completion = new ArrayList<>();
        String subcommand = args[0];
        if (args.length == 1) {

            List<String> validSubcommands = new ArrayList<>();
            validSubcommands.add("add");
            validSubcommands.add("edit");
            validSubcommands.add("glow");
            validSubcommands.add("remove");
            validSubcommands.add("rename");
            validSubcommands.add("tag");
            validSubcommands.add("unglow");
            validSubcommands.add("unname");

            for (String arg : validSubcommands) {
                if (arg.toLowerCase().startsWith(subcommand.toLowerCase())) {
                    completion.add(arg);
                }
            }

            return completion;
        }

        if (!(sender instanceof Player player)) {
            return null;
        }

        if ((!subcommand.equalsIgnoreCase("edit")) && (!subcommand.equalsIgnoreCase("remove"))) {
            return null;
        }

        ItemMeta meta = player.getInventory().getItemInMainHand().getItemMeta();
        if (meta == null) {
            return null;
        }
        List<Component> lore = meta.lore();
        if (lore == null) {
            return null;
        }

        if (args.length == 2) {
            int size = lore.size();
            if (size < 1) {
                return null;
            }
            List<String> validInts = new ArrayList<>();
            for (int i = 1; i <= size; i++) {
                validInts.add(String.valueOf(i));
            }

            for (String arg : validInts) {
                if (arg.toLowerCase().startsWith(args[1].toLowerCase())) {
                    completion.add(arg);
                }
            }

            return completion;
        }

        if (args.length == 3) {
            if (!subcommand.equalsIgnoreCase("edit")) {
                return null;
            }
            int lineNumber = 0;
            try {
                lineNumber = Integer.parseInt(args[1]);
            } catch (Exception e) {
                return null;
            }
            if (lineNumber == 0) {
                return null;
            }
            Component line = lore.get(lineNumber-1);
            String lineAsString = LegacyComponentSerializer.legacyAmpersand().serialize(line);

            if (lineAsString.startsWith(args[2])) {
                completion.add(lineAsString);
            }
            return completion;
        }

        return null;

    }
}
