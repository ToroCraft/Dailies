package net.torocraft.dailies;

import java.util.Set;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.torocraft.dailies.capabilities.CapabilityDailiesHandler;
import net.torocraft.dailies.capabilities.IDailiesCapability;
import net.torocraft.dailies.quests.DailyQuest;
import net.torocraft.dailies.quests.Reward;
import scala.actors.threadpool.Arrays;

public class BaileyInventory extends TileEntity implements IInventory, ITickable {

	public static final int SUBMIT_ITEM_COUNT = 3;
	public static final int OUTPUT_ITEM_COUNT = 1;
	
	public static final int REWARD_OUTPUT_INDEX = 3;
	
	public static final int TOTAL_SLOT_COUNT = SUBMIT_ITEM_COUNT + OUTPUT_ITEM_COUNT;
	
	private ItemStack[] itemStacks = new ItemStack[TOTAL_SLOT_COUNT];
	
	private EntityPlayer player = null;
	private IDailiesCapability playerDailiesCapability = null;
	private Set<DailyQuest> acceptedQuests;
	
	private boolean rewardAcheieved = false;
	private boolean rewardTaken = false;
	private int rewardInputIndex;
	private int rewardInputCount;
	
	@Override
	public String getName() {
		return "Bailey's Inventory";
	}

	@Override
	public boolean hasCustomName() {
		return false;
	}

	@Override
	public ITextComponent getDisplayName() {
		return new TextComponentString(this.getName());
	}

	@Override
	public int getSizeInventory() {
		return itemStacks.length;
	}

	@Override
	public ItemStack getStackInSlot(int index) {
		return itemStacks[index];
	}

	@Override
	public ItemStack decrStackSize(int index, int count) {
		ItemStack slotStack = getStackInSlot(index);
		if(slotStack == null) {
			return null;
		}
		
		ItemStack stackRemoved;
		if(slotStack.stackSize <= count) {
			stackRemoved = slotStack;
			setInventorySlotContents(index, null);
		} else {
			stackRemoved = slotStack.splitStack(count);
			if(slotStack.stackSize == 0) {
				setInventorySlotContents(index, null);
			}
		}
		
		if(index == REWARD_OUTPUT_INDEX) {
			rewardTaken = true;
		}
		
		markDirty();
		
		return stackRemoved;
	}

	@Override
	public ItemStack removeStackFromSlot(int index) {
		ItemStack itemStack = getStackInSlot(index);
		if(itemStack != null) {
			setInventorySlotContents(index, null);
		}
		
		return itemStack;
	}

	@Override
	public void setInventorySlotContents(int index, ItemStack stack) {
		itemStacks[index] = stack;
		if(stack != null && stack.stackSize > getInventoryStackLimit()) {
			stack.stackSize = getInventoryStackLimit();
		}
		markDirty();
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return true;
	}

	@Override
	public void openInventory(EntityPlayer player) {
		this.player = player;
		this.playerDailiesCapability = player.getCapability(CapabilityDailiesHandler.DAILIES_CAPABILITY, null);
	}

	@Override
	public void closeInventory(EntityPlayer player) {
		
		for(int x = 0; x < getSizeInventory(); x++) {
			if(itemStacks[x] != null) {
				player.dropItem(itemStacks[x], false);
			}
		}
	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		return true;
	}

	@Override
	public int getField(int id) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setField(int id, int value) {
		// TODO Auto-generated method stub
	}

	@Override
	public int getFieldCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void clear() {
		Arrays.fill(itemStacks, null);
	}

	@Override
	public void update() {
		checkForReward();
	}
	
	private void checkForReward() {
		if(playerDailiesCapability == null) {
			return;
		}
		
		if(rewardTaken) {
			decrStackSize(rewardInputIndex, rewardInputCount);
			rewardTaken = false;
		}
		
		rewardAcheieved = false;
		for(int x = 0; x < getSizeInventory() - 1; x++) {
			if(itemStacks[x] != null) {
				checkForReward(itemStacks[x], x);
			}
		}
		
		if(!rewardAcheieved) {
			removeStackFromSlot(REWARD_OUTPUT_INDEX);
		}
	}
	
	private void checkForReward(ItemStack stack, int index) {
		acceptedQuests = playerDailiesCapability.getAcceptedQuests();
		
		int id;
		for(DailyQuest quest : acceptedQuests) {
			id = Item.getIdFromItem(stack.getItem());
			if(id == quest.reward.type && stack.stackSize >= quest.target.quantity) { 
				buildReward(quest.reward);
				rewardAcheieved = true;
				rewardInputIndex = index;
				rewardInputCount = quest.target.quantity;
			}
		}
	}
	
	private void buildReward(Reward reward) {
		ItemStack rewardStack = new ItemStack(Item.getItemById(reward.type));
		rewardStack.stackSize = reward.quantity;
		setInventorySlotContents(REWARD_OUTPUT_INDEX, rewardStack);
	}
}
