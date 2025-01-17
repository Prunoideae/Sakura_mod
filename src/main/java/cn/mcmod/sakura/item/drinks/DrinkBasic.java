package cn.mcmod.sakura.item.drinks;

import cn.mcmod.sakura.SakuraMain;
import cn.mcmod.sakura.item.ItemLoader;
import cn.mcmod.sakura.util.RecipesUtil;
import cn.mcmod.sakura.util.TagPropertyAccessor.TagPropertyInteger;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Optional.Interface;
import net.minecraftforge.fml.common.Optional.Method;
import toughasnails.api.stat.capability.ITemperature;
import toughasnails.api.stat.capability.IThirst;
import toughasnails.api.temperature.Temperature;
import toughasnails.api.temperature.TemperatureHelper;
import toughasnails.api.thirst.ThirstHelper;

@Interface(iface="toughasnails.api.thirst.IDrink", modid="toughasnails")
public class DrinkBasic extends ItemFood {
	private PotionEffect[][] effect;
	private int max_cups;
	
	public String[] subNames;

	
	public static final TagPropertyInteger cup_amount = new TagPropertyInteger("cup_amount");
	public static final TagPropertyInteger max_cup_amount = new TagPropertyInteger("max_cup_amount");
	public DrinkBasic(String name,int cups, String[] subNames, PotionEffect[][] effects) {
		super(1, 0.1f, false);
		this.setUnlocalizedName(SakuraMain.MODID+"."+name);
		this.setAlwaysEdible();
		this.setHasSubtypes(subNames!=null&&subNames.length > 0);
		this.setMaxStackSize(1);
		this.effect=effects!=null&&effects.length>0?effects:null;
		this.max_cups = cups;
		this.subNames = subNames!=null&&subNames.length > 0?subNames: null;
	}

