package com.dnamaster10.tcgui.objects.buttons;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

import static com.dnamaster10.tcgui.TraincartsGui.getPlugin;

public class HeadData {
    public enum HeadType {
        GREEN_CHECK
    }
    private static String getUrlFromType(HeadType type) {
        String urlPrefix = "https://textures.minecraft.net/texture/"
        switch (type) {
            case GREEN_CHECK -> {
                return urlPrefix + "4312ca4632def5ffaf2eb0d9d7cc7b55a50c4e3920d90372aab140781f5dfbc4";
            }
        }
    }
    private static final UUID RANDOM_UUID = UUID.fromString("68f92a5b-8980-4e0c-a479-89e41ce1ada6");

    private static PlayerProfile getProfile(HeadType type) {
        PlayerProfile profile = Bukkit.createPlayerProfile(RANDOM_UUID);
        PlayerTextures textures = profile.getTextures();
        URL urlObject;
        try {
            urlObject = new URL(getUrlFromType(type));
        } catch (MalformedURLException e) {
            getPlugin().getLogger().warning("An error occurred creating a player head within a GUI: " + e);
            return null;
        }
        textures.setSkin(urlObject);
        profile.setTextures(textures);
    }
    public static ItemStack getPlayerHeadItem(HeadType type) {
        PlayerProfile headProfile = getProfile(type);
        if (headProfile == null) {
            //If head creation failed, just return a potato
            return new ItemStack(Material.POTATO, 1);
        }

        ItemStack head = new ItemStack(Material.PLAYER_HEAD, 1);
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        assert meta != null;

        meta.setOwnerProfile(headProfile);
        head.setItemMeta(meta);
        return head;
    }
}
