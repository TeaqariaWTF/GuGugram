package tw.nekomimi.nekogram.folder;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.R;

import java.util.LinkedHashMap;

import tw.nekomimi.nekogram.NekoConfig;


public class FolderIconHelper {
    public static LinkedHashMap<String, Integer> folderIcons = new LinkedHashMap<>();

    static {
        folderIcons.put("\uD83D\uDC31", R.drawable.filter_cat);
        folderIcons.put("\uD83D\uDCD5", R.drawable.filter_book);
        folderIcons.put("\uD83D\uDCB0", R.drawable.filter_money);
        //folderIcons.put("\uD83D\uDCF8", R.drawable.filter_camera);
        folderIcons.put("\uD83C\uDFAE", R.drawable.filter_game);
        //folderIcons.put("\uD83C\uDFE1", R.drawable.filter_house);
        folderIcons.put("\uD83D\uDCA1", R.drawable.filter_light);
        folderIcons.put("\uD83D\uDC4C", R.drawable.filter_like);
        //folderIcons.put("\u2795", R.drawable.filter_plus);
        folderIcons.put("\uD83C\uDFB5", R.drawable.filter_note);
        folderIcons.put("\uD83C\uDFA8", R.drawable.filter_palette);
        folderIcons.put("\u2708\uFE0F", R.drawable.filter_travel);
        folderIcons.put("\u26BD\uFE0F", R.drawable.filter_sport);
        folderIcons.put("\u2B50", R.drawable.filter_favorite);
        folderIcons.put("\uD83C\uDF93", R.drawable.filter_study);
        folderIcons.put("\uD83D\uDEEB", R.drawable.filter_airplane);
        //folderIcons.put("\uD83E\uDDA0", R.drawable.filter_microbe);
        //folderIcons.put("\uD83D\uDC68\u200D\uD83D\uDCBC", R.drawable.filter_worker);
        folderIcons.put("\uD83D\uDC64", R.drawable.filter_private);
        folderIcons.put("\uD83D\uDC65", R.drawable.filter_group);
        folderIcons.put("\uD83D\uDCAC", R.drawable.filter_all);
        folderIcons.put("\u2705", R.drawable.filter_unread);
        //folderIcons.put("\u2611\uFE0F", R.drawable.filter_check);
        folderIcons.put("\uD83E\uDD16", R.drawable.filter_bots);
        //folderIcons.put("\uD83D\uDDC2", R.drawable.filter_folders);
        folderIcons.put("\uD83D\uDC51", R.drawable.filter_crown);
        folderIcons.put("\uD83C\uDF39", R.drawable.filter_flower);
        folderIcons.put("\uD83C\uDFE0", R.drawable.filter_home);
        folderIcons.put("\u2764", R.drawable.filter_love);
        folderIcons.put("\uD83C\uDFAD", R.drawable.filter_mask);
        folderIcons.put("\uD83C\uDF78", R.drawable.filter_party);
        folderIcons.put("\uD83D\uDCC8", R.drawable.filter_trade);
        folderIcons.put("\uD83D\uDCBC", R.drawable.filter_work);
        folderIcons.put("\uD83D\uDD14", R.drawable.filter_unmuted);
        folderIcons.put("\uD83D\uDCE2", R.drawable.filter_channels);
        folderIcons.put("\uD83D\uDCC1", R.drawable.filter_custom);
        folderIcons.put("\uD83D\uDCCB", R.drawable.filter_setup);
        //folderIcons.put("\uD83D\uDCA9", R.drawable.filter_poo);
    }

    public static int getIconWidth() {
        return AndroidUtilities.dp(28);
    }

    public static int getPadding() {
        if (NekoConfig.tabsTitleType == NekoConfig.TITLE_TYPE_MIX) {
            return AndroidUtilities.dp(6);
        }
        return 0;
    }

    public static int getTotalIconWidth() {
        int result = 0;
        if (NekoConfig.tabsTitleType != NekoConfig.TITLE_TYPE_TEXT) {
            result = getIconWidth() + getPadding();
        }
        return result;
    }

    public static int getPaddingTab() {
        if (NekoConfig.tabsTitleType != NekoConfig.TITLE_TYPE_ICON) {
            return AndroidUtilities.dp(32);
        }
        return AndroidUtilities.dp(16);
    }

    public static int getTabIcon(String emoji) {
        if (emoji != null) {
            var folderIcon = folderIcons.get(emoji);
            if (folderIcon != null) {
                return folderIcon;
            }
        }
        return R.drawable.filter_custom;
    }
}
