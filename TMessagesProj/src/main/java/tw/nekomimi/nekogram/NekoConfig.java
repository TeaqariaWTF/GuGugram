package tw.nekomimi.nekogram;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;

import org.tcp2ws.tcp2wsServer;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildConfig;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.NotificationsService;
import org.telegram.messenger.R;
import org.telegram.messenger.SharedConfig;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import tw.nekomimi.nekogram.helpers.remote.AnalyticsHelper;
import tw.nekomimi.nekogram.helpers.remote.ConfigHelper;
import tw.nekomimi.nekogram.translator.DeepLTranslator;
import tw.nekomimi.nekogram.translator.Translator;

@SuppressLint("ApplySharedPref")
public class NekoConfig {
    //TODO: refactor

    public static final int TITLE_TYPE_TEXT = 0;
    public static final int TITLE_TYPE_ICON = 1;
    public static final int TITLE_TYPE_MIX = 2;

    public static final int ID_TYPE_HIDDEN = 0;
    public static final int ID_TYPE_API = 1;
    public static final int ID_TYPE_BOTAPI = 2;

    public static final int TRANS_TYPE_NEKO = 0;
    public static final int TRANS_TYPE_TG = 1;
    public static final int TRANS_TYPE_EXTERNAL = 2;

    public static final int DOUBLE_TAP_ACTION_NONE = 0;
    public static final int DOUBLE_TAP_ACTION_REACTION = 1;
    public static final int DOUBLE_TAP_ACTION_TRANSLATE = 2;
    public static final int DOUBLE_TAP_ACTION_REPLY = 3;
    public static final int DOUBLE_TAP_ACTION_SAVE = 4;
    public static final int DOUBLE_TAP_ACTION_REPEAT = 5;
    public static final int DOUBLE_TAP_ACTION_EDIT = 6+1;

    public static final int TABLET_AUTO = 0;
    public static final int TABLET_ENABLE = 1;
    public static final int TABLET_DISABLE = 2;

    private static final String EMOJI_FONT_AOSP = "NotoColorEmoji.ttf";

    private static final Object sync = new Object();
    public static boolean useIPv6 = false;
    public static boolean showHiddenFeature = true;

    public static boolean useSystemEmoji = SharedConfig.useSystemEmoji;
    public static boolean ignoreBlocked = false;
    public static boolean disablePhotoSideAction = true;
    public static boolean hideKeyboardOnChatScroll = false;
    public static boolean rearVideoMessages = false;
    public static boolean hideAllTab = false;
    public static boolean confirmAVMessage = false;
    public static boolean askBeforeCall = true;
    public static boolean disableNumberRounding = false;
    public static boolean disableGreetingSticker = false;
    public static boolean autoTranslate = false;
    public static boolean showRPCError = false;
    public static boolean keepFormatting = true;
    public static float stickerSize = 14.0f;
    public static String translationProvider = Translator.PROVIDER_GOOGLE;
    public static String translationTarget = "app";
    public static int deepLFormality = DeepLTranslator.FORMALITY_DEFAULT;
    public static int tabsTitleType = TITLE_TYPE_MIX;
    public static int idType = ID_TYPE_API;
    public static int maxRecentStickers = 20;
    public static int transType = TRANS_TYPE_NEKO;
    public static int doubleTapAction = DOUBLE_TAP_ACTION_REACTION;
    public static HashSet<String> restrictedLanguages = new HashSet<>();

    public static boolean showAddToSavedMessages = true;
    public static boolean showReport = false;
    public static boolean showPrPr = false;
    public static boolean showViewHistory = false;
    public static boolean showAdminActions = false;
    public static boolean showChangePermissions = false;
    public static boolean showDeleteDownloadedFile = false;
    public static boolean showMessageDetails = false;
    public static boolean showTranslate = true;
    public static boolean showRepeat = true;
    public static boolean showNoQuoteForward = true;
    public static boolean showCopyPhoto = false;