	public int getMaxCup() {
		return this.max_cups;
	}
	
	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> list) {
		if(this.isInCreativeTab(tab))
			if(getSubNames()!=null)
			{
				for(int i = 0; i < getSubNames().length; i++){
						ItemStack stack =new ItemStack(this, 1, i);
						cup_amount.set(RecipesUtil.getItemTagCompound(stack), 0);
						max_cup_amount.set(RecipesUtil.getItemTagCompound(stack), max_cups);
						list.add(stack);
						}
			}
			else{
				ItemStack stack =new ItemStack(this);
				cup_amount.set(RecipesUtil.getItemTagCompound(stack), 0);
				max_cup_amount.set(RecipesUtil.getItemTagCompound(stack), max_cups);
				list.add(stack);
				}
	}

	public String[] getSubNames() {
		return subNames;
	}
	@Override
	public String getUnlocalizedName(ItemStack stack) {
		if(getSubNames()!=null) {
			String subName = stack.getMetadata() < getSubNames().length?"item."+getSubNames()[stack.getMetadata()]: "";
			return subName;
		}
		return this.getUnlocalizedName();
	}
	
	@Override
	public void onCreated(ItemStack stack, World worldIn, EntityPlayer playerIn) {
		cup_amount.set(RecipesUtil.getItemTagCompound(stack), 0);
		max_cup_amount.set(RecipesUtil.getItemTagCompound(stack), max_cups);
		super.onCreated(stack, worldIn, playerIn);
	}
	
	public PotionEffect[] getEffectList(ItemStack stack){
		return stack.getMetadata() < getAllEffectList().length?getAllEffectList()[stack.getMetadata()]: new PotionEffect[]{};
	}
	
	public PotionEffect[][] getAllEffectList() {
		return effect;
	}

	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn)
    {
        ItemStack itemstack = playerIn.getHeldItem(handIn);

        if (playerIn.canEat(true)||playerIn.isCreative())
        {
            playerIn.setActiveHand(handIn);
            return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemstack);
        }
        else
        {
            return new ActionResult<ItemStack>(EnumActionResult.FAIL, itemstack);
        }
    }
	 public EnumAction getItemUseAction(ItemStack stack)
	    {
	        return EnumAction.DRINK;
	    }
	 public ItemStack onItemUseFinish(ItemStack stack, World worldIn, EntityLivingBase entityLiving)
	    {
		 if (entityLiving instanceof EntityPlayer)
	        {
	            EntityPlayer entityplayer = (EntityPlayer)entityLiving;
	            entityplayer.getFoodStats().addStats(this, stack);
	            worldIn.playSound((EntityPlayer)null, entityplayer.posX, entityplayer.posY, entityplayer.posZ, SoundEvents.ENTITY_VILLAGER_YES, SoundCategory.PLAYERS, 0.5F, worldIn.rand.nextFloat() * 0.1F + 0.9F);
	            this.onFoodEaten(stack, worldIn, entityplayer);
	            entityplayer.addStat(StatList.getObjectUseStats(this));
	            if (entityplayer instanceof EntityPlayerMP)
	            {
	                CriteriaTriggers.CONSUME_ITEM.trigger((EntityPlayerMP)entityplayer, stack);
	            }
	            int cups = cup_amount.get(RecipesUtil.getItemTagCompound(stack));
	            if(cups <max_cup_amount.get(RecipesUtil.getItemTagCompound(stack))){
	            	cup_amount.add(RecipesUtil.getItemTagCompound(stack), 1);
	       		 System.out.println("cups:"+cup_amount.get(RecipesUtil.getItemTagCompound(stack)));
	            	return stack;
	            }else{
	            	cup_amount.set(RecipesUtil.getItemTagCompound(stack), max_cup_amount.get(RecipesUtil.getItemTagCompound(stack)));
	       		 return new ItemStack(ItemLoader.cup,1,0);
	            }
	        }
		 System.out.println("?cups:"+cup_amount.get(RecipesUtil.getItemTagCompound(stack)));
		 return new ItemStack(ItemLoader.cup,1,0);
	    }

	@Override
	public boolean showDurabilityBar(ItemStack stack) {
		return cup_amount.get(RecipesUtil.getItemTagCompound(stack))>0;
	} 
	 
	 @Override
	public double getDurabilityForDisplay(ItemStack stack) {
		return (double)cup_amount.get(RecipesUtil.getItemTagCompound(stack))/(double)max_cup_amount.get(RecipesUtil.getItemTagCompound(stack));
	}
	 
	@Override
	protected void onFoodEaten(ItemStack stack, World worldIn, EntityPlayer player) {
		if(getEffectList(stack)!=null&&getEffectList(stack).length>0){
			for(PotionEffect effect1 : getEffectList(stack)){
					if (effect1 != null && effect1.getPotion() != null) {
						Potion por = effect1.getPotion();
						if (por == null)
							continue;
						int amp = effect1.getAmplifier();
						int dur = effect1.getDuration();
						if (player.isPotionActive(effect1.getPotion())) {
							PotionEffect check = player.getActivePotionEffect(por);
							dur += check.getDuration();
							amp ++;
						}
						player.addPotionEffect(new PotionEffect(effect1.getPotion(), dur, amp));
					}
			}
		}
	}
	

	
	  @Method(modid="toughasnails")
	  public void drink(EntityLivingBase entity)
	  {
	    EntityPlayer player = (EntityPlayer)entity;
	    IThirst thirst = ThirstHelper.getThirstData(player);
	    
	    thirst.addStats(getThirst(), getHydration());
	  }
	  
	  @Method(modid="toughasnails")
	  public int getThirst()
	  {
	    return 8;
	  }
	  
	  @Method(modid="toughasnails")
	  public float getHydration()
	  {
	    return 0.6F;
	  }
	  
	  @Method(modid="toughasnails")
	  public float getPoisonChance()
	  {
	    return 0.0F;
	  }
	  @Method(modid="toughasnails")
	  public void changeTemperature(EntityLivingBase entity)
	  {
	    EntityPlayer player = (EntityPlayer)entity;
	    ITemperature temperature = TemperatureHelper.getTemperatureData(player);
	    if (temperature.getTemperature().getRawValue() <= 10) {
	      temperature.setTemperature(new Temperature(temperature.getTemperature().getRawValue() + 1));
	    } else if (temperature.getTemperature().getRawValue() >= 14) {
	      temperature.setTemperature(new Temperature(temperature.getTemperature().getRawValue() - 1));
	    }
	  }
}
