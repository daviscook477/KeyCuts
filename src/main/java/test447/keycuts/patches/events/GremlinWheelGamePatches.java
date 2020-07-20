package test447.keycuts.patches.events;

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
import com.megacrit.cardcrawl.events.shrines.GremlinWheelGame;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.TipHelper;
import com.megacrit.cardcrawl.helpers.input.InputActionSet;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.localization.UIStrings;
import java.util.ArrayList;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import test447.keycuts.KeyCuts;

public class GremlinWheelGamePatches
{
	@SpirePatch(clz=GremlinWheelGame.class, method="update")
	public static class Update
	{
		@SpireInsertPatch(locator=Locator.class)
		public static void Insert(GremlinWheelGame self)
		{
			if (!KeyCuts.useProceedHotKeys())
				return;
			if (InputActionSet.endTurn.isJustPressed())
			{
				Hitbox buttonHb = (Hitbox) ReflectionHacks.getPrivate(self, GremlinWheelGame.class, "buttonHb");
				buttonHb.hovered = true;
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

	@SpirePatch(clz=GremlinWheelGame.class, method="render")
	public static class Render
	{
		public static void Postfix(GremlinWheelGame self, SpriteBatch sb)
		{
			if (!KeyCuts.useProceedHotKeys())
				return;
			Hitbox buttonHb = (Hitbox) ReflectionHacks.getPrivate(self, GremlinWheelGame.class, "buttonHb");
			UIStrings UIStrings = CardCrawlGame.languagePack.getUIString(KeyCuts.MOD_ID + ":tooltips");
			String[] TEXT = UIStrings.TEXT;
			if (buttonHb.hovered && !Settings.isTouchScreen) {
				TipHelper.renderGenericTip(buttonHb.x, buttonHb.y + 550.0f * Settings.scale, TEXT[5] + " (" +
						InputActionSet.endTurn.getKeyString() + ")", TEXT[0]);
			}
		}
	}
}
