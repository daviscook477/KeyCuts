package test447.keycuts.patches.rewards;

import com.evacipated.cardcrawl.modthespire.lib.LineFinder;
import com.evacipated.cardcrawl.modthespire.lib.Matcher;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertLocator;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import java.util.ArrayList;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import test447.keycuts.KeyCuts;

public class AbstractRelicPatches
{

	@SpirePatch(clz=AbstractRelic.class, method="update")
	public static class Update
	{
		@SpireInsertPatch(locator=Locator.class)
		public static void Insert(AbstractRelic self)
		{
			if (!KeyCuts.useChestHotKeys())
				return;
			if (BossRelicSelectScreenPatches.Update.chosenRelic == self)
			{
				self.hb.hovered = true;
				self.hb.clicked = true;
			}
		}
	}

	private static class Locator extends SpireInsertLocator {
		public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
			Matcher finalMatcher = new Matcher.MethodCallMatcher(Hitbox.class, "update");
			return new int[] {LineFinder.findInOrder(ctMethodToPatch, new ArrayList<>(), finalMatcher)[0] + 1};
		}
	}

}
