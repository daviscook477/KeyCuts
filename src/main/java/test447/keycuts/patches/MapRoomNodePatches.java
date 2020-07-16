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
import com.megacrit.cardcrawl.map.MapRoomNode;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.screens.DungeonMapScreen;
import java.util.ArrayList;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import test447.keycuts.KeyCuts;
import test447.keycuts.helpers.MapHelper;
import test447.keycuts.helpers.MapRoomNodeLabel;

public class MapRoomNodePatches
{
	@SpirePatch(clz=MapRoomNode.class, method="update")
	public static class Update
	{
		@SpireInsertPatch(locator=Locator.class)
		public static void Insert(MapRoomNode self)
		{
			if (!KeyCuts.useMapHotKeys())
				return;
			int slot = MapHelper.getMapScreenNodeChoices().indexOf(self);
			if (slot == -1 || slot >= InputActionSet.selectCardActions.length)
				return;
			if (InputActionSet.selectCardActions[slot].isJustPressed())
			{
				self.hb.hovered = true;
				AbstractDungeon.dungeonMapScreen.clicked = true;
			}
		}

		private static class Locator extends SpireInsertLocator {
			public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
				Matcher matcher = new Matcher.MethodCallMatcher(Hitbox.class, "update");
				return new int[] {LineFinder.findInOrder(ctMethodToPatch, new ArrayList<>(), matcher)[0] + 1};
			}
		}
	}

	@SpirePatch(clz=MapRoomNode.class, method="render")
	public static class Render
	{
		public static void Postfix(MapRoomNode self, SpriteBatch sb)
		{
			if (!KeyCuts.showMapHotKeys())
				return;
			int slot = MapHelper.getMapScreenNodeChoices().indexOf(self);
			if (slot == -1 || slot >= InputActionSet.selectCardActions.length)
				return;
			if (!AbstractDungeon.getCurrRoom().phase.equals(AbstractRoom.RoomPhase.COMPLETE))
				return;
			float SPACING_X = (float) ReflectionHacks.getPrivateStatic(MapRoomNode.class, "SPACING_X");
			float OFFSET_X = (float) ReflectionHacks.getPrivateStatic(MapRoomNode.class, "OFFSET_X");
			float OFFSET_Y = (float) ReflectionHacks.getPrivateStatic(MapRoomNode.class, "OFFSET_Y");
			float x = self.x * SPACING_X + OFFSET_X + self.offsetX;
			float y = self.y * Settings.MAP_DST_Y + OFFSET_Y + DungeonMapScreen.offsetY + self.offsetY;
			float scale = (float) ReflectionHacks.getPrivate(self, MapRoomNode.class, "scale");
			float textY = y + 64.0f * Settings.scale * scale;
			DungeonMapScreenPatches.Render.lateRenderLabels.add(
					new MapRoomNodeLabel(InputActionSet.selectCardActions[slot].getKeyString(),
							x, textY));
		}
	}
}
