package test447.keycuts.patches.potions;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.input.InputActionSet;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.ui.panels.PotionPopUp;
import test447.keycuts.KeyCuts;

public class PotionPopUpPatches
{
	@SpirePatch(clz=PotionPopUp.class, method="updateInput")
	public static class UpdateInput
	{
		public static void Prefix(PotionPopUp self, Hitbox ___hbTop, Hitbox ___hbBot)
		{
			if (!KeyCuts.usePotionHotKeys())
				return;
			if (InputActionSet.selectCardActions[0].isJustPressed())
			{
				___hbTop.hovered = true;
				InputHelper.justClickedLeft = true;
			}
			else if (InputActionSet.selectCardActions[1].isJustPressed())
			{
				___hbBot.hovered = true;
				InputHelper.justClickedRight = true;
			}
		}
	}
}
