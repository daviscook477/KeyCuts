package test447.keycuts.patches;

import basemod.ReflectionHacks;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.TipHelper;
import com.megacrit.cardcrawl.helpers.input.InputActionSet;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.ui.buttons.CardSelectConfirmButton;
import test447.keycuts.KeyCuts;

public class CardSelectConfirmButtonPatches
{
	@SpirePatch(clz=CardSelectConfirmButton.class, method="render")
	public static class Render
	{
		public static void Postfix(CardSelectConfirmButton self, SpriteBatch sb)
		{
			boolean isHidden = (boolean) ReflectionHacks.getPrivate(self, CardSelectConfirmButton.class, "isHidden");
			if (isHidden)
				return;
			float TAKE_Y = (float) ReflectionHacks.getPrivateStatic(CardSelectConfirmButton.class, "TAKE_Y");
			String buttonText = (String) ReflectionHacks.getPrivate(self, CardSelectConfirmButton.class, "buttonText");
			UIStrings UIStrings = CardCrawlGame.languagePack.getUIString(KeyCuts.MOD_ID + ":tooltips");
			String[] TEXT = UIStrings.TEXT;
			if (self.hb.hovered && !Settings.isTouchScreen) {
				TipHelper.renderGenericTip(Settings.WIDTH / 2.0F - 256.0f * Settings.scale, TAKE_Y - 50.0F * Settings.scale, buttonText + " (" +
						InputActionSet.confirm.getKeyString() + ")", TEXT[0]);
			}
		}
	}
}
