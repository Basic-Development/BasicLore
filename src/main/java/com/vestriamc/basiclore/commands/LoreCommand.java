package com.vestriamc.basiclore.commands;

import com.vestriamc.basiclore.utils.FormatUtil;
import com.vestriamc.basiclore.utils.Messages;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Year;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@SuppressWarnings("deprecation")
public class LoreCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull final CommandSender sender,
                             @NotNull final Command command,
                             @NotNull final String label,
                             @NotNull final String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage(Messages.errConsoleSender());
            return true;
        }

        if (args.length == 0) {
            help(player);
            return true;
        }

        String subcommand = args[0];
        if (subcommand.equalsIgnoreCase("help") || subcommand.equalsIgnoreCase("?")) {
            help(player);
            return true;
        }

        // Validate their command based on the acceptable subcommand arguments
        List<String> validLoreCommands = new ArrayList<>();
        validLoreCommands.add("add");
        validLoreCommands.add("remove");
        validLoreCommands.add("edit");
        validLoreCommands.add("rename");
        validLoreCommands.add("unname");
        validLoreCommands.add("glow");
        validLoreCommands.add("unglow");
        validLoreCommands.add("tag");

        if (!validLoreCommands.contains(subcommand)) {
            player.sendMessage(Messages.errInvalidArgument("Lore Command", subcommand));
            return true;
        }

        //They must hold an item to apply lore to
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item.getType() == Material.AIR) {
            player.sendMessage(Messages.errNoHeldItem());
            return true;
        }
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            player.sendMessage(Messages.errNoItemMeta());
            return true;
        }

        switch (subcommand) {
            case ("add") -> {

                if (!player.hasPermission("lore.add")) {
                    sender.sendMessage(Messages.errNoPermission());
                    return true;
                }

                List<String> lore = meta.getLore();
                if (lore == null) {
                    lore = new ArrayList<>();
                }

                String unformattedLoreLine = FormatUtil.getInputAfterIndex(args, 0);
                if (unformattedLoreLine.isBlank() || unformattedLoreLine.isEmpty()) {
                    player.sendMessage(Messages.errMissingArgument("Item Lore"));
                    return true;
                }
                String formattedLoreLine = FormatUtil.formatInputLegacy(unformattedLoreLine);

                lore.add(formattedLoreLine);
                meta.setLore(lore);
                item.setItemMeta(meta);
                sender.sendMessage(Messages.infoAddedLore(formattedLoreLine));
                return true;
            }
            case ("remove") -> { //lore remove <lineNum>

                if (!player.hasPermission("lore.remove")) {
                    sender.sendMessage(Messages.errNoPermission());
                    return true;
                }

                int lineToEdit = 0;
                try {
                    lineToEdit = Integer.parseInt(args[1]);
                } catch (Exception e) {
                    sender.sendMessage(Messages.errInvalidArgument("line number", args[1]));
                    return true;
                }
                List<String> lore = meta.getLore();
                if (lore == null) {
                    sender.sendMessage(Messages.errNoLoreToRemove());
                    return true;
                }
                if (lineToEdit > lore.size()) {
                    sender.sendMessage(Messages.errNoLoreAtLine(lineToEdit));
                    return true;
                }
                String oldLoreLine = lore.get(lineToEdit-1);
                lore.remove(lineToEdit-1);
                meta.setLore(lore);
                item.setItemMeta(meta);
                sender.sendMessage(Messages.infoRemovedLore(lineToEdit,oldLoreLine));
                return true;

            }
            case ("edit") -> { //lore edit <lineNum> <newLineOfLore>

                if (!player.hasPermission("lore.edit")) {
                    sender.sendMessage(Messages.errNoPermission());
                    return true;
                }

                int lineToEdit = 0;
                try {
                    lineToEdit = Integer.parseInt(args[1]);
                } catch (Exception e) {
                    sender.sendMessage(Messages.errInvalidArgument("line number", args[1]));
                    return true;
                }
                List<String> lore = meta.getLore();
                if (lore == null) {
                    sender.sendMessage(Messages.errNoLoreToEdit());
                    return true;
                }
                if (lineToEdit > lore.size()) {
                    sender.sendMessage(Messages.errNoLoreAtLine(lineToEdit));
                    return true;
                }

                String unformattedLoreLine = FormatUtil.getInputAfterIndex(args, 1);
                if (unformattedLoreLine.isBlank() || unformattedLoreLine.isEmpty()) {
                    player.sendMessage(Messages.errMissingArgument("Item Lore"));
                    return true;
                }
                String formattedLoreLine = FormatUtil.formatInputLegacy(unformattedLoreLine);

                String oldLoreLine = lore.get(lineToEdit-1);
                lore.set(lineToEdit-1, formattedLoreLine);
                meta.setLore(lore);
                item.setItemMeta(meta);
                sender.sendMessage(Messages.infoEditedLore(oldLoreLine, formattedLoreLine));
                return true;

            }
            case ("rename") -> { //lore rename <newItemName>

                if (!sender.hasPermission("lore.rename")) {
                    sender.sendMessage(Messages.errNoPermission());
                    return true;
                }

                String unformattedDisplayName = FormatUtil.getInputAfterIndex(args, 0);
                if (unformattedDisplayName.isBlank() || unformattedDisplayName.isEmpty()) {
                    player.sendMessage(Messages.errMissingArgument("Item Name"));
                    return true;
                }

                String formattedDisplayName = FormatUtil.formatInputLegacy(unformattedDisplayName);
                meta.setDisplayName(formattedDisplayName);
                item.setItemMeta(meta);

                player.sendMessage(Messages.infoRenamedItem(formattedDisplayName));

            }
            case ("unname") -> { // lore unname

                if (!player.hasPermission("lore.unname")) {
                    player.sendMessage(Messages.errNoPermission());
                    return true;
                }

                Component displayName = meta.displayName();
                if (displayName == null) {
                    sender.sendMessage(Messages.errNoNameToRemove());
                    return true;
                }
                meta.displayName(null);
                item.setItemMeta(meta);

                player.sendMessage(Messages.infoRemovedItemName(displayName));
            }
            case ("glow") -> {

                if (!sender.hasPermission("lore.glow")) {
                    sender.sendMessage(Messages.errNoPermission());
                    return true;
                }

                item.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
                item.addItemFlags(ItemFlag.HIDE_ENCHANTS);

                player.sendMessage(Messages.infoAddedGlowing());

                return true;

            }
            case ("unglow") -> {

                if (!sender.hasPermission("lore.glow")) {
                    sender.sendMessage(Messages.errNoPermission());
                    return true;
                }

                item.removeEnchantment(Enchantment.DURABILITY);
                item.removeItemFlags(ItemFlag.HIDE_ENCHANTS);

                player.sendMessage(Messages.infoRemovedGlowing());

                return true;

            }
            case ("tag") -> {
                if (!player.hasPermission("lore.tag")) {
                    sender.sendMessage(Messages.errNoPermission());
                    return true;
                }

                List<Component> lore = meta.lore();
                if (lore == null) {
                    lore = new ArrayList<>();
                }
                lore.add(Messages.separator());

                if (args.length >= 2) {
                    meta.lore(lore);
                    item.setItemMeta(meta);

                    ItemMeta im = item.getItemMeta();
                    List<String> itemLore = im.getLore();
                    if (itemLore == null) {
                        player.sendMessage(Messages.errNoItemMeta());
                        return true;
                    }

                    String unformattedLoreLine = FormatUtil.getInputAfterIndex(args, 0);
                    String formattedLoreLine = FormatUtil.formatInputLegacy(unformattedLoreLine);
                    itemLore.add(formattedLoreLine);
                    im.setLore(itemLore);
                    item.setItemMeta(im);

                    player.sendMessage(Messages.infoAddedLore(formattedLoreLine));

                    return true;

                }

                Year year = Year.now();

                Component formattedLoreLine = Component.text("Vestria " + year, TextColor.color(0x720077))
                        .decoration(TextDecoration.ITALIC, false)
                        .decoration(TextDecoration.BOLD, true);

                lore.add(formattedLoreLine);
                meta.lore(lore);
                item.setItemMeta(meta);

                player.sendMessage(Messages.infoAddedLore(formattedLoreLine));

            }
            default -> {
                help(player);
            }
        }
        return true;
    }

    public void help(@NotNull final Player player) {
        List<Component> helpMessages = Messages.help(player);
        for (Component c : helpMessages) {
            player.sendMessage(c);
        }
    }
}
