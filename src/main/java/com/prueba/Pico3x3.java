package com.prueba;

import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import com.prueba.commands.PicoCommand;
import com.prueba.listeners.PlayerListener;
import com.prueba.managers.PluginManager;

public class Pico3x3 extends JavaPlugin {
   public void onEnable() {
      this.saveDefaultConfig();
      PluginManager.getInstance().initialize();
      this.getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
      PluginCommand picoCommand = this.getCommand("pico3x3");
      if (picoCommand != null) {
         PicoCommand commandInstance = new PicoCommand(this);
         picoCommand.setExecutor(commandInstance);
         picoCommand.setTabCompleter(commandInstance);
      }

      this.getLogger().info("prueba has been enabled!");
   }

   public void onDisable() {
      this.getLogger().info("prueba has been disabled!");
   }
}
