package test447.keycuts.patches.events;

import basemod.ReflectionHacks;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.events.GenericEventDialog;
import com.megacrit.cardcrawl.helpers.input.InputActionSet;
import test447.keycuts.KeyCuts;

public class GenericEventDialogPatches
{
	@SpirePatch(clz=GenericEventDialog.class, method="update")
	public static class Update
	{
		public static void Prefix(GenericEventDialog self)
		{
			if (!KeyCuts.useDialogHotKeys())
				return;
			boolean show = (boolean) ReflectionHacks.getPrivate(self, GenericEventDialog.class, "show");
			int i;
			if (show)
			{
				for (i = 0; i < Math.min(InputActionSet.selectCardActions.length, self.optionList.size()); i++)
				{
					if (InputActionSet.selectCardActions[i].isJustPressed() && GenericEventDialog.waitForInput && !self.optionList.get(i).isDisabled)
					{
						GenericEventDialog.selectedOption = i;
						self.optionList.get(i).pressed = false;
						GenericEventDialog.waitForInput = false;
					}
				}
			}
		}
	}
}