    public static boolean hidePhone = true;
    public static boolean transparentStatusBar = false;
    public static int tabletMode = TABLET_AUTO;
    public static boolean openArchiveOnPull = false;
    public static boolean avatarAsDrawerBackground = false;
    public static boolean avatarBackgroundBlur = true;
    public static boolean avatarBackgroundDarken = true;
    public static boolean showTabsOnForward = false;
    public static int nameOrder = 1;
    public static int eventType = 0;
    public static boolean newYear = false;
    public static boolean unlimitedFavedStickers = false;
    public static boolean unlimitedPinnedDialogs = false;
    public static boolean disableAppBarShadow = false;
    public static boolean mediaPreview = true;
    public static boolean autoPauseVideo = true;
    public static boolean disableProximityEvents = false;
    public static boolean mapDriftingFix = false;
    public static boolean increaseVoiceMessageQuality = false;
    public static boolean voiceEnhancements = false;
    public static boolean disableInstantCamera = false;
    public static boolean tryToOpenAllLinksInIV = false;
    public static boolean enableAnalytics = true;
    public static boolean formatTimeWithSeconds = false;
    public static boolean accentAsNotificationColor = false;
    public static boolean silenceNonContacts = false;
    public static boolean disableJumpToNextChannel = false;
    public static boolean disableVoiceMessageAutoPlay = false;
    public static boolean disableMarkdownByDefault = false;
    public static boolean hideTimeOnSticker = false;
    public static boolean showOriginal = true;

    public static final String WS_ADDRESS = "ws.neko";
    private static int socksPort = -1;
    private static boolean tcp2wsStarted = false;
    private static tcp2wsServer tcp2wsServer;
    public static boolean wsEnableTLS = true;
    public static boolean wsUseMTP = false;
    public static boolean wsUseDoH = true;

    public static final ArrayList<DatacenterInfo> datacenterInfos = new ArrayList<>(5);

    public static boolean verifyLinkTip = false;

    public static boolean residentNotification = false;

    public static boolean shouldNOTTrustMe = false;

    public static boolean customEmojiFont;
    public static String customEmojiFontPath;
    private static Typeface customEmojiTypeface;
    private static Typeface systemEmojiTypeface;
    public static boolean loadSystemEmojiFailed = false;

    public static ArrayList<TLRPC.Update> pendingChangelog;

    public static boolean isChineseUser = false;

    private static final SharedPreferences.OnSharedPreferenceChangeListener listener = (preferences, key) -> {
        var map = new HashMap<String, String>(1);
        map.put("key", key);
        AnalyticsHelper.trackEvent("Neko config changed", map);
    };
    private static boolean configLoaded;

    static {
        loadConfig();
    }

    public static void buildAppChangelog(TLRPC.TL_help_appUpdate appUpdate) {
        if (!BuildConfig.VERSION_NAME.equals(appUpdate.version) || appUpdate.text == null) {
            return;
        }
        var update = new TLRPC.TL_updateServiceNotification();
        update.flags = 2;
        update.message = appUpdate.text;
        update.entities = appUpdate.entities;
        ArrayList<TLRPC.Update> updates = new ArrayList<>();
        updates.add(update);
        NekoConfig.pendingChangelog = updates;
    }

    public static int getSocksPort(int port) {
        if (tcp2wsStarted && socksPort != -1) {
            return socksPort;
        }
        try {
            if (port != -1) {
                socksPort = port;
            } else {
                ServerSocket socket = new ServerSocket(0);
                socksPort = socket.getLocalPort();
                socket.close();
            }
            socksPort = port;
            if (!tcp2wsStarted) {
                tcp2wsServer = new tcp2wsServer()
                        .setTgaMode(false)
                        .setTls(wsEnableTLS)
                        .setIfMTP(wsUseMTP)
                        .setIfDoH(wsUseDoH);
                tcp2wsServer.start(socksPort);
                tcp2wsStarted = true;
                var map = new HashMap<String, String>();
                map.put("buildType", BuildConfig.BUILD_TYPE);
                map.put("isChineseUser", String.valueOf(isChineseUser));
                AnalyticsHelper.trackEvent("tcp2ws started", map);
            }
            return socksPort;
        } catch (Exception e) {
            FileLog.e(e);
            if (port != -1) {
                return getSocksPort(-1);
            } else {
                return -1;
            }
        }
    }

