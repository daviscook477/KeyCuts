package test447.keycuts.patches;

import basemod.ReflectionHacks;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.map.DungeonMap;
import com.megacrit.cardcrawl.screens.DungeonMapScreen;
import test447.keycuts.KeyCuts;

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
}
