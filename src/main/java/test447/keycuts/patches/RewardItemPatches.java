package test447.keycuts.patches;

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
import com.megacrit.cardcrawl.rewards.RewardItem;
import com.megacrit.cardcrawl.screens.CombatRewardScreen;
import java.util.ArrayList;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import test447.keycuts.KeyCuts;

public class RewardItemPatches
{
	private static boolean consumed[] = new boolean[InputActionSet.selectCardActions.length];

	public static boolean isJustPressed(int slot) {
		if (consumed[slot])
			return false;
		boolean result = InputActionSet.selectCardActions[slot].isJustPressed();
		if (result)
			consumed[slot] = true;
		return result;
	}

	@SpirePatch(clz= CombatRewardScreen.class, method="update")
	public static class CombatRewardScreenUpdate
	{
		public static void Prefix(CombatRewardScreen self)
		{
			int i;
			for (i = 0; i < consumed.length; i++)
			{
				consumed[i] = false;
			}
		}
	}

	@SpirePatch(clz=RewardItem.class, method="update")
	public static class RewardItemUpdate
	{
		@SpireInsertPatch(locator=UpdateLocator.class)
		public static void Insert(RewardItem self)
		{
			if (!KeyCuts.useCombatRewardHotKeys())
				return;
			int slot = AbstractDungeon.combatRewardScreen.rewards.indexOf(self);
			if (slot >= InputActionSet.selectCardActions.length)
				return;
			if (isJustPressed(slot))
			{
				CardCrawlGame.sound.playA("UI_CLICK_1", 0.1F);
				self.hb.clicked = true;
			}
		}
	}

	private static class UpdateLocator extends SpireInsertLocator {
		public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
			Matcher finalMatcher = new Matcher.MethodCallMatcher(Hitbox.class, "update");
			return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<>(), finalMatcher);
		}
	}

	@SpirePatch(clz=RewardItem.class, method="render")
	public static class Render
	{
		public static void Postfix(RewardItem self, SpriteBatch sb)
		{
			if (!KeyCuts.showCombatRewardHotKeys())
				return;
			int slot = AbstractDungeon.combatRewardScreen.rewards.indexOf(self);
			if (slot >= InputActionSet.selectCardActions.length)
				return;
			float x = RewardItem.REWARD_ITEM_X + self.hb.width - 80.0f * Settings.scale;
			FontHelper.renderFontRightAligned(sb, FontHelper.buttonLabelFont, InputActionSet.selectCardActions[slot].getKeyString(),
					x, self.y, Settings.CREAM_COLOR);
		}
	}
}
