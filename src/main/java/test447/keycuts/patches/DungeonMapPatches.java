package test447.keycuts.patches;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.map.DungeonMap;
import javassist.CannotCompileException;
import javassist.CtBehavior;

import java.util.ArrayList;

public class DungeonMapPatches
{
	// method from https://github.com/ForgottenArbiter/CommunicationMod/blob/master/src/main/java/communicationmod/patches/DungeonMapPatch.java
	// published under MIT license
	// (c) ForgottenArbiter 2020
	@SpirePatch(clz=DungeonMap.class, method="update")
	public static class Update
	{
		public static boolean doBossHover = false;

		@SpireInsertPatch(locator=Locator.class)
		public static void Insert(DungeonMap self)
		{
			if (doBossHover)
			{
				self.bossHb.hovered = true;
				InputHelper.justClickedLeft = true;
				doBossHover = false;
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