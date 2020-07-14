package test447.keycuts.patches;

import basemod.ReflectionHacks;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.events.RoomEventDialog;
import com.megacrit.cardcrawl.helpers.input.InputActionSet;
import test447.keycuts.KeyCuts;

public class RoomEventDialogPatches
{
	@SpirePatch(clz=RoomEventDialog.class, method="update")
	public static class Update
	{
		public static void Prefix(RoomEventDialog self)
		{
			if (!KeyCuts.useDialogHotKeys())
				return;
			boolean show = (boolean) ReflectionHacks.getPrivate(self, RoomEventDialog.class, "show");
			int i;
			if (show)
			{
				for (i = 0; i < Math.min(InputActionSet.selectCardActions.length, RoomEventDialog.optionList.size()); i++)
				{
					if (InputActionSet.selectCardActions[i].isJustPressed() && RoomEventDialog.waitForInput)
					{
						RoomEventDialog.selectedOption = i;
						RoomEventDialog.optionList.get(i).pressed = false;
						RoomEventDialog.waitForInput = false;
					}
				}
			}
		}
	}
}
