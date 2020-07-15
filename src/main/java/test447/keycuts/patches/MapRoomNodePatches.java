package test447.keycuts.patches;

import com.evacipated.cardcrawl.modthespire.lib.LineFinder;
import com.evacipated.cardcrawl.modthespire.lib.Matcher;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertLocator;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.map.MapRoomNode;
import java.util.ArrayList;
import javassist.CannotCompileException;
import javassist.CtBehavior;

public class MapRoomNodePatches
{
	// method from https://github.com/ForgottenArbiter/CommunicationMod/blob/master/src/main/java/communicationmod/patches/MapRoomNodeHoverPatch.java
	// published under MIT license
	// (c) ForgottenArbiter 2020
	@SpirePatch(clz=MapRoomNode.class, method="update")
	public static class Update
	{
		public static MapRoomNode hoverNode;
		public static boolean doHover = false;

		@SpireInsertPatch(locator=Locator.class)
		public static void Insert(MapRoomNode self)
		{
			if (doHover)
			{
				if (hoverNode == self)
				{
					self.hb.hovered = true;
					doHover = false;
				}
				else
				{
					self.hb.hovered = false;
				}
			}
		}

		private static class Locator extends SpireInsertLocator {
			public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
				Matcher matcher = new Matcher.MethodCallMatcher(Hitbox.class, "update");
				return new int[] {LineFinder.findInOrder(ctMethodToPatch, new ArrayList<Matcher>(), matcher)[0] + 1};
			}
		}

	}
}
