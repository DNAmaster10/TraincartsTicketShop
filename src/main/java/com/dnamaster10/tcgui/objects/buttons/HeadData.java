package com.dnamaster10.tcgui.objects.buttons;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;
import java.util.UUID;

import static com.dnamaster10.tcgui.TraincartsGui.getPlugin;
import static com.dnamaster10.tcgui.objects.buttons.HeadData.HeadType.*;

public class HeadData {
    public enum HeadType {
        GREEN_CHECK,
        RED_CROSS,
        GRAY_BACK_ARROW,
        GREEN_PLUS,
        CHAT_ARROW_RIGHT,
        CHAT_ARROW_LEFT
    }
    private static final String urlPrefix = "https://textures.minecraft.net/texture/";
    private static String getUrlFromType(HeadType type) {
        switch (type) {
            case GREEN_CHECK -> {
                return urlPrefix + "4312ca4632def5ffaf2eb0d9d7cc7b55a50c4e3920d90372aab140781f5dfbc4";
            }
            case RED_CROSS -> {
                return urlPrefix + "beb588b21a6f98ad1ff4e085c552dcb050efc9cab427f46048f18fc803475f7";
            }
            case GRAY_BACK_ARROW -> {
                return urlPrefix + "1b701c1f05e319d6b28f61b28b66a7e2a846a510de322bdc96e94a2388b78469";
            }
            case GREEN_PLUS -> {
                return urlPrefix + "5ff31431d64587ff6ef98c0675810681f8c13bf96f51d9cb07ed7852b2ffd1";
            }
            case CHAT_ARROW_RIGHT -> {
                return urlPrefix + "8399e5da82ef7765fd5e472f3147ed118d981887730ea7bb80d7a1bed98d5ba";
            }
            case CHAT_ARROW_LEFT -> {
                return urlPrefix + "76ebaa41d1d405eb6b60845bb9ac724af70e85eac8a96a5544b9e23ad6c96c62";
            }
        }
        return null;
    }
    private static HeadType getTypeFromUrl(String url) {
        //TODO SPLIT THE URL THING FROM PREFIX
        int lastIndex = url.lastIndexOf('/');
        String code = url.substring(lastIndex + 1);
        switch (code) {
            case "4312ca4632def5ffaf2eb0d9d7cc7b55a50c4e3920d90372aab140781f5dfbc4" -> {
                return GREEN_CHECK;
            }
            case "beb588b21a6f98ad1ff4e085c552dcb050efc9cab427f46048f18fc803475f7" -> {
                return RED_CROSS;
            }
            case "1b701c1f05e319d6b28f61b28b66a7e2a846a510de322bdc96e94a2388b78469" -> {
                return GRAY_BACK_ARROW;
            }
            case "5ff31431d64587ff6ef98c0675810681f8c13bf96f51d9cb07ed7852b2ffd1" -> {
                return GREEN_PLUS;
            }
            case "8399e5da82ef7765fd5e472f3147ed118d981887730ea7bb80d7a1bed98d5ba" -> {
                return CHAT_ARROW_RIGHT;
            }
            case "76ebaa41d1d405eb6b60845bb9ac724af70e85eac8a96a5544b9e23ad6c96c62" -> {
                return CHAT_ARROW_LEFT;
            }
        }
        return null;
    }
    public static HeadType getHeadTypeFromItem(ItemStack item) {
        if (!(item.getItemMeta() instanceof SkullMeta meta)) {
            return null;
        }
        PlayerProfile profile = meta.getOwnerProfile();
        if (profile == null) {
            return null;
        }
        if (profile.getTextures().getSkin() == null) {
            return null;
        }
        return getTypeFromUrl(profile.getTextures().getSkin().toString());
    }
    private static final UUID RANDOM_UUID = UUID.fromString("68f92a5b-8980-4e0c-a479-89e41ce1ada6");

    private static PlayerProfile getProfile(HeadType type) {
        PlayerProfile profile = Bukkit.createPlayerProfile(RANDOM_UUID);
        PlayerTextures textures = profile.getTextures();
        URL urlObject;
        try {
            String url = getUrlFromType(type);
            urlObject = new URL(Objects.requireNonNull(url));
        } catch (MalformedURLException e) {
            getPlugin().getLogger().warning("An error occurred creating a player head within a GUI: " + e);
            return null;
        }
        textures.setSkin(urlObject);
        profile.setTextures(textures);
        return profile;
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
