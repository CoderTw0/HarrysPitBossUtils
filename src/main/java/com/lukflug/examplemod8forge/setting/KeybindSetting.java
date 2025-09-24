package com.lukflug.examplemod8forge.setting;

import org.lwjgl.input.Keyboard;

import com.lukflug.panelstudio.base.IBoolean;
import com.lukflug.panelstudio.setting.IKeybindSetting;

public class KeybindSetting extends Setting<Integer> implements IKeybindSetting {
	private boolean lastKeyState = false;
	public KeybindSetting (String displayName, String configName, String description, IBoolean visible, Integer value) {
		super(displayName,configName,description,visible,value);
	}

	@Override
	public int getKey() {
		return getValue();
	}

	@Override
	public void setKey (int key) {
		setValue(key);
	}

	@Override
	public String getKeyName() {
		return Keyboard.getKeyName(getKey());
	}

	public boolean isPressed() {
		boolean current = Keyboard.isKeyDown(getKey());
		boolean pressed = current && !lastKeyState;
		lastKeyState = current;
		return pressed;
	}
}
