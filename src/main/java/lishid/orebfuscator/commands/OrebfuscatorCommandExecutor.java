package lishid.orebfuscator.commands;

import lishid.orebfuscator.utils.OrebfuscatorCalculationThread;
import lishid.orebfuscator.utils.OrebfuscatorConfig;
import org.bukkit.ChatColor;
import lishid.orebfuscator.utils.PermissionRelay;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.CommandExecutor;

public class OrebfuscatorCommandExecutor implements CommandExecutor {
    
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player && !PermissionRelay.hasPermission((Player) sender, "Orebfuscator.admin")) {
            sender.sendMessage(ChatColor.RED + "You do not have permissions.");
            return true;
        }

        if (args.length == 0) return false;

        if (args[0].equalsIgnoreCase("engine") && args.length > 1) {
            int engine;
            try {
                engine = new Integer(args[1]);
            } catch (NumberFormatException e) {
                sender.sendMessage(args[1] + " is not a number!");
                return true;
            }

            if (engine != 1 && engine != 2) {
                sender.sendMessage(args[1] + " is not a valid EngineMode!");
                return true;
            }

            OrebfuscatorConfig.SetEngineMode(engine);
            sender.sendMessage("[Orebfuscator] Engine set to: " + engine);
        } else {
            if (args[0].equalsIgnoreCase("updateradius") && args.length > 1) {
                int radius;
                try {
                    radius = new Integer(args[1]);
                } catch (NumberFormatException e) {
                    sender.sendMessage(args[1] + " is not a number!");
                    return true;
                }

                OrebfuscatorConfig.SetUpdateRadius(radius);
                sender.sendMessage("[Orebfuscator] UpdateRadius set to: " + radius);
            } else if (args[0].equalsIgnoreCase("initialradius") && args.length > 1) {
                int radius;
                try {
                    radius = new Integer(args[1]);
                } catch (NumberFormatException e) {
                    sender.sendMessage(args[1] + " is not a number!");
                    return true;
                }
                OrebfuscatorConfig.SetInitialRadius(radius);
                sender.sendMessage("[Orebfuscator] InitialRadius set to: " + radius);
            } else if (args[0].equalsIgnoreCase("threads") && args.length > 1) {
                int threads;
                try {
                    threads = new Integer(args[1]);
                } catch (NumberFormatException e) {
                    sender.sendMessage(args[1] + " is not a number!");
                    return true;
                }
                OrebfuscatorConfig.SetProcessingThreads(threads);
                if (OrebfuscatorCalculationThread.CheckThreads()) {
                    OrebfuscatorCalculationThread.SyncThreads();
                }
                sender.sendMessage("[Orebfuscator] Processing Threads set to: " + threads);
            } else if (args[0].equalsIgnoreCase("enable") || args[0].equalsIgnoreCase("disable")) {
                final boolean data = args[0].equalsIgnoreCase("enable");
                if (args[0].equalsIgnoreCase("enable") && args.length == 1) {
                    OrebfuscatorConfig.SetEnabled(true);
                    sender.sendMessage("[Orebfuscator] Enabled.");
                } else if (args[0].equalsIgnoreCase("disable") && args.length == 1) {
                    OrebfuscatorConfig.SetEnabled(false);
                    sender.sendMessage("[Orebfuscator] Disabled.");
                }
                if (args.length > 1) {
                    if (args[1].equalsIgnoreCase("updatebreak")) {
                        OrebfuscatorConfig.SetUpdateOnBreak(data);
                        sender.sendMessage("[Orebfuscator] OnBlockBreak update " + (data ? "enabled" : "disabled") + ".");
                    } else if (args[1].equalsIgnoreCase("updatedamage")) {
                        OrebfuscatorConfig.SetUpdateOnDamage(data);
                        sender.sendMessage("[Orebfuscator] OnBlockDamage update " + (data ? "enabled" : "disabled") + ".");
                    } else if (args[1].equalsIgnoreCase("updatephysics")) {
                        OrebfuscatorConfig.SetUpdateOnPhysics(data);
                        sender.sendMessage("[Orebfuscator] OnBlockPhysics update " + (data ? "enabled" : "disabled") + ".");
                    } else if (args[1].equalsIgnoreCase("updateexplosion")) {
                        OrebfuscatorConfig.SetUpdateOnExplosion(data);
                        sender.sendMessage("[Orebfuscator] Creeper explosion update " + (data ? "enabled" : "disabled") + ".");
                    } else if (args[1].equalsIgnoreCase("darknesshide")) {
                        OrebfuscatorConfig.SetDarknessHideBlocks(data);
                        sender.sendMessage("[Orebfuscator] Darkness obfuscation " + (data ? "enabled" : "disabled") + ".");
                    } else if (args[1].equalsIgnoreCase("op")) {
                        OrebfuscatorConfig.SetNoObfuscationForOps(data);
                        sender.sendMessage("[Orebfuscator] OP's obfuscator " + (data ? "enabled" : "disabled") + ".");
                    } else if (args[1].equalsIgnoreCase("perms")) {
                        OrebfuscatorConfig.SetNoObfuscationForPermission(data);
                        sender.sendMessage("[Orebfuscator] Permissions obfuscator " + (data ? "enabled" : "disabled") + ".");
                    } else if (args[1].equalsIgnoreCase("world") && args.length > 2) {
                        OrebfuscatorConfig.SetDisabledWorlds(args[2], !data);
                        sender.sendMessage("[Orebfuscator] World \"" + args[2] + "\" obfuscation " + (data ? "enabled" : "disabled") + ".");
                    }
                }
            } else if (args[0].equalsIgnoreCase("reload")) {
                OrebfuscatorConfig.Reload();
                sender.sendMessage("[Orebfuscator] Reload complete.");
            } else if (args[0].equalsIgnoreCase("status")) {
                sender.sendMessage("[Orebfuscator] Orebfuscator status:");
                sender.sendMessage("[Orebfuscator] Plugin is: " + (OrebfuscatorConfig.Enabled() ? "Enabled" : "Disabled"));
                sender.sendMessage("[Orebfuscator] EngineMode: " + OrebfuscatorConfig.EngineMode());
                sender.sendMessage("[Orebfuscator] Executing Threads: " + OrebfuscatorCalculationThread.getThreads());
                sender.sendMessage("[Orebfuscator] Processing Threads Max: " + OrebfuscatorConfig.ProcessingThreads());
                sender.sendMessage("[Orebfuscator] Disabled worlds: " + OrebfuscatorConfig.disabledWorlds());
            }
        }

        return true;
    }

}
