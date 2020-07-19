package test447.keycuts.patches.shop;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.Loader;
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
import java.util.ArrayList;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import test447.keycuts.KeyCuts;

public class HeartMerchantPatches
{
	@SpirePatch(cls="downfall.util.HeartMerchant", method="update")
	public static class Update
	{
		@SpireInsertPatch(locator=Locator.class)
		public static void Insert(Object self)
		{
			if (!KeyCuts.useShopHotKeys())
				return;
			if (InputActionSet.selectCardActions[0].isJustPressed())
			{
				try
				{
					Class heartMerchant = Class.forName("downfall.util.HeartMerchant");
					Hitbox hb = (Hitbox) heartMerchant.getDeclaredField("hb").get(self);
					hb.hovered = true;
					InputHelper.justClickedLeft = true;
				}
				catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException e)
				{
					// rethrow as unchecked exception since silently failing isn't super useful for debugging
					if (Loader.DEBUG)
					{
						throw new RuntimeException(e);
					}
					// otherwise silently fail since letting a user continue with mouse clicks is better than crashing
					// because hotkeys aren't working
				}
			}
		}
	}

	private static class Locator extends SpireInsertLocator {
		public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
			Matcher finalMatcher = new Matcher.MethodCallMatcher(Hitbox.class, "update");
			return new int[] {LineFinder.findInOrder(ctMethodToPatch, new ArrayList<>(), finalMatcher)[0] + 1};
		}
	}

	@SpirePatch(cls="downfall.util.HeartMerchant", method="render")
	public static class Render
	{
		public static void Postfix(Object self, SpriteBatch sb)
		{
			if (!KeyCuts.showShopHotKeys())
				return;
			if (!AbstractDungeon.isScreenUp)
			{
				try
				{
					Class heartMerchant = Class.forName("downfall.util.HeartMerchant");
					Hitbox hb = (Hitbox) heartMerchant.getDeclaredField("hb").get(self);
					FontHelper.renderFontCentered(sb, FontHelper.buttonLabelFont, InputActionSet.selectCardActions[0].getKeyString(),
							hb.cX + 20.0f * Settings.scale, hb.y + 700.0f * Settings.scale + 20.0f * Settings.scale, Settings.CREAM_COLOR);
				}
				catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException e)
				{
					// rethrow as unchecked exception since silently failing isn't super useful for debugging
					if (Loader.DEBUG)
					{
						throw new RuntimeException(e);
					}
					// otherwise silently fail since letting a user continue with mouse clicks is better than crashing
					// because hotkeys aren't working
				}
			}
		}
	}

}
