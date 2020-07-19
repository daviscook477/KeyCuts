package test447.keycuts.patches.campfire;

import basemod.ReflectionHacks;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.rooms.CampfireUI;
import com.megacrit.cardcrawl.ui.campfire.AbstractCampfireOption;
import java.util.ArrayList;

public class CampfireUIPatches
{
	public static final String SLOT_REPLACEMENT_INDICATOR = "!M!";

	@SpirePatch(clz=CampfireUI.class, method="initializeButtons")
	public static class InitializeButtons
	{
		public static void Postfix(CampfireUI self)
		{
			ArrayList<AbstractCampfireOption> buttons = (ArrayList<AbstractCampfireOption>) ReflectionHacks.getPrivate(self, CampfireUI.class, "buttons");
			for (AbstractCampfireOption option : buttons)
			{
				String label = (String) ReflectionHacks.getPrivate(option, AbstractCampfireOption.class, "label");
				label += SLOT_REPLACEMENT_INDICATOR;
				ReflectionHacks.setPrivate(option, AbstractCampfireOption.class, "label", label);
			}
		}
	}
}
