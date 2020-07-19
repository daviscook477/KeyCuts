package test447.keycuts.patches.shop;

import basemod.ReflectionHacks;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.Loader;
import com.evacipated.cardcrawl.modthespire.lib.LineFinder;
import com.evacipated.cardcrawl.modthespire.lib.Matcher;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertLocator;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.input.InputActionSet;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.shop.ShopScreen;
import com.megacrit.cardcrawl.shop.StorePotion;
import com.megacrit.cardcrawl.shop.StoreRelic;
import com.megacrit.cardcrawl.vfx.FloatyEffect;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import test447.keycuts.KeyCuts;

public class ShopScreenPatches
{
	public static HashMap<AbstractCard, Integer> shopCardPositionMap = new HashMap<>();
	public static HashMap<StoreRelic, Integer> shopRelicPositionMap = new HashMap<>();
	public static HashMap<StorePotion, Integer> shopPotionPositionMap = new HashMap<>();

	// SHIFT
	public static final int[] SHORTCUT_MODIFIER_KEYS_2 = { 59, 60 };

	public static boolean isShortcutModifierKey2Pressed()
	{
		for (int keycode : SHORTCUT_MODIFIER_KEYS_2)
		{
			if (Gdx.input.isKeyPressed(keycode))
			{
				return true;
			}
		}
		return false;
	}

	@SpirePatch(clz=ShopScreen.class, method="setStartingCardPositions")
	public static class SetStartingCardPositions
	{
		public static void Postfix(ShopScreen self)
		{
			shopCardPositionMap.clear();
			int i;
			for (i = 0; i < self.coloredCards.size(); i++)
			{
				shopCardPositionMap.put(self.coloredCards.get(i), i);
			}
			int j;
			for (j = 0; j < self.colorlessCards.size(); j++)
			{
				shopCardPositionMap.put(self.colorlessCards.get(j), j + self.coloredCards.size());
			}
		}
	}

	@SpirePatch(clz=ShopScreen.class, method="initRelics")
	public static class InitRelics
	{
		public static void Postfix(ShopScreen self)
		{
			shopRelicPositionMap.clear();
			ArrayList<StoreRelic> relics = (ArrayList<StoreRelic>) ReflectionHacks.getPrivate(self, ShopScreen.class, "relics");
			int i;
			for (i = 0; i < relics.size(); i++)
			{
				shopRelicPositionMap.put(relics.get(i), i);
			}
		}
	}

	@SpirePatch(clz=ShopScreen.class, method="initPotions")
	public static class InitPotions
	{
		public static void Postfix(ShopScreen self)
		{
			shopPotionPositionMap.clear();
			ArrayList<StorePotion> potions = (ArrayList<StorePotion>) ReflectionHacks.getPrivate(self, ShopScreen.class, "potions");
			int i;
			for (i = 0; i < potions.size(); i++)
			{
				shopPotionPositionMap.put(potions.get(i), i);
			}
		}
	}

