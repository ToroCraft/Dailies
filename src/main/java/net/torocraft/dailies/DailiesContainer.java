package net.torocraft.dailies;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.torocraft.dailies.gui.MenuRegistryHandler;

public class DailiesContainer extends AbstractContainerMenu {

	private final int HOTBAR_SLOT_COUNT = 9;
	private final int INVENTORY_ROW_COUNT = 3;
	private final int INVENTORY_COLUMN_COUNT = 9;
	private final int SUBMIT_ITEM_ROW_COUNT = 1;
	private final int SUBMIT_ITEM_COLUMN_COUNT = 3;
	
	private final int VANILLA_SLOT_COUNT = HOTBAR_SLOT_COUNT + (INVENTORY_COLUMN_COUNT * INVENTORY_ROW_COUNT);
	private final int BAILEY_INVENTORY_SLOT_COUNT = 3;
	
	private final int VANILLA_FIRST_SLOT_INDEX = 0;
	private final int BAILEY_INVENTORY_FIRST_SLOT_INDEX = VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT;
	
	private final int SLOT_X_SPACING = 18;
    private final int SLOT_Y_SPACING = 18;
	
    private final int HOTBAR_XPOS = 8;
	private final int HOTBAR_YPOS = 106;
	
	private final int INVENTORY_XPOS = 8;
	private final int INVENTORY_YPOS = 48;;
	
	private final int SUBMIT_ITEM_XPOS = 30;
	private final int SUBMIT_ITEM_YPOS = 17;
	
	private final int OUTPUT_ITEM_XPOS = 117;
	private final int OUTPUT_ITEM_YPOS = 17;
	
	private final BaileyInventory baileyInventory;

	public DailiesContainer(int id, Inventory playerInventory) {
		this(id, playerInventory, new BaileyInventory());
	}

	public DailiesContainer(int id, Inventory playerInventory, BaileyInventory baileyInventory) {
		super(MenuRegistryHandler.DAILIES_CONTAINER.get(), id);
		this.baileyInventory = baileyInventory;
		this.baileyInventory.startOpen(playerInventory.player);
		for (int x = 0; x < HOTBAR_SLOT_COUNT; x++) {
			addSlot(new Slot(playerInventory, x, HOTBAR_XPOS + SLOT_X_SPACING * x, HOTBAR_YPOS));
		}
		for (int x = 0; x < INVENTORY_ROW_COUNT; x++) {
			for (int y = 0; y < INVENTORY_COLUMN_COUNT; y++) {
				int slotNumber = HOTBAR_SLOT_COUNT + x * INVENTORY_COLUMN_COUNT + y;
				int xPos = INVENTORY_XPOS + y * SLOT_X_SPACING;
				int yPos = INVENTORY_YPOS + x * SLOT_Y_SPACING;
				addSlot(new Slot(playerInventory, slotNumber,  xPos, yPos));
			}
		}
		for (int x = 0; x < SUBMIT_ITEM_ROW_COUNT; x++) {
			for(int y = 0; y < SUBMIT_ITEM_COLUMN_COUNT; y++) {
				int slotNumber = x * SUBMIT_ITEM_COLUMN_COUNT + y;
				int xPos = SUBMIT_ITEM_XPOS + y * SLOT_X_SPACING;
				int yPos = SUBMIT_ITEM_YPOS + x * SLOT_Y_SPACING;
				addSlot(new Slot(baileyInventory, slotNumber, xPos, yPos));
			}
		}
		addSlot(new SlotOutput(baileyInventory, 3, OUTPUT_ITEM_XPOS, OUTPUT_ITEM_YPOS));
	}
	
	@Override
	public ItemStack quickMoveStack(Player player, int index) {
		Slot slot = this.slots.get(index);
		if(slot == null || !slot.hasItem()) {
			return ItemStack.EMPTY;
		}
		ItemStack sourceStack = slot.getItem();
		ItemStack copyOfSourceStack = sourceStack.copy();
		if(indexIsForAVanillaSlot(index)) {
			if(!moveItemStackTo(sourceStack, BAILEY_INVENTORY_FIRST_SLOT_INDEX, BAILEY_INVENTORY_FIRST_SLOT_INDEX + BAILEY_INVENTORY_SLOT_COUNT, false)) {
				return ItemStack.EMPTY;
			}
		} else if(indexIsForABaileyInventorySlot(index) || indexIsForBaileyOutputSlot(index)) {
			if(!mergeStackFromBaileyToPlayer(sourceStack)) {
				return ItemStack.EMPTY;
			}
		} else {
			return ItemStack.EMPTY;
		}
		if(sourceStack.getCount() == 0) {
			slot.set(ItemStack.EMPTY);
		} else {
			slot.setChanged();
		}
		slot.onTake(player, sourceStack);
		return copyOfSourceStack;
	}

	private boolean mergeStackFromBaileyToPlayer(ItemStack sourceStack) {
		return moveItemStackTo(sourceStack, VANILLA_FIRST_SLOT_INDEX, VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT, false);
	}

	private boolean indexIsForAVanillaSlot(int index) {
		return index >= VANILLA_FIRST_SLOT_INDEX && index < VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT;
	}
	
	private boolean indexIsForABaileyInventorySlot(int index) {
		return index >= BAILEY_INVENTORY_FIRST_SLOT_INDEX && index < BAILEY_INVENTORY_FIRST_SLOT_INDEX + BAILEY_INVENTORY_SLOT_COUNT;
	}
	
	private boolean indexIsForBaileyOutputSlot(int index) {
		return index == BAILEY_INVENTORY_FIRST_SLOT_INDEX + BAILEY_INVENTORY_SLOT_COUNT;
	}
	
	@Override
	public boolean stillValid(Player player) {
		return true;
	}
	
	@Override
	public void removed(Player player) {
		super.removed(player);
		this.baileyInventory.stopOpen(player);
	}
	
	@Override
	public void broadcastChanges() {
		super.broadcastChanges();
		this.baileyInventory.checkForReward();
	}

	public class SlotOutput extends Slot {

		public SlotOutput(Container inventoryIn, int index, int xPosition, int yPosition) {
			super(inventoryIn, index, xPosition, yPosition);
		}
		
		@Override
		public boolean mayPlace(ItemStack stack) {
			return false;
		}
	}
}
