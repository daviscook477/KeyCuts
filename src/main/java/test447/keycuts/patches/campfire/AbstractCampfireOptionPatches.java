package test447.keycuts.patches.campfire;

import basemod.ReflectionHacks;
import com.evacipated.cardcrawl.modthespire.lib.LineFinder;
import com.evacipated.cardcrawl.modthespire.lib.Matcher;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertLocator;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.input.InputActionSet;
import com.megacrit.cardcrawl.rooms.CampfireUI;
import com.megacrit.cardcrawl.rooms.RestRoom;
import com.megacrit.cardcrawl.ui.campfire.AbstractCampfireOption;
import java.util.ArrayList;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import test447.keycuts.KeyCuts;

public class AbstractCampfireOptionPatches
{
	@SpirePatch(clz=AbstractCampfireOption.class, method="update")
	public static class Update
	{
		@SpireInsertPatch(locator=Locator.class, localvars={"canClick"})
		public static void Insert(AbstractCampfireOption self, boolean canClick)
		{
			CampfireUI campfireUI = ((RestRoom)AbstractDungeon.getCurrRoom()).campfireUI;
			ArrayList<AbstractCampfireOption> buttons = (ArrayList<AbstractCampfireOption>) ReflectionHacks.getPrivate(campfireUI, CampfireUI.class, "buttons");
			String label = (String) ReflectionHacks.getPrivate(self, AbstractCampfireOption.class, "label");
			String visualIndicator = "";
			int slot = buttons.indexOf(self);
			if (KeyCuts.showCampfireHotKeys() && slot < InputActionSet.selectCardActions.length)
				visualIndicator = " (" + InputActionSet.selectCardActions[slot].getKeyString() + ")";
			label = label.replace(CampfireUIPatches.SLOT_REPLACEMENT_INDICATOR, visualIndicator);
			ReflectionHacks.setPrivate(self, AbstractCampfireOption.class, "label", label);
			if (!KeyCuts.useCampfireHotKeys())
				return;
			if (!canClick)
				return;
			if (slot >= InputActionSet.selectCardActions.length)
				return;
			if (InputActionSet.selectCardActions[slot].isJustPressed())
			{
				CardCrawlGame.sound.play("UI_CLICK_1");
				self.hb.hovered = true;
				self.hb.clicked = true;
			}
		}
	}

	private static class Locator extends SpireInsertLocator {
		public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
			Matcher finalMatcher = new Matcher.FieldAccessMatcher(Hitbox.class, "clicked");
			return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<>(), finalMatcher);
		}
	}
}
