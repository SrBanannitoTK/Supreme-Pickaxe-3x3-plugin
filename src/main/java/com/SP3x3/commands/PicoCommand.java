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

   @Override
   public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
      if (this.plugin.getConfig().getBoolean("command.op-only", true) && !sender.isOp()) {
         String noOpMsg = this.plugin.getConfig().getString("messages.no-op", "&cSolo OPs.");
         sender.sendMessage(Utils.colorize(noOpMsg != null ? noOpMsg : ""));
         return true;
      }

      if (args.length < 1) {
         sender.sendMessage("§6§lPico3x3 §7| §eUso: /pico3x3 <jugador|reload> [material] [radio] [profundidad: true|false] [encantamiento] [nivel]");
         return true;
      }

      if (args[0].equalsIgnoreCase("reload")) {
         this.plugin.reloadConfig();
         String reloadMsg = this.plugin.getConfig().getString("messages.reload", "&aConfiguración recargada.");
         sender.sendMessage(Utils.colorize(reloadMsg != null ? reloadMsg : ""));
         return true;
      }

      Player target = Bukkit.getPlayer(args[0]);
      if (target == null) {
         sender.sendMessage("§cJugador no encontrado.");
         return true;
      }

      Material mat;
      if (args.length >= 2) {
         Material customMat = Material.matchMaterial(args[1].toUpperCase());
         if (customMat != null && customMat.toString().contains("_PICKAXE")) {
            mat = customMat;
         } else {
            sender.sendMessage("§cMaterial inválido o no es un pico. Usando el de la configuración.");
            mat = getDefaultMaterial();
         }
      } else {
         mat = getDefaultMaterial();
      }

      int radius = this.plugin.getConfig().getInt("pickaxe.radius", 1);
      if (args.length >= 3) {
         try {
            radius = Integer.parseInt(args[2]);
            if (radius < 0) radius = 0;
         } catch (NumberFormatException e) {
            sender.sendMessage("§cRadio inválido. Usando el de la configuración.");
         }
      }

      boolean breakDepth = this.plugin.getConfig().getBoolean("pickaxe.break-depth", true);
      if (args.length >= 4) {
         breakDepth = Boolean.parseBoolean(args[3]);
      }

      int side = radius * 2 + 1;
      String areaText = breakDepth ? side + "x" + side + "x" + side : side + "x" + side;

      ItemStack pico = new ItemStack(mat);
      ItemMeta meta = pico.getItemMeta();
      
      if (meta != null) {

         String configName = this.plugin.getConfig().getString("pickaxe.name", "&6&lSUPER PICO {area}");
         meta.setDisplayName(Utils.colorize((configName != null ? configName : "").replace("{area}", areaText)));
         
         List<String> rawLore = this.plugin.getConfig().getStringList("pickaxe.lore");
         List<String> coloredLore = rawLore.stream()
               .map((line) -> Utils.colorize((line != null ? line : "").replace("{area}", areaText)))
               .collect(Collectors.toList());
         meta.setLore(coloredLore);

         meta.getPersistentDataContainer().set(new NamespacedKey(this.plugin, "pico3x3"), PersistentDataType.BYTE, (byte) 1);
         meta.getPersistentDataContainer().set(new NamespacedKey(this.plugin, "pico_radius"), PersistentDataType.INTEGER, radius);
         meta.getPersistentDataContainer().set(new NamespacedKey(this.plugin, "pico_depth"), PersistentDataType.BYTE, (byte) (breakDepth ? 1 : 0));

         if (args.length >= 6) {
            try {
               NamespacedKey key = NamespacedKey.minecraft(args[4].toLowerCase());
               Enchantment enchant = (Enchantment) Registry.ENCHANTMENT.get(key);
               int level = Integer.parseInt(args[5]);
               if (enchant != null) {
                  meta.addEnchant(enchant, level, true);
               }
            } catch (Exception ignored) {}
         }

         pico.setItemMeta(meta);
      }

      target.getInventory().addItem(pico);
      
      String giveMsg = this.plugin.getConfig().getString("messages.give", "&a¡Has recibido el Pico Legendario {area}!");
      target.sendMessage(Utils.colorize((giveMsg != null ? giveMsg : "").replace("{area}", areaText)));
      return true;
   }

   private Material getDefaultMaterial() {
      String matStr = this.plugin.getConfig().getString("pickaxe.material", "NETHERITE_PICKAXE");
      Material mat = Material.matchMaterial(matStr != null ? matStr : "NETHERITE_PICKAXE");
      return mat != null ? mat : Material.NETHERITE_PICKAXE;
   }

   @Override
   public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
      if (args.length == 1) {
         List<String> sub = new ArrayList<>();
         sub.add("reload");
         Bukkit.getOnlinePlayers().forEach((p) -> sub.add(p.getName()));
         return StringUtil.copyPartialMatches(args[0], sub, new ArrayList<>());
      } 
      
      if (args[0].equalsIgnoreCase("reload")) {
         return new ArrayList<>();
      }

      switch (args.length) {
         case 2:
            List<String> pickaxes = Arrays.stream(Material.values())
                  .filter(m -> m != null && m.toString().contains("_PICKAXE"))
                  .map(m -> m.toString().toLowerCase())
                  .collect(Collectors.toList());
            return StringUtil.copyPartialMatches(args[1], pickaxes, new ArrayList<>());


         case 3:
            return StringUtil.copyPartialMatches(args[2], Arrays.asList("1", "2", "3", "4"), new ArrayList<>());

         case 4:
            return StringUtil.copyPartialMatches(args[3], Arrays.asList("true", "false"), new ArrayList<>());

         case 5:
            List<String> enchants = new ArrayList<>();
            for (Enchantment e : Registry.ENCHANTMENT) {
               if (e instanceof Keyed) {
                  enchants.add(((Keyed) e).getKey().getKey());
               }
            }
            return StringUtil.copyPartialMatches(args[4], enchants, new ArrayList<>());

         case 6:
            return StringUtil.copyPartialMatches(args[5], Arrays.asList("1", "2", "3", "4", "5", "10"), new ArrayList<>());

         default:
            return new ArrayList<>();
      }
   }
}