    public static int getSocksPort() {
        return getSocksPort(6356);
    }

    public static void wsReloadConfig() {
        if (tcp2wsServer != null) {
            try {
                tcp2wsServer.setTls(wsEnableTLS).setIfMTP(wsUseMTP).setIfDoH(wsUseDoH);
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
    }

    public static boolean isDirectApp() {
        return "release".equals(BuildConfig.BUILD_TYPE) || "debug".equals(BuildConfig.BUILD_TYPE);
    }

    public static void loadConfig() {
        synchronized (sync) {
            if (configLoaded) {
                return;
            }
            isChineseUser = ApplicationLoader.applicationContext.getResources().getBoolean(R.bool.isChineseUser);

            SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE);
            useIPv6 = preferences.getBoolean("useIPv6", false);
            hidePhone = preferences.getBoolean("hidePhone", true);
            ignoreBlocked = preferences.getBoolean("ignoreBlocked2", false);
            tabletMode = preferences.getInt("tabletMode", TABLET_AUTO);
            nameOrder = preferences.getInt("nameOrder", 1);
            transparentStatusBar = preferences.getBoolean("transparentStatusBar", false);
            residentNotification = preferences.getBoolean("residentNotification", false);
            showAddToSavedMessages = preferences.getBoolean("showAddToSavedMessages", true);
            showReport = preferences.getBoolean("showReport", false);
            showPrPr = preferences.getBoolean("showPrPr", isChineseUser);
            showViewHistory = preferences.getBoolean("showViewHistory", false);
            showAdminActions = preferences.getBoolean("showAdminActions", false);
            showChangePermissions = preferences.getBoolean("showChangePermissions", false);
            showDeleteDownloadedFile = preferences.getBoolean("showDeleteDownloadedFile", false);
            showMessageDetails = preferences.getBoolean("showMessageDetails", false);
            showTranslate = preferences.getBoolean("showTranslate", true);
            showRepeat = preferences.getBoolean("showRepeat", true);
            eventType = preferences.getInt("eventType", 0);
            newYear = preferences.getBoolean("newYear", false);
            stickerSize = preferences.getFloat("stickerSize", 14.0f);
            unlimitedFavedStickers = preferences.getBoolean("unlimitedFavedStickers", false);
            unlimitedPinnedDialogs = preferences.getBoolean("unlimitedPinnedDialogs", false);
            translationProvider = preferences.getString("translationProvider2", isChineseUser ? Translator.PROVIDER_LINGO : Translator.PROVIDER_GOOGLE);
            disablePhotoSideAction = preferences.getBoolean("disablePhotoSideAction", true);
            openArchiveOnPull = preferences.getBoolean("openArchiveOnPull", false);
            showHiddenFeature = preferences.getBoolean("showHiddenFeature5",true);
            hideKeyboardOnChatScroll = preferences.getBoolean("hideKeyboardOnChatScroll", false);
            avatarAsDrawerBackground = preferences.getBoolean("avatarAsDrawerBackground", false);
            avatarBackgroundBlur = preferences.getBoolean("avatarBackgroundBlur", false);
            avatarBackgroundDarken = preferences.getBoolean("avatarBackgroundDarken", false);
            useSystemEmoji = preferences.getBoolean("useSystemEmoji", SharedConfig.useSystemEmoji);
            showTabsOnForward = preferences.getBoolean("showTabsOnForward", false);
            rearVideoMessages = preferences.getBoolean("rearVideoMessages", false);
            hideAllTab = preferences.getBoolean("hideAllTab", false);
            tabsTitleType = preferences.getInt("tabsTitleType2", TITLE_TYPE_MIX);
            confirmAVMessage = preferences.getBoolean("confirmAVMessage", false);
            askBeforeCall = preferences.getBoolean("askBeforeCall", true);
            shouldNOTTrustMe = preferences.getBoolean("shouldNOTTrustMe", false);
            disableNumberRounding = preferences.getBoolean("disableNumberRounding", false);
            disableAppBarShadow = preferences.getBoolean("disableAppBarShadow", false);
            mediaPreview = preferences.getBoolean("mediaPreview", true);
            idType = preferences.getInt("idType", ID_TYPE_API);
            autoPauseVideo = preferences.getBoolean("autoPauseVideo", true);
            disableProximityEvents = preferences.getBoolean("disableProximityEvents", false);
            customEmojiFontPath = preferences.getString("customEmojiFontPath", "");
            customEmojiFont = preferences.getBoolean("customEmojiFont", false);
            mapDriftingFix = preferences.getBoolean("mapDriftingFix", isChineseUser);
            increaseVoiceMessageQuality = preferences.getBoolean("increaseVoiceMessageQuality", false);
            voiceEnhancements = preferences.getBoolean("voiceEnhancements", false);
            disableInstantCamera = preferences.getBoolean("disableInstantCamera", false);
            tryToOpenAllLinksInIV = preferences.getBoolean("tryToOpenAllLinksInIV", false);
            enableAnalytics = preferences.getBoolean("enableAnalytics", true);
            formatTimeWithSeconds = preferences.getBoolean("formatTimeWithSeconds", false);
            accentAsNotificationColor = preferences.getBoolean("accentAsNotificationColor", false);
            silenceNonContacts = preferences.getBoolean("silenceNonContacts", false);
            showNoQuoteForward = preferences.getBoolean("showNoQuoteForward", true);
            wsEnableTLS = preferences.getBoolean("wsEnableTLS", true);
            wsUseMTP = preferences.getBoolean("wsUseMTP", false);
            wsUseDoH = preferences.getBoolean("wsUseDoH", true);
            translationTarget = preferences.getString("translationTarget", "app");
            maxRecentStickers = preferences.getInt("maxRecentStickers", 20);
            disableJumpToNextChannel = preferences.getBoolean("disableJumpToNextChannel", false);
            disableGreetingSticker = preferences.getBoolean("disableGreetingSticker", false);
            autoTranslate = preferences.getBoolean("autoTranslate", false);
            disableVoiceMessageAutoPlay = preferences.getBoolean("disableVoiceMessageAutoPlay", false);
            transType = preferences.getInt("transType", TRANS_TYPE_NEKO);
            showCopyPhoto = preferences.getBoolean("showCopyPhoto", false);
            verifyLinkTip = preferences.getBoolean("verifyLinkTip2", false);
            doubleTapAction = preferences.getInt("doubleTapAction", DOUBLE_TAP_ACTION_REACTION);
            restrictedLanguages = new HashSet<>(preferences.getStringSet("restrictedLanguages", new HashSet<>()));
            disableMarkdownByDefault = preferences.getBoolean("disableMarkdownByDefault", false);
            showRPCError = preferences.getBoolean("showRPCError", false);
            hideTimeOnSticker = preferences.getBoolean("hideTimeOnSticker", false);
            showOriginal = preferences.getBoolean("showOriginal", true);
            preferences.registerOnSharedPreferenceChangeListener(listener);

            for (int a = 1; a <= 5; a++) {
                datacenterInfos.add(new DatacenterInfo(a));
            }

            var map = new HashMap<String, String>();
            map.put("buildType", BuildConfig.BUILD_TYPE);
            map.put("isChineseUser", String.valueOf(isChineseUser));
            AnalyticsHelper.trackEvent("Load config", map);
            configLoaded = true;
        }
    }

    public static boolean isChatCat(TLRPC.Chat chat) {
        return ConfigHelper.getVerify().stream().anyMatch(id -> id == chat.id);
    }

    public static void saveRestrictedLanguages() {
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putStringSet("restrictedLanguages", restrictedLanguages);
        editor.commit();
    }

    public static void setDoubleTapAction(int action) {
        doubleTapAction = action;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("doubleTapAction", doubleTapAction);
        editor.commit();
    }

    public static void setVerifyLinkTip(boolean shown) {
        verifyLinkTip = shown;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("verifyLinkTip2", verifyLinkTip);
        editor.commit();
    }

    public static void setTransType(int type) {
        transType = type;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("transType", transType);
        editor.commit();
    }

    public static void setWsUseMTP(boolean use) {
        wsUseMTP = use;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("wsUseMTP", wsUseMTP);
        editor.commit();
    }

    public static void toggleShowOriginal() {
        showOriginal = !showOriginal;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("showOriginal", showOriginal);
        editor.commit();
    }

    public static void toggleDisableMarkdownByDefault() {
        disableMarkdownByDefault = !disableMarkdownByDefault;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("disableMarkdownByDefault", disableMarkdownByDefault);
        editor.commit();
    }

    public static void toggleHideTimeOnSticker() {
        hideTimeOnSticker = !hideTimeOnSticker;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("hideTimeOnSticker", hideTimeOnSticker);
        editor.commit();
    }

    public static void toggleShowRPCError() {
        showRPCError = !showRPCError;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("showRPCError", showRPCError);
        editor.commit();
    }

    public static void toggleWsEnableDoH() {
        wsUseDoH = !wsUseDoH;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("wsUseDoH", wsUseDoH);
        editor.commit();
    }

    public static void toggleWsEnableTLS() {
        wsEnableTLS = !wsEnableTLS;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("wsEnableTLS", wsEnableTLS);
        editor.commit();
    }

    public static void toggleShowAddToSavedMessages() {
        showAddToSavedMessages = !showAddToSavedMessages;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("showAddToSavedMessages", showAddToSavedMessages);
        editor.commit();
    }

    public static void toggleShowReport() {
        showReport = !showReport;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("showReport", showReport);
        editor.commit();
    }


    public static void toggleShowViewHistory() {
        showViewHistory = !showViewHistory;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("showViewHistory", showViewHistory);
        editor.commit();
    }

    public static void toggleShowPrPr() {
        showPrPr = !showPrPr;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("showPrPr", showPrPr);
        editor.commit();
    }

    public static void toggleShowAdminActions() {
        showAdminActions = !showAdminActions;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("showAdminActions", showAdminActions);
        editor.commit();
    }

    public static void toggleShowChangePermissions() {
        showChangePermissions = !showChangePermissions;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("showChangePermissions", showChangePermissions);
        editor.commit();
    }

    public static void toggleShowDeleteDownloadedFile() {
        showDeleteDownloadedFile = !showDeleteDownloadedFile;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("showDeleteDownloadedFile", showDeleteDownloadedFile);
        editor.commit();
    }

    public static void toggleShowMessageDetails() {
        showMessageDetails = !showMessageDetails;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("showMessageDetails", showMessageDetails);
        editor.commit();
    }

    public static void toggleShowRepeat() {
        showRepeat = !showRepeat;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("showRepeat", showRepeat);
        editor.commit();
    }

    public static void toggleIPv6() {
        useIPv6 = !useIPv6;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("useIPv6", useIPv6);
        editor.commit();
    }

    public static void toggleHidePhone() {
        hidePhone = !hidePhone;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("hidePhone", hidePhone);
        editor.commit();
    }

    public static void toggleIgnoreBlocked() {
        ignoreBlocked = !ignoreBlocked;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("ignoreBlocked2", ignoreBlocked);
        editor.commit();
    }

    public static void setTabletMode(int mode) {
        tabletMode = mode;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("tabletMode", tabletMode);
        editor.commit();
    }

    public static void setNameOrder(int order) {
        nameOrder = order;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("nameOrder", nameOrder);
        editor.commit();
    }

    public static void toggleResidentNotification() {
        residentNotification = !residentNotification;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("residentNotification", residentNotification);
        editor.commit();
        ApplicationLoader.applicationContext.stopService(new Intent(ApplicationLoader.applicationContext, NotificationsService.class));
        ApplicationLoader.startPushService();
    }

    public static void setEventType(int type) {
        eventType = type;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("eventType", eventType);
        editor.commit();
    }

    public static void toggleNewYear() {
        newYear = !newYear;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("newYear", newYear);
        editor.commit();
    }

    public static void toggleUnlimitedFavedStickers() {
        unlimitedFavedStickers = !unlimitedFavedStickers;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("unlimitedFavedStickers", unlimitedFavedStickers);
        editor.commit();
    }

    public static void toggleUnlimitedPinnedDialogs() {
        unlimitedPinnedDialogs = !unlimitedPinnedDialogs;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("unlimitedPinnedDialogs", unlimitedPinnedDialogs);
        editor.commit();
    }

    public static void toggleShowTranslate() {
        showTranslate = !showTranslate;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("showTranslate", showTranslate);
        editor.commit();
    }

    public static void setStickerSize(float size) {
        stickerSize = size;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putFloat("stickerSize", stickerSize);
        editor.commit();
    }

    public static void setTranslationProvider(String provider) {
        translationProvider = provider;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("translationProvider2", translationProvider);
        editor.commit();
    }

    public static void setTranslationTarget(String target) {
        translationTarget = target;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("translationTarget", translationTarget);
        editor.commit();
    }

    public static void setDeepLFormality(int formality) {
        deepLFormality = formality;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("deepLFormality", deepLFormality);
        editor.commit();
    }

    public static void toggleDisablePhotoSideAction() {
        disablePhotoSideAction = !disablePhotoSideAction;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("disablePhotoSideAction", disablePhotoSideAction);
        editor.commit();
    }

    public static void toggleOpenArchiveOnPull() {
        openArchiveOnPull = !openArchiveOnPull;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("openArchiveOnPull", openArchiveOnPull);
        editor.commit();
    }

    public static void toggleShowHiddenFeature() {
        showHiddenFeature = !showHiddenFeature;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("showHiddenFeature5", showHiddenFeature);
        editor.commit();
    }

    public static void toggleHideKeyboardOnChatScroll() {
        hideKeyboardOnChatScroll = !hideKeyboardOnChatScroll;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("hideKeyboardOnChatScroll", hideKeyboardOnChatScroll);
        editor.commit();
    }

    public static void toggleAvatarAsDrawerBackground() {
        avatarAsDrawerBackground = !avatarAsDrawerBackground;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("avatarAsDrawerBackground", avatarAsDrawerBackground);
        editor.commit();
    }

    public static void toggleAvatarBackgroundBlur() {
        avatarBackgroundBlur = !avatarBackgroundBlur;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("avatarBackgroundBlur", avatarBackgroundBlur);
        editor.commit();
    }

    public static void toggleAvatarBackgroundDarken() {
        avatarBackgroundDarken = !avatarBackgroundDarken;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("avatarBackgroundDarken", avatarBackgroundDarken);
        editor.commit();
    }

    public static void toggleUseSystemEmoji() {
        useSystemEmoji = !useSystemEmoji;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("useSystemEmoji", useSystemEmoji);
        editor.commit();
    }

    public static void toggleShowTabsOnForward() {
        showTabsOnForward = !showTabsOnForward;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("showTabsOnForward", showTabsOnForward);
        editor.commit();
    }

    public static void toggleRearVideoMessages() {
        rearVideoMessages = !rearVideoMessages;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("rearVideoMessages", rearVideoMessages);
        editor.commit();
    }

    public static void toggleHideAllTab() {
        hideAllTab = !hideAllTab;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("hideAllTab", hideAllTab);
        editor.commit();
    }

    public static void setTabsTitleType(int type) {
        tabsTitleType = type;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("tabsTitleType2", tabsTitleType);
        editor.commit();
    }

    public static void toggleConfirmAVMessage() {
        confirmAVMessage = !confirmAVMessage;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("confirmAVMessage", confirmAVMessage);
        editor.commit();
    }

    public static void toggleAskBeforeCall() {
        askBeforeCall = !askBeforeCall;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("askBeforeCall", askBeforeCall);
        editor.commit();
    }

    public static void toggleShouldNOTTrustMe() {
        shouldNOTTrustMe = !shouldNOTTrustMe;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("shouldNOTTrustMe", shouldNOTTrustMe);
        editor.commit();
    }

    public static void toggleDisableNumberRounding() {
        disableNumberRounding = !disableNumberRounding;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("disableNumberRounding", disableNumberRounding);
        editor.commit();
    }

    public static void toggleDisableGreetingSticker() {
        disableGreetingSticker = !disableGreetingSticker;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("disableGreetingSticker", disableGreetingSticker);
        editor.commit();
    }

    public static void toggleDisableAppBarShadow() {
        disableAppBarShadow = !disableAppBarShadow;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("disableAppBarShadow", disableAppBarShadow);
        editor.commit();
    }

    public static void toggleMediaPreview() {
        mediaPreview = !mediaPreview;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("mediaPreview", mediaPreview);
        editor.commit();
    }

    public static void setIdType(int type) {
        idType = type;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("idType", idType);
        editor.commit();
    }

    public static void toggleAutoPauseVideo() {
        autoPauseVideo = !autoPauseVideo;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("autoPauseVideo", autoPauseVideo);
        editor.commit();
    }

    public static void toggleDisableProximityEvents() {
        disableProximityEvents = !disableProximityEvents;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("disableProximityEvents", disableProximityEvents);
        editor.commit();
    }

    public static void toggleMapDriftingFix() {
        mapDriftingFix = !mapDriftingFix;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("mapDriftingFix", mapDriftingFix);
        editor.commit();
    }

    public static void toggleIncreaseVoiceMessageQuality() {
        increaseVoiceMessageQuality = !increaseVoiceMessageQuality;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("increaseVoiceMessageQuality", increaseVoiceMessageQuality);
        editor.commit();
    }

    public static void toggleVoiceEnhancements() {
        voiceEnhancements = !voiceEnhancements;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("voiceEnhancements", voiceEnhancements);
        editor.commit();
    }

    public static void toggleDisabledInstantCamera() {
        disableInstantCamera = !disableInstantCamera;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("disableInstantCamera", disableInstantCamera);
        editor.commit();
    }

    public static void toggleTryToOpenAllLinksInIV() {
        tryToOpenAllLinksInIV = !tryToOpenAllLinksInIV;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("tryToOpenAllLinksInIV", tryToOpenAllLinksInIV);
        editor.commit();
    }

    public static void toggleFormatTimeWithSeconds() {
        formatTimeWithSeconds = !formatTimeWithSeconds;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("formatTimeWithSeconds", formatTimeWithSeconds);
        editor.commit();
    }

    public static void toggleAccentAsNotificationColor() {
        accentAsNotificationColor = !accentAsNotificationColor;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("accentAsNotificationColor", accentAsNotificationColor);
        editor.commit();
    }

    public static void toggleSilenceNonContacts() {
        silenceNonContacts = !silenceNonContacts;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("silenceNonContacts", silenceNonContacts);
        editor.commit();
    }

    public static void toggleDisableJumpToNextChannel() {
        disableJumpToNextChannel = !disableJumpToNextChannel;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("disableJumpToNextChannel", disableJumpToNextChannel);
        editor.commit();
    }

    public static void toggleShowNoQuoteForward() {
        showNoQuoteForward = !showNoQuoteForward;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("showNoQuoteForward", showNoQuoteForward);
        editor.commit();
    }

    public static void toggleShowCopyPhoto() {
        showCopyPhoto = !showCopyPhoto;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("showCopyPhoto", showCopyPhoto);
        editor.commit();
    }

    public static void toggleAutoTranslate() {
        autoTranslate = !autoTranslate;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("autoTranslate", autoTranslate);
        editor.commit();
    }

    public static void toggleDisableVoiceMessageAutoPlay() {
        disableVoiceMessageAutoPlay = !disableVoiceMessageAutoPlay;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("disableVoiceMessageAutoPlay", disableVoiceMessageAutoPlay);
        editor.commit();
    }

    public static void setMaxRecentStickers(int size) {
        maxRecentStickers = size;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("maxRecentStickers", maxRecentStickers);
        editor.commit();
    }

    public static void toggleCustomEmojiFont() {
        customEmojiFont = !customEmojiFont;
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("customEmojiFont", customEmojiFont);
        editor.commit();
    }

    public static boolean setCustomEmojiFontPath(String path) {
        if (path != null) {
            Typeface typeface;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                typeface = new Typeface.Builder(path)
                        .build();
            } else {
                typeface = Typeface.createFromFile(path);
            }
            if (typeface == null || typeface.equals(Typeface.DEFAULT)) {
                return false;
            }
            customEmojiTypeface = typeface;
            customEmojiFontPath = path;
        } else {
            customEmojiTypeface = null;
            customEmojiFontPath = null;
        }
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekoconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("customEmojiFontPath", customEmojiFontPath);
        editor.commit();
        return true;
    }

