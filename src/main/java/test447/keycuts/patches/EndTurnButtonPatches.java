package test447.keycuts.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.ui.buttons.EndTurnButton;

public class EndTurnButtonPatches
{
	@SpirePatch(clz=EndTurnButton.class, method="update")
	public static class Update
	{
		public static boolean dontEndTurn = false;

		public static SpireReturn Prefix(EndTurnButton self)
		{
			boolean lastDontEndTurn = dontEndTurn;
			dontEndTurn = false;
			if (lastDontEndTurn)
				return SpireReturn.Return(null);
			else
				return SpireReturn.Continue();
		}
	}
}
