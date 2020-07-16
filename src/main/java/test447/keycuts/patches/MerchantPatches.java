package test447.keycuts.patches;

import basemod.ReflectionHacks;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.LineFinder;
import com.evacipated.cardcrawl.modthespire.lib.Matcher;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertLocator;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.input.InputActionSet;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.shop.Merchant;
import java.util.ArrayList;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import test447.keycuts.KeyCuts;

public class MerchantPatches
{
	@SpirePatch(clz=Merchant.class, method="update")
	public static class Update
	{
		@SpireInsertPatch(locator=Locator.class)
		public static void Insert(Merchant self)
		{
			if (!KeyCuts.useShopHotKeys())
				return;
			if (InputActionSet.selectCardActions[0].isJustPressed())
			{
				Hitbox hb = (Hitbox) ReflectionHacks.getPrivate(self, Merchant.class, "hb");
				hb.hovered = true;
				InputHelper.justClickedLeft = true;
			}
		}
	}

	private static class Locator extends SpireInsertLocator {
		public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
			Matcher finalMatcher = new Matcher.MethodCallMatcher(Hitbox.class, "update");
			return new int[] {LineFinder.findInOrder(ctMethodToPatch, new ArrayList<>(), finalMatcher)[0] + 1};
		}
	}

	@SpirePatch(clz=Merchant.class, method="render")
	public static class Render
	{
		public static void Postfix(Merchant self, SpriteBatch sb)
		{
			if (!KeyCuts.showShopHotKeys())
				return;
			if (!AbstractDungeon.isScreenUp)
			{
				FontHelper.renderFontCentered(sb, FontHelper.buttonLabelFont, InputActionSet.selectCardActions[0].getKeyString(),
						self.hb.cX + 20.0f * Settings.scale, self.hb.y + 300.0f * Settings.scale + 20.0f * Settings.scale, Settings.CREAM_COLOR);
			}
		}
	}
}
