package ua.wyverno.natives.keyboard;

import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;

public interface NativeKeyboardPressScript {
    void keyPress(NativeKeyEvent event);
}