    public static Typeface getSystemEmojiTypeface() {
        if (!loadSystemEmojiFailed && systemEmojiTypeface == null) {
            try {
                Pattern p = Pattern.compile(">(.*emoji.*)</font>", Pattern.CASE_INSENSITIVE);
                BufferedReader br = new BufferedReader(new FileReader("/system/etc/fonts.xml"));
                String line;
                while ((line = br.readLine()) != null) {
                    Matcher m = p.matcher(line);
                    if (m.find()) {
                        systemEmojiTypeface = Typeface.createFromFile("/system/fonts/" + m.group(1));
                        FileLog.d("emoji font file fonts.xml = " + m.group(1));
                        break;
                    }
                }
                br.close();
            } catch (Exception e) {
                FileLog.e(e);
            }
            if (systemEmojiTypeface == null) {
                try {
                    systemEmojiTypeface = Typeface.createFromFile("/system/fonts/" + EMOJI_FONT_AOSP);
                    FileLog.d("emoji font file = " + EMOJI_FONT_AOSP);
                } catch (Exception e) {
                    FileLog.e(e);
                    loadSystemEmojiFailed = true;
                }
            }
        }
        return systemEmojiTypeface;
    }

    public static Typeface getCustomEmojiTypeface() {
        if (customEmojiTypeface == null) {
            try {
                customEmojiTypeface = Typeface.createFromFile(customEmojiFontPath);
            } catch (Exception e) {
                FileLog.e(e);
                customEmojiTypeface = null;
                if (customEmojiFont) NekoConfig.toggleCustomEmojiFont();
                setCustomEmojiFontPath(null);
            }
        }
        return customEmojiTypeface;
    }

