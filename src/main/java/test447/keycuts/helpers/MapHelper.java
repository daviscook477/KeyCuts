package test447.keycuts.helpers;

import com.evacipated.cardcrawl.modthespire.Loader;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.map.MapRoomNode;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import java.util.ArrayList;

public class MapHelper
{
	// cache based on when the current room changes for performance reasons
	private static AbstractRoom lastCurrentRoom = null;
	private static ArrayList<MapRoomNode> nodeChoiceCache = new ArrayList<>();

	public static boolean isDownfallMap()
	{
		if (!Loader.isModLoaded("downfall"))
			return false;
		boolean result = false;
		try
		{
			Class evilModeCharacterSelect = Class.forName("downfall.patches.EvilModeCharacterSelect");
			result = evilModeCharacterSelect.getDeclaredField("evilMode").getBoolean(null);
		}
		catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException e)
		{
			// rethrow as unchecked exception since silently failing isn't super useful for debugging
			if (Loader.DEBUG)
			{
				throw new RuntimeException(e);
			}
			// otherwise silently fail since letting a user continue with mouse clicks is better than crashing
			// because hotkeys aren't working
		}
		return result;
	}

	public static void addVanillaFirstRooms(ArrayList<MapRoomNode> choices)
	{
		ArrayList<ArrayList<MapRoomNode>> map = AbstractDungeon.map;
		for(MapRoomNode node : map.get(0)) {
			if (node.hasEdges()) {
				choices.add(node);
			}
		}
	}

	public static void addDownfallFirstRooms(ArrayList<MapRoomNode> choices)
	{
		ArrayList<ArrayList<MapRoomNode>> map = AbstractDungeon.map;
		for(MapRoomNode node : map.get(map.size() - 1)) {
			if (node.hasEdges()) {
				choices.add(node);
			}
		}
	}

	// method from https://github.com/ForgottenArbiter/CommunicationMod/blob/a15e8a50ad6d9c1af2e9168bd72d0e07b21e6c7b/src/main/java/communicationmod/ChoiceScreenUtils.java
	// published under MIT license
	// (c) ForgottenArbiter 2020
	public static ArrayList<MapRoomNode> getMapScreenNodeChoices() {
		if (lastCurrentRoom != null && lastCurrentRoom == AbstractDungeon.getCurrRoom())
			return nodeChoiceCache;
		ArrayList<MapRoomNode> choices = new ArrayList<>();
		MapRoomNode currMapNode = AbstractDungeon.getCurrMapNode();
		ArrayList<ArrayList<MapRoomNode>> map = AbstractDungeon.map;
		if(!AbstractDungeon.firstRoomChosen) {
			if (isDownfallMap())
			{
				addDownfallFirstRooms(choices);
			}
			else
			{
				addVanillaFirstRooms(choices);
			}
		} else {
			for (ArrayList<MapRoomNode> rows : map) {
				for (MapRoomNode node : rows) {
					if (node.hasEdges()) {
						boolean normalConnection = currMapNode.isConnectedTo(node);
						boolean wingedConnection = currMapNode.wingedIsConnectedTo(node);
						if (normalConnection || wingedConnection) {
							choices.add(node);
						}
					}
				}
			}
		}
		nodeChoiceCache = choices;
		return choices;
	}
}
