package com.vestriamc.basiclore;

import com.vestriamc.basiclore.commands.LoreCommand;
import com.vestriamc.basiclore.commandTabCompletion.LoreCommandCompletion;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public final class BasicLore extends JavaPlugin {

    public static MiniMessage miniMessage;
    public static Logger logger;

    @Override
    public void onEnable() {
        miniMessage = MiniMessage.miniMessage();
        logger = this.getLogger();

        PluginCommand command = getCommand("lore");
        if (command != null) {
            command.setExecutor(new LoreCommand());
            command.setTabCompleter(new LoreCommandCompletion());
        }

    }

}
