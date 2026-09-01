package com.vestriamc.basiclore.commands;

import com.vestriamc.basiclore.BasicLore;
import com.vestriamc.basiclore.utils.Messages;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;
import org.incendo.cloud.Command;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.minecraft.extras.MinecraftHelp;
import org.incendo.cloud.paper.util.sender.PlayerSource;
import org.incendo.cloud.paper.util.sender.Source;
import org.incendo.cloud.parser.standard.BooleanParser;
import org.incendo.cloud.parser.standard.IntegerParser;
import org.incendo.cloud.parser.standard.StringParser;
import org.jetbrains.annotations.Nullable;

import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static net.kyori.adventure.text.format.TextDecoration.ITALIC;
import static net.kyori.adventure.text.format.TextDecoration.State.FALSE;

@DefaultQualifier(NonNull.class)
public final class LoreCommand {

    private static final LegacyComponentSerializer SERIALIZER = LegacyComponentSerializer
            .builder()
            .character('&')
            .hexColors()
            .hexCharacter('#')
            .build();

    public void register(final CommandManager<Source> manager) {

        Command.Builder<Source> baseCommand = manager
                .commandBuilder("lore");

        manager.command(
                baseCommand
                        .senderType(PlayerSource.class)
                        .literal("add")
                        .permission("lore.add")
                        .required("text", StringParser.greedyStringParser())
                        .handler(this::handleAdd)
        );

        manager.command(
                baseCommand
                        .senderType(PlayerSource.class)
                        .literal("remove")
                        .permission("lore.remove")
                        .required("line", IntegerParser.integerParser())
                        .handler(this::handleRemove)
        );

        manager.command(
                baseCommand
                        .senderType(PlayerSource.class)
                        .literal("edit")
                        .permission("lore.edit")
                        .required("line", IntegerParser.integerParser())
                        .required("text", StringParser.greedyStringParser())
                        .handler(this::handleEdit)
        );

        manager.command(
                baseCommand
                        .senderType(PlayerSource.class)
                        .literal("rename")
                        .permission("lore.rename")
                        .required("text", StringParser.greedyStringParser())
                        .handler(this::handleRename)
        );

        manager.command(
                baseCommand
                        .senderType(PlayerSource.class)
                        .literal("removename", "unname")
                        .permission("lore.unname")
                        .handler(this::handleRemoveName)
        );

        manager.command(
                baseCommand
                        .senderType(PlayerSource.class)
                        .literal("glow")
                        .optional("value", BooleanParser.booleanParser())
                        .permission("lore.glow")
                        .handler(this::handleGlow)
        );

        manager.command(
                baseCommand
                        .senderType(PlayerSource.class)
                        .literal("hideeffects")
                        .optional("value", BooleanParser.booleanParser())
                        .permission("lore.hideeffects")
                        .handler(this::handleEffects)
        );

        manager.command(
                baseCommand
                        .senderType(PlayerSource.class)
                        .literal("tag")
                        .optional("tag", StringParser.greedyStringParser())
                        .permission("lore.tag")
                        .handler(this::handleTag)
        );

        MinecraftHelp<Source> minecraftHelp = MinecraftHelp.create(
                "/lore help",
                manager,
                Source::source
        );

        manager.command(
                baseCommand
                        .literal("help")
                        .permission("message.command.help")
                        .handler((ctx) -> minecraftHelp.queryCommands("", ctx.sender()))
        );


    }

    private void handleAdd(final CommandContext<PlayerSource> context) {

        Player player = context.sender().source();

        //Ensure the player is holding an item in their hand
        ItemStack item = player.getInventory().getItemInMainHand();
        ItemMeta meta = item.getItemMeta();

        String inputText = context.get("text");
        Component line = SERIALIZER.deserialize(inputText).decorationIfAbsent(ITALIC, FALSE);

        List<Component> lore = Objects.requireNonNullElse(meta.lore(), new ArrayList<>());

        lore.add(line);

        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        pdc.set(BasicLore.loreKey, PersistentDataType.STRING, "LORE");

        meta.lore(lore);
        item.setItemMeta(meta);

        player.sendMessage(Messages.infoAddedLore(line));

    }

    private void handleRemove(final CommandContext<PlayerSource> context) {

        Player player = context.sender().source();


        int lineNumber = context.get("line");

        ItemStack item = player.getInventory().getItemInMainHand();
        ItemMeta meta = item.getItemMeta();

        List<Component> lore = Objects.requireNonNullElse(meta.lore(), new ArrayList<>());

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

    private void handleEdit(final CommandContext<PlayerSource> context) {

        Player player = context.sender().source();

        ItemStack item = player.getInventory().getItemInMainHand();
        ItemMeta meta = item.getItemMeta();
        List<Component> lore = Objects.requireNonNullElse(meta.lore(), new ArrayList<>());


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
    private void handleRename(final CommandContext<PlayerSource> context) {

        Player player = context.sender().source();

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

    private void handleRemoveName(final CommandContext<PlayerSource> context) {

        Player player = context.sender().source();

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
    private void handleGlow(final CommandContext<PlayerSource> context) {

        Player player = context.sender().source();
        ItemStack item = player.getInventory().getItemInMainHand();
        ItemMeta meta = item.getItemMeta();

        boolean doSetGlowing = context.getOrDefault("value", true);

        if (doSetGlowing) {

            PersistentDataContainer pdc = meta.getPersistentDataContainer();
            pdc.set(BasicLore.loreKey, PersistentDataType.STRING, "LORE");

            meta.setEnchantmentGlintOverride(true);

            item.setItemMeta(meta);

            player.sendMessage(Messages.infoAddedGlowing());

        } else {


            meta.setEnchantmentGlintOverride(false);
            item.setItemMeta(meta);

            player.sendMessage(Messages.infoRemovedGlowing());

        }

    }

    private void handleEffects(final CommandContext<PlayerSource> context) {

        Player player = context.sender().source();
        ItemStack item = player.getInventory().getItemInMainHand();

        boolean doHideEffects = context.getOrDefault("value", true);

        if (doHideEffects) {
            item.addItemFlags(ItemFlag.HIDE_STORED_ENCHANTS);
            item.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            item.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            player.sendMessage(Messages.infoHidEffects());

        } else {
            item.addItemFlags(ItemFlag.HIDE_STORED_ENCHANTS);
            item.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            item.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            player.sendMessage(Messages.infoUnHidEffects());

        }

    }

    private void handleTag(final CommandContext<PlayerSource> context) {

        Player player = context.sender().source();

        ItemStack item = player.getInventory().getItemInMainHand();
        ItemMeta meta = item.getItemMeta();
        List<Component> lore = Objects.requireNonNullElse(meta.lore(), new ArrayList<>());

        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        pdc.set(BasicLore.loreKey, PersistentDataType.STRING, "LORE");

        lore.add(Messages.separator());

        String tagString = context.getOrDefault("tag", "&#efb8ff&lVestria " + Year.now());
        Component tag = SERIALIZER.deserialize(tagString).decorationIfAbsent(ITALIC, FALSE);

        lore.add(tag);
        meta.lore(lore);
        item.setItemMeta(meta);

        player.sendMessage(Messages.infoAddedLore(Messages.separator()));
        player.sendMessage(Messages.infoAddedLore(tag));

    }

}
