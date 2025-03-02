package com.dnamaster10.traincartsticketshop.util;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import static com.dnamaster10.traincartsticketshop.TraincartsTicketShop.getPlugin;

public class VaultHook {
    private Economy economy;
    private Permission permission;

    public void loadEconomy() {
        getPlugin().getLogger().info("Checking for Economy availability...");
        if (getPlugin().getServer().getPluginManager().getPlugin("Vault") == null) {
            getPlugin().getLogger().info("Vault was not installed, so economy functionality has been disabled.");
        }
        RegisteredServiceProvider<Economy> rsp = getPlugin().getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            getPlugin().getLogger().info("No economy plugin was found, so economy functionality has been disabled.");
        }
        assert rsp != null;
        economy = rsp.getProvider();
        getPlugin().getLogger().info("Economy functionality has been enabled.");
    }

    public boolean hasEconomy() {
        return economy != null;
    }

    public boolean hasPermissions() {
        return permission != null;
    }

    public boolean hasAccount(Player player) {
        return economy.hasAccount(player);
    }

    public double getBalance(Player player) {
        return economy.getBalance(player);
    }

    public EconomyResponse withdrawMoney(Player player, double amount) {
        return economy.withdrawPlayer(player, amount);
    }

    public String format(double amount) {
        return economy.format(amount);
    }

    public EconomyResponse depositMoney(Player player, double amount) {
        return economy.depositPlayer(player, amount);
    }
}
