package test447.keycuts.patches.potions;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import test447.keycuts.KeyCuts;

public class InputHelperPatches
{
	@SpirePatch(clz=InputHelper.class, method="getCardSelectedByHotkey")
	public static class GetCardSelectedByHotkey
	{
		public static SpireReturn<AbstractCard> Prefix(CardGroup cards)
		{
			if (KeyCuts.usePotionHotKeys() && InputHelper.isShortcutModifierKeyPressed())
				return SpireReturn.Return(null);
			else
				return SpireReturn.Continue();
		}
	}
}
