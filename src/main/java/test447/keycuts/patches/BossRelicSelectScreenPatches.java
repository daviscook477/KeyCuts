package test447.keycuts.patches;

import basemod.ReflectionHacks;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.input.InputActionSet;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.screens.select.BossRelicSelectScreen;
import com.megacrit.cardcrawl.vfx.FloatyEffect;
import test447.keycuts.KeyCuts;

public class BossRelicSelectScreenPatches
{
	@SpirePatch(clz=BossRelicSelectScreen.class, method="update")
	public static class Update
	{
		public static AbstractRelic chosenRelic = null;

		public static void Postfix(BossRelicSelectScreen self)
		{
			chosenRelic = null;
			if (!KeyCuts.useChestHotKeys())
				return;
			int i;
			for (i = 0; i < self.relics.size(); i++)
			{
				if (i >= InputActionSet.selectCardActions.length)
					continue;
				AbstractRelic relic = self.relics.get(i);
				if (InputActionSet.selectCardActions[i].isJustPressed())
				{
					chosenRelic = relic;
					InputHelper.justClickedLeft = true;
					return;
				}
			}
		}
	}

	@SpirePatch(clz=BossRelicSelectScreen.class, method="render")
	public static class Render
	{
		public static void Postfix(BossRelicSelectScreen self, SpriteBatch sb)
		{
			if (!KeyCuts.showChestHotKeys())
				return;
			int i;
			for (i = 0; i < self.relics.size(); i++)
			{
				if (i >= InputActionSet.selectCardActions.length)
					continue;
				AbstractRelic relic = self.relics.get(i);
				FloatyEffect f_effect = (FloatyEffect) ReflectionHacks.getPrivate(relic, AbstractRelic.class, "f_effect");
				FontHelper.renderFontCentered(sb, FontHelper.buttonLabelFont, InputActionSet.selectCardActions[i].getKeyString(),
						relic.currentX + f_effect.x, relic.currentY + f_effect.y + 22.0f * Settings.scale * relic.scale, Settings.CREAM_COLOR);
			}
		}
	}
}
