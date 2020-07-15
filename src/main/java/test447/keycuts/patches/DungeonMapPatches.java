package test447.keycuts.patches;

import basemod.ReflectionHacks;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.input.InputActionSet;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.map.DungeonMap;
import com.megacrit.cardcrawl.screens.DungeonMapScreen;
import javassist.CannotCompileException;
import javassist.CtBehavior;

import java.util.ArrayList;
import test447.keycuts.KeyCuts;

public class DungeonMapPatches
{
	@SpirePatch(clz=DungeonMap.class, method="update")
	public static class Update
	{
		@SpireInsertPatch(locator=Locator.class)
		public static void Insert(DungeonMap self)
		{
			if (!KeyCuts.useMapHotKeys())
				return;
			if (DungeonMap.boss != null && AbstractDungeon.getCurrMapNode().y == 14 &&
					AbstractDungeon.screen == AbstractDungeon.CurrentScreen.MAP &&
					InputActionSet.selectCardActions[0].isJustPressed())
			{
				self.bossHb.hovered = true;
				InputHelper.justClickedLeft = true;
			}
		}

		private static class Locator extends SpireInsertLocator {
			public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
				Matcher matcher = new Matcher.MethodCallMatcher(Hitbox.class, "update");
				return new int[] {LineFinder.findInOrder(ctMethodToPatch, new ArrayList<>(), matcher)[0] + 1};
			}
		}
	}

	@SpirePatch(clz=DungeonMap.class, method="renderBossIcon")
	public static class RenderBossIcon
	{
		public static void Postfix(DungeonMap self, SpriteBatch sb)
		{
			if (!KeyCuts.showMapHotKeys())
				return;
			if (DungeonMap.boss != null && AbstractDungeon.getCurrMapNode().y == 14 && AbstractDungeon.screen == AbstractDungeon.CurrentScreen.MAP)
			{
				float mapOffsetY = (float) ReflectionHacks.getPrivate(self, DungeonMap.class, "mapOffsetY");
				float BOSS_OFFSET_Y = (float) ReflectionHacks.getPrivateStatic(DungeonMap.class, "BOSS_OFFSET_Y");
				// same as BOSS_H b/c image is square
				float BOSS_W = (float) ReflectionHacks.getPrivateStatic(DungeonMap.class, "BOSS_W");
				FontHelper.renderFontCentered(sb, FontHelper.buttonLabelFont, InputActionSet.selectCardActions[0].getKeyString(),
						Settings.WIDTH / 2.0F, DungeonMapScreen.offsetY + mapOffsetY + BOSS_OFFSET_Y + BOSS_W,
						Color.WHITE, 1.5f);
			}
		}
	}
}