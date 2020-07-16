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
import com.megacrit.cardcrawl.map.DungeonMap;
import com.megacrit.cardcrawl.screens.DungeonMapScreen;
import java.util.ArrayList;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import test447.keycuts.KeyCuts;
import test447.keycuts.helpers.MapRoomNodeLabel;

public class DungeonMapScreenPatches
{
	/**
	 * the default behavior when opening the map for the boss encounter is to open the map
	 * to show exactly the boss icon with no space above it
	 *
	 * this means that the hotkey indicator for the boss will not be visible by default
	 *
	 * this patch makes the dungeon map screen open above the boss icon for the boss fight
	 * in order to make the hotkey indicator visible
	 */
	@SpirePatch(clz=DungeonMapScreen.class, method="open")
	public static class Open
	{
		public static void Postfix(DungeonMapScreen self, boolean doScrollingAnimation)
		{
			if (!KeyCuts.showMapHotKeys())
				return;
			if (!doScrollingAnimation)
			{
				if (AbstractDungeon.getCurrMapNode().y == 14)
				{
					float BOSS_W = (float) ReflectionHacks.getPrivateStatic(DungeonMap.class, "BOSS_W");
					DungeonMapScreen.offsetY -= (BOSS_W * 0.5f);
					ReflectionHacks.setPrivate(self, DungeonMapScreen.class, "targetOffsetY", DungeonMapScreen.offsetY);
				}
			}
		}
	}

	@SpirePatch(clz=DungeonMapScreen.class, method="render")
	public static class Render
	{
		public static ArrayList<MapRoomNodeLabel> lateRenderLabels = new ArrayList<>();

		public static void Prefix(DungeonMapScreen self, SpriteBatch sb)
		{
			lateRenderLabels.clear();
		}

		@SpireInsertPatch(locator=Locator.class)
		public static void Insert(DungeonMapScreen self, SpriteBatch sb)
		{
			for (MapRoomNodeLabel label : lateRenderLabels)
			{
				FontHelper.renderFontCentered(sb, FontHelper.buttonLabelFont,
						label.keyString,
						label.x, label.y, Settings.CREAM_COLOR);
			}
		}

		private static class Locator extends SpireInsertLocator {
			public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
				Matcher matcher = new Matcher.MethodCallMatcher(DungeonMap.class, "renderBossIcon");
				return new int[] {LineFinder.findInOrder(ctMethodToPatch, new ArrayList<>(), matcher)[0] + 1};
			}
		}
	}
}
