package com.lukflug.examplemod8forge;

import com.lukflug.examplemod8forge.module.Debug.DebugLogger;
import com.lukflug.examplemod8forge.module.Debug.NametagDebug;
import com.lukflug.examplemod8forge.module.Debug.SidebarDebugger;
import com.lukflug.examplemod8forge.module.helpers.BossMessageParser;
import com.lukflug.examplemod8forge.module.Debug.ChatMessageDebug;
import com.lukflug.examplemod8forge.module.helpers.PersistenceHelper;
import com.lukflug.examplemod8forge.module.impl.*;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;

import com.lukflug.examplemod8forge.module.Category;
import com.lukflug.examplemod8forge.module.ClickGUIModule;
import com.lukflug.examplemod8forge.module.HUDEditorModule;
import com.lukflug.examplemod8forge.module.LogoModule;
import com.lukflug.examplemod8forge.module.TabGUIModule;
import com.lukflug.examplemod8forge.module.WatermarkModule;

import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;

@Mod(modid=ExampleMod.MODID,name=ExampleMod.NAME,version=ExampleMod.VERSION)
public class ExampleMod {
	public static final String MODID="Harrys Pit Boss Helper";
	public static final String NAME="PanelStudio-MC8-Forge BossHelper";
	public static final String VERSION="0.2.4";
	public static Logger logger;
	private static ClickGUI gui;

	@EventHandler
	public void preInit (FMLPreInitializationEvent event) {
		logger = event.getModLog();
		PersistenceHelper.load();

		Runtime.getRuntime().addShutdownHook(new Thread( () -> {
			PersistenceHelper.save();
		}));
	}

	@EventHandler
	public void init (FMLInitializationEvent event) {
		Category.init();
		Category.OTHER.modules.add(new ClickGUIModule());
		Category.OTHER.modules.add(new HUDEditorModule());
		Category.HUD.modules.add(new TabGUIModule());
		Category.HUD.modules.add(new WatermarkModule());
		Category.HUD.modules.add(new LogoModule());
		Category.DEBUG.modules.add(new DebugLogger());
		Category.DEBUG.modules.add(new ChatMessageDebug());
		Category.DEBUG.modules.add(new NametagDebug());
		Category.DEBUG.modules.add(new SidebarDebugger());
		Category.BossHelpers.modules.add(new ShadowWarning());
		Category.BossHelpers.modules.add(new ShatterWarning());
		Category.BossHelpers.modules.add(new CurseDropEstimate());
		Category.BossHelpers.modules.add(new RoombaWarning());
		Category.BossHelpers.modules.add(new SpawnTimers());
		Category.BossHelpers.modules.add(new PylonHighlight());
		Category.BossHelpers.modules.add(new AutoClicker());
		Category.PitStuff.modules.add(new SessionStats());


		gui=new ClickGUI();
		MinecraftForge.EVENT_BUS.register(this);
		MinecraftForge.EVENT_BUS.register(new BossMessageParser());
	}
	
	@SubscribeEvent
	public void onRender (RenderGameOverlayEvent.Post event) {
		if (event.type==RenderGameOverlayEvent.ElementType.HOTBAR) gui.render();
	}

	@Mod.EventHandler
	public void onShutdown(FMLServerStoppingEvent event) {
		PersistenceHelper.save();
	}
	
	@SubscribeEvent
	public void onKeyInput (KeyInputEvent event) {
		if (Keyboard.isKeyDown(ClickGUIModule.keybind.getKey())) gui.enterGUI();
		if (Keyboard.isKeyDown(HUDEditorModule.keybind.getKey())) gui.enterHUDEditor();
		if (Keyboard.getEventKeyState()) gui.handleKeyEvent(Keyboard.getEventKey());
	}
}
