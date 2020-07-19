package test447.keycuts.patches.events;

import basemod.ReflectionHacks;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.input.InputActionSet;
import com.megacrit.cardcrawl.ui.buttons.LargeDialogOptionButton;
import test447.keycuts.KeyCuts;

public class LargeDialogOptionButtonPatches
{
	@SpirePatch(clz=LargeDialogOptionButton.class, method="render")
	public static class Render
	{
		public static void Postfix(LargeDialogOptionButton self, SpriteBatch sb)
		{
			if (!KeyCuts.showDialogHotKeys())
				return;
			if (self.slot >= InputActionSet.selectCardActions.length)
				return;
			float x = (float) ReflectionHacks.getPrivate(self, LargeDialogOptionButton.class, "x");
			float y = (float)ReflectionHacks.getPrivate(self, LargeDialogOptionButton.class, "y");
			float TEXT_ADJUST_X = -1.0f * (float)ReflectionHacks.getPrivateStatic(LargeDialogOptionButton.class, "TEXT_ADJUST_X");
			FontHelper.renderFontRightAligned(sb, FontHelper.buttonLabelFont, InputActionSet.selectCardActions[self.slot].getKeyString(),
					x + TEXT_ADJUST_X, y, Settings.CREAM_COLOR);
		}
	}
}
