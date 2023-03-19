package ua.wyverno.natives.keyboard;

import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;

public interface NativeKeyboardReleasedScript {
    void keyReleased(NativeKeyEvent event);
}
