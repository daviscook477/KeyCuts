package test447.keycuts.patches.rewards;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.Loader;
import com.evacipated.cardcrawl.modthespire.lib.LineFinder;
import com.evacipated.cardcrawl.modthespire.lib.Matcher;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertLocator;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.input.InputActionSet;
import com.megacrit.cardcrawl.rewards.RewardItem;
import com.megacrit.cardcrawl.screens.CombatRewardScreen;
import java.io.File;
import java.lang.reflect.Modifier;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtBehavior;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import org.clapper.util.classutil.AndClassFilter;
import org.clapper.util.classutil.ClassFilter;
import org.clapper.util.classutil.ClassFinder;
import org.clapper.util.classutil.ClassInfo;
import org.clapper.util.classutil.ClassModifiersClassFilter;
import org.clapper.util.classutil.InterfaceOnlyClassFilter;
import org.clapper.util.classutil.NotClassFilter;
import test447.keycuts.KeyCuts;

public class RewardItemPatches
{
	private static boolean consumed[] = null;

	public static boolean isJustPressed(int slot) {
		if (consumed == null)
			consumed = new boolean[InputActionSet.selectCardActions.length];
		if (consumed[slot])
			return false;
		boolean result = InputActionSet.selectCardActions[slot].isJustPressed();
		if (result)
			consumed[slot] = true;
		return result;
	}

	@SpirePatch(clz= CombatRewardScreen.class, method="update")
	public static class CombatRewardScreenUpdate
	{
		public static void Prefix(CombatRewardScreen self)
		{
			if (consumed == null)
				consumed = new boolean[InputActionSet.selectCardActions.length];
			int i;
			for (i = 0; i < consumed.length; i++)
			{
				consumed[i] = false;
			}
		}
	}

	@SpirePatch(clz=RewardItem.class, method="update")
	public static class Update
	{
		@SpireInsertPatch(locator=UpdateLocator.class)
		public static void Insert(RewardItem self)
		{
			if (!KeyCuts.useCombatRewardHotKeys())
				return;
			int slot = AbstractDungeon.combatRewardScreen.rewards.indexOf(self);
			if (slot >= InputActionSet.selectCardActions.length)
				return;
			if (isJustPressed(slot))
			{
				CardCrawlGame.sound.playA("UI_CLICK_1", 0.1F);
				self.hb.clicked = true;
			}
		}
	}

	private static class UpdateLocator extends SpireInsertLocator {
		public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
			Matcher finalMatcher = new Matcher.MethodCallMatcher(Hitbox.class, "update");
			return new int[] {LineFinder.findInOrder(ctMethodToPatch, new ArrayList<>(), finalMatcher)[0] + 1};
		}
	}

	@SpirePatch(clz=CardCrawlGame.class, method=SpirePatch.CONSTRUCTOR)
	public static class RewardItemSubClassUpdate
	{
		public static void Raw(CtBehavior ctBehavior) throws NotFoundException, CannotCompileException
		{
			Collection<ClassInfo> foundClasses = getRewardItemSubClasses(ctBehavior);
			ClassPool pool = ctBehavior.getDeclaringClass().getClassPool();

			UpdateLocator locator = new UpdateLocator();
			String src = Update.class.getName() + ".Insert($0);";

			for (ClassInfo classInfo : foundClasses)
			{
				try
				{
					CtMethod update = null;
					CtClass ctClass = pool.get(classInfo.getClassName());
					try
					{
						update = ctClass.getDeclaredMethod("update");
					}
					catch (NotFoundException e)
					{
						// ignored
					}

					if (update != null)
					{
						int[] locs = locator.Locate(update);
						int i;
						for (i = 0; i < locs.length; i++)
						{
							update.insertAt(locs[i], src);
						}
					}
				}
				catch (NotFoundException | PatchingException e)
				{
					// ignored
				}
			}
		}
	}

	@SpirePatch(clz=RewardItem.class, method="render")
	public static class Render
	{
		public static void Postfix(RewardItem self, SpriteBatch sb)
		{
			if (!KeyCuts.showCombatRewardHotKeys())
				return;
			int slot = AbstractDungeon.combatRewardScreen.rewards.indexOf(self);
			if (slot >= InputActionSet.selectCardActions.length)
				return;
			float x = RewardItem.REWARD_ITEM_X + self.hb.width - 80.0f * Settings.scale;
			FontHelper.renderFontRightAligned(sb, FontHelper.buttonLabelFont, InputActionSet.selectCardActions[slot].getKeyString(),
					x, self.y, Settings.CREAM_COLOR);
		}
	}

	@SpirePatch(clz=CardCrawlGame.class, method=SpirePatch.CONSTRUCTOR)
	public static class RewardItemSubClassRender
	{
		public static void Raw(CtBehavior ctBehavior) throws NotFoundException, CannotCompileException
		{
			Collection<ClassInfo> foundClasses = getRewardItemSubClasses(ctBehavior);
			ClassPool pool = ctBehavior.getDeclaringClass().getClassPool();

			String src = Render.class.getName() + ".Postfix($0, $1);";

			for (ClassInfo classInfo : foundClasses)
			{
				try
				{
					CtMethod render = null;
					CtClass ctClass = pool.get(classInfo.getClassName());
					try
					{
						render = ctClass.getDeclaredMethod("render");
					}
					catch (NotFoundException e)
					{
						// ignored
					}

					if (render != null)
					{
						render.insertAfter(src);
					}
				}
				catch (NotFoundException e)
				{
					// ignored
				}
			}
		}
	}

	public static Collection<ClassInfo> getRewardItemSubClasses(CtBehavior ctBehavior) throws NotFoundException {
		ClassFinder finder = new ClassFinder();
		// This only searches mod files, as base game potions shouldn't show whatmod anyways
		finder.add(
				Arrays.stream(Loader.MODINFOS)
						.map(modInfo -> modInfo.jarURL)
						.filter(Objects::nonNull)
						.map(url -> {
							try {
								return url.toURI();
							} catch (URISyntaxException e) {
								return null;
							}
						})
						.filter(Objects::nonNull)
						.map(File::new)
						.collect(Collectors.toList())
		);

		ClassPool pool = ctBehavior.getDeclaringClass().getClassPool();

		ClassFilter filter =
				new AndClassFilter(
						new NotClassFilter(new InterfaceOnlyClassFilter()),
						new ClassModifiersClassFilter(Modifier.PUBLIC),
						new SuperClassFilter(pool, RewardItem.class)
				);
		Collection<ClassInfo> foundClasses = new ArrayList<>();
		finder.findClasses(foundClasses, filter);
		return foundClasses;
	}

	private static class SuperClassFilter implements ClassFilter
	{
		private ClassPool pool;
		private CtClass baseClass;

		public SuperClassFilter(ClassPool pool, Class<?> baseClass) throws NotFoundException
		{
			this.pool = pool;
			this.baseClass = pool.get(baseClass.getName());
		}

		@Override
		public boolean accept(ClassInfo classInfo, ClassFinder classFinder)
		{
			try {
				CtClass ctClass = pool.get(classInfo.getClassName());
				while (ctClass != null) {
					if (ctClass.equals(baseClass)) {
						return true;
					}
					ctClass = ctClass.getSuperclass();
				}
			} catch (NotFoundException ignored) {
			}

			return false;
		}
	}

}
