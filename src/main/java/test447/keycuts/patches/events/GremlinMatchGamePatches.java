package test447.keycuts.patches.events;

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
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.events.shrines.GremlinMatchGame;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.input.InputActionSet;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.localization.UIStrings;
import java.util.ArrayList;
import java.util.HashMap;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import test447.keycuts.KeyCuts;

public class GremlinMatchGamePatches
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

	public static HashMap<AbstractCard, Integer> gremlinCardPositionMap = new HashMap<>();

	@SpirePatch(clz=GremlinMatchGame.class, method="placeCards")
	public static class PlaceCards
	{
		public static void Postfix(GremlinMatchGame self)
		{
			gremlinCardPositionMap.clear();
			CardGroup cards = (CardGroup) ReflectionHacks.getPrivate(self, GremlinMatchGame.class, "cards");
			// the game lays them out in a diagonal ordering so unwrap it back to row-by-row
			int row = 0;
			int col = 0;
			int i = 0;
			while (i < cards.group.size())
			{
				int CARDS_PER_LINE = 4;
				int ROWS = 3;
				gremlinCardPositionMap.put(cards.group.get(i), row * CARDS_PER_LINE + col);
				i++;
				row++;
				if (row >= ROWS)
					row = 0;
				col++;
				if (col >= CARDS_PER_LINE)
					col = 0;
			}
		}
	}

	@SpirePatch(clz=GremlinMatchGame.class, method="updateMatchGameLogic")
	public static class UpdateMatchGameLogic
	{
		public static int row = 0;

		public static void Prefix(GremlinMatchGame self)
		{
			if (!KeyCuts.useCardRewardHotKeys())
				return;
			if (isShortcutModifierKeyJustPressed())
			{
				row++;
				int NUM_ROWS = 3;
				if (row >= NUM_ROWS)
					row = 0;
			}
		}

		@SpireInsertPatch(locator= UpdateLocator.class, localvars={"c"})
		public static void Insert(GremlinMatchGame self, AbstractCard c)
		{
			if (!gremlinCardPositionMap.containsKey(c))
				return;
			int slot = gremlinCardPositionMap.get(c);
			int CARDS_PER_LINE = 4;
			int row = slot / CARDS_PER_LINE;
			if (row != UpdateMatchGameLogic.row)
				return;
			int modifiedSlot = slot - row * CARDS_PER_LINE;
			if (modifiedSlot >= InputActionSet.selectCardActions.length)
				return;
			if (InputActionSet.selectCardActions[modifiedSlot].isJustPressed())
			{
				c.hb.hovered = true;
				InputHelper.justClickedLeft = true;
			}
		}
	}

	private static class UpdateLocator extends SpireInsertLocator {
		public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
			Matcher matcher = new Matcher.MethodCallMatcher(Hitbox.class, "update");
			return new int[] {LineFinder.findInOrder(ctMethodToPatch, new ArrayList<>(), matcher)[0] + 1};
		}
	}

	@SpirePatch(clz=GremlinMatchGame.class, method="render")
	public static class Render
	{
		@SpireInsertPatch(locator=RenderLocator.class)
		public static void Insert(GremlinMatchGame self, SpriteBatch sb)
		{
			CardGroup cards = (CardGroup) ReflectionHacks.getPrivate(self, GremlinMatchGame.class, "cards");
			for (AbstractCard card : cards.group)
			{
				if (!gremlinCardPositionMap.containsKey(card))
					continue;
				int slot = gremlinCardPositionMap.get(card);
				int CARDS_PER_LINE = 4;
				int row = slot / CARDS_PER_LINE;
				int tabRow = UpdateMatchGameLogic.row + 1;
				int NUM_ROWS = 3;
				if (tabRow >= NUM_ROWS)
					tabRow = 0;
				if (row == UpdateMatchGameLogic.row)
				{
					float height = AbstractCard.IMG_HEIGHT * card.drawScale / 2.0F;
					float topOfCard = card.current_y + height;
					float textSpacing = 20.0F * Settings.scale;
					float textY = topOfCard + textSpacing;
					int modifiedSlot = slot - row * CARDS_PER_LINE;
					if (modifiedSlot >= InputActionSet.selectCardActions.length)
						continue;
					FontHelper.renderFontCentered(sb, FontHelper.buttonLabelFont, InputActionSet.selectCardActions[modifiedSlot].getKeyString(),
							card.current_x, textY, Settings.CREAM_COLOR);
				}
				else if (row == tabRow)
				{
					float height = AbstractCard.IMG_HEIGHT * card.drawScale / 2.0F;
					float topOfCard = card.current_y + height;
					float textSpacing = 20.0f * Settings.scale;
					float textY = topOfCard + textSpacing;
					UIStrings UIStrings = CardCrawlGame.languagePack.getUIString(KeyCuts.MOD_ID + ":tooltips");
					String[] TEXT = UIStrings.TEXT;
					FontHelper.renderFontCentered(sb, FontHelper.buttonLabelFont, TEXT[4],
							card.current_x, textY, Settings.CREAM_COLOR, 0.7f);
				}
			}
		}
	}

	private static class RenderLocator extends SpireInsertLocator {
		public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
			Matcher matcher = new Matcher.MethodCallMatcher(CardGroup.class, "render");
			return new int[] {LineFinder.findInOrder(ctMethodToPatch, new ArrayList<>(), matcher)[0] + 1};
		}
	}
}
