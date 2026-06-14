package com.SP3x3.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.StringUtil;

import com.SP3x3.utils.Utils;

public class PicoCommand implements CommandExecutor, TabCompleter {
   private final JavaPlugin plugin;

   public PicoCommand(JavaPlugin plugin) {
      this.plugin = plugin;
   }

   public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
      if (this.plugin.getConfig().getBoolean("command.op-only", true) && !sender.isOp()) {
         String noOpMsg = this.plugin.getConfig().getString("messages.no-op", "&cSolo OPs.");
         sender.sendMessage(Utils.colorize(noOpMsg != null ? noOpMsg : ""));
         return true;
      } else if (args.length < 1) {
         sender.sendMessage("§6§lPico3x3 §7| §eUso: /pico3x3 <jugador|reload> [encantamiento] [nivel]");
         return true;
      } else if (args[0].equalsIgnoreCase("reload")) {
         this.plugin.reloadConfig();
         String reloadMsg = this.plugin.getConfig().getString("messages.reload", "&aConfiguración recargada.");
         sender.sendMessage(Utils.colorize(reloadMsg != null ? reloadMsg : ""));
         return true;
      } else {
         Player target = Bukkit.getPlayer(args[0]);
         if (target == null) {
            sender.sendMessage("§cJugador no encontrado.");
            return true;
         } else {
            int radius = this.plugin.getConfig().getInt("pickaxe.radius", 1);
            int side = radius * 2 + 1;
            String areaText = side + "x" + side;
            String matStr = this.plugin.getConfig().getString("pickaxe.material", "NETHERITE_PICKAXE");
            Material mat = Material.matchMaterial(matStr != null ? matStr : "NETHERITE_PICKAXE");
            if (mat == null) {
               mat = Material.NETHERITE_PICKAXE;
            }

            ItemStack pico = new ItemStack(mat);
            ItemMeta meta = pico.getItemMeta();
            if (meta != null) {
               String configName = this.plugin.getConfig().getString("pickaxe.name", "&6&lSUPER PICO {area}");
               meta.setDisplayName(Utils.colorize((configName != null ? configName : "").replace("{area}", areaText)));
               List<String> rawLore = this.plugin.getConfig().getStringList("pickaxe.lore");
               List<String> coloredLore = rawLore.stream().map((line) -> Utils.colorize((line != null ? line : "").replace("{area}", areaText))).collect(Collectors.toList());
               meta.setLore(coloredLore);
               meta.getPersistentDataContainer().set(new NamespacedKey(this.plugin, "pico3x3"), PersistentDataType.BYTE, (byte)1);
               if (args.length >= 3) {
                  try {
                     NamespacedKey key = NamespacedKey.minecraft(args[1].toLowerCase());
                     Enchantment enchant = (Enchantment)Registry.ENCHANTMENT.get(key);
                     int level = Integer.parseInt(args[2]);
                     if (enchant != null) {
                        meta.addEnchant(enchant, level, true);
                     }
                  } catch (IndexOutOfBoundsException | NumberFormatException var19) {
                  }
               }

               pico.setItemMeta(meta);
            }

            target.getInventory().addItem(new ItemStack[]{pico});
            String giveMsg = this.plugin.getConfig().getString("messages.give", "&aHas recibido el pico {area}.");
            target.sendMessage(Utils.colorize((giveMsg != null ? giveMsg : "").replace("{area}", areaText)));
            return true;
         }
      }
   }

   public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
      if (args.length == 1) {
         List<String> sub = new ArrayList<>();
         sub.add("reload");
         Bukkit.getOnlinePlayers().forEach((p) -> sub.add(p.getName()));
         return StringUtil.copyPartialMatches(args[0], sub, new ArrayList<>());
      } else if (args.length == 2 && !args[0].equalsIgnoreCase("reload")) {
         List<String> enchants = new ArrayList<>();

         for(Enchantment e : Registry.ENCHANTMENT) {
            if (e instanceof Keyed) {
               Keyed k = (Keyed)e;
               enchants.add(k.getKey().getKey());
            }
         }

         return StringUtil.copyPartialMatches(args[1], enchants, new ArrayList<>());
      } else {
         return args.length == 3 && !args[0].equalsIgnoreCase("reload") ? StringUtil.copyPartialMatches(args[2], Arrays.asList("1", "2", "3", "4", "5", "10"), new ArrayList<>()) : new ArrayList<>();
      }
   }
}
