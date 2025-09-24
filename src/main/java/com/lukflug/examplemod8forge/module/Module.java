package com.lukflug.examplemod8forge.module;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import com.lukflug.examplemod8forge.module.helpers.PersistenceHelper;
import com.lukflug.examplemod8forge.setting.Setting;
import com.lukflug.panelstudio.base.IBoolean;
import com.lukflug.panelstudio.base.IToggleable;
import com.lukflug.panelstudio.setting.IModule;
import com.lukflug.panelstudio.setting.ISetting;

public abstract class Module implements IModule {
	public final String displayName, description;
	public final IBoolean visible;
	public final List<Setting<?>> settings = new ArrayList<>();
	public final boolean toggleable;
	private boolean enabled = false;

	private final IToggleable toggle = new IToggleable() {
		@Override
		public boolean isOn() {
			return enabled;
		}

		@Override
		public void toggle() {
			enabled = !enabled;
			if (enabled) {
				onEnable();
			} else {
				onDisable();
			}
			saveState();
		}
	};

	public Module(String displayName, String description, IBoolean visible, boolean toggleable) {
		this.displayName = displayName;
		this.description = description;
		this.visible = visible;
		this.toggleable = toggleable;


	}

	@Override
	public String getDisplayName() {
		return displayName;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public IBoolean isVisible() {
		return visible;
	}

	@Override
	public IToggleable isEnabled() {
		return toggleable ? toggle : null;
	}

	@Override
	public Stream<ISetting<?>> getSettings() {
		return settings.stream()
				.filter(setting -> setting instanceof ISetting)
				.sorted((a, b) -> a.displayName.compareTo(b.displayName))
				.map(setting -> (ISetting<?>) setting);
	}


	public void setModuleEnabled(boolean enabled) {
		if (this.enabled != enabled) {
			this.enabled = enabled;
			if (enabled) onEnable();
			else onDisable();
		}
	}

	protected void loadState() {
		boolean savedEnabled = PersistenceHelper.getBoolean(displayName, "enabled", false);
		setModuleEnabled(savedEnabled);
	}

	protected void saveState() {
		PersistenceHelper.setBoolean(displayName, "enabled", enabled);
		PersistenceHelper.save();
	}

	protected void onEnable() {}
	protected void onDisable() {
		saveState();
	}
}
