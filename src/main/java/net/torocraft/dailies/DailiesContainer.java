package net.torocraft.dailies;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.world.World;
import net.minecraft.inventory.Slot;
import net.torocraft.dailies.entities.EntityBailey;


public class DailiesContainer extends Container {

	private final int HOTBAR_SLOT_COUNT = 9;
	private final int INVENTORY_ROW_COUNT = 3;
	private final int INVENTORY_COLUMN_COUNT = 9;
	private final int SUBMIT_ITEM_ROW_COUNT = 1;
	private final int SUBMIT_ITEM_COLUMN_COUNT = 3;
	
	private final int SLOT_X_SPACING = 18;
    private final int SLOT_Y_SPACING = 18;
	
    private final int HOTBAR_XPOS = 8;
	private final int HOTBAR_YPOS = 106;
	
	private final int INVENTORY_XPOS = 8;
	private final int INVENTORY_YPOS = 48;;
	
	private final int SUBMIT_ITEM_XPOS = 30;
	private final int SUBMIT_ITEM_YPOS = 17;
	
	private final BaileyInventory baileyInventory;
	
	public DailiesContainer(EntityPlayer player, EntityBailey bailey, World world) {
		this.baileyInventory = new BaileyInventory();
		
		for (int x = 0; x < HOTBAR_SLOT_COUNT; x++) {
			addSlotToContainer(new Slot(player.inventory, x, HOTBAR_XPOS + SLOT_X_SPACING * x, HOTBAR_YPOS));
		}
		
		for (int x = 0; x < INVENTORY_ROW_COUNT; x++) {
			for (int y = 0; y < INVENTORY_COLUMN_COUNT; y++) {
				int slotNumber = HOTBAR_SLOT_COUNT + x * INVENTORY_COLUMN_COUNT + y;
				int xPos = INVENTORY_XPOS + y * SLOT_X_SPACING;
				int yPos = INVENTORY_YPOS + x * SLOT_Y_SPACING;
				addSlotToContainer(new Slot(player.inventory, slotNumber,  xPos, yPos));
			}
		}
		
		for (int x = 0; x < SUBMIT_ITEM_ROW_COUNT; x++) {
			for(int y = 0; y < SUBMIT_ITEM_COLUMN_COUNT; y++) {
				int slotNumber = x * SUBMIT_ITEM_COLUMN_COUNT + y; 
				int xPos = SUBMIT_ITEM_XPOS + y * SLOT_X_SPACING;
				int yPos = SUBMIT_ITEM_YPOS + x * SLOT_Y_SPACING;
				addSlotToContainer(new Slot(baileyInventory, slotNumber, xPos, yPos));
			}
		}
	}
	
	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return true;
	}
	
	@Override
	public void onContainerClosed(EntityPlayer player) {
		super.onContainerClosed(player);
		this.baileyInventory.closeInventory(player);
	}

}
