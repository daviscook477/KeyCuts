package test447.keycuts.patches;

import basemod.ReflectionHacks;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.LineFinder;
import com.evacipated.cardcrawl.modthespire.lib.Matcher;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertLocator;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.TipHelper;
import com.megacrit.cardcrawl.helpers.input.InputActionSet;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.ui.buttons.ProceedButton;
import java.util.ArrayList;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import test447.keycuts.KeyCuts;

public class ProceedButtonPatches
{
	@SpirePatch(clz=ProceedButton.class, method="update")
	public static class Update
	{
		public static boolean dontProceed = false;

		@SpireInsertPatch(locator=Locator.class)
		public static void Insert(ProceedButton self)
		{
			boolean lastDontProceed = dontProceed;
			dontProceed = false;
			if (lastDontProceed)
				return;
			if (!KeyCuts.useProceedHotKeys())
				return;
			if (InputActionSet.endTurn.isJustPressed())
			{
				CardCrawlGame.sound.play("UI_CLICK_1");
				Hitbox hb = (Hitbox) ReflectionHacks.getPrivate(self, ProceedButton.class, "hb");
				hb.clicked = true;
			}
		}
	}

	private static class Locator extends SpireInsertLocator {
		public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
			Matcher finalMatcher = new Matcher.MethodCallMatcher(Hitbox.class, "update");
			return new int[] {LineFinder.findInOrder(ctMethodToPatch, new ArrayList<>(), finalMatcher)[0] + 1};
		}
	}

	@SpirePatch(clz=ProceedButton.class, method="render")
	public static class Render
	{
		public static void Postfix(ProceedButton self, SpriteBatch sb)
		{
			if (!KeyCuts.useProceedHotKeys())
				return;
			Hitbox hb = (Hitbox) ReflectionHacks.getPrivate(self, ProceedButton.class, "hb");
			float x = (float) ReflectionHacks.getPrivate(self, ProceedButton.class, "current_x");
			float y = (float) ReflectionHacks.getPrivate(self, ProceedButton.class, "current_y");
			String label = (String) ReflectionHacks.getPrivate(self, ProceedButton.class, "label");
			UIStrings UIStrings = CardCrawlGame.languagePack.getUIString(KeyCuts.MOD_ID + ":tooltips");
			String[] TEXT = UIStrings.TEXT;
			if (hb.hovered && !Settings.isTouchScreen) {
				TipHelper.renderGenericTip(x - 140.0F * Settings.scale, y + 200.0F * Settings.scale, label + " (" +
						InputActionSet.endTurn.getKeyString() + ")", TEXT[0]);
			}
		}
	}
}
