package test447.keycuts;

import basemod.BaseMod;
import basemod.ModLabeledToggleButton;
import basemod.ModPanel;
import basemod.interfaces.EditStringsSubscriber;
import basemod.interfaces.PostInitializeSubscriber;
import com.evacipated.cardcrawl.modthespire.lib.SpireConfig;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;

import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.UIStrings;
import java.io.IOException;
import java.util.Properties;

@SpireInitializer
public class KeyCuts implements PostInitializeSubscriber, EditStringsSubscriber
{
    public static final String MOD_ID = "test447_keycuts";
    private static SpireConfig modConfig = null;

    public static void initialize()
    {
        BaseMod.subscribe(new KeyCuts());
        try {
            Properties defaults = new Properties();
            // dialog
            defaults.put("UseDialogHotKeys", Boolean.toString(true));
            defaults.put("ShowDialogHotKeys", Boolean.toString(true));

            // card rewards
            defaults.put("UseCardRewardHotKeys", Boolean.toString(true));
            defaults.put("ShowCardRewardHotKeys", Boolean.toString(true));

            // combat rewards
            defaults.put("UseCombatRewardHotKeys", Boolean.toString(true));
            defaults.put("ShowCombatRewardHotKeys", Boolean.toString(true));

            // campfires
            defaults.put("UseCampfireHotKeys", Boolean.toString(true));
            defaults.put("ShowCampfireHotKeys", Boolean.toString(true));

            // proceed/skip/confirm buttons
            defaults.put("UseProceedHotKeys", Boolean.toString(true));

            // map navigation
            defaults.put("UseMapHotKeys", Boolean.toString(true));
            defaults.put("ShowMapHotKeys", Boolean.toString(true));

            // chests
            defaults.put("UseChestHotKeys", Boolean.toString(true));
            defaults.put("ShowChestHotKeys", Boolean.toString(true));

            // shops
            defaults.put("UseShopHotKeys", Boolean.toString(true));
            defaults.put("ShowShopHotKeys", Boolean.toString(true));

            modConfig = new SpireConfig("KeyCuts", "Config", defaults);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void receivePostInitialize() {
        UIStrings UIStrings = CardCrawlGame.languagePack.getUIString(MOD_ID + ":settings");
        String[] TEXT = UIStrings.TEXT;
        ModPanel settingsPanel = new ModPanel();
        addToggleButtons(useDialogHotKeys(), "UseDialogHotKeys", TEXT[0], showDialogHotKeys(), "ShowDialogHotKeys", TEXT[1], settingsPanel);
        addToggleButtons(useCardRewardHotKeys(), "UseCardRewardHotKeys", TEXT[2], showCardRewardHotKeys(), "ShowCardRewardHotKeys", TEXT[3], settingsPanel);
        addToggleButtons(useCombatRewardHotKeys(), "UseCombatRewardHotKeys", TEXT[4], showCombatRewardHotKeys(), "ShowCombatRewardHotKeys", TEXT[5], settingsPanel);
        addToggleButtons(useCampfireHotKeys(), "UseCampfireHotKeys", TEXT[6], showCampfireHotKeys(), "ShowCampfireHotKeys", TEXT[7], settingsPanel);
        addToggleButton(useProceedHotKeys(), "UseProceedHotKeys", TEXT[8], settingsPanel);
        addToggleButtons(useMapHotKeys(), "UseMapHotKeys", TEXT[9], showMapHotKeys(), "ShowMapHotKeys", TEXT[10], settingsPanel);
        addToggleButtons(useChestHotKeys(), "UseChestHotKeys", TEXT[11], showChestHotKeys(), "ShowChestHotKeys", TEXT[12], settingsPanel);
        addToggleButtons(useShopHotKeys(), "UseShopHotKeys", TEXT[13], showShopHotKeys(), "ShowShopHotKeys", TEXT[14], settingsPanel);
        BaseMod.registerModBadge(ImageMaster.loadImage(MOD_ID + "Resources/img/modBadge.png"), MOD_ID, "test447", "Play even more of the game with just your keyboard", settingsPanel);
    }

    @Override
    public void receiveEditStrings() {
        BaseMod.loadCustomStringsFile(UIStrings.class, MOD_ID + "Resources/localization/eng/UI-Strings.json");
    }

    private float xPos = 360.0f;
    private float yPos = 740.0f;

    public void addToggleButtons(boolean enabled, String key, String text, boolean connectedEnabled, String connectedKey, String connectedText, ModPanel settingsPanel)
    {
        ModLabeledToggleButton connectedToggleButton = new ModLabeledToggleButton(connectedText, xPos, yPos,
                Settings.CREAM_COLOR, FontHelper.charDescFont, connectedEnabled, settingsPanel, l -> {},
                button ->
                {
                    if (modConfig != null)
                    {
                        modConfig.setBool(connectedKey, button.enabled);
                        try {
                            modConfig.save();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
        yPos -= 50.0f;
        settingsPanel.addUIElement(connectedToggleButton);
        ModLabeledToggleButton toggleButton = new ModLabeledToggleButton(text, xPos, yPos,
                Settings.CREAM_COLOR, FontHelper.charDescFont, enabled, settingsPanel, l -> {},
                button ->
                {
                    if (modConfig != null)
                    {
                        modConfig.setBool(key, button.enabled);
                        if (!button.enabled)
                        {
                            modConfig.setBool(connectedKey, false);
                            connectedToggleButton.toggle.enabled = false;
                        }
                        try {
                            modConfig.save();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
        yPos -= 50.0f;
        settingsPanel.addUIElement(toggleButton);
    }

    public void addToggleButton(boolean enabled, String key, String text, ModPanel settingsPanel)
    {
        ModLabeledToggleButton toggleButton = new ModLabeledToggleButton(text, xPos, yPos,
                Settings.CREAM_COLOR, FontHelper.charDescFont, enabled, settingsPanel, l -> {},
                button ->
                {
                    if (modConfig != null)
                    {
                        modConfig.setBool(key, button.enabled);
                        try {
                            modConfig.save();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
        yPos -= 50.0f;
        settingsPanel.addUIElement(toggleButton);
    }

    public static boolean useDialogHotKeys()
    {
        if (modConfig == null)
            return false;
        return modConfig.getBool("UseDialogHotKeys");
    }

    public static boolean showDialogHotKeys()
    {
        if (modConfig == null)
            return false;
        return modConfig.getBool("ShowDialogHotKeys");
    }

    public static boolean useCardRewardHotKeys()
    {
    if (modConfig == null)
        return false;
    return modConfig.getBool("UseCardRewardHotKeys");
}

    public static boolean showCardRewardHotKeys()
    {
        if (modConfig == null)
            return false;
        return modConfig.getBool("ShowCardRewardHotKeys");
    }

    public static boolean useCombatRewardHotKeys()
    {
        if (modConfig == null)
            return false;
        return modConfig.getBool("UseCombatRewardHotKeys");
    }

    public static boolean showCombatRewardHotKeys()
    {
        if (modConfig == null)
            return false;
        return modConfig.getBool("ShowCombatRewardHotKeys");
    }

    public static boolean useCampfireHotKeys()
    {
        if (modConfig == null)
            return false;
        return modConfig.getBool("UseCampfireHotKeys");
    }

    public static boolean showCampfireHotKeys()
    {
        if (modConfig == null)
            return false;
        return modConfig.getBool("ShowCampfireHotKeys");
    }

    public static boolean useProceedHotKeys()
    {
        if (modConfig == null)
            return false;
        return modConfig.getBool("UseProceedHotKeys");
    }

    public static boolean useMapHotKeys()
    {
        if (modConfig == null)
            return false;
        return modConfig.getBool("UseMapHotKeys");
    }

    public static boolean showMapHotKeys()
    {
        if (modConfig == null)
            return false;
        return modConfig.getBool("ShowMapHotKeys");
    }

    public static boolean useChestHotKeys()
    {
        if (modConfig == null)
            return false;
        return modConfig.getBool("UseChestHotKeys");
    }

    public static boolean showChestHotKeys()
    {
        if (modConfig == null)
            return false;
        return modConfig.getBool("ShowChestHotKeys");
    }

    public static boolean useShopHotKeys()
    {
        if (modConfig == null)
            return false;
        return modConfig.getBool("UseShopHotKeys");
    }

    public static boolean showShopHotKeys()
    {
        if (modConfig == null)
            return false;
        return modConfig.getBool("ShowShopHotKeys");
    }
}
