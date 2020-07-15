package test447.keycuts.patches;

import basemod.ReflectionHacks;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.TipHelper;
import com.megacrit.cardcrawl.helpers.input.InputActionSet;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.ui.buttons.ProceedButton;
import test447.keycuts.KeyCuts;

public class ProceedButtonPatches
{
	@SpirePatch(clz=ProceedButton.class, method="render")
	public static class Render
	{
		public static void Postfix(ProceedButton self, SpriteBatch sb)
		{
			Hitbox hb = (Hitbox) ReflectionHacks.getPrivate(self, ProceedButton.class, "hb");
			float x = (float) ReflectionHacks.getPrivate(self, ProceedButton.class, "current_x");
			float y = (float) ReflectionHacks.getPrivate(self, ProceedButton.class, "current_y");
			String label = (String) ReflectionHacks.getPrivate(self, ProceedButton.class, "label");
			UIStrings UIStrings = CardCrawlGame.languagePack.getUIString(KeyCuts.MOD_ID + ":tooltips");
			String[] TEXT = UIStrings.TEXT;
			if (hb.hovered && !Settings.isTouchScreen) {
				TipHelper.renderGenericTip(x - 140.0F * Settings.scale, y + 100.0F * Settings.scale, label + " (" +
						InputActionSet.endTurn.getKeyString() + ")", TEXT[0]);
			}
		}
	}
}
