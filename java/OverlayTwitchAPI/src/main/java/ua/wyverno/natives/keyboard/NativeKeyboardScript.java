package ua.wyverno.natives.keyboard;

import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;

public interface NativeKeyboardScript {
    void keyPress(NativeKeyEvent event);
    void keyRelease(NativeKeyEvent event);


}