    public static int getNotificationColor() {
        if (accentAsNotificationColor) {
            int color = 0;
            if (Theme.getActiveTheme().hasAccentColors()) {
                color = Theme.getActiveTheme().getAccentColor(Theme.getActiveTheme().currentAccentId);
            }
            if (color == 0) {
                color = Theme.getColor(Theme.key_actionBarDefault) | 0xff000000;
            }
            float brightness = AndroidUtilities.computePerceivedBrightness(color);
            if (brightness >= 0.721f || brightness <= 0.279f) {
                color = Theme.getColor(Theme.key_windowBackgroundWhiteBlueHeader) | 0xff000000;
            }
            return color;
        } else {
            return Color.parseColor("#E4C1DB");        }
    }

    public static File getTelegramPath() {
        File path = null;
        if (!TextUtils.isEmpty(SharedConfig.storageCacheDir)) {
            if (!Environment.getExternalStorageDirectory().getAbsolutePath().startsWith(SharedConfig.storageCacheDir)) {
                File[] dirs = ApplicationLoader.applicationContext.getExternalFilesDirs(null);
                for (File dir : dirs) {
                    if (dir.getAbsolutePath().startsWith(SharedConfig.storageCacheDir)) {
                        path = dir;
                        break;
                    }
                }
            }
        }
        if (path == null) {
            path = ApplicationLoader.applicationContext.getExternalFilesDir(null);
        }
        File telegramPath = new File(path, "Telegram");
        //noinspection ResultOfMethodCallIgnored
        telegramPath.mkdirs();
        return telegramPath;
    }

    public static class DatacenterInfo {

        public int id;

        public long pingId;
        public long ping;
        public boolean checking;
        public boolean available;
        public long availableCheckTime;

        public DatacenterInfo(int i) {
            id = i;
        }
    }
}
