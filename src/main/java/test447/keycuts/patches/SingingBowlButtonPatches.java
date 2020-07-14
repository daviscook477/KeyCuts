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
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.input.InputActionSet;
import com.megacrit.cardcrawl.screens.CardRewardScreen;
import com.megacrit.cardcrawl.ui.buttons.SingingBowlButton;
import com.megacrit.cardcrawl.ui.buttons.SkipCardButton;
import java.util.ArrayList;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import test447.keycuts.KeyCuts;

public class SingingBowlButtonPatches
{
	public static int getOptionsBefore()
	{
		int optionsBefore = AbstractDungeon.cardRewardScreen.rewardGroup.size();
		SkipCardButton skipButton = (SkipCardButton) ReflectionHacks.getPrivate(AbstractDungeon.cardRewardScreen, CardRewardScreen.class, "skipButton");
		boolean isHidden = (boolean)ReflectionHacks.getPrivate(skipButton, SkipCardButton.class, "isHidden");
		if (!isHidden)
			optionsBefore += 1;
		return optionsBefore;
	}

	@SpirePatch(clz=SingingBowlButton.class, method="update")
	public static class Update
	{
		@SpireInsertPatch(locator= Locator.class)
		public static void Insert(SingingBowlButton self)
		{
			if (!KeyCuts.useCardRewardHotKeys())
				return;
			int optionsBefore = getOptionsBefore();
			if (optionsBefore >= InputActionSet.selectCardActions.length)
				return;
			if (InputActionSet.selectCardActions[optionsBefore].isJustPressed())
			{
				CardCrawlGame.sound.play("UI_CLICK_1");
				self.hb.clicked = true;
			}
		}
	}

	private static class Locator extends SpireInsertLocator {
		public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
			Matcher finalMatcher = new Matcher.MethodCallMatcher(Hitbox.class, "update");
			return new int[] {LineFinder.findInOrder(ctMethodToPatch, new ArrayList<>(), finalMatcher)[0] + 1};
		}
	}

	@SpirePatch(clz=SingingBowlButton.class, method="render")
	public static class Render
	{
		public static void Postfix(SingingBowlButton self, SpriteBatch sb)
		{
			if (!KeyCuts.showCardRewardHotKeys())
				return;
			int optionsBefore = getOptionsBefore();
			if (optionsBefore >= InputActionSet.selectCardActions.length)
				return;
			boolean isHidden = (boolean) ReflectionHacks.getPrivate(self, SingingBowlButton.class, "isHidden");
			if (isHidden)
				return;
			float current_x = (float) ReflectionHacks.getPrivate(self, SingingBowlButton.class, "current_x");
			float textSpacing = 80.0f * Settings.scale;
			float textY = SkipCardButton.TAKE_Y + textSpacing;
			FontHelper.renderFontCentered(sb, FontHelper.buttonLabelFont, InputActionSet.selectCardActions[optionsBefore].getKeyString(),
					current_x, textY, Settings.CREAM_COLOR);
		}
	}
}
