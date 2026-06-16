# ⛏️ Supreme Pickaxe 3x3

[![Modrinth](https://img.shields.io/badge/Modrinth-Download-emerald?style=flat-for-the-badge&logo=modrinth)](https://modrinth.com/plugin/pico-supremo-3x3)
[!["Discord"](https://img.shields.io/discord/1221191065171136512?style=flat-for-the-badge&color=5865F2&logo=discord)](https://discord.gg/mzSe767Hb)
[!["PayPal"](https://img.shields.io/badge/Donate-PayPal-00457C?style=flat-for-the-badge&logo=paypal)](https://www.paypal.me/SrBanannito)

The ultimate, highly optimized, and lightweight 3×3 mining tool solution tailored for **Paper** and **Spigot** servers. Perfect for Survival, SkyBlock, or Prison game modes to implement custom milestones or VIP perks.

---

## 🛡️ Advanced WorldGuard Integration

The plugin features a smart, native integration with **WorldGuard** to prevent griefing and exploits in protected areas:
* **Total Protection:** If a player attempts to mine entirely inside a restricted region, the action is instantly canceled.
* **Smart Fragmented Mining:** Ideal for custom public mines (`/warp mina`)! If the 3x3 mining radius overlaps two different regions, the plugin dynamically evaluates the flags. It will **only destroy blocks** inside the region where `block-break` is explicitly allowed, leaving the protected blocks completely untouched.

---

## ✨ Key Features

* 📦 **Custom Radius Support:** Easily clear blocks in a customizable area (3x3 by default).
* ⚡ **High Performance:** Asynchronous block handling designed to handle fast, mass block breaking without dropping server TPS.
* 🔮 **Enchantment Support:** Works out of the box with vanilla enchantments (Fortune, Silk Touch, Efficiency) even when given via commands.
* ⚙️ **100% Configurable:** Full control over item materials, custom names, lore, sounds, particles, and blacklisted blocks via `config.yml`.

---

## 🔧 Commands & Permissions

By default, permissions are kept simple and are tailored for server administrators (**OP**). Custom permission nodes can be enabled optionally in the configuration.

| Command | Description | Permission |
| :--- | :--- | :--- |
| `/pico3x3 [player] [enchant] [level]` | Gives a custom 3x3 pickaxe with optional enchants to a player. | `OP` |
| `/pico3x3 reload` | Reloads the plugin configuration files dynamically. | `OP` |

---

## 🛠️ Development & Requirements

### Dependencies
* **Java 17** or higher.
* **WorldGuard API** (Required for region protection checks).
* **WorldEdit API** (Mandatory dependency).

### Build System
This project uses **Maven** for dependency management and build automation. To compile the project yourself, clone the repository and run:

```bash
mvn clean package
