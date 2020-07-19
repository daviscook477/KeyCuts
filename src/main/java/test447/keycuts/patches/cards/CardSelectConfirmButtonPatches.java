package test447.keycuts.patches.cards;

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
import com.megacrit.cardcrawl.ui.buttons.CardSelectConfirmButton;
import java.util.ArrayList;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import test447.keycuts.KeyCuts;
import test447.keycuts.patches.EndTurnButtonPatches;
import test447.keycuts.patches.ProceedButtonPatches;

public class CardSelectConfirmButtonPatches
{
	@SpirePatch(clz=CardSelectConfirmButton.class, method="update")
	public static class Update
	{
		@SpireInsertPatch(locator=Locator.class)
		public static void Insert(CardSelectConfirmButton self)
		{
			if (!KeyCuts.allowCardSelectConfirmEndTurn())
				return;
			if (InputActionSet.endTurn.isJustPressed())
			{
				CardCrawlGame.sound.play("UI_CLICK_1");
				self.hb.clicked = true;
				// this will be used in combat so cant have
				// the end turn button firing this press even
				// though we use same hotkey
				EndTurnButtonPatches.Update.dontEndTurn = true;
				// this will be used when choosing rewards
				// at the end of a room so cant have the proceed
				// button firing this press even though we
				// use same hotkey
				ProceedButtonPatches.Update.dontProceed = true;
			}
		}
	}

	private static class Locator extends SpireInsertLocator {
		public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
			Matcher finalMatcher = new Matcher.MethodCallMatcher(Hitbox.class, "update");
			return new int[] {LineFinder.findInOrder(ctMethodToPatch, new ArrayList<>(), finalMatcher)[0] + 1};
		}
	}

	@SpirePatch(clz=CardSelectConfirmButton.class, method="render")
	public static class Render
	{
		public static void Postfix(CardSelectConfirmButton self, SpriteBatch sb)
		{
			if (!KeyCuts.allowCardSelectConfirmEndTurn())
				return;
			boolean isHidden = (boolean) ReflectionHacks.getPrivate(self, CardSelectConfirmButton.class, "isHidden");
			if (isHidden)
				return;
			float TAKE_Y = (float) ReflectionHacks.getPrivateStatic(CardSelectConfirmButton.class, "TAKE_Y");
			String buttonText = (String) ReflectionHacks.getPrivate(self, CardSelectConfirmButton.class, "buttonText");
			UIStrings UIStrings = CardCrawlGame.languagePack.getUIString(KeyCuts.MOD_ID + ":tooltips");
			String[] TEXT = UIStrings.TEXT;
			if (self.hb.hovered && !Settings.isTouchScreen) {
				TipHelper.renderGenericTip(Settings.WIDTH / 2.0F - 160.0f * Settings.scale, TAKE_Y - 100.0F * Settings.scale, buttonText + " (" +
						InputActionSet.endTurn.getKeyString() + ")", TEXT[1]);
			}
		}
	}
}
