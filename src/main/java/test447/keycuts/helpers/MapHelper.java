package test447.keycuts.helpers;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.dungeons.TheEnding;
import com.megacrit.cardcrawl.map.MapRoomNode;
import java.util.ArrayList;
import test447.keycuts.patches.DungeonMapPatches;
import test447.keycuts.patches.MapRoomNodePatches;

public class MapHelper
{
	// method from https://github.com/ForgottenArbiter/CommunicationMod/blob/a15e8a50ad6d9c1af2e9168bd72d0e07b21e6c7b/src/main/java/communicationmod/ChoiceScreenUtils.java
	// published under MIT license
	// (c) ForgottenArbiter 2020
	public static ArrayList<MapRoomNode> getMapScreenNodeChoices() {
		ArrayList<MapRoomNode> choices = new ArrayList<>();
		MapRoomNode currMapNode = AbstractDungeon.getCurrMapNode();
		ArrayList<ArrayList<MapRoomNode>> map = AbstractDungeon.map;
		if(!AbstractDungeon.firstRoomChosen) {
			for(MapRoomNode node : map.get(0)) {
				if (node.hasEdges()) {
					choices.add(node);
				}
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
		return choices;
	}

	// method from https://github.com/ForgottenArbiter/CommunicationMod/blob/a15e8a50ad6d9c1af2e9168bd72d0e07b21e6c7b/src/main/java/communicationmod/ChoiceScreenUtils.java
	// published under MIT license
	// (c) ForgottenArbiter 2020
	public static void makeMapChoice(int choice) {
		MapRoomNode currMapNode = AbstractDungeon.getCurrMapNode();
		if(currMapNode.y == 14 || (AbstractDungeon.id.equals(TheEnding.ID) && currMapNode.y == 2)) {
			if(choice == 0) {
				DungeonMapPatches.Update.doBossHover = true;
				return;
			} else {
				throw new IndexOutOfBoundsException("Only a boss node can be chosen here.");
			}
		}
		ArrayList<MapRoomNode> nodeChoices = getMapScreenNodeChoices();
		MapRoomNodePatches.Update.hoverNode = nodeChoices.get(choice);
		MapRoomNodePatches.Update.doHover = true;
		AbstractDungeon.dungeonMapScreen.clicked = true;
	}
}
