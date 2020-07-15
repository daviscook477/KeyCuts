package test447.keycuts.patches;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.ByRef;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.input.InputActionSet;
import com.megacrit.cardcrawl.screens.CardRewardScreen;
import test447.keycuts.KeyCuts;

public class CardRewardScreenPatches
{
	@SpirePatch(clz=CardRewardScreen.class, method="cardSelectUpdate")
	public static class CardSelectUpdate
	{
		@SpireInsertPatch(loc=257, localvars={"hoveredCard"})
		public static void Insert(CardRewardScreen self, @ByRef AbstractCard[] hoveredCard)
		{
			if (!KeyCuts.useCardRewardHotKeys())
				return;
			int i;
			for (i = 0; i < Math.min(InputActionSet.selectCardActions.length, self.rewardGroup.size()); i++)
			{
				if (InputActionSet.selectCardActions[i].isJustPressed())
				{
					hoveredCard[0] = self.rewardGroup.get(i);
					CardCrawlGame.sound.playV("CARD_OBTAIN", 0.4f);
					hoveredCard[0].hb.clicked = true;
				}
			}
		}
	}

	@SpirePatch(clz=CardRewardScreen.class, method="renderCardReward")
	public static class RenderCardReward
	{
		public static void Postfix(CardRewardScreen self, SpriteBatch sb)
		{
			if (!KeyCuts.showCardRewardHotKeys())
				return;
			int i;
			for (i = 0; i < Math.min(InputActionSet.selectCardActions.length, self.rewardGroup.size()); i++)
			{
				AbstractCard card = self.rewardGroup.get(i);
				float height = AbstractCard.IMG_HEIGHT * card.drawScale / 2.0F;
				float topOfCard = card.current_y + height;
				float textSpacing = 50.0F * Settings.scale;
				float textY = topOfCard + textSpacing;
				FontHelper.renderFontCentered(sb, FontHelper.buttonLabelFont, InputActionSet.selectCardActions[i].getKeyString(),
						card.current_x, textY, Settings.CREAM_COLOR);
			}
		}
	}
}
