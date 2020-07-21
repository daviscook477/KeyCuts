package test447.keycuts.patches.potions;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.LineFinder;
import com.evacipated.cardcrawl.modthespire.lib.Matcher;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertLocator;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.input.InputActionSet;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.ui.panels.TopPanel;
import java.util.ArrayList;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import test447.keycuts.KeyCuts;

public class TopPanelPatches
{
	@SpirePatch(clz=TopPanel.class, method="updatePotions")
	public static class UpdatePotions
	{
		@SpireInsertPatch(locator=Locator.class, localvars={"p"})
		public static void Insert(TopPanel self, AbstractPotion p)
		{
			if (!KeyCuts.usePotionHotKeys())
				return;
			if (!InputHelper.isShortcutModifierKeyPressed())
				return;
			int slot = AbstractDungeon.player.potions.indexOf(p);
			if (slot >= InputActionSet.selectCardActions.length)
				return;
			if (InputActionSet.selectCardActions[slot].isJustPressed())
			{
				p.hb.hovered = true;
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

	@SpirePatch(clz=TopPanel.class, method="renderPotions")
	public static class RenderPotions
	{
		public static void Postfix(TopPanel self, SpriteBatch sb)
		{
			if (!KeyCuts.showPotionHotKeys())
				return;
			if (!InputHelper.isShortcutModifierKeyPressed())
			{
				int middle = AbstractDungeon.player.potions.size() / 2;
				AbstractPotion potion = AbstractDungeon.player.potions.get(middle);
				UIStrings UIStrings = CardCrawlGame.languagePack.getUIString(KeyCuts.MOD_ID + ":tooltips");
				String[] TEXT = UIStrings.TEXT;
				FontHelper.renderFontCentered(sb, FontHelper.buttonLabelFont, TEXT[2],
						potion.posX, potion.posY - 35.0f * Settings.scale, Settings.CREAM_COLOR);
			}
			else
			{
				int i;
				for (i = 0; i < AbstractDungeon.player.potions.size(); i++)
				{
					AbstractPotion potion = AbstractDungeon.player.potions.get(i);
					if (i >= InputActionSet.selectCardActions.length)
						continue;
					FontHelper.renderFontCentered(sb, FontHelper.buttonLabelFont, InputActionSet.selectCardActions[i].getKeyString(),
							potion.posX, potion.posY - 35.0f * Settings.scale, Settings.CREAM_COLOR);
				}
			}
		}
	}
}
