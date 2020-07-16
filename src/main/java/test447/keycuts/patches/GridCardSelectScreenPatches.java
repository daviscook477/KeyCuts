package test447.keycuts.patches;

import basemod.ReflectionHacks;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.LineFinder;
import com.evacipated.cardcrawl.modthespire.lib.Matcher;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertLocator;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.input.InputActionSet;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.screens.select.GridCardSelectScreen;
import com.megacrit.cardcrawl.ui.buttons.PeekButton;
import java.util.ArrayList;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import test447.keycuts.KeyCuts;

public class GridCardSelectScreenPatches
{
	// TAB
	public static final int[] SHORTCUT_MODIFIER_KEYS = { 61 };

	public static boolean isShortcutModifierKeyJustPressed()
	{
		for (int keycode : SHORTCUT_MODIFIER_KEYS)
		{
			if (Gdx.input.isKeyJustPressed(keycode))
			{
				return true;
			}
		}
		return false;
	}

	@SpirePatch(clz=GridCardSelectScreen.class, method="update")
	public static class Update
	{
		public static int row = 0;

		@SpireInsertPatch(locator=Locator.class)
		public static void Insert(GridCardSelectScreen self)
		{
			if (!KeyCuts.useCardRewardHotKeys())
				return;
			int CARDS_PER_LINE = (int) ReflectionHacks.getPrivateStatic(GridCardSelectScreen.class, "CARDS_PER_LINE");
			// cycle through rows with TAB
			if (isShortcutModifierKeyJustPressed())
			{
				row++;
				int rows = (int) Math.ceil((float) self.targetGroup.size() / (float) CARDS_PER_LINE);
				if (row >= rows)
					row = 0;
			}
			int i;
			for (i = 0; i < CARDS_PER_LINE; i++)
			{
				if (i >= InputActionSet.selectCardActions.length)
					continue;
				if (InputActionSet.selectCardActions[i].isJustPressed())
				{
					int cardPosition = CARDS_PER_LINE * row + i;
					AbstractCard hoveredCard = self.targetGroup.group.get(cardPosition);
					hoveredCard.hb.clicked = true;
					ReflectionHacks.setPrivate(self, GridCardSelectScreen.class, "hoveredCard", hoveredCard);
					InputHelper.justClickedLeft = true;
					return;
				}
			}
		}
	}

	private static class Locator extends SpireInsertLocator {
		public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
			Matcher finalMatcher = new Matcher.MethodCallMatcher(GridCardSelectScreen.class, "updateCardPositionsAndHoverLogic");
			int[] result = LineFinder.findAllInOrder(ctMethodToPatch, new ArrayList<>(), finalMatcher);
			int i;
			for (i = 0; i < result.length; i++)
			{
				result[i] += 1;
			}
			return result;
		}
	}

	@SpirePatch(clz=GridCardSelectScreen.class, method="render")
	public static class Render
	{
		public static void Postfix(GridCardSelectScreen self, SpriteBatch sb)
		{
			if (!KeyCuts.useCardRewardHotKeys())
				return;
			if (PeekButton.isPeeking)
				return;
			int CARDS_PER_LINE = (int) ReflectionHacks.getPrivateStatic(GridCardSelectScreen.class, "CARDS_PER_LINE");
			int i;
			for (i = 0; i < self.targetGroup.group.size(); i++)
			{
				AbstractCard card = self.targetGroup.group.get(i);
				int row = i / CARDS_PER_LINE;
				int tabRow = Update.row + 1;
				int rows = (int) Math.ceil((float) self.targetGroup.size() / (float) CARDS_PER_LINE);
				if (tabRow >= rows)
					tabRow = 0;
				if (row == Update.row)
				{
					float height = AbstractCard.IMG_HEIGHT * card.drawScale / 2.0F;
					float topOfCard = card.current_y + height;
					float textSpacing = 20.0F * Settings.scale;
					float textY = topOfCard + textSpacing;
					int slot = i - row * CARDS_PER_LINE;
					if (slot >= InputActionSet.selectCardActions.length)
						continue;
					FontHelper.renderFontCentered(sb, FontHelper.buttonLabelFont, InputActionSet.selectCardActions[slot].getKeyString(),
							card.current_x, textY, Settings.CREAM_COLOR);
				}
				else if (row == tabRow)
				{
					float height = AbstractCard.IMG_HEIGHT * card.drawScale / 2.0F;
					float topOfCard = card.current_y + height;
					float textSpacing = 20.0f * Settings.scale;
					float textY = topOfCard + textSpacing;
					FontHelper.renderFontCentered(sb, FontHelper.buttonLabelFont, "TAB",
							card.current_x, textY, Settings.CREAM_COLOR, 0.7f);
				}
			}
		}
	}

	@SpirePatch(clz=GridCardSelectScreen.class, method="open", paramtypez={CardGroup.class, int.class, String.class, boolean.class, boolean.class, boolean.class, boolean.class})
	public static class Open
	{
		public static void Prefix(GridCardSelectScreen self)
		{
			Update.row = 0;
		}
	}
}
