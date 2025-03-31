package com.vestriamc.basiclore;

import com.vestriamc.basiclore.commands.LoreCommand;
import com.vestriamc.basiclore.listeners.BlockPlaceEventListener;
import com.vestriamc.basiclore.listeners.EntityShootBowEventListener;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.paper.PaperCommandManager;
import org.incendo.cloud.paper.util.sender.PaperSimpleSenderMapper;
import org.incendo.cloud.paper.util.sender.Source;

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
        CommandManager<Source> manager = PaperCommandManager.builder(PaperSimpleSenderMapper.simpleSenderMapper())
                .executionCoordinator(ExecutionCoordinator.asyncCoordinator())
                .buildOnEnable(this);

        //todo: implement suggestions for edit and rename command
        // to populate deserialized pre-existing text for tab completion.

        new LoreCommand().register(manager);

        //register listeners
        PluginManager pm = this.getServer().getPluginManager();
        pm.registerEvents(new BlockPlaceEventListener(), this);
        pm.registerEvents(new EntityShootBowEventListener(), this);

    }

}
