package net.torocraft.dailies;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class DailiesContainer extends Container {

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
	
	public DailiesContainer(EntityPlayer player, BaileyInventory baileyInventory, World world) {
		this.baileyInventory = baileyInventory;
		this.baileyInventory.openInventory(player);
		
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
		
		addSlotToContainer(new SlotOutput(baileyInventory, 3, OUTPUT_ITEM_XPOS, OUTPUT_ITEM_YPOS));
	}
	
	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int index) {
		Slot slot = (Slot)this.inventorySlots.get(index);
        if(slot == null || !slot.getHasStack()) {
        	return ItemStack.field_190927_a;
        }
        
        ItemStack sourceStack = slot.getStack();
        ItemStack copyOfSourceStack = sourceStack.copy();
        
        if(indexIsForAVanillaSlot(index)) {
        	if(!mergeItemStack(sourceStack, BAILEY_INVENTORY_FIRST_SLOT_INDEX, BAILEY_INVENTORY_FIRST_SLOT_INDEX + BAILEY_INVENTORY_SLOT_COUNT, false)) {
        		return ItemStack.field_190927_a;
        	}
        } else if(indexIsForABaileyInventorySlot(index) || indexIsForBaileyOutputSlot(index)) {
        	if(!mergeStackFromBaileyToPlayer(sourceStack)) {
        		return ItemStack.field_190927_a;
        	}
        } else {
        	return ItemStack.field_190927_a;
        }
        
        if(sourceStack.func_190916_E() == 0) {
        	slot.putStack(ItemStack.field_190927_a);
        } else {
        	slot.onSlotChanged();
        }
        
        slot.func_190901_a(player, sourceStack);
        return copyOfSourceStack;
	}

	private boolean mergeStackFromBaileyToPlayer(ItemStack sourceStack) {
		return mergeItemStack(sourceStack, VANILLA_FIRST_SLOT_INDEX, VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT, false);
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
	public boolean canInteractWith(EntityPlayer playerIn) {
		return true;
	}
	
	@Override
	public void onContainerClosed(EntityPlayer player) {
		super.onContainerClosed(player);
		this.baileyInventory.closeInventory(player);
	}
	
	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		this.baileyInventory.checkForReward();
	}
	
	public class SlotOutput extends Slot {

		public SlotOutput(IInventory inventoryIn, int index, int xPosition, int yPosition) {
			super(inventoryIn, index, xPosition, yPosition);
		}
		
		@Override
		public boolean isItemValid(ItemStack stack) {
			return false;
		}
	}
}
