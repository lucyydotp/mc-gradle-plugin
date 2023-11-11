package me.lucyydotp.mcgradle.papertest;

import com.earth2me.essentials.Essentials;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class PaperTestPlugin extends JavaPlugin {
    @Override
    public void onEnable() {
        final var ess = ((Essentials) Bukkit.getPluginManager().getPlugin("Essentials"));
        getLogger().info("Hooking into essentials! Nickname prefix is " + ess.getSettings().getNicknamePrefix());
    }
}
