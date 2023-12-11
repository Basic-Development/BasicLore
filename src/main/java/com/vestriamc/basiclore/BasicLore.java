package com.vestriamc.basiclore;

import cloud.commandframework.CommandManager;
import cloud.commandframework.bukkit.CloudBukkitCapabilities;
import cloud.commandframework.execution.AsynchronousCommandExecutionCoordinator;
import cloud.commandframework.paper.PaperCommandManager;
import com.vestriamc.basiclore.commands.LoreCommand;
import com.vestriamc.basiclore.listeners.LoreItemPlaceListener;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public final class BasicLore extends JavaPlugin {

    public static MiniMessage miniMessage;
    public static Logger logger;

    public static NamespacedKey loreKey;

    @Override
    public void onEnable() {
        miniMessage = MiniMessage.miniMessage();
        logger = this.getLogger();

        loreKey = new NamespacedKey(this, "lore_key");

        //register commands
        CommandManager<CommandSender> commandManager = this.createCommandManager();
        commandManager.registerCommandPostProcessor(new LoreCommand.HeldItemPostProcessor());

        //todo: implement suggestions for edit and rename command
        // to populate deserialized pre-existing text for tab completion.

        new LoreCommand().register(commandManager);

        //register listeners
        PluginManager pm = this.getServer().getPluginManager();
        pm.registerEvents(new LoreItemPlaceListener(), this);

    }

    private CommandManager<CommandSender> createCommandManager() {
        PaperCommandManager<CommandSender> commandManager;

        try {
            commandManager = PaperCommandManager.createNative(
                    this, AsynchronousCommandExecutionCoordinator
                            .<CommandSender>builder()
                            .withAsynchronousParsing().build()
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        if (commandManager.hasCapability(CloudBukkitCapabilities.ASYNCHRONOUS_COMPLETION)) {
            commandManager.registerAsynchronousCompletions();
        }

        return commandManager;

    }
}