	@SpirePatch(clz=ShopScreen.class, method="update")
	public static class Update
	{
		public static void SelectCards(ShopScreen self)
		{
			for (AbstractCard card : self.coloredCards)
			{
				if (!shopCardPositionMap.containsKey(card))
					continue;
				int slot = shopCardPositionMap.get(card);
				if (slot >= InputActionSet.selectCardActions.length)
					continue;
				try
				{
					Method purchaseCard = ShopScreen.class.getDeclaredMethod("purchaseCard", AbstractCard.class);
					purchaseCard.setAccessible(true);
					if (InputActionSet.selectCardActions[slot].isJustPressed())
					{
						purchaseCard.invoke(self, card);
						return;
					}
				}
				catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e)
				{
					// rethrow as unchecked exception since silently failing isn't super useful for debugging
					if (Loader.DEBUG)
					{
						throw new RuntimeException(e);
					}
					// otherwise silently fail since letting a user continue with mouse clicks is better than crashing
					// because hotkeys aren't working
				}
			}
			for (AbstractCard card : self.colorlessCards)
			{
				if (!shopCardPositionMap.containsKey(card))
					continue;
				int slot = shopCardPositionMap.get(card);
				if (slot >= InputActionSet.selectCardActions.length)
					continue;
				try
				{
					Method purchaseCard = ShopScreen.class.getDeclaredMethod("purchaseCard", AbstractCard.class);
					purchaseCard.setAccessible(true);
					if (InputActionSet.selectCardActions[slot].isJustPressed())
					{
						purchaseCard.invoke(self, card);
						return;
					}
				}
				catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e)
				{
					// rethrow as unchecked exception since silently failing isn't super useful for debugging
					if (Loader.DEBUG)
					{
						throw new RuntimeException(e);
					}
					// otherwise silently fail since letting a user continue with mouse clicks is better than crashing
					// because hotkeys aren't working
				}
			}
		}

		public static void SelectRelics(ShopScreen self)
		{
			ArrayList<StoreRelic> relics = (ArrayList<StoreRelic>) ReflectionHacks.getPrivate(self, ShopScreen.class, "relics");
			for (StoreRelic storeRelic : relics)
			{
				if (!shopRelicPositionMap.containsKey(storeRelic))
					continue;
				int slot = shopRelicPositionMap.get(storeRelic);
				if (slot >= InputActionSet.selectCardActions.length)
					continue;
				if (InputActionSet.selectCardActions[slot].isJustPressed())
				{
					storeRelic.purchaseRelic();
					return;
				}
			}
		}

		public static void SelectPotions(ShopScreen self)
		{
			ArrayList<StorePotion> potions = (ArrayList<StorePotion>) ReflectionHacks.getPrivate(self, ShopScreen.class, "potions");
			for (StorePotion storePotion : potions)
			{
				if (!shopPotionPositionMap.containsKey(storePotion))
					continue;
				int slot = shopPotionPositionMap.get(storePotion);
				if (slot >= InputActionSet.selectCardActions.length)
					continue;
				if (InputActionSet.selectCardActions[slot].isJustPressed())
				{
					storePotion.purchasePotion();
					return;
				}
			}
		}

		public static void SelectPurge(ShopScreen self)
		{
			if (!self.purgeAvailable)
				return;
			try
			{
				Method purchasePurge = ShopScreen.class.getDeclaredMethod("purchasePurge");
				purchasePurge.setAccessible(true);
				if (InputActionSet.endTurn.isJustPressed()) {
					purchasePurge.invoke(self);
					return;
				}
			}
			catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e)
			{
				// rethrow as unchecked exception since silently failing isn't super useful for debugging
				if (Loader.DEBUG)
				{
					throw new RuntimeException(e);
				}
				// otherwise silently fail since letting a user continue with mouse clicks is better than crashing
				// because hotkeys aren't working
			}
		}

		@SpireInsertPatch(locator=Locator.class)
		public static void Insert(ShopScreen self)
		{
			if (!KeyCuts.useShopHotKeys())
				return;
			// swap between selections based on modifier
			if (InputHelper.isShortcutModifierKeyPressed())
				SelectPotions(self);
			else if (isShortcutModifierKey2Pressed())
				SelectRelics(self);
			else
			{
				SelectCards(self);
				SelectPurge(self);
			}
		}

		private static class Locator extends SpireInsertLocator {
			public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
				Matcher matcher = new Matcher.MethodCallMatcher(ShopScreen.class, "updateHand");
				return new int[] {LineFinder.findInOrder(ctMethodToPatch, new ArrayList<>(), matcher)[0] + 1};
			}
		}
	}

	@SpirePatch(clz=ShopScreen.class, method="renderCardsAndPrices")
	public static class RenderCardsAndPrices
	{
		public static void Postfix(ShopScreen self, SpriteBatch sb)
		{
			if (!KeyCuts.showShopHotKeys())
				return;
			// don't show cards when either modifier is held
			if (InputHelper.isShortcutModifierKeyPressed() || isShortcutModifierKey2Pressed())
				return;
			for (AbstractCard card : self.coloredCards)
			{
				if (!shopCardPositionMap.containsKey(card))
					continue;
				int slot = shopCardPositionMap.get(card);
				if (slot >= InputActionSet.selectCardActions.length)
					continue;
				float height = AbstractCard.IMG_HEIGHT * card.drawScale / 2.0F;
				float topOfCard = card.current_y + height;
				float textSpacing = 30.0F * Settings.scale;
				float textY = topOfCard + textSpacing;
				FontHelper.renderFontCentered(sb, FontHelper.buttonLabelFont, InputActionSet.selectCardActions[slot].getKeyString(),
						card.current_x, textY, Settings.CREAM_COLOR);
			}
			for (AbstractCard card : self.colorlessCards)
			{
				if (!shopCardPositionMap.containsKey(card))
					continue;
				int slot = shopCardPositionMap.get(card);
				if (slot >= InputActionSet.selectCardActions.length)
					continue;
				float height = AbstractCard.IMG_HEIGHT * card.drawScale / 2.0F;
				float topOfCard = card.current_y + height;
				float textSpacing = 30.0F * Settings.scale;
				float textY = topOfCard + textSpacing;
				FontHelper.renderFontCentered(sb, FontHelper.buttonLabelFont, InputActionSet.selectCardActions[slot].getKeyString(),
						card.current_x, textY, Settings.CREAM_COLOR);
			}
		}
	}

	@SpirePatch(clz=ShopScreen.class, method="renderRelics")
	public static class RenderRelics
	{
		public static void Postfix(ShopScreen self, SpriteBatch sb)
		{
			if (!KeyCuts.showShopHotKeys())
				return;
			// don't show relics when modifier is held
			if (InputHelper.isShortcutModifierKeyPressed())
				return;
			boolean modifierPressed = isShortcutModifierKey2Pressed();
			ArrayList<StoreRelic> relics = (ArrayList<StoreRelic>) ReflectionHacks.getPrivate(self, ShopScreen.class, "relics");
			for (StoreRelic storeRelic : relics)
			{
				if (!shopRelicPositionMap.containsKey(storeRelic))
					continue;
				int slot = shopRelicPositionMap.get(storeRelic);
				if (slot >= InputActionSet.selectCardActions.length)
					continue;
				AbstractRelic relic = storeRelic.relic;
				FloatyEffect f_effect = (FloatyEffect) ReflectionHacks.getPrivate(relic, AbstractRelic.class, "f_effect");
				String numberString = InputActionSet.selectCardActions[slot].getKeyString();
				UIStrings UIStrings = CardCrawlGame.languagePack.getUIString(KeyCuts.MOD_ID + ":tooltips");
				String[] TEXT = UIStrings.TEXT;
				FontHelper.renderFontCentered(sb, FontHelper.buttonLabelFont,
						modifierPressed ? numberString : TEXT[3],
						relic.currentX + f_effect.x, relic.currentY + f_effect.y + 26.0f * Settings.scale * relic.scale,
						Settings.CREAM_COLOR, modifierPressed ? 1.0f : 0.7f);
			}
		}
	}

	@SpirePatch(clz=ShopScreen.class, method="renderPotions")
	public static class RenderPotions
	{
		public static void Postfix(ShopScreen self, SpriteBatch sb)
		{
			if (!KeyCuts.showShopHotKeys())
				return;
			// don't show potions when secondary modifier is held
			if (isShortcutModifierKey2Pressed())
				return;
			boolean modifierPressed = InputHelper.isShortcutModifierKeyPressed();
			ArrayList<StorePotion> potions = (ArrayList<StorePotion>) ReflectionHacks.getPrivate(self, ShopScreen.class, "potions");
			for (StorePotion storePotion : potions)
			{
				if (!shopPotionPositionMap.containsKey(storePotion))
					continue;
				int slot = shopPotionPositionMap.get(storePotion);
				if (slot >= InputActionSet.selectCardActions.length)
					continue;
				AbstractPotion potion = storePotion.potion;
				String numberString = InputActionSet.selectCardActions[slot].getKeyString();
				UIStrings UIStrings = CardCrawlGame.languagePack.getUIString(KeyCuts.MOD_ID + ":tooltips");
				String[] TEXT = UIStrings.TEXT;
				FontHelper.renderFontCentered(sb, FontHelper.buttonLabelFont,
						modifierPressed ? numberString : TEXT[2],
						potion.posX, potion.posY + 22.0f * Settings.scale * potion.scale,
						Settings.CREAM_COLOR, modifierPressed ? 1.0f : 0.7f);
			}
		}
	}

	@SpirePatch(clz=ShopScreen.class, method="renderPurge")
	public static class RenderPurge
	{
		public static void Postfix(ShopScreen self, SpriteBatch sb)
		{
			if (!KeyCuts.showShopHotKeys())
				return;
			// don't show purge when either modifier is held
			if (InputHelper.isShortcutModifierKeyPressed() || isShortcutModifierKey2Pressed())
				return;
			if (!self.purgeAvailable)
				return;
			float purgeCardX = (float) ReflectionHacks.getPrivate(self, ShopScreen.class, "purgeCardX");
			float purgeCardY = (float) ReflectionHacks.getPrivate(self, ShopScreen.class, "purgeCardY");
			float purgeCardScale = (float) ReflectionHacks.getPrivate(self, ShopScreen.class, "purgeCardScale");
			float CARD_H = 150.0F * Settings.scale;
			FontHelper.renderFontCentered(sb, FontHelper.buttonLabelFont,
					InputActionSet.endTurn.getKeyString(),
					purgeCardX, purgeCardY + CARD_H * purgeCardScale - 20.0f * Settings.scale * purgeCardScale,					Settings.CREAM_COLOR);
		}
	}
}
