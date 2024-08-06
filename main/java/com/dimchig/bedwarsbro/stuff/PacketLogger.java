package com.dimchig.bedwarsbro.stuff;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.dimchig.bedwarsbro.ChatSender;

import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C0EPacketClickWindow;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

public class PacketLogger {
	@SubscribeEvent
	public void onClientPacket(final FMLNetworkEvent.ClientConnectedToServerEvent event) {
		Minecraft.getMinecraft().addScheduledTask(new Runnable() {
			@Override
			public void run() {
				event.manager.channel().pipeline().addBefore("packet_handler", "packet_logger",
						new PacketLoggingHandler());
			}
		});
	}

	private static class PacketLoggingHandler extends io.netty.channel.ChannelDuplexHandler {
		@Override
		public void write(io.netty.channel.ChannelHandlerContext ctx, Object msg,
				io.netty.channel.ChannelPromise promise) throws Exception {
			if (msg instanceof net.minecraft.network.Packet) {
				if (msg instanceof C0EPacketClickWindow) {
					C0EPacketClickWindow packet = (C0EPacketClickWindow) msg;
					// packet.
					// ChatSender.addText("Sent packet: " + msg.getClass().getName());
					//ChatSender.addText("&bWin: " + "&fW&c" + packet.getWindowId() + "&f, S&e" + packet.getSlotId()
					//		+ "&f, UB&a" + packet.getUsedButton() + "&f, M&b" + packet.getMode() + "&f, I&d"
					//		+ packet.getClickedItem() + "&f, A&9" + packet.getActionNumber());

					if (false) {
						if (packet.getClickedItem() == null
								|| packet.getClickedItem().getItem() != Item.getItemFromBlock(Blocks.acacia_fence)) {

							final int currentWindow = packet.getWindowId();

							sendCustomClickWindowPackets(
									new int[] { currentWindow, currentWindow + 1, currentWindow + 2 },
									new int[] { 2, 19, 0 }, new int[] { 0, 0, 0 }, 5);

						}
					}

					// sendCustomClickWindowPacket(5, 38, 0, 6, new ItemStack(Blocks.sandstone), 6);
				}
			}
			super.write(ctx, msg, promise);
		}
	}

	public static void sendCustomClickWindowPacket(int windowId, int slotId, int usedButton, int clickMode,
			ItemStack clickedItem, int actionNumber) {
		C0EPacketClickWindow packet = new C0EPacketClickWindow(windowId, slotId, usedButton, clickMode, clickedItem,
				(short) actionNumber);
		Minecraft.getMinecraft().getNetHandler().addToSendQueue(packet);
	}

	public static void sendCustomClickWindowPackets(final int[] windowIds, final int[] slotIds, final int[] clickModes,
			final int delay) {
		if (windowIds.length != slotIds.length || slotIds.length != clickModes.length) {
			//ChatSender.addText("&cAll input arrays must have the same length!");
			return;
		}

		for (int i = 0; i < windowIds.length; i++) {
			final int index = i;
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						TimeUnit.MILLISECONDS.sleep(delay * index);
						sendCustomClickWindowPacket(windowIds[index], slotIds[index], 0, clickModes[index],
								new ItemStack(Blocks.acacia_fence), index + 10);
						// ChatSender.addText("clicking &e&l" + index);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}).start();
		}
	}
}