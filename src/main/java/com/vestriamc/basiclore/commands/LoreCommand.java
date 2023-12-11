package com.vestriamc.basiclore.commands;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import cloud.commandframework.arguments.standard.BooleanArgument;
import cloud.commandframework.arguments.standard.IntegerArgument;
import cloud.commandframework.arguments.standard.StringArgument;
import cloud.commandframework.context.CommandContext;
import cloud.commandframework.execution.postprocessor.CommandPostprocessingContext;
import cloud.commandframework.execution.postprocessor.CommandPostprocessor;
import cloud.commandframework.meta.CommandMeta;
import cloud.commandframework.minecraft.extras.MinecraftHelp;
import cloud.commandframework.services.types.ConsumerService;
import com.vestriamc.basiclore.BasicLore;
import com.vestriamc.basiclore.utils.Messages;
import io.leangen.geantyref.TypeToken;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.framework.qual.DefaultQualifier;

import java.time.Year;
import java.util.ArrayList;
import java.util.List;

import static net.kyori.adventure.text.format.TextDecoration.ITALIC;
import static net.kyori.adventure.text.format.TextDecoration.State.FALSE;

@DefaultQualifier(NonNull.class)
public final class LoreCommand {

    private static final TypeToken<Boolean> BOOLEAN = new TypeToken<>() {
    };
    private static final CommandMeta.Key<Boolean> REQUIRES_HELD_ITEM = CommandMeta.Key.of(BOOLEAN, "requires_held_item");


    private static final LegacyComponentSerializer SERIALIZER = LegacyComponentSerializer
            .builder()
            .character('&')
            .hexColors()
            .hexCharacter('#')
            .build();

    public void register(final CommandManager<CommandSender> manager) {

        Command.Builder<CommandSender> baseCommand = manager
                .commandBuilder("lore")
                .senderType(Player.class)
                .meta(REQUIRES_HELD_ITEM, true);

        manager.command(
                baseCommand
                        .literal("add")
                        .permission("lore.add")
                        .argument(StringArgument.greedy("text"))
                        .handler(this::handleAdd)
        );

        manager.command(
                baseCommand
                        .literal("remove")
                        .permission("lore.remove")
                        .argument(IntegerArgument.of("line"))
                        .handler(this::handleRemove)
        );

        manager.command(
                baseCommand
                        .literal("edit")
                        .permission("lore.edit")
                        .argument(IntegerArgument.of("line"))
                        .argument(StringArgument.greedy("text"))
                        .handler(this::handleEdit)
        );

        manager.command(
                baseCommand
                        .literal("rename")
                        .permission("lore.rename")
                        .argument(StringArgument.greedy("text"))
                        .handler(this::handleRename)
        );

        manager.command(
                baseCommand
                        .literal("removename", "unname")
                        .permission("lore.unname")
                        .handler(this::handleRemoveName)
        );

        manager.command(
                baseCommand
                        .literal("glow")
                        .argument(BooleanArgument.optional("value"))
                        .permission("lore.glow")
                        .handler(this::handleGlow)
        );

        manager.command(
                baseCommand
                        .literal("hideeffects")
                        .argument(BooleanArgument.optional("value"))
                        .permission("lore.hideeffects")
                        .handler(this::handleEffects)
        );

        manager.command(
                baseCommand
                        .literal("tag")
                        .argument(StringArgument.optional("tag", StringArgument.StringMode.GREEDY))
                        .permission("lore.tag")
                        .handler(this::handleTag)
        );

        MinecraftHelp<CommandSender> minecraftHelp = MinecraftHelp.createNative(
                "/commands help",
                manager
        );

        manager.command(
                baseCommand
                        .literal("help")
                        .permission("message.command.help")
                        .handler((ctx) -> minecraftHelp.queryCommands("", ctx.getSender()))
        );


    }

    private void handleAdd(final CommandContext<CommandSender> context) {

        Player player = (Player) context.getSender();

        //Ensure the player is holding an item in their hand
        ItemStack item = player.getInventory().getItemInMainHand();
        ItemMeta meta = item.getItemMeta();

        String inputText = context.get("text");
        Component line = SERIALIZER.deserialize(inputText).decorationIfAbsent(ITALIC, FALSE);

        @Nullable List<Component> lore = meta.lore();
        if (lore == null) {
            lore = new ArrayList<>();
        }

        lore.add(line);

        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        pdc.set(BasicLore.loreKey, PersistentDataType.STRING, "LORE");

        meta.lore(lore);
        item.setItemMeta(meta);

        player.sendMessage(Messages.infoAddedLore(line));

    }

    private void handleRemove(final CommandContext<CommandSender> context) {

        Player player = (Player) context.getSender();

        int lineNumber = context.get("line");

        ItemStack item = player.getInventory().getItemInMainHand();
        ItemMeta meta = item.getItemMeta();

        @Nullable List<Component> lore = meta.lore();
        if (lore == null) {
            player.sendMessage(Messages.errNoLoreToRemove());
            return;
        }

        int numLines = lore.size();
        if (lineNumber < 0 || lineNumber > numLines) {
            player.sendMessage(Messages.errNoLoreAtLine(lineNumber));
            return;
        }

        Component oldLoreLine = lore.get(lineNumber - 1);
        lore.remove(lineNumber - 1);
        meta.lore(lore);
        item.setItemMeta(meta);

        player.sendMessage(Messages.infoRemovedLore(lineNumber, oldLoreLine));

    }

