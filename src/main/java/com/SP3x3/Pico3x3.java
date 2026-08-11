package com.SP3x3;

import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import com.SP3x3.commands.PicoCommand;
import com.SP3x3.listeners.PlayerListener;
import com.SP3x3.managers.PluginManager;

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

      this.getLogger().info("Supreme Pickaxe 3x3 a sido avilitado con exito!!");
   }

   public void onDisable() {
      this.getLogger().info("Supreme Pickaxe 3x3 a sido desactivado!");
   }
}