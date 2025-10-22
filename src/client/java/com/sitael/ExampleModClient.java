package com.sitael;

import com.sitael.gui.InputScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

import java.util.concurrent.atomic.AtomicBoolean;

public class ExampleModClient implements ClientModInitializer {

    private static KeyBinding openChatKey;

    @Override
    public void onInitializeClient() {
        openChatKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "text input window",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_R,
                "db mod"
        ));

        AtomicBoolean screenOpenedThisTick = new AtomicBoolean(false);

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (openChatKey.wasPressed()) {

                if (client.player != null && !screenOpenedThisTick.get() ) {
                    screenOpenedThisTick.set(true);
                    client.setScreen(new InputScreen());
                }
            } else {
                screenOpenedThisTick.set(false);
            }
        });

    }
}