package test447.keycuts.patches.cards;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.ByRef;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertLocator;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.input.InputActionSet;
import com.megacrit.cardcrawl.screens.CardRewardScreen;
import javassist.CtBehavior;
import javassist.bytecode.BadBytecode;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.CodeIterator;
import javassist.bytecode.MethodInfo;
import test447.keycuts.KeyCuts;

public class CardRewardScreenPatches
{
	@SpirePatch(clz=CardRewardScreen.class, method="cardSelectUpdate")
	public static class CardSelectUpdate
	{
		@SpireInsertPatch(locator=Locator.class, localvars={"hoveredCard"})
		public static void Insert(CardRewardScreen self, @ByRef AbstractCard[] hoveredCard) {
			if (!KeyCuts.useCardRewardHotKeys())
				return;
			int i;
			for (i = 0; i < Math.min(InputActionSet.selectCardActions.length, self.rewardGroup.size()); i++) {
				if (InputActionSet.selectCardActions[i].isJustPressed()) {
					hoveredCard[0] = self.rewardGroup.get(i);
					CardCrawlGame.sound.playV("CARD_OBTAIN", 0.4f);
					hoveredCard[0].hb.clicked = true;
				}
			}
		}

		private static class Locator extends SpireInsertLocator
		{
			public static final int[] ALOAD = new int[] {0x19, 0x2a, 0x2b, 0x2c, 0x2d};

			// locates the first null check in the method
			public int[] Locate(CtBehavior ctMethodToPatch) throws BadBytecode, PatchingException
			{
				MethodInfo methodInfo = ctMethodToPatch.getMethodInfo();
				CodeAttribute codeAttribute = methodInfo.getCodeAttribute();
				CodeIterator iterator = codeAttribute.iterator();
				iterator.begin();
				while (iterator.hasNext())
				{
					int index = iterator.next();
					int byteValue = iterator.byteAt(index);
					if (byteValue == 0xC6) // ifnull
					{
						// expecting a load instruction before the null check
						// we want to insert before the load instruction
						int prevIndex = index  - 1;
						int prevByteValue = iterator.byteAt(prevIndex);
						boolean found = false;
						int i;
						for (i = 0; i < ALOAD.length; i++)
						{
							if (ALOAD[i] == prevByteValue)
								found = true;
						}
						if (!found)
							throw new PatchingException("\"ifnull\" (0xC6) bytecode was not preceded by an \"alod\" bytecode");
						return new int[] {methodInfo.getLineNumber(prevIndex)};
					}
				}
				throw new PatchingException("Could not find \"ifnull\" (0xC6) bytecode");
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
