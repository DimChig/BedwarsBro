package com.dimchig.bedwarsbro.hints;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;

import com.dimchig.bedwarsbro.ChatSender;
import com.dimchig.bedwarsbro.Main;
import com.dimchig.bedwarsbro.Main.CONFIG_MSG;
import com.dimchig.bedwarsbro.hints.BWItemsHandler.BWItemColor;
import com.dimchig.bedwarsbro.hints.BWItemsHandler.BWItemType;

import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class BWBed {
	public int part1_posX;
	public int part1_posY;
	public int part1_posZ;
	public int part2_posX;
	public int part2_posY;
	public int part2_posZ;
	
	public class DefenceLayer {
		public int index;
		public BWItemType type;
		public float percentage;
		public ArrayList<BlockPos> arr;
		public int empty_space_cnt;
		
		public DefenceLayer(ArrayList<BlockPos> arr, int index, BWItemType type, float percentage, int empty_space_cnt) {
			this.index = index;
			this.type = type;
			this.percentage = percentage;
			this.arr = arr;
			this.empty_space_cnt = empty_space_cnt;
		}
	}
	
	public ArrayList<DefenceLayer> defence;
	public BWItemColor color;
	public BWBed(int part1_posX, int part1_posY, int part1_posZ, int part2_posX, int part2_posY, int part2_posZ) {
		this.part1_posX = part1_posX;
		this.part1_posY = part1_posY;
		this.part1_posZ = part1_posZ;
		this.part2_posX = part2_posX;
		this.part2_posY = part2_posY;
		this.part2_posZ = part2_posZ;
	}
	
	public void showLayers(final World world) throws Exception {
		if (this.defence.size() == 0) return;
		
		class Pop {
			public BlockPos pos;
			public IBlockState state;
			public Pop(BlockPos pos, IBlockState state) {
				this.pos = pos;
				this.state = state;
			}	
		}
		final ArrayList<Pop> pops = new ArrayList<Pop>();
		
		//hide all
		for (DefenceLayer layer: this.defence) {
			for (BlockPos pos: layer.arr) {
				pops.add(new Pop(pos, world.getBlockState(pos)));
				setBlock(world, pos, Blocks.air);
			}
		}

		//show in delay
		
		final int delay = Main.getConfigInt(CONFIG_MSG.BED_SCANNER_ANIMATION_DELAY);
		
		for (final DefenceLayer layer: this.defence) {
			new java.util.Timer().schedule( 
			        new java.util.TimerTask() {
			            @Override
			            public void run() {
			        			for (BlockPos pos: layer.arr) {
			        				for (Pop p: pops) {
			        					if (p.pos.getX() == pos.getX() && p.pos.getY() == pos.getY() && p.pos.getZ() == pos.getZ()) {
			        						world.updateBlockTick(pos, world.getBlockState(pos).getBlock(), 0, 1);
			        						if (world.getBlockState(pos).getBlock().getLocalizedName().contains("air")) {
			        							setBlock(world, pos, p.state);
			        						}
			        					}
				        			}
			        			}
			            }
			        }, 
			        delay * (layer.index + 1)
			);
		}
	}
	
	public void setBlock(World world, BlockPos pos, Block block) {
		setBlock(world, pos, block.getDefaultState());
	}
	
	public void setBlock(World world, BlockPos pos, IBlockState state) {
		world.setBlockState(pos, state);
	}
	
	public void scanDefence(World world) {
		this.defence = new ArrayList<DefenceLayer>();
	
		/*IBlockState new_block_state = Blocks.emerald_block.getDefaultState();
		Minecraft.getMinecraft().theWorld.setBlockState(new BlockPos(this.part1_posX, this.part1_posY + 6, this.part1_posZ), new_block_state);*/
		
		ArrayList<BlockPos> arr_conected = new ArrayList<BlockPos>();
		arr_conected.add(new BlockPos(this.part1_posX, this.part1_posY, this.part1_posZ));
		arr_conected.add(new BlockPos(this.part2_posX, this.part2_posY, this.part2_posZ));
		
		ArrayList<BlockPos> arr_ignore = new ArrayList<BlockPos>();
		arr_ignore.addAll(arr_conected);
		
		EnumDyeColor[] dyeColors = new EnumDyeColor[]{EnumDyeColor.RED, EnumDyeColor.YELLOW, EnumDyeColor.LIME, EnumDyeColor.LIGHT_BLUE};
		
		int min_y = this.part1_posY;
		for (int layer = 0; layer <= 5; layer++) {
			ArrayList<BlockPos> arr = scanNearestBlocks(arr_conected, arr_ignore, min_y);
			arr_ignore.addAll(arr);
			arr_conected = arr;
			
			HashMap<BWItemType, Integer> map = new HashMap<BWItemType, Integer>();
			int cnt_not_air = 0;
			for (BlockPos p: arr) {
				//new_block_state = Blocks.stained_glass.getDefaultState().withProperty(Blocks.stained_glass.COLOR, dyeColors[layer % dyeColors.length]);
				//Minecraft.getMinecraft().theWorld.setBlockState(new BlockPos(p.getX(), p.getY(), p.getZ()), new_block_state);
				
				Block block = Minecraft.getMinecraft().theWorld.getBlockState(p).getBlock();
				String name = block.getUnlocalizedName().substring(5);
				if (!block.getUnlocalizedName().contains("air")) {
					cnt_not_air++;
					BWItem item = BWItemsHandler.findItem(name, "");
					if (item == null || item.type == null) continue;
					if (map != null && map.containsKey(item.type)) {
						map.put(item.type, map.get(item.type) + 1);
					} else map.put(item.type, 1);
				}
			}
			
			BWItemType max_item = null;
			int max_val = -2;
			for (Map.Entry<BWItemType, Integer> entry : map.entrySet()) {
				//ChatSender.addText("&e" + entry.getKey() + " &7= &b" + entry.getValue());
				int v = -1;
				
				if (entry.getKey() == BWItemType.BLOCK_WOOL) v = 0;
				else if (entry.getKey() == BWItemType.BLOCK_WOOD) v = 1;
				else if (entry.getKey() == BWItemType.BLOCK_CLAY) v = 2;
				else if (entry.getKey() == BWItemType.BLOCK_LADDER) v = -1;
				else if (entry.getKey() == BWItemType.BLOCK_ENDSTONE) v = 3;
				else if (entry.getKey() == BWItemType.BLOCK_GLASS) v = 0;
				else if (entry.getKey() == BWItemType.BLOCK_OBSIDIAN) v = 4;
				if (v > max_val) {
					max_val = v;
					max_item = entry.getKey();
				}
			}
			
			//ChatSender.addText("Connected: &b" + arr_conected.size() + "&f, ignore = &c" + arr_ignore.size());
			
			float air_treshold = 0.3f; // if less than 0.% of defence is blocks -> break;
			if (map.size() == 0 || max_item == null) {
				//ChatSender.addText("&6Break");
				break;
			} else {
				//ChatSender.addText("max item " + max_item + ", " + ((float)map.get(max_item) / (float)arr.size()));
				BWItemType item_type = max_item;
				this.defence.add(new DefenceLayer(arr, layer, item_type, (float)map.get(max_item) / (float)arr.size(), arr.size() - cnt_not_air));
			}
		}
		
		/*ChatSender.addText("&a&lDEFENCE: ");
		for (DefenceLayer layer: this.defence) {
			//ChatSender.addText("&b" + layer.type + " &7-> &e" + (int)(layer.percentage * 100));
		}*/
	}
	
	public ArrayList<BlockPos> scanNearestBlocks(ArrayList<BlockPos> connected_positions, ArrayList<BlockPos> ignore_positions, int min_y) {
		ArrayList<BlockPos> arr = new ArrayList<BlockPos>();
		for (BlockPos p: connected_positions) {
			int range = 1;
			for (int yi = -range; yi <= range; yi++) {
				for (int xi = -range; xi <= range; xi++) {
					for (int zi = -range; zi <= range; zi++) {
						int bx = p.getX() + xi;
						int by = p.getY() + yi;
						int bz = p.getZ() + zi;
						
						if (by < min_y) continue;
						
						boolean isIgnored = false;
						for (BlockPos p_ignore: ignore_positions) {
							if (bx == p_ignore.getX() && by == p_ignore.getY() && bz == p_ignore.getZ()) {
								isIgnored = true;
								break;
							}
						}
						for (BlockPos p_ignore: arr) {
							if (bx == p_ignore.getX() && by == p_ignore.getY() && bz == p_ignore.getZ()) {
								isIgnored = true;
								break;
							}
						}
						
						if (isIgnored == true) continue;
						
						if (isBlockConnectsToBlock(bx, by, bz, p.getX(), p.getY(), p.getZ())) {
							arr.add(new BlockPos(bx, by, bz));
						}
					}
				}
			}
		}
		return arr;
	}
	
	public static boolean isBlockConnectsToBlock(int x1, int y1, int z1, int x2, int y2, int z2) {
		int distX = Math.abs(x1 - x2);
		int distY = Math.abs(y1 - y2);
		int distZ = Math.abs(z1 - z2);
		if (distX == 1 && distY == 0 && distZ == 0) {
			return true;
		} else if (distX == 0 && distY == 1 && distZ == 0) {
			return true;
		} else if (distX == 0 && distY == 0 && distZ == 1) {
			return true;
		}
		return false;
	}
	
	public String getAnalysis() {
		int defence_layer_count = 0;
		
		ArrayList<String> defence_blocks = new ArrayList<String>();
		String defence_blocks_string = "";
		
		HashSet<String> defence_requirements = new HashSet<String>();
		String defence_requirements_string = "";
		String extras = "";
		
		for (DefenceLayer layer: this.defence) {
			if (layer.type != null && (layer.percentage == 1 || (layer.index + 1 < this.defence.size() && layer.percentage > 0 && layer.type != BWItemType.BLOCK_OBSIDIAN))) {
				defence_layer_count++;
				//String item_name = getBlockNameByType(layer.type);
				String item_name = "&c-";
				if (layer.type == BWItemType.BLOCK_WOOL) item_name = "&7Шерсть";
				else if (layer.type == BWItemType.BLOCK_WOOD) item_name = "&6Дерево";
				else if (layer.type == BWItemType.BLOCK_CLAY) item_name = "&fБетон";
				else if (layer.type == BWItemType.BLOCK_LADDER) item_name = "&7Лестница";
				else if (layer.type == BWItemType.BLOCK_ENDSTONE) item_name = "&eЭндерняк";
				else if (layer.type == BWItemType.BLOCK_GLASS) item_name = "&7Стекло";
				else if (layer.type == BWItemType.BLOCK_OBSIDIAN) item_name = "&b&lОБСА";
				defence_blocks.add(item_name);
				
				
				if (layer.type == BWItemType.BLOCK_ENDSTONE || layer.type == BWItemType.BLOCK_CLAY) {
					if (!defence_requirements.contains("&b&lАлмазная Кирка")) defence_requirements.add("&6Кирка");
				} else if (layer.type == BWItemType.BLOCK_OBSIDIAN) {
					defence_requirements.add("&b&lАлмазная Кирка");
					defence_requirements.remove("&6Кирка");
				} else if (layer.type == BWItemType.BLOCK_WOOD) {
					defence_requirements.add("&6Топор");
				} else if (layer.type == BWItemType.BLOCK_WOOL) {
					//defence_requirements.add("&fНожницы");
				}
				
				
			} else if (layer.index == 0 && layer.type == BWItemType.BLOCK_OBSIDIAN && layer.percentage < 1) {
				defence_layer_count = 1;
				extras = "Не полная защита ОБСОЙ! &8" + (int)(layer.percentage * 100) + "%";
			}
			
			
		}
		
		defence_blocks_string = "&7";
		String prev_block = "none";
		int inline_count = 0;
		Collections.reverse(defence_blocks);
		defence_blocks.add("none");
		if (defence_blocks.size() > 0) {
			for (String s: defence_blocks) {
				if (prev_block.equals("none")) {
					prev_block = s;
					inline_count = 1;
				} else if (prev_block.equals(s)) {
					inline_count += 1;
				} else {
					defence_blocks_string += prev_block;
					if (inline_count > 1) defence_blocks_string += " &7x&c" + inline_count;
					defence_blocks_string += " &8▸ ";
					inline_count = 1;
					prev_block = s;
				}
			}
		}
		if (defence_blocks_string.length() > 5) {
			defence_blocks_string = defence_blocks_string.substring(0, defence_blocks_string.length() - 5).trim();
		}
		
		defence_requirements_string = "";
		for (String s: defence_requirements) defence_requirements_string += s + "&7, ";
		if (defence_requirements_string.length() > 2) {
			defence_requirements_string = defence_requirements_string.substring(0, defence_requirements_string.length() - 2).trim();
		}
		
		String str = "";
		if (defence_layer_count == 0) {
			str = "&fНет защиты";
		} else {
			if (defence_blocks.size() > 1) str += "" + defence_blocks_string + "";	
		}
		if (extras.length() > 0) str += " &b" + extras;
		return str;
	}
	
}
