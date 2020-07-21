package test447.keycuts.patches.potions;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import test447.keycuts.KeyCuts;

public class AbstractPlayerPatches
{
	@SpirePatch(clz=AbstractPlayer.class, method="renderCardHotKeyText")
	public static class RenderCardHotKeyText
	{
		public static SpireReturn Prefix(AbstractPlayer self, SpriteBatch sb)
		{
			if (KeyCuts.usePotionHotKeys() && InputHelper.isShortcutModifierKeyPressed())
				return SpireReturn.Return(null);
			else
				return SpireReturn.Continue();
		}
	}
}