    private void handleEdit(final CommandContext<CommandSender> context) {

        Player player = (Player) context.getSender();

        ItemStack item = player.getInventory().getItemInMainHand();
        ItemMeta meta = item.getItemMeta();
        @Nullable List<Component> lore = meta.lore();
        if (lore == null) {
            player.sendMessage(Messages.errNoLoreToEdit());
            return;
        }

        int line = context.get("line");
        String newText = context.get("text");

        if (line < 0 || line > lore.size()) {
            player.sendMessage(Messages.errNoLoreAtLine(line));
            return;
        }

        Component newLore = SERIALIZER.deserialize(newText).decorationIfAbsent(ITALIC, FALSE);

        Component previousLore = lore.get(line - 1);
        lore.set(line - 1, newLore);

        meta.lore(lore);
        item.setItemMeta(meta);

        player.sendMessage(Messages.infoEditedLore(previousLore, newLore));
    }

    //renames an item and also tags it as a Lore item, so that it can be prevented from being placed.
    private void handleRename(final CommandContext<CommandSender> context) {

        Player player = (Player) context.getSender();

        ItemStack item = player.getInventory().getItemInMainHand();
        ItemMeta meta = item.getItemMeta();

        String name = context.get("text");
        Component displayName = SERIALIZER.deserialize(name).decorationIfAbsent(ITALIC, FALSE);

        meta.displayName(displayName);

        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        pdc.set(BasicLore.loreKey, PersistentDataType.STRING, "LORE");

        item.setItemMeta(meta);

        player.sendMessage(Messages.infoRenamedItem(displayName));

    }

    private void handleRemoveName(final CommandContext<CommandSender> context) {

        Player player = (Player) context.getSender();

        ItemStack item = player.getInventory().getItemInMainHand();
        ItemMeta meta = item.getItemMeta();

        @Nullable Component displayName = meta.displayName();
        if (displayName == null) {
            player.sendMessage(Messages.errNoNameToRemove());
            return;
        }

        meta.displayName(null);
        item.setItemMeta(meta);

        player.sendMessage(Messages.infoRemovedItemName(displayName));

    }

    //Adds Unbreaking 1 and hides enchants on an item, and tags it as a Lore item.
    private void handleGlow(final CommandContext<CommandSender> context) {

        Player player = (Player) context.getSender();
        ItemStack item = player.getInventory().getItemInMainHand();
        ItemMeta meta = item.getItemMeta();

        boolean doSetGlowing = context.getOrDefault("value", true);

        if (doSetGlowing) {

            PersistentDataContainer pdc = meta.getPersistentDataContainer();
            pdc.set(BasicLore.loreKey, PersistentDataType.STRING, "LORE");

            item.setItemMeta(meta);

            item.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
            item.addItemFlags(ItemFlag.HIDE_ENCHANTS);

            player.sendMessage(Messages.infoAddedGlowing());

        } else {

            item.removeEnchantment(Enchantment.DURABILITY);
            item.removeItemFlags(ItemFlag.HIDE_ENCHANTS);

            player.sendMessage(Messages.infoRemovedGlowing());

        }

    }

    private void handleEffects(final CommandContext<CommandSender> context) {

        Player player = (Player) context.getSender();
        ItemStack item = player.getInventory().getItemInMainHand();

        boolean doHideEffects = context.getOrDefault("value", true);

        if (doHideEffects) {
            item.addItemFlags(ItemFlag.HIDE_ITEM_SPECIFICS);
            player.sendMessage(Messages.infoHidEffects());

        } else {
            item.removeItemFlags(ItemFlag.HIDE_ITEM_SPECIFICS);
            player.sendMessage(Messages.infoUnHidEffects());

        }

    }

    private void handleTag(final CommandContext<CommandSender> context) {

        Player player = (Player) context.getSender();

        ItemStack item = player.getInventory().getItemInMainHand();
        ItemMeta meta = item.getItemMeta();
        @Nullable List<Component> lore = meta.lore();
        if (lore == null) {
            lore = new ArrayList<>();
        }

        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        pdc.set(BasicLore.loreKey, PersistentDataType.STRING, "LORE");

        lore.add(Messages.separator());

        String tagString = context.getOrDefault("tag", "&#720077&lVestria " + Year.now());
        Component tag = SERIALIZER.deserialize(tagString).decorationIfAbsent(ITALIC, FALSE);

        lore.add(tag);
        meta.lore(lore);
        item.setItemMeta(meta);

        player.sendMessage(Messages.infoAddedLore(Messages.separator()));
        player.sendMessage(Messages.infoAddedLore(tag));

    }

    public static final class HeldItemPostProcessor implements CommandPostprocessor<CommandSender> {

        @Override
        public void accept(final CommandPostprocessingContext<CommandSender> context) {
            final Command<CommandSender> command = context.getCommand();
            if (!command.getCommandMeta().getOrDefault(REQUIRES_HELD_ITEM, false)) {
                return;
            }

            Player player = (Player) context.getCommandContext().getSender();

            ItemStack item = player.getInventory().getItemInMainHand();

            if (item.isEmpty()) {

                player.sendMessage(Messages.errNoHeldItem());
                ConsumerService.interrupt();

            }

            if (item.getItemMeta() == null) {

                player.sendMessage(Messages.errNoItemMeta());
                ConsumerService.interrupt();

            }

        }
    }

}
